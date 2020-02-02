package marketmaker;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer implements Runnable {

    private PricingService pricingService;
    private boolean isAlive = true;
    static final int PORT = 3000;

    public TCPServer(PricingService pricingService) {
        this.pricingService = pricingService;
    }


    public void run() {
        isAlive = true;
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        do {
            Socket socket = null;
            DataOutputStream dataOutputStream = null;
            BufferedReader bufferedReaderStream = null;
            try {
                socket = serverSocket.accept();
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                InputStream inputStream = socket.getInputStream();
                bufferedReaderStream = new BufferedReader(new InputStreamReader(inputStream));
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            } catch (Exception e) {
                e.printStackTrace();
            }

            new RequestProcessorImpl(bufferedReaderStream, dataOutputStream, this.pricingService).run();

        } while (isAlive);
    }

    public void stop() {
        isAlive = false;
    }


}
