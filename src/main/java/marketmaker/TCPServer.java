package marketmaker;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {

    private PricingService pricingService;

    public TCPServer(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    static final int PORT = 3000;

    public void start() {
        ServerSocket serverSocket = getServerSocket();

        do {
            InputOutputAdapter inputOutputAdapter = new InputOutputAdapter(serverSocket).invoke();
            DataOutputStream dataOutputStream = inputOutputAdapter.getDataOutputStream();
            BufferedReader bufferedReaderStream = inputOutputAdapter.getBufferedReaderStream();
            new RequestProcessorImpl(bufferedReaderStream, dataOutputStream, this.pricingService).run();

        } while (true);
    }

    private ServerSocket getServerSocket() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverSocket;
    }

    private class InputOutputAdapter {
        private ServerSocket serverSocket;
        private DataOutputStream dataOutputStream;
        private BufferedReader bufferedReaderStream;

        public InputOutputAdapter(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }

        public DataOutputStream getDataOutputStream() {
            return dataOutputStream;
        }

        public BufferedReader getBufferedReaderStream() {
            return bufferedReaderStream;
        }

        public InputOutputAdapter invoke() {
            Socket socket = null;
            dataOutputStream = null;
            bufferedReaderStream = null;
            try {
                socket = serverSocket.accept();
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                InputStream inputStream = socket.getInputStream();
                bufferedReaderStream = new BufferedReader(new InputStreamReader(inputStream));
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                    dataOutputStream.close();
                    bufferedReaderStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return this;
        }
    }
}
