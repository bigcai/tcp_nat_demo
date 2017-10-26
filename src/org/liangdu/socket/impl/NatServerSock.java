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
 *
 * 
 * @author caishengzhong
 *
 */
public class NatServerSock implements SocketRunnable {

	private String serverIp;
	private String serverPort;
	private String localPort;
	
	public NatServerSock( String serverIp, String serverPort, String localPort ) {
		this.serverIp = serverIp;
		this.serverPort = serverPort;
		this.localPort = localPort;
	}

	public void start() {
		Socket socket = null;
		try {
			String[] serIp = this.serverIp.split("\\.");
			byte serAddr[] = { 
					Integer.valueOf(serIp[0]).byteValue(),
					Integer.valueOf(serIp[1]).byteValue(),
					Integer.valueOf(serIp[2]).byteValue(),
					Integer.valueOf(serIp[3]).byteValue() };
			// 初始化用于连接外网的客户端socket
			socket = new Socket();
			socket.setReuseAddress(true);			// 设置为重用
			socket.bind(new InetSocketAddress(socket.getLocalAddress(), Integer.valueOf(this.localPort).intValue() ));  // 绑定指定的端口
			socket.connect(new InetSocketAddress(InetAddress.getByAddress(serAddr), Integer.valueOf(this.serverPort).intValue()) );
			
			// 从客户端socket获取输入输出流
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			String line;
			line = "A line sent by the NatServerSock";
			out.println(line);
			line = in.readLine();
			System.out.println("nat client got: " + line);
			
			out.close();
			in.close();
			
			// 重用刚刚创建的Client socket, 创建 NatServer socket
			ServerSocket serverSock = new ServerSocket();
			serverSock.setReuseAddress(true);		// 设置为重用
			serverSock.bind( new InetSocketAddress(socket.getLocalAddress(), socket.getLocalPort() ) );		// 重用客户端的IP&Port 
			Socket newsocket = null;
			int accessCountor = 0;
			while ((newsocket = serverSock.accept()) != null) {
				BufferedReader inNewsocket = new BufferedReader(new InputStreamReader( newsocket.getInputStream(), "utf-8" ));
				PrintWriter outNewsocket = new PrintWriter(newsocket.getOutputStream(), true);
				line = null;
				if ((line = inNewsocket.readLine()) != null) {
					System.out.println("【 NatServer got】: " + line);
					line = "A line sent by the NatServer " + newsocket.getLocalSocketAddress() + ((line.indexOf("awesome planet") != -1)? ", your access count : " + (++accessCountor) : ", who are you?");
					outNewsocket.println(line);
				}
				
				inNewsocket.close();
				outNewsocket.close();
				newsocket.close();
			}
			
			
			serverSock.close();
			socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void run() {
		start();
	}

}
