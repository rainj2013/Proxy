package cn.rainj2013;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

public class MyProxy {

	public static void main(String[] args) {
		ServerSocket server = null;
		try {
				server = new ServerSocket(3721);
				while(true){
					Socket socket = server.accept();
					InputStream in = socket.getInputStream();
					OutputStream out = socket.getOutputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					String address;
					address = reader.readLine();
					if(address==null)
						continue;
					if(address.startsWith("GET ")&&address.endsWith(" HTTP/1.1")){
						address = (address.split(" ")[1].trim());
					}
					
					if(!address.contains("http://"))
						continue;
					System.out.println(address);
					
					System.setProperty("proxySet", "true");  
			        System.setProperty("socksProxyHost", "127.0.0.1");  
			        System.setProperty("socksProxyPort", "1080");
					URL url = new URL(address);
					InputStream ins = url.openConnection().getInputStream();
					byte[] bytes = new byte[1024];
					int len = 0;
					while((len = ins.read(bytes,0,1024))!=-1){
						out.write(bytes,0,len);
					}
					
					ins.close();
					out.close();
					in.close();
					socket.close();
				}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
