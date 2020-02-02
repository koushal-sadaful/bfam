package harness;// A simple Client Server Protocol .. Client for Echo Server

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class TestClient {
    private int port;
    private BufferedReader is = null;
    private PrintWriter os = null;
    private Socket s1 = null;

    public TestClient(int port) {
        this.port = port;
    }

    public void start() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            try {
                s1 = new Socket(address, port);
                is = new BufferedReader(new InputStreamReader(s1.getInputStream()));
                os = new PrintWriter(s1.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
                System.err.print("[Test Client] IO Exception");
            }

            System.out.println("[Test Client] Client Address : " + address);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String executeInstruction(String instruction) {
        String response = null;
        try {
            os.println(instruction);
            os.flush();
            response = is.readLine();
            System.out.println("Server Response : " + response);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[Test Client] Socket read Error");
        }
        return response;
    }

    public void stop() {
        try {
            is.close();
            os.close();
            s1.close();
            System.out.println("[Test Client] Connection Closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}