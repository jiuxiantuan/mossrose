package com.jiuxian.mossrose.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import com.google.common.base.Throwables;

public final class NetworkUtils {

	public static String getLocalIp() {
		try {
			InetAddress localAddress = InetAddress.getLocalHost();
			if (localAddress != null && !localAddress.isAnyLocalAddress() && !localAddress.isLoopbackAddress()) {
				return localAddress.getHostAddress();
			}
		} catch (UnknownHostException e1) {
			throw Throwables.propagate(e1);
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
			throw Throwables.propagate(e);
		}
		throw new RuntimeException("Cannot get local ip.");
	}

	public static void main(String[] args) {
		System.out.println(getLocalIp());
	}

}
