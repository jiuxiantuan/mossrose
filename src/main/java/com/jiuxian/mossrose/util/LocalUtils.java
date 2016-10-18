package com.jiuxian.mossrose.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import com.google.common.base.Throwables;

public final class LocalUtils {

	public static final String LOCALHOST = "127.0.0.1";

	public static final String ANYHOST = "0.0.0.0";

	public static String getLocalIp() {
		try {
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					ip = (InetAddress) addresses.nextElement();
					if (ip instanceof Inet4Address && isValidAddress(ip)) {
						return ip.getHostAddress();
					}
				}
			}
		} catch (SocketException e) {
			throw Throwables.propagate(e);
		}
		throw new RuntimeException("Cannot get local ip.");
	}

	private static boolean isValidAddress(InetAddress address) {
		if (address == null || address.isLoopbackAddress())
			return false;
		String name = address.getHostAddress();
		return (name != null && !ANYHOST.equals(name) && !LOCALHOST.equals(name));
	}

	public static void main(String[] args) {
		System.out.println(getLocalIp());
	}

}
