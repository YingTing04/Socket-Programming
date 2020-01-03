package a1;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class XClient {
	public static void main(String[] args) throws IOException {
		try {
			InetAddress ip = InetAddress.getByName("localhost");
			Socket socket = new Socket(ip, 6066);
			Scanner sc = new Scanner(System.in);

			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			//prompt client to enter name
			System.out.println(in.readLine());
			String name = sc.nextLine();
			out.println(name);
			out.flush();
			
			while (true) {
				//prompt client to enter message
				String message = in.readLine();
				System.out.println(message);
				
				//if server is full
				if (message.equals("Server full!")) {
					break;
				}
				
				//message sent from client to server
				String data = sc.nextLine();
				out.println(data);
				out.flush();

				//terminating of connection
				String data_processed = data.toLowerCase();
				if (data_processed.equals("exit")) {
					System.out.println("Closing connection");
					socket.close();
					System.out.println("Connection closed");
					break;
				}

				String fromServer = in.readLine();
				System.out.println(fromServer);
			}

			sc.close();
			in.close();
			out.close();
			socket.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
