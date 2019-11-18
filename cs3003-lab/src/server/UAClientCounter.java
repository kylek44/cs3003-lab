package server;

public class UAClientCounter {
	
	static int count = 0;
	
	public static synchronized void add() {
		count++;
	}
	
	public static synchronized void remove() {
		count--;
	}
	
	public static int getCount() {
		return count;
	}

}
