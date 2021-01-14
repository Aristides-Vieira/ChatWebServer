package server;

import org.academiadecodigo.bootcamp.Prompt;
import org.academiadecodigo.bootcamp.scanners.menu.MenuInputScanner;
import org.academiadecodigo.bootcamp.scanners.string.PasswordInputScanner;
import org.academiadecodigo.bootcamp.scanners.string.StringInputScanner;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {


    private String userName;
    private String password;

    private final String ip = "127.0.0.1";
    private final int serverPort = 46667;
    private PrintWriter out;
    private BufferedReader in;
    private final Socket clientSocket;
    private final Server server;
    private MenuInputScanner menuInputScanner;
    private final String[] menuOptions = {"log In", "Register", "Exit"};
    private Prompt promptOut;
    private PrintStream output;
    private StringInputScanner register;
    private Scanner scanner;

    public ClientHandler(Socket clientSocket, Server server) {
        this.clientSocket = clientSocket;
        this.server = server;

    }


    public void init() {
        try {
            server.clientXRunning(this);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            output = new PrintStream(clientSocket.getOutputStream(), true);
            promptOut = new Prompt(clientSocket.getInputStream(), output);

            menuInputScanner = new MenuInputScanner(menuOptions);
            menuInputScanner.setMessage("Already a member? -- Type: exit to menu to go back" );
            int answerMenu = promptOut.getUserInput(menuInputScanner);
            System.out.println("asd");


            if (answerMenu - 1 == 2) {
                out.println("Farewell");
                clientSocket.close();
                in.close();
                out.close();

            } else if (answerMenu - 1 == 1) {
                StringInputScanner setUsername = new StringInputScanner();
                setUsername.setMessage("Choose Username: ");
                userName = promptOut.getUserInput(setUsername);
                if (userName.equals("exit to menu")) {
                    init();
                }

                while (checkUsername(userName)) {
                    out.println("Username already taken.");
                    userName = promptOut.getUserInput(setUsername);
                    if (userName.equals("exit to menu")) {
                        init();
                    }
                }

                PasswordInputScanner setPassword = new PasswordInputScanner();
                setPassword.setMessage("Choose your password (UTF-8 only): ");
                password = promptOut.getUserInput(setPassword);
                if (password.equals("exit to menu")) {
                    init();
                }
                connectClient();
            } else {

                StringInputScanner confirmUsername = new StringInputScanner();
                confirmUsername.setMessage("username: ");
                userName = promptOut.getUserInput(confirmUsername);
                if (userName.equals("exit to menu")) {
                    init();
                }

                while (!checkUsername(userName)) {
                    out.println("Username not found, have you registered yet? ");
                    confirmUsername.setMessage("username: ");
                    userName = promptOut.getUserInput(confirmUsername);
                    if (userName.equals("exit to menu")) {
                        init();
                    }
                }

                PasswordInputScanner confirmPassword = new PasswordInputScanner();
                confirmPassword.setMessage("Password: ");
                password = promptOut.getUserInput(confirmPassword);
                if (password.equals("exit to menu")) {
                    init();
                }

                while (checkPassword(password, userName)) {
                    out.println("Wrong password try again ");
                    password = promptOut.getUserInput(confirmPassword);
                    if (password.equals("exit to menu")) {
                        init();
                    }
                }
            connectClient();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connectClient () {
        server.addClientXtoArray(this);
        server.clientXRunning(this);
    }


    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public boolean checkUsername(String userName) {
        return server.checkUsername(userName);
    }


    public boolean checkPassword(String password, String userName) {
        return server.checkPassword(password, userName);
    }


    public void recieveBroadcast(String messageRecieved) {
        out.println(messageRecieved);
    }


    public void sendBroadcastToAll(String messageToBeBroadcast) {
        String nameMessage = userName.concat(": " + messageToBeBroadcast);
        server.broadcast(nameMessage);
    }

    public void sendBroadcast(String messageToBeBroadcast) {
        String nameMessage = userName.concat(": " + messageToBeBroadcast);
        server.broadcast(nameMessage, this);
    }


    @Override
    public void run() {
        try {
            String nameHasConnected = userName.concat(" has joined!");
            sendBroadcastToAll(nameHasConnected);

            while (!clientSocket.isClosed()) {

                String incomingMessage = in.readLine();
                System.out.println("Message Received " + incomingMessage);

                if (incomingMessage.equals(userName.concat(" out"))) {
                    sendBroadcastToAll(userName.concat(" has left the server!"));
                    out.close();
                    in.close();
                    clientSocket.close();

                } else if (incomingMessage.equals("") || incomingMessage.equals("\n") || incomingMessage.equals(" ")) {
                    out.println("Blank messages will not be sent!");

                } else {
                    sendBroadcast(incomingMessage);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
