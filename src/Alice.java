/*
Name: Eric Zhu	
Student number: A0180486X
Is this a group submission (no)?

If it is a group submission:
Name of 2nd group member: THE_OTHER_NAME_HERE_PLEASE
Student number of 2nd group member: THE_OTHER_NO

 */

// Please DO NOT copy from the Internet (or anywhere else)
// Instead, if you see nice code somewhere try to understand it.
//
// After understanding the code, put it away, do not look at it,
// and write your own code.
// Subsequent exercises will build on the knowledge that
// you gain during this exercise. Possibly also the exam.
//
// We will check for plagiarism. Please be extra careful and
// do not share solutions with your friends.
//
// Good practices include
// (1) Discussion of general approaches to solve the problem
//     excluding detailed design discussions and code reviews.
// (2) Hints about which classes to use
// (3) High level UML diagrams
//
// Bad practices include (but are not limited to)
// (1) Passing your solution to your friends
// (2) Uploading your solution to the Internet including
//     public repositories
// (3) Passing almost complete skeleton codes to your friends
// (4) Coding the solution for your friend
// (5) Sharing the screen with a friend during coding
// (6) Sharing notes
//
// If you want to solve this assignment in a group,
// you are free to do so, but declare it as group work above.

import java.net.*;
import java.nio.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.util.zip.CRC32;
import java.io.*;

class Alice {
	private int seqNum = 0;
	

	private String _fileToSend;
	private int _port;
	String _fileNameAtBob;
	private DatagramPacket _previousPacket;

	public static void main(String[] args) throws Exception {
		// Do not modify this method
		if (args.length != 3) {
			System.out.println("Usage: java Alice <path/filename> <unreliNetPort> <rcvFileName>");
			System.exit(1);
		}
		new Alice(args[0], Integer.parseInt(args[1]), args[2]);
	}

	public Alice(String fileToSend, int port, String filenameAtBob) throws Exception {
		// Implement me
		_fileToSend = fileToSend;
		_port = port;
		_fileNameAtBob = filenameAtBob;

		DatagramSocket aliceSocket = new DatagramSocket();
		aliceSocket.setSoTimeout(100);

		File file = new File(_fileToSend);

		byte[] b = new byte[1024 - (_fileNameAtBob.getBytes().length + 20)];

		FileInputStream fis = new FileInputStream(file);

		BufferedInputStream bis = new BufferedInputStream(fis);
		//bis.mark(1024 - (_fileNameAtBob.getBytes().length + 20));

		while ( bis.read(b) != -1) {

			int dataLength = b.length;
			
			if(bis.available() == 0){
				dataLength = (int)(file.length() - (b.length*seqNum));
				
				System.out.println(dataLength);
				System.out.println(seqNum);
				System.out.println(file.length());
				
				System.out.println("Last Packet!");
		
			}
			
			
			CRC32 crc = new CRC32();
			crc.update(b);

			Packet packet = new Packet(b, seqNum, crc.getValue(), _fileNameAtBob, dataLength);
			
			
			DatagramPacket sendPkt = new DatagramPacket(packet.getPacketArray(), packet.getPacketArray().length,
					InetAddress.getLocalHost(), _port);

			aliceSocket.send(sendPkt);
			
	
//			System.out.println(packet._seqNum);
//			System.out.println(packet._checkSum);
//			System.out.println(packet._nameLength);
//			System.out.println(b);	
//			System.out.println(packet._data.length);
//			System.out.println("Break");
			
//			System.out.println(packet1._seqNum);
//			System.out.println(packet1._checkSum);
//			System.out.println(packet1._nameLength);
//			System.out.println(packet1._data);	
//			System.out.println(packet1._data.length);

			// ib = inputBuffer
			byte[] ib = new byte[10];
			
			DatagramPacket p = new DatagramPacket(ib, ib.length);
			
	
			boolean ackNotRecieved = true;
			
			while(ackNotRecieved){
			try{
				aliceSocket.receive(p);
				ackNotRecieved = false;
			} catch (SocketTimeoutException e){
				System.out.println("No ACK Recieved");
				aliceSocket.send(sendPkt);
			}
			}
		

			
			//System.out.println(new String(p.getData()));

			String ackResponse = new String(p.getData()).trim();
			
			String yes = "Yes";
			String nak = "nak";

			boolean over = false;
			while (!ackResponse.equals(yes)) {
			
				System.out.println("ack not Yes:" + ackResponse);
			
			if(ackResponse.equals(nak)){
					try{
					System.out.println("Ack asked for PP");
					aliceSocket.send(_previousPacket);
				aliceSocket.receive(p);
					ackResponse = new String(p.getData()).trim();
					over = true;
					}catch (SocketTimeoutException ex){
						aliceSocket.send(_previousPacket);
					}
					
				}
				
				else{
				
				try {
					System.out.println(ackResponse);
					System.out.println("Stuck?");
					aliceSocket.send(sendPkt);
					aliceSocket.receive(p);
					ackResponse = new String(p.getData()).trim();
				} catch (SocketTimeoutException e) {
					System.out.println("ACK not Yes");
					aliceSocket.send(sendPkt);
			
				}

				}
		}
			
			//System.out.println(seqNum);
			
			
			
			
			if(over){
				aliceSocket.send(sendPkt);
				
				ackNotRecieved = true;
				
				while(ackNotRecieved){
				try{
					aliceSocket.receive(p);
					ackNotRecieved = false;
				} catch (SocketTimeoutException e){
					System.out.println("No ACK Recieved");
					aliceSocket.send(sendPkt);
				}
				}
				
				ackResponse = new String(p.getData()).trim();
				
				while (!ackResponse.equals(yes)) {
					
					System.out.println("ack not Yes:" + ackResponse);
	
					
					try {
						System.out.println(ackResponse);
						System.out.println("Stuck?");
						aliceSocket.send(sendPkt);
						aliceSocket.receive(p);
						ackResponse = new String(p.getData()).trim();
					} catch (SocketTimeoutException e) {
						System.out.println("ACK not Yes");
						aliceSocket.send(sendPkt);
					}
			}
			
			}
			
			_previousPacket = sendPkt;
			
			if(ackResponse.equals(yes)){
			seqNum++;
			}
			ackResponse = null;
			p = null;
		}
		
		System.exit(0);

	}

	}
	
