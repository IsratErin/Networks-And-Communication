package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    
    private static final int BUFFER_SIZE = 1024;
	
	 public TCPClient() {
	    }

	    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {
	    	Socket socket = new Socket(hostname, port);
	    	
	        socket.getOutputStream().write(toServerBytes, 0 , toServerBytes.length);
	        
	        byte[] fixedBuffer = new byte[BUFFER_SIZE];
	        ByteArrayOutputStream baus = new ByteArrayOutputStream();
	        int nOfBytesRead;
	        while((nOfBytesRead = socket.getInputStream().read(fixedBuffer,0,fixedBuffer.length)) != -1) { 
	    	baus.write(fixedBuffer, 0,nOfBytesRead);
	        }
	        baus.flush();
	        socket.close();     
	        return baus.toByteArray();
	    }
}
