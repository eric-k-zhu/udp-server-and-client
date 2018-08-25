/*
Name:Eric Zhu
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

// This class is not absolutely necessary as you can mash everything
// directly into Alice and Bob classes. However, it might be nicer
// to have this class, which makes your code more organized and readable.
// Furthermore, it makes the assignment easier, as you might be able to
// reuse code

class Packet {
    // Implement me
	byte[] _packet;
	int _seqNum;
	Long _checkSum;
	int _nameLength;
	int _dataLength;

	String _writeName;
	byte[] _data;
	
	
	public Packet(byte[] data, int seqNum, Long checksum, String writeName, int dataLength){
		
		_data = data;
		_seqNum = seqNum;
		_checkSum = checksum;
		_writeName = writeName;
		_nameLength = writeName.length();
		_dataLength = dataLength;
		
		
		
		byte[] header = createHeader(seqNum,checksum,writeName);
		_packet = concatenate(header,data);
		
	}
	
	public Packet(byte[] packet){
		_packet = packet;
		parseByteArray(packet);
	}
	
	
	public byte[] createHeader(int seqNum, Long checksum, String writeName){
		ByteBuffer buf = ByteBuffer.allocate(20);
		buf.putInt(seqNum);
		buf.putLong(checksum);
		
		byte[] newName = writeName.getBytes();
		buf.putInt(newName.length);
		buf.putInt(_dataLength);
	
		byte[] array = buf.array();
		
		
		
		
		byte[] header = concatenate(array, newName);
		
		
		
		return header;
	}
	
	private byte[] concatenate(byte[] buffer1, byte[] buffer2) {
        byte[] returnBuffer = new byte[buffer1.length + buffer2.length];
        System.arraycopy(buffer1, 0, returnBuffer, 0, buffer1.length);
        System.arraycopy(buffer2, 0, returnBuffer, buffer1.length, buffer2.length);
        return returnBuffer;
    }
	
	public byte[] getPacketArray(){
		return _packet;
	}
	
	public void parseByteArray(byte[] packet){
		ByteBuffer bb = ByteBuffer.wrap(packet);
		 _seqNum = bb.getInt();
		 _checkSum = bb.getLong();
		 _nameLength = bb.getInt();
		 _dataLength = bb.getInt();
		 
		 byte[] writeNameArray = new byte[_nameLength];
		 
		 bb.get(writeNameArray);

		  
		 _writeName = new String(writeNameArray);
		 
		 byte[] data = new byte[packet.length - (_nameLength + 20)];
		
		 bb.get(data);
		 
		_data = data;
		
		 
		
	}
}