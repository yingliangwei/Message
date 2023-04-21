package com.example.message.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.message.service.FirstService;

import java.util.List;

//开机自启
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        initBootService(context);
    }

    private void initBootService(Context context) {
        if (!serverIsRunning(context, FirstService.class.getName())) {
            Intent intent = new Intent(new Intent(context, FirstService.class));
            intent.setPackage(context.getPackageName());
            //运行Service
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
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
}
