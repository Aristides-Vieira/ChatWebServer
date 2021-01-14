package clients;

public class ClientMain {

    public static void main(String[] args) {

        Client aris = new Client();

        aris.init();
        Thread thread = new Thread(aris);
        thread.start();
        aris.listen();
    }

}
