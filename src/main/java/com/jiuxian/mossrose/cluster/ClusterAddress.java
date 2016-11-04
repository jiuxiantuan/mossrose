package com.jiuxian.mossrose.cluster;

public class ClusterAddress {

	private String host;
	private int port;

	private static final String SPLITTER = ":";

	public ClusterAddress(String plainAddress) {
		this(plainAddress.substring(0, plainAddress.indexOf(SPLITTER)), Integer.parseInt(plainAddress.substring(plainAddress.indexOf(SPLITTER) + 1)));
	}

	public ClusterAddress(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	public String toPlainAddress() {
		return host + SPLITTER + port;
	}

	@Override
	public String toString() {
		return "ClusterAddress [host=" + host + ", port=" + port + "]";
	}

}
