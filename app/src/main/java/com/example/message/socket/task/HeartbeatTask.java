package com.example.message.socket.task;

import com.example.message.socket.SocketManage;

public class HeartbeatTask extends Thread {
    private int size = 3;
    private final SocketManage socketManage;
    private boolean isRun;

    public HeartbeatTask(SocketManage socketManage) {
        this.socketManage = socketManage;
        isRun = false;
    }

    public void setRun(boolean run) {
        isRun = run;
    }

    @Override
    public void run() {
        while (true) {
            if (isRun) {
                break;
            }
            if (size <= 0) {
                socketManage.close();
                break;
            }

            if (socketManage.getSocketChannel() != null && socketManage.getSocketChannel().isConnected()) {
                boolean is = socketManage.print("0");
                if (is) {
                    try {
                        Thread.sleep(30 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        socketManage.close();
                        break;
                    }
                    if (socketManage.isHeartbeatTask()) {
                        size = 3;
                        socketManage.setHeartbeatTask(false);
                    } else {
                        size = size - 1;
                    }
                }
            } else {
                socketManage.close();
                break;
            }
        }
    }
}
