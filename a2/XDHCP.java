package a2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class XDHCP {
	static int count = 0;
	static int count2 = 0;

	// hashmap of IPConfig
	static HashMap<String, IPConfig> IPConfigHM = new HashMap<String, IPConfig>();
	// hashmap of Timer threads
	static HashMap<String, TimerDHCP> TimerHM = new HashMap<String, TimerDHCP>();
	// available IP addresses
	static ArrayList<String> availableIP = new ArrayList<String>(
			Arrays.asList("192.168.1.3/24", "192.168.1.4/24", "192.168.1.5/24", "192.168.1.6/24"));

	public static void main(String[] args) throws IOException {
		DatagramSocket DHCPSocket = new DatagramSocket(7070);
		DHCPSocket.setBroadcast(true);
		System.out.println("Starting DHCP Server");

		while (true) {
			try {
				// receiving packet from client
				byte[] buf = new byte[256];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				DHCPSocket.receive(packet);
				String received = new String(packet.getData(), 0, packet.getLength());
				int port = packet.getPort();

				// processing packet
				String ip = "";
				String cmd = "";
				if (received.contains(",")) {
					ip = received.split(",")[0];
					cmd = received.split(",")[1];
				}

				if (count < 4) {
					if (cmd.equalsIgnoreCase("renew")) {
						// print lease time for debugging
						// System.out.println(IPConfigHM.get(ip).getLeaseTime());

						// renew timer thread
						TimerHM.get(ip).renew();
						TimerDHCP t = new TimerDHCP(ip);
						TimerHM.put(ip, t);

						// send message to client
						buf = "Connection renewed!".getBytes();
						InetAddress address = packet.getAddress();
						packet = new DatagramPacket(buf, buf.length, address, port);
						DHCPSocket.send(packet);

						// print available IP for debugging
						// for (int i = 0; i < availableIP.size(); i++)
						// System.out.println(availableIP.get(i));

						// print lease time for debugging
						// System.out.println(IPConfigHM.get(ip).getLeaseTime());

					} else if (cmd.equalsIgnoreCase("release")) {
						// release IP address
						TimerHM.get(ip).release();

						// print available IP for debugging
						// for (int i = 0; i < availableIP.size(); i++)
						// System.out.println(availableIP.get(i));

					} else {
						count++;
						count2++;

						// assigning IP to client
						System.out.println("Client " + count2 + " is connected");
						System.out.println("Assigning IP Config for Client " + count2);
						IPConfig clientData = new IPConfig(XDHCP.availableIP.get(0));
						System.out.println("Client " + count2 + " has IP " + clientData.getIPAddress());
						// save IPConfig to hashmap
						IPConfigHM.put(clientData.getIPAddress(), clientData);
						// remove available IP
						availableIP.remove(0);

						// print available IP for debugging
						// for (int i = 0; i < availableIP.size(); i++)
						// System.out.println(availableIP.get(i));

						// start timer thread
						TimerDHCP t = new TimerDHCP(clientData.getIPAddress());
						TimerHM.put(clientData.getIPAddress(), t);

						// format: IP, GW, DNS port number, lease time
						String str_IPConfig = clientData.getIPAddress() + "," + clientData.getGatewayIP() + ","
								+ clientData.getDNSIP() + "," + clientData.getLeaseTime();
						buf = str_IPConfig.getBytes();

						// send IPConfig to client
						InetAddress address = InetAddress.getByName("255.255.255.255");
						packet = new DatagramPacket(buf, buf.length, address, port);
						DHCPSocket.send(packet);
					}

				} else {
					System.out.println("Server full!");
				}

			} catch (Exception e) {
				DHCPSocket.close();
				e.printStackTrace();
				break;
			}
		}

	}

}

class TimerDHCP {
	Timer timer;
	String ip;

	// creating Timer thread
	public TimerDHCP(String ip) {
		timer = new Timer();
		this.ip = ip;
		timer.scheduleAtFixedRate(new MyTimerTask(ip), 0, 1000);
	}

	class MyTimerTask extends TimerTask {
		private String ip;
		int i = 60;

		public MyTimerTask(String info) {
			this.ip = info;
		}

		@Override
		// countdown timer
		public void run() {
			i--;
			XDHCP.IPConfigHM.get(ip).setLeaseTime(i);
			// timeout has been reached
			if (i <= 0) {
				System.out.println("Timeout for client with ip " + ip);
				XDHCP.availableIP.add(XDHCP.IPConfigHM.get(ip).getIPAddress());
				XDHCP.IPConfigHM.remove(ip);
				XDHCP.TimerHM.remove(ip);
				System.out.println("IP released!");
				XDHCP.count--;
				timer.cancel();
			}
		}
	}

	// renew connection
	public void renew() {
		System.out.println("Connection renewed for client with ip " + ip);
		timer.cancel();
	}

	// release connection
	public void release() {
		System.out.println("Connection released for client with ip " + ip);
		XDHCP.availableIP.add(XDHCP.IPConfigHM.get(ip).getIPAddress());
		XDHCP.IPConfigHM.remove(ip);
		XDHCP.TimerHM.remove(ip);
		System.out.println("IP released!");
		XDHCP.count--;
		timer.cancel();
	}
}

// IPConfig class
class IPConfig {
	private String IPAddress;
	private static String gatewayIP = "192.168.1.1";
	private static String DNSIP = "9090";
	private long leaseTime = 60;

	public IPConfig(String IPAddress) {
		this.IPAddress = IPAddress;
	}

	public String getIPAddress() {
		return this.IPAddress;
	}

	public void setIPAddress(String s) {
		this.IPAddress = s;
	}

	public String getGatewayIP() {
		return this.gatewayIP;
	}

	public String getDNSIP() {
		return this.DNSIP;
	}

	public long getLeaseTime() {
		return this.leaseTime;
	}

	public void setLeaseTime(long l) {
		this.leaseTime = l;
	}

	public void resetLeaseTime() {
		this.leaseTime = 60;
	}
}
