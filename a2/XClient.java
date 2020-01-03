package a2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class XClient {
	public static void main(String[] args) throws IOException {
		try {
			String name = "";
			Scanner sc = new Scanner(System.in);

			// establish connection with DHCP Server
			DatagramSocket socket = new DatagramSocket();
			byte[] buf = new byte[256];
			InetAddress address = InetAddress.getByName("localhost");
			DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 7070);
			socket.send(packet);

			// receive IPConfig from server
			packet = new DatagramPacket(buf, buf.length);
			socket.receive(packet); // wait till a packet is received
			String received = new String(packet.getData(), 0, packet.getLength());

			// process received data and retrieve IP Address
			String[] receivedData = received.split(",");
			name = receivedData[0];
			System.out.println(name);

			while (true) {
				System.out.println("Enter domain name for DNS Server, enter renew or release for DHCP Server: ");
				String cmd = sc.nextLine();
				String send = name + "," + cmd;
				buf = send.getBytes();

				// renew connection
				if (cmd.equalsIgnoreCase("renew")) {
					packet = new DatagramPacket(buf, buf.length, address, 7070);
					socket.send(packet);

					// receive confirmation message
					packet = new DatagramPacket(buf, buf.length);
					socket.receive(packet); // wait till a packet is received
					String msg = new String(packet.getData(), 0, packet.getLength());
					System.out.println(msg);

				}
				// release connection
				else if (cmd.equalsIgnoreCase("release")) {
					packet = new DatagramPacket(buf, buf.length, address, 7070);
					socket.send(packet);
					socket.close();
					return;
				}
				// retrieve domain information
				else {
					packet = new DatagramPacket(buf, buf.length, address, Integer.valueOf(receivedData[2]));
					socket.send(packet);

					// receive domain IP
					packet = new DatagramPacket(buf, buf.length);
					socket.receive(packet); // wait till a packet is received
					String receivedIP = new String(packet.getData(), 0, packet.getLength());

					String IPV4 = receivedIP.split(",")[0];
					String packetReq = "| f2-ab-17-8b-93-b3 | a5-c9-e0-9b-53-fe | " + IPV4 + " | " + name + " | 80 | "
							+ Integer.toString(socket.getLocalPort()) + " | " + cmd;
					System.out.println(packetReq);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
