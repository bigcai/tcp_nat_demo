package org.liangdu.socket.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.liangdu.socket.SocketRunnable;

/**
 * ���пͻ���
 * 
 * @author caishengzhong
 * 
 */
public class ClientSock implements SocketRunnable {

	private String serverIp;
	private String serverPort;

	public ClientSock(String serverIp, String serverPort) {
		this.serverIp = serverIp;
		this.serverPort = serverPort;
	}

	public void start() {
		sleep(5000);
		Socket socket = null;
		try {
			String[] serIp = this.serverIp.split("\\.");
			byte serAddr[] = { Integer.valueOf(serIp[0]).byteValue(),
					Integer.valueOf(serIp[1]).byteValue(),
					Integer.valueOf(serIp[2]).byteValue(),
					Integer.valueOf(serIp[3]).byteValue() };

			String natIp = null;
			String natPort = null;
			while (true) {
				String line = null;
				PrintWriter out = null;
				BufferedReader in = null;
				if (natIp == null && natPort == null) {
					socket = new Socket();
					socket.connect(new InetSocketAddress(InetAddress
							.getByAddress(serAddr), Integer.valueOf(
							this.serverPort).intValue()));

					out = new PrintWriter(socket.getOutputStream(), true);
					in = new BufferedReader(new InputStreamReader(
							socket.getInputStream()));
					out.println("I am finding nat server");
					line = in.readLine();
					System.out.println("client1 got: " + line);

					out.close();
					in.close();
					socket.close();
					
					String prefix = "A line sent by the server, nat server is ";
					if (line != null && !line.contains(prefix)) {
						sleep(600);
					} else {
						String[] tmps = line.substring(prefix.length()).split(
								":");
						natIp = tmps[0];
						natPort = tmps[1];
						line = null;
					}
				} else {
					String[] natIpArr = natIp.split("\\.");
					byte serAddrByte[] = {
							Integer.valueOf(natIpArr[0]).byteValue(),
							Integer.valueOf(natIpArr[1]).byteValue(),
							Integer.valueOf(natIpArr[2]).byteValue(),
							Integer.valueOf(natIpArr[3]).byteValue() };

					socket = new Socket();
					socket.connect(new InetSocketAddress(InetAddress
							.getByAddress(serAddrByte), Integer
							.valueOf(natPort).intValue()));

					out = new PrintWriter(socket.getOutputStream(), true);
					in = new BufferedReader(new InputStreamReader(
							socket.getInputStream()));
					out.println("Hello World, I am from other awesome planet");
					line = in.readLine();
					System.out.println("【 client2 got 】: " + line);

					sleep(1000);
					out.close();
					in.close();
					socket.close();
				}

			}

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			Thread.sleep(600);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		start();
	}

}
