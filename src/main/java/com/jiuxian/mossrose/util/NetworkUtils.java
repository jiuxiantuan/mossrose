package com.jiuxian.mossrose.util;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

public final class NetworkUtils {

    public static String getLocalIp() {
        try {
            InetAddress localAddress = InetAddress.getLocalHost();
            if (localAddress != null && !localAddress.isAnyLocalAddress() && !localAddress.isLoopbackAddress()) {
                return localAddress.getHostAddress();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface current = interfaces.nextElement();
                if (!current.isUp() || current.isLoopback() || current.isVirtual())
                    continue;
                Enumeration<InetAddress> addresses = current.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr.isLoopbackAddress())
                        continue;
                    if (addr instanceof Inet4Address) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException("Cannot get local ip.");
    }

    public static String getReachableIp(String remoteHost, int port) {
        try(Socket s = new Socket(remoteHost, port)) {
            return s.getLocalAddress().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        System.out.println(getLocalIp());
    }

}