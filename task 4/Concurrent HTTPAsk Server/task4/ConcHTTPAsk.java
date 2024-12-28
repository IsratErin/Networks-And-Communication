import java.net.*;
import java.io.*;


public class ConcHTTPAsk {
    public static void main( String[] args) {
        try {
            int port = Integer.parseInt(args[0]);
            ServerSocket serverSocket = new ServerSocket(port);

            while (true) {
                Socket socket = serverSocket.accept();
                Runnable t = new MyRunnable(socket);
                new Thread(t).start();

            }

        } catch (ArrayIndexOutOfBoundsException e) {
            System.exit(1);

        } catch (IOException e) {
            System.exit(1);
        }
    }
//zip task4.zip task4/tcpclient/TCPClient.java task4/MyRunnable.java task4/ConcHTTPAsk.java
   
}