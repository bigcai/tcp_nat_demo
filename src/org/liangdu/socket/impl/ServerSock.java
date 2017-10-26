package org.liangdu.socket.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.liangdu.socket.SocketRunnable;

/**
 * 
 * 
 * @author caishengzhong
 * 
 */
public class ServerSock implements SocketRunnable {

	private String serverPort;

	public ServerSock(String serverPort) {
		this.serverPort = serverPort;
	}

	public void start() {
		ServerSocket serverSocket = null;
		Socket socket = null;
		String natIP = null;
		String natPort = null;
		try {
			// 创建外网服务器Socket
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress(Integer.valueOf(
					this.serverPort).intValue()));
			while ((socket = serverSocket.accept()) != null) {

				// 读取数据
				BufferedReader in = new BufferedReader(new InputStreamReader(
						socket.getInputStream(), "utf-8"));
				PrintWriter out = new PrintWriter(socket.getOutputStream(),
						true);
				String line = null;
				if ((line = in.readLine()) != null) {
					System.out.println("server got: " + line);
					if (line.contains("A line sent by the NatServerSock")) {
						line = "A line sent by the server, I have saved your IP&Port {  "
								+ socket.getRemoteSocketAddress() + " }";
						out.println(line);
						String[] tmps = socket.getRemoteSocketAddress()
								.toString().split(":");
						natIP = tmps[0].substring(1);
						natPort = tmps[1];

					} else if (natIP != null && natPort != null) {
						line = "A line sent by the server, nat server is "
								+ natIP + ":" + natPort;
						out.println(line);
					}

				}
				in.close();
				out.close();
				socket.close();

			}
			serverSocket.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		start();
	}
}
