package clients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable {


    private BufferedReader in;
    private PrintWriter out;
    private Socket clientSocket;


    public Client() {
    }

    public void init() {
        try {
            String serverIP = "127.0.0.1";
            int serverPort = 46667;
            clientSocket = new Socket(serverIP, serverPort);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen() {
        try {

            while (!clientSocket.isClosed()) {

                String readServerMessage;
                readServerMessage = in.readLine();
                if (readServerMessage == null) {
                    in.close();
                    out.flush();
                    out.close();
                    clientSocket.close();
                }

                System.out.println(readServerMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {

            BufferedReader readFromConsole = new BufferedReader(new InputStreamReader(System.in));
            while (!clientSocket.isClosed()) {
                String consoleInput;
                consoleInput = readFromConsole.readLine();
                out.println(consoleInput);
            }

            in.close();
            readFromConsole.close();
            out.flush();
            out.close();
            clientSocket.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



