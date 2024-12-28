import java.net.*;
import java.io.*;
import tcpclient.TCPClient;
import java.nio.charset.StandardCharsets;
public class MyRunnable implements Runnable {
	String h200 = "HTTP/1.1 200 OK\r\n\r\n";
    String h400 = "HTTP/1.1 400 Bad Request\r\n";
    String h404 = "HTTP/1.1 404 Not Found\r\n";
    String h500 = "HTTP/1.1 500 Internal Server Error\r\n";
    
    
    
    private Socket socket;
    private StringBuilder response = new StringBuilder();

    private String hostname = "";
    private Integer port = null;
    private byte[] bytesToServer = new byte[0];
    private Integer timeout = null;
    private Integer limit = null;
    private boolean shutdown = false;
   
    public MyRunnable(Socket socket) {
        this.socket = socket;        
    }

    public void run() {
    	
    	
    	
        try {
        	
        	byte[] b = new byte[1024];
            int bytesRead = this.socket.getInputStream().read(b);
            String decoded_request = new String(b, 0, bytesRead, StandardCharsets.UTF_8);

           
            if (!decoded_request.startsWith("GET") || !decoded_request.contains("HTTP/1.1")) {   
                this.response.append(h400);
                throw new Exception("Bad Request");
            }
            String url = decoded_request.split("\\s+")[1];
            String[] parameters = url.split("\\?");

            if (parameters.length > 0 && parameters[0].equals("/ask")) {
                if (parameters.length < 2) {
                    this.response.append(h400);
                    throw new Exception("Bad Request");
                }

             String[] paraParts = parameters[1].split("&");
             for (String part : paraParts) {
                 String[] partValues = part.split("=");

                
                 if(partValues.length <= 1) {
                     this.response.append(h400);
                     throw new Exception("Bad Request");
                 }

                 switch (partValues[0]) {
                     case "shutdown":
                         shutdown = Boolean.parseBoolean(partValues[1]);
                         break;
                     case "timeout":
                         timeout = Integer.parseInt(partValues[1]);
                         break;
                     case "limit":
                         limit = Integer.parseInt(partValues[1]);
                         break;
                     case "hostname":
                         hostname = partValues[1];
                         break;
                     case "port":
                         port = Integer.parseInt(partValues[1]);
                         break;
                     case "string":
                         bytesToServer = partValues[1].getBytes();
                         break;
                     default:
                         this.response.append(h400);
                         throw new Exception("Bad Request");
                 }
             }
             if (hostname.isEmpty() || port == 0) {
                 this.response.append(h400);
                 throw new Exception("Bad Request");
             }
             try {
                 this.response.append(h200);

                 TCPClient client = new TCPClient(shutdown, timeout, limit);
                 this.response.append(new String(client.askServer(hostname, port, bytesToServer)));

             } catch (Exception e) {
                
             	 this.response.append(h404);
             	 throw new Exception("Not Found");
             }
            } else {
                this.response.append(h404);
                throw new Exception("Not Found");
            }
            close_the_Socket();
        } catch (Exception e) {
            e.printStackTrace();
            close_the_Socket();  
        }
                
                
           
    }

  


    private void close_the_Socket() {
        try {
        	
        	this.socket.getOutputStream().write(this.response.toString().getBytes());
            this.socket.close();
        } catch (IOException e) {
            // e.printStackTrace();
        }
    }
}