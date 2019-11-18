package client;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.Socket;

public class UADataNode {
	
	private static final int PORT = 32000;
	private static final String IP = "";
	
	public static void main(String[] args) {
		
		try {
			while (true) {
				Socket socket = new Socket(IP, PORT);
				byte[] data = new byte[2048];
				
				InputStream in = socket.getInputStream();
				int bytesReceived = in.read(data, 0, data.length);
				
				if (bytesReceived == 4) {
					break;
				}
				
				byte[] filenameBytes = new byte[50];
				
				for (int i = 0; i < 50; i++) {
					filenameBytes[i] = data[i];
				}
				
				String filename = new String(filenameBytes);
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filename));
				
				System.out.println("Writing file: " + filename);
				out.write(data, 50, data.length);
				out.close();
				
				socket.close();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}

}
