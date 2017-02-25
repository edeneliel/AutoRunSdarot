package eden.eliel.Listeners;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Eden on 2/25/2017.
 */
public class TCPListener extends Thread implements Runnable, Listener{
    private ServerSocket welcomeSocket;
    private Socket connectionSocket;
    private String str;

    public TCPListener(int port){
        try {
            welcomeSocket = new ServerSocket(port);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public double onRequest(String str) {
        System.out.println("Got :"+ str);
        return 0;
    }

    @Override
    public void run() {
        while (true){
            try {
                connectionSocket = welcomeSocket.accept();
                InputStreamReader inputStreamReader = new InputStreamReader(connectionSocket.getInputStream());
                BufferedReader bufferedInputStream = new BufferedReader(inputStreamReader);
                str = bufferedInputStream.readLine();
                if (str != null) {
                    double a = onRequest(str);
                    PrintWriter out = new PrintWriter(connectionSocket.getOutputStream(), true);
                    out.println(a);
                    out.close();

                }
                bufferedInputStream.close();
                inputStreamReader.close();
                connectionSocket.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
