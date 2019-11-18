package server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class UAMaster {
	
	private static final int PORT = 32000;
	private static final int MAX_CONNECTIONS = 10;
	
	private int dataNodes;
	private String path;
	private ServerSocket server;
	
	final static ConcurrentHashMap<Integer, Socket> pool = new ConcurrentHashMap<>();

	public UAMaster(int dataNodes, String path) {
		this.dataNodes = dataNodes;
		this.path = path;
	}
	
	public void start() {
		try {
			File[] files = new File(path).listFiles();
			Map<Integer, Queue<File>> fileQueues = new HashMap<>();
			Map<String, Integer> dataNodeMap = new HashMap<>();
			setup(files, fileQueues);
			server = new ServerSocket(PORT);
			int dataNodeId = 0;
			String serverAddress;
			
			while (true) {
				if (UAClientCounter.getCount() < MAX_CONNECTIONS) {
					UAClientCounter.add();
					Socket socket = server.accept();
					serverAddress = socket.getInetAddress().getHostAddress();
					
					if (!dataNodeMap.containsKey(serverAddress)) {
						dataNodeMap.put(serverAddress, dataNodeId++);
					}
					
					System.out.println("Connected. Id: " + dataNodeMap.get(serverAddress) + " Server Address: " + serverAddress);
					
					if (!fileQueues.get(dataNodeMap.get(serverAddress)).isEmpty()) {
						new UAClientConnection(socket, fileQueues.get(dataNodeMap.get(serverAddress)).poll()).run();
					} else {
						new UAClientConnection(socket, null);
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		if (server != null) {
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void setup(File[] files, Map<Integer, Queue<File>> fileQueues) {
		if (files == null) {
			return;
		}
		
		for (int i = 0; i < this.dataNodes; i++) {
			fileQueues.put(i, new ArrayDeque<>());
		}
		
		int hash;
		
		for (File file : files) {
			if (file.isFile()) {
				hash = file.hashCode() % this.dataNodes;
				fileQueues.get(hash).add(file);
			}
		}
	}
	
	public static void main(String[] args) {
		
		String path = "";
		int nodes = 0;
		
		if (args == null || args.length < 2) {
			System.out.println("Please provide number of data nodes and an input path.");
			return;
		} else {
			nodes = Integer.parseInt(args[0]);
			path = args[1];
		}
		
		UAMaster master = new UAMaster(nodes, path);
		master.start();
		
	}

}
