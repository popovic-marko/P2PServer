import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Server {

	static LinkedList<ServerThread> clients = new LinkedList<ServerThread>();

	public static void main(String[] args) {

		int port = 2222;
		Socket clientSocket = null;
		int i =0;
		
		try {
			@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(port);
			
			while(true){
				clientSocket = serverSocket.accept();
				clients.addLast(new ServerThread(clientSocket, clients));
				clients.getLast().start();
				System.out.println("new client #" +(++i));
				
			}

		} catch (IOException e) {
			//e.printStackTrace();
			System.out.println("Socket error...");
		}
	}
}
