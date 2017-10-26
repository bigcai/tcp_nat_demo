package org.liangdu;

import org.liangdu.socket.impl.ClientSock;
import org.liangdu.socket.impl.NatServerSock;
import org.liangdu.socket.impl.ServerSock;

public class TCPNatTestDemo {

	/**
	 * action type��
	 * 
	 * -c as client -s as server -ns as nat server
	 * 
	 * e.g. -c or -s or -ns
	 * -s 6698  -c 192.168.1.102 6698  -ns 192.168.1.102 6698 54915
	 * 
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String CLIENT_TYPE = "-c".trim();
		String SERVER_TYPE = "-s".trim();
		String NAT_SERVER_TYPE = "-ns".trim();
		Thread ts = null;
		Thread tc = null;
		Thread tns = null;
		
		int i = 0;

		while (i < args.length && args[i] != null ) {
			  // 如果扫描到-s，则开启外网服务器，参数：{本地服务器端口}
			  if (args[i].trim().equals(SERVER_TYPE)) {

				int cliArgNum = 2;
				String serverPort = null;

				if (args[i + 1].trim() != null) {
					serverPort = args[i + 1].trim();
				}

				(ts = new Thread ( new ServerSock(serverPort) )).start();
				i += cliArgNum;
				
				// 如果扫描到-c，则开启非LAN的客户端，参数：{外网服务器IP和端口}
			} else if (args[i].trim().equals(CLIENT_TYPE)) {
				int cliArgNum = 3;
				String serverIp = null;
				String serverPort = null;
				if (args[i + 1].trim() != null) {
					serverIp = args[i + 1].trim();
				}
				if (args[i + 2].trim() != null) {
					serverPort = args[i + 2].trim();
				}
				
				(tc = new Thread ( new ClientSock(serverIp, serverPort) )).start();
				i += cliArgNum;
				
				// 如果扫描到-ns，则开启内网服务器，参数：{外网服务器IP和端口，以及内网的服务器端口}
			}  else if (args[i].trim().equals(NAT_SERVER_TYPE)) {
				int cliArgNum = 3;
				String serverIp = null;
				String serverPort = null;
				String localPort = null;
				if (args[i + 1].trim() != null) {
					serverIp = args[i + 1].trim();
				}
				if (args[i + 2].trim() != null) {
					serverPort = args[i + 2].trim();
				}
				
				if (args[i + 3].trim() != null) {
					localPort = args[i + 3].trim();
				}
				
				(tns = new Thread ( new NatServerSock(serverIp, serverPort, localPort) )).start();
				i += cliArgNum;

			} 
			  

		}
		try {
			if (ts!=null) 
				ts.join();
			if (ts!=null) 
				tc.join();
			if (tns!=null) 
				tns.join();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
