/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 *
 * @author vv
 */
public class ServerSupport {
    
    public static final String LOCAL_HOST = "localhost";
    private static final int SERVER_POLL_TIMEOUT = 60 * 1000;
    private static final int SERVER_POLL_RECONNECT_TIME = 200;
    
    @SuppressWarnings("SleepWhileInLoop") //NOI18N
    public static void waitForServer(String host, int port) throws InterruptedException, ServerTimeOutException {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < SERVER_POLL_TIMEOUT) {
            try {
                try (Socket s = new Socket(host, port)) {
                    assert s != null;
                    return;
                }
            } catch (IOException ex) {
                //no-op
            }
            Thread.sleep(SERVER_POLL_RECONNECT_TIME);
        }
        throw new ServerTimeOutException();
    }

    public static boolean isLocalHost(String serverHost) {
        try {
            InetAddress addr = InetAddress.getByName(serverHost);
            // Check if the address is a valid special local or loop back
            if (addr.isAnyLocalAddress() || addr.isLoopbackAddress()) {
                return true;
            }
            // Check if the address is defined on any interface
            return NetworkInterface.getByInetAddress(addr) != null;
        } catch (UnknownHostException | SocketException e) {
            return false;
        }
    }
    
    public static class ServerTimeOutException extends Exception {

        public ServerTimeOutException() {
            super("Timeout elapsed while waiting for Server to start.");
        }
    }
}
