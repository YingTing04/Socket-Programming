package a2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

public class XDNS {
	// hashmap of all mappings
	static HashMap<String, String> HMOfAllIPMapping = new HashMap<String, String>();

	// populate hashmap with mappings
	public static void setUpMapping() {
		HMOfAllIPMapping.put("www.sdxcentral.com", "104.20.242.119,2606:4700:10::6814:f277");
		HMOfAllIPMapping.put("www.lightreading.com", "104.25.195.108,2606:4700:20::6819:c46c");
		HMOfAllIPMapping.put("www.linuxfoundation.org", "23.185.0.2,2620:12a:8000::2");
		HMOfAllIPMapping.put("www.cncf.io", "23.185.0.3,2620:12a:8000::3");
	}

	public static void main(String[] args) throws IOException {
		setUpMapping();

		DatagramSocket DNSSocket = new DatagramSocket(9090);
		System.out.println("Starting DNS Server");

		while (true) {
			try {
				// receive packet from client
				byte[] buf = new byte[256];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				DNSSocket.receive(packet);

				// process input
				String received = new String(packet.getData(), 0, packet.getLength());
				String domain = received.split(",")[1];

				// retrieve IP address
				String response = HMOfAllIPMapping.get(domain);

				// send packet to client
				buf = response.getBytes();
				InetAddress address = packet.getAddress();
				int port = packet.getPort();
				packet = new DatagramPacket(buf, buf.length, address, port);
				System.out.println("Sending packet");
				DNSSocket.send(packet);

			} catch (Exception e) {
				DNSSocket.close();
				e.printStackTrace();
				break;
			}
		}

	}

}
