import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.LinkedList;

public class ServerThread extends Thread {
	
	BufferedReader clientInput = null;
	PrintStream serverOutput = null;
	PrintWriter printer = null;
	
	
	String name = null;
	Date date;
	
	Socket communication = null;
	LinkedList<ServerThread> clients;
	
	public ServerThread(Socket socket, LinkedList<ServerThread> clients){
		this.communication = socket;
		this.clients = clients;
	}
	
	@SuppressWarnings({ "unused", "null", "deprecation" })
	public void run(){
		
		String newLine = null;
		String newRecipient;
		LinkedList<ServerThread> recipients = new LinkedList<>();
		boolean end = false;
		
		
		try {
			//___________________TCP________________
			clientInput = new BufferedReader(
					new InputStreamReader(communication.getInputStream()));
			serverOutput = new PrintStream(communication.getOutputStream());
			
			
			serverOutput.println("Welcome!");
			
	//  Log in __________				
			while(true){
				try {
					boolean exists = false;
					int i =0;
					serverOutput.println("Enter your username: ");
					String tempName;
					try {
						 tempName = clientInput.readLine();
					} catch (Exception e) {
						System.out.println("Unknown client connection timed out."+ e.getMessage());
						date = new Date();
						printer = new PrintWriter(
								new BufferedWriter(
										new FileWriter("Info.txt",true)));
						printer.println("Unknown clients connection timed out. /"+date.toString());
						printer.close();
						clients.remove(this);
						return;
					}
					
					while(i<clients.size()){
						if(clients.get(i)!=null && clients.get(i)!= this
								&& clients.get(i).name!=null && clients.get(i).name.equals(tempName)){
							serverOutput.println("'"+tempName+"' already exist. Try again.");
							exists = true;
							break;
						}else{
							i++;
						}
					}
					if(exists==false){
					name = tempName;
					break;
					}
				} catch (Exception e) {
					serverOutput.println("Invalid input. Try again. ");
					}
				}
			
			
			
			serverOutput.println("Welcome " + name);
			int i = 0;
			String list = "";
			while (i<clients.size()) {
				if(clients.get(i) != null && clients.get(i)!= this){
					list += clients.get(i).name + "; ";
					i++;
				}else{
				i++;
				}
			}	
			serverOutput.println("Online clinets: " + list);
			//  Log in __________
			
			
		while(true){
			
			String request = clientInput.readLine();
			i = 0;
			if(request.contains("getOnline")){
			   list = "";
				while (i<clients.size()) {
					if(clients.get(i) != null && clients.get(i)!= this){
						String temp = clients.get(i).name + "; ";
						list = list.concat(temp);
						i++;
					}else{
					i++;
					}
				}
				serverOutput.println("Online clients: " + list);
			
			}else if(request.startsWith("conn")){
				String user = request.substring(6);
				boolean found = false;	
					int j = 0;
					while(j < clients.size()){
						
						if(clients.get(j).name.equals(user)){
							found = true;
							clients.get(j).serverOutput.println(name + " has requested connection with you. Accept?");
							
							String response = clients.get(j).clientInput.readLine();
							
							if(response.equals("yes")){
							serverOutput.println("conn: yes/"
													+clients.get(j).communication.getInetAddress().toString()+ "/"
														+clients.get(j).communication.getPort());
//							clients.get(j).serverOutput.println(communication.getInetAddress().toString() + "/"
//													+ communication.getPort());
							clients.get(j).destroy();
							clients.remove(j);
							clients.remove(this);
							return;
							}else{
								serverOutput.println("conn: no");
							}
						}else{
							j++;
						}
						
					}if(found == false){
						serverOutput.println("User not found");
					}
					
				
			}else {
				serverOutput.println("Goodbye!");
				clients.remove(this);
				return;
			}
		}	
						
		} catch (IOException e) {
			serverOutput.println("Server error...");
			e.printStackTrace();
			}
		}
		
	
		
	
}
