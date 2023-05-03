package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {

   private static int BUFFER_SIZE = 1024;
	 private boolean shutdown = false;
	 private Integer timeout = null;
	private Integer limit = null;
	
	 public TCPClient() {
	    }
	 public TCPClient(boolean shutdown, Integer timeout, Integer limit) {
		 this.shutdown = shutdown;
		 this.timeout  = timeout;
		 this.limit  = limit;
	 }
	    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {
	    	Socket socket = new Socket(hostname, port);
	    	
	        socket.getOutputStream().write(toServerBytes, 0 , toServerBytes.length);
	        if(shutdown) {
	        	socket.shutdownOutput();
	        }
	       
	        byte[] fixedBuffer = new byte[limit != null && limit < BUFFER_SIZE ? limit : BUFFER_SIZE];
	        ByteArrayOutputStream baus = new ByteArrayOutputStream();
	        
	        try {
	        	
	            if (timeout != null) {
	                socket.setSoTimeout(timeout);
	            }
	        int nOfBytesRead;
	        int countBytes= 0;
		        
	        
	        while((nOfBytesRead = socket.getInputStream().read(fixedBuffer,0,fixedBuffer.length)) != -1) { 
	    	baus.write(fixedBuffer, 0,nOfBytesRead);
	    	countBytes+= nOfBytesRead;
	    	 if(limit!= null && countBytes>= limit) {
	    		break;
	    	 }
	    	 
	         if (limit != null && countBytes + BUFFER_SIZE > limit) {
	              fixedBuffer = new byte[limit - countBytes];
	         }
	        }
	        
	        } catch (SocketTimeoutException e) {
	            System.out.println(" Time out ");
	        } 
	        socket.close();
	             
	        return baus.toByteArray();
	    }
    
    
}
