package com.example.message.socket.listener;

import com.example.message.socket.SocketManage;

public interface OnConnection {
    void success(SocketManage manage);

    default void error(SocketManage manage) {
    }
}
