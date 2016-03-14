package cn.rainj2013;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MyProxy {

	public static void main(String[] args) {
		ServerSocket server = null;
		Socket socket = null;
		try {
			server = new ServerSocket(3721);
			ExecutorService exec = Executors.newCachedThreadPool();
			while (true) {
				socket = server.accept();
				Future<String> future = exec.submit(new MyTask(socket));
				try {
					future.get(10, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					continue;
				} catch (ExecutionException e) {
					continue;
				} catch (TimeoutException e) {
					continue;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class MyTask implements Callable<String> {
	private Socket socket;

	public MyTask(Socket socket) {
		super();
		this.socket = socket;
	}

	@Override
	public String call() {
		InputStream in;
		try {
			in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			String address;
			address = reader.readLine();
			if (address == null)
				return "fail";
			if (address.startsWith("GET ") && address.endsWith(" HTTP/1.1")) {
				address = (address.split(" ")[1].trim());
			}

			if (!address.contains("http://"))
				return "fail";
			System.out.println(address);

			/*System.setProperty("proxySet", "true");
			System.setProperty("socksProxyHost", "202.116.148.193");
			System.setProperty("socksProxyPort", "1080");*/
			URL url = new URL(address);
			InputStream ins = url.openConnection().getInputStream();
			
			int len;
			if(address.endsWith(".js")||address.endsWith(".css")||address.endsWith(".jpg")||address.endsWith(".png")||address.endsWith(".gif")){
				byte[] data = new byte[1024];
				while ((len = ins.read(data, 0, 1024))!=-1) {
					out.write(data, 0, len);
				}
			}else {
				BufferedReader br = new BufferedReader(new InputStreamReader(ins));
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out,"GBK"));
				char[] data = new char[1024];
				while ((len = br.read(data, 0, 1024))!=-1) {
					bw.write(data, 0, len);
				}
				br.close();
				bw.close();
			}
			ins.close();
			out.close();
			in.close();
			socket.close();
			System.out.println("end");
			return "success";
		} catch (IOException e) {
			e.printStackTrace();
			return "fail";
		}
	}

}