# Socket-Programming

CISC 435 Computer Communications &amp; Networks assignments completed at Queen's University in Fall 2019

## A1
- Implements a client-server chatting application using TCP
- Server echoes message entered by client
- Server can handle at most 3 clients
- Server maintains a cache of accepted clients
- Client can send an `exit` message to terminate the connection

## A2
- Implements a DHCP server and a DNS server which can handle multiple clients using UDP
- Client first connects to the DHCP server, which assigns an IP configuration to him
- DHCP server can handle at most 4 clients
- After which, client can request for an IP address from the DNS server for one of the following domains

| Domain Name | IP Address (V4) |
| ----------- | --------------- |
| www.sdxcentral.com | 104.20.242.119 |
| www.lightreading.com | 104.25.195.108 |
| www.linuxfoundation.org | 23.185.0.2 |
| www.cncf.io | 23.185.0.3 |

- Each client has a lease time of 60 seconds with the DHCP server
- Client can renew his lease time to the DHCP server by sending a `renew` message
- Client can release his IP configuration from the DHCP server by sending a `release` message
