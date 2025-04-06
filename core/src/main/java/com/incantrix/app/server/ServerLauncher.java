package com.incantrix.app.server;

import com.incantrix.network.Network;

import java.io.IOException;

public class ServerLauncher {
    public static void main(String[] args) throws IOException {
        new GameServer();
        System.out.println("Server running on port " + Network.PORT);
    }
}
