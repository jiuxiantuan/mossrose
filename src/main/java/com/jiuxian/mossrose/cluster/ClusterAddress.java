/**
 * Copyright 2015-2020 jiuxian.com.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
