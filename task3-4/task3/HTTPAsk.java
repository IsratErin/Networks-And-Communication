import java.net.*;
import java.io.*;
import tcpclient.TCPClient;
import java.nio.charset.StandardCharsets;

public class HTTPAsk {
    public static void main( String[] args) {
        try {
            int port = Integer.parseInt(args[0]);
            ServerSocket serverSocket = new ServerSocket(port);

            while (true) {
                Socket socket = serverSocket.accept();
                handleRequest(socket);
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            System.exit(1);

        } catch (IOException e) {
            System.exit(1);
        }
    }

    private static void handleRequest(Socket socket) {
    	String h200 = "HTTP/1.1 200 OK\r\n\r\n";
        String h400 = "HTTP/1.1 400 Bad Request\r\n";
        String h404 = "HTTP/1.1 404 Not Found\r\n";
        String h500 = "HTTP/1.1 500 Internal Server Error\r\n";
        OutputStream output= null;
        StringBuilder response = null;
        try {
            output = socket.getOutputStream();
            response = new StringBuilder();
            
            byte[] b = new byte[1024];
            int bytesRead = socket.getInputStream().read(b);
            String decoded_request = new String(b, 0, bytesRead, StandardCharsets.UTF_8);
            
            if (!decoded_request.startsWith("GET") || !decoded_request.contains("HTTP/1.1")) {
            
                response.append(h400);
                throw new Exception("Bad Request");
            }

            String url = decoded_request.split("\\s+")[1];
            String[] parameters = url.split("\\?");

            if (parameters.length > 0 && parameters[0].equals("/ask")) {
                if (parameters.length < 2) {
                    response.append(h400);
                    throw new Exception("Bad Request");
                }

                String[] paraParts = parameters[1].split("&");

                String hostname = "";
                //String hostname = null;
                int port = 0;
                byte[] bytesToServer = new byte[0];
                Integer timeout = null;
                Integer limit = null;
                boolean shutdown = false;

                for (String part : paraParts) {
                    String[] partValues = part.split("=");

                    //if(partValues.length < 2) {
                    if(partValues.length <= 1) {
                        response.append(h400);
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
                            response.append(h400);
                            throw new Exception("Bad Request");
                    }
                }

                
                if (hostname.isEmpty() || port == 0) {
                    response.append(h400);
                    throw new Exception("Bad Request");
                }

                try {
                    response.append(h200);

                    TCPClient client = new TCPClient(shutdown, timeout, limit);
                    response.append(new String(client.askServer(hostname, port, bytesToServer)));

                } catch (Exception e) {
                   // response.append(h500);
                    //throw new Exception("Internal Server Error");
                    response.append(h404);
                	 throw new Exception("Not Found");
                }

            } else {
                response.append(h404);
                throw new Exception("Not Found");
            }

            output.write(response.toString().getBytes());
            socket.close();

        } catch (Exception e) {
            try {
               // OutputStream socketOutput = socket.getOutputStream();
                //StringBuilder response = new StringBuilder();
               // response.append(h400);
                //socketOutput.write(response.toString().getBytes());
                output.write(response.toString().getBytes());
                socket.close();
            } catch (Exception ex) {
                // ignore
            }
        }
    
    }
}

