import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
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
	public String userCall = null;
	public int userPort = -1;

	Socket communication = null;
	LinkedList<ServerThread> clients;

	public ServerThread(Socket socket, LinkedList<ServerThread> clients) {
		this.communication = socket;
		this.clients = clients;
	}

	@SuppressWarnings({ "unused" })
	public void run() {

		String newLine = null;
		String newRecipient;
		LinkedList<ServerThread> recipients = new LinkedList<>();
		boolean end = false;

		try {
			// ___________________TCP________________
			clientInput = new BufferedReader(new InputStreamReader(communication.getInputStream()));
			serverOutput = new PrintStream(communication.getOutputStream());

			serverOutput.println("Welcome!");

			// Log in __________
			while (true) {
				try {
					boolean exists = false;
					int i = 0;
					serverOutput.println("Enter your username: ");
					String tempName;
					try {
						tempName = clientInput.readLine();
						if (tempName.equals("null")) {
							throw new Exception(); // MOJ DODATAK
						}
					} catch (Exception e) {
						System.out.println("Unknown client connection timed out." + e.getMessage());
						date = new Date();
						printer = new PrintWriter(new BufferedWriter(new FileWriter("Info.txt", true)));
						printer.println("Unknown clients connection timed out. /" + date.toString());
						printer.close();
						clients.remove(this);
						return;
					}

					while (i < clients.size()) {
						if (clients.get(i) != null && clients.get(i) != this && clients.get(i).name != null
								&& clients.get(i).name.equals(tempName)) {
							serverOutput.println("'" + tempName + "' already exist. Try again.");
							exists = true;
							break;
						} else {
							i++;
						}
					}
					if (exists == false) {
						name = tempName;
						break;
					}
				} catch (Exception e) {
					serverOutput.println("Invalid input. Try again. ");
				}
			}

			serverOutput.println("Welcome " + name);
			userPort = Integer.parseInt(clientInput.readLine().trim());
			int i = 0;
			String list = "";
			while (i < clients.size()) {
				if (clients.get(i) != null && clients.get(i) != this) {
					list += clients.get(i).name + "; ";
					i++;
				} else {
					i++;
				}
			}
			serverOutput.println("Online clinets: " + list);
			// Log in __________

			while (true) {

				String request = clientInput.readLine();
				i = 0;

				if (request.contains("getOnline")) {
					list = "";
					while (i < clients.size()) {
						if (clients.get(i) != null && clients.get(i) != this) {
							String temp = clients.get(i).name + "; ";
							list = list.concat(temp);
							i++;
						} else {
							i++;
						}
					}
					serverOutput.println("Online clients: " + list);

				} else if (request.startsWith("conn")) {
					userCall = request.substring(6);
					boolean found = false;
					int j = 0;
					while (j < clients.size()) {

						if (clients.get(j).name.equals(userCall)) {
							found = true;
							clients.get(j).serverOutput.println(name + " has requested connection with you. Accept?");
							j++;

						} else {
							j++;
						}

					}
					if (found == false) {
						serverOutput.println("User not found");
					}

				} else if (request.contains("yes")) {
					int ii = 0;
					boolean b = false;

					while (ii < clients.size()) {
						if ((clients.get(ii) != this) && (clients.get(ii).userCall.equals(this.name))) {
							b = true;

							serverOutput.println(clients.get(ii).communication.getInetAddress().toString() + "/"
									+ clients.get(ii).userPort);

							clients.get(ii).serverOutput
									.println("conn: yes/" + communication.getInetAddress().toString() + "/" + userPort);

							clients.remove(ii);
							clients.remove(this);
							return;

						}
						ii++;

					}
					if (!b) {
						serverOutput.println("Error...");
					}

				} else if (request.contains("no")) {
					int ii = 0;
					while (ii < clients.size()) {
						if (clients.get(ii).userCall == this.name) {
							clients.get(ii).serverOutput.println("conn: no");
						} else {
							serverOutput.println("Error...");
						}
					}

				} else {
					serverOutput.println("Goodbye!");
					clients.remove(this);
					return;
				}
			}

		} catch (Exception e) {
			serverOutput.println("Server error...");
		}
	}

}
