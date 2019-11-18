package server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;

public class UAClientConnection implements Runnable {
	
	private final Socket socket;
	private final File file;
	private BufferedInputStream bis;
	private OutputStream out;
	private byte[] data;
	
	public UAClientConnection(Socket socket, File file) {
		this.socket = socket;
		this.file = file;
	}

	@Override
	public void run() {
		try {
			if (file == null) {
				data = "done".getBytes();
				out = socket.getOutputStream();
				out.write(data);
				out.flush();
				System.out.println("Sent \"done\" to socket at Address: " + this.socket.getInetAddress().getHostAddress());
			} else {
				data = new byte[(int) file.length() + 50];
				byte[] filenameBytes = file.getName().getBytes();
				
				for (int i = 0; i < filenameBytes.length; i++) {
					data[i] = filenameBytes[i];
				}
				
				bis = new BufferedInputStream(new FileInputStream(file));
				
				// Read in file to send
				System.out.println("Reading file " + this.file.getAbsolutePath());
				bis.read(data, 50, data.length);
				bis.close();
				out = socket.getOutputStream();
				
				// Send file
				System.out.println("Writing " + this.file.getAbsolutePath() + " to Address: " + this.socket.getInetAddress().getHostAddress());
				out.write(data);
				out.flush();				
			}
			
			// Close socket and lower open socket count
			System.out.println("Closing socket to Address: " + this.socket.getInetAddress().getHostAddress());
			this.socket.close();
			UAClientCounter.remove();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
