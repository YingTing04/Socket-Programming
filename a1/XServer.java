package a1;

import java.io.*;
import java.net.*;
import java.util.Date;


public class XServer {
	//count is max number of clients server can accept
	static int count = 0;
	//cache stores data of previous clients
	static CacheData [] cache = new CacheData[10];
	//count2 is number of clients
	static int count2 = 0;
	
	//update number of clients using server
	public static void updateCount() {count--;}
	
	//update end date and time for each client in cache
	public static void updateCache(int x) {
		cache[x].setEndDateTime(new Date());
	}
	
	//update client name in cache
	public static void updateClientName(int x, String name) {
		cache[x].setClientName(name);
	}
	
	//print contents in cache
	public static void printCache() {
		System.out.println("Printing cache");
		for (int i = 0; i < count2; i++) {
			System.out.println("Client ID: " + cache[i].getClientID());
			System.out.println("Client Name: " + cache[i].getClientName());
			System.out.println("Start Date and Time: " + cache[i].getStartDateTime());
			System.out.println("End Date and Time: " + cache[i].getEndDateTime());
		}
		System.out.println("End printing cache");
	}
	
	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = new ServerSocket(6066);
		System.out.println("Starting server");
		
		while (true) {
			Socket clientSocket = null;
			
			try {
				//attempting to accept a connection
				clientSocket = serverSocket.accept();
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

				//server is not full
				if (count < 3) {
					count ++;
					count2 ++;
					cache[count2 -1] = new CacheData("Client " + count2, null, new Date(), null);
					System.out.println("Client " + count2 + " is connected");

					System.out.println("Assigning new thread for Client " + count2);
					ClientThread c = new ClientThread(clientSocket, out, in, count2);

					c.start();

				}
				
				//server is full
				else {
					out.println("Server full!");
					out.flush();
					clientSocket.close();
					System.out.println("Server full!");
					printCache();
				}
				
			} catch (Exception e) {
				clientSocket.close();
				e.printStackTrace();
			}
		}
	}
}

class ClientThread extends Thread {
	private Socket clientSocket;
	PrintWriter out;
	BufferedReader in;
	int count2;

	public ClientThread(Socket clientSocket, PrintWriter out, BufferedReader in, int count2) {
		this.clientSocket = clientSocket;
		this.out = out;
		this.in = in;
		this.count2 = count2;
	}

	public void run() {
		//requests for client's name
		try{
			out.println("Enter your name: ");
			out.flush();
			String name = in.readLine();
			XServer.updateClientName(count2-1, name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//client can send messages to server and receive an echo
		while (true) {
			try {
				out.println("Enter any line for echo: ");
				out.flush();
				String data = in.readLine();
				String data_processed = data.toLowerCase();
				
				//terminating of connection
				if (data_processed.equals("exit")) {
					System.out.println("Client " + count2 + " sends exit");
					System.out.println("Closing connection for Client " + count2);
					this.clientSocket.close();
					System.out.println("Connection closed for Client " + count2);
					XServer.updateCount();
					XServer.updateCache(count2-1);
					XServer.printCache();
					break;
				} else {
					out.println(data_processed + " ACK");
					out.flush();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
			this.out.close();
			this.in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

//object in cache
class CacheData {
	private String clientName;
	private String clientID;
	private Date startDateTime;
	private Date endDateTime;
	
	public CacheData(String clientID, String clientName, Date startDateTime, Date endDateTime) {
		this.clientID = clientID;
		this.clientName = clientName;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
	}
	
	public String getClientID() {
		return clientID;
	}
	
	public void setClientID(String s) {
		this.clientID = s;
	}
	
	public String getClientName() {
		return clientName;
	}
	
	public void setClientName(String s) {
		this.clientName = s;
	}
	
	public Date getStartDateTime() {
		return startDateTime;
	}
	
	public void setStartDateTime(Date d) {
		this.startDateTime = d;
	}
	
	public Date getEndDateTime() {
		return endDateTime;
	}
	
	public void setEndDateTime(Date d) {
		this.endDateTime = d;
	}
}

