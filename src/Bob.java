/*
Name: Eric Zhu
Student number: A0180486X
Is this a group submission (no)?

If it is a group submission:
Name of 2nd group member: THE_OTHER_NAME_HERE_PLEASE
Student number of 2nd group member: THE_OTHER_NO

*/


import java.net.*;
import java.nio.*;
import java.util.Arrays;
import java.util.zip.CRC32;
import java.io.*;
 

class Bob {
    private int seqNum = 0;
    private DatagramSocket socket;

    public static void main(String[] args) throws Exception {
        // Do not modify this method
        if (args.length != 1) {
            System.out.println("Usage: java Bob <port>");
            System.exit(1);
        }
        new Bob(Integer.parseInt(args[0]));
    }

    public Bob(int port) throws Exception {
        // Implement me
    	socket = new DatagramSocket(port);
    	
    	int i = 0;
    	FileOutputStream fos = null;
    	BufferedOutputStream bos = null;
    	
    	while(true){
    		byte[] inBuffer = new byte[1024];
    		DatagramPacket p = new DatagramPacket(inBuffer, inBuffer.length);
    
    		socket.receive(p);
    	
    		Packet packet = null;
    		try {
    			 packet = new Packet(p.getData());
    		} catch(BufferUnderflowException e){
    			continue;
    		}
    			
    		
    		CRC32 crc = new CRC32();
    		crc.update(packet._data);
    		
    		byte[] outBuffer = "Yes".getBytes();
			DatagramPacket ack = new DatagramPacket(outBuffer, outBuffer.length, p.getAddress(),p.getPort());
    		
			
			System.out.println(packet._seqNum);
			
			
			if(packet._seqNum < seqNum || packet._seqNum == seqNum +1 ){
				if(packet._seqNum == seqNum +1 ){
					byte[] previousBuffer = "nak".getBytes();
					
					DatagramPacket previousRqst = new DatagramPacket(
					previousBuffer, previousBuffer.length,p.getAddress(),p.getPort());
					
					System.out.println("Loop Caught");
					System.out.println(packet._seqNum);
					System.out.println(seqNum);
					System.out.println(new String (previousRqst.getData()).trim());
					socket.send(previousRqst);
				}
				
				
				if(packet._seqNum < seqNum){
				socket.send(ack);
				System.out.println(packet._seqNum);
				System.out.println("Repeat ACK");
				}
			}
			
    		
		//	System.out.println(packet._checkSum);
		//	System.out.println(crc.getValue());
			
			if(seqNum == packet._seqNum && packet._checkSum == crc.getValue()){
    			System.out.println("Recieved Packet" + seqNum);
    			seqNum++;
    			
    		if(i == 0){
    				fos = new FileOutputStream(packet._writeName);
    				bos = new BufferedOutputStream(fos);
    				i++;
    		}
    			
    		int u = packet._dataLength;
    		  
  		    byte[] data = packet._data;
  		    if(u != 1024 - (packet._writeName.getBytes().length + 20)){
  		    	data = Arrays.copyOf(packet._data, u);
  		    	
  		    }
    		     		      		
    			bos.write(data);
    			System.out.println(data.length);
    			bos.flush();
    			
    			socket.send(ack);
    			System.out.println("sent intial ack");
    		}
    		
    		
    	}
    	
    }
}
    
    