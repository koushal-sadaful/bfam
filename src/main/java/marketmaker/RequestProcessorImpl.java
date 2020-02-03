package marketmaker;

import marketmaker.common.Request;
import marketmaker.common.RequestParser;
import marketmaker.common.RequestParserImpl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;

public class RequestProcessorImpl implements RequestProcessor {

    private PricingService pricingService;
    private DataOutputStream dataOutputStream;
    private BufferedReader bufferedReaderStream;
    private RequestParser requestParser;
    private boolean isAlive = true;

    public RequestProcessorImpl(BufferedReader readStream, DataOutputStream writeStream, PricingService pricingService) {
        this.pricingService = pricingService;
        this.dataOutputStream = writeStream;
        this.bufferedReaderStream = readStream;
        this.requestParser = new RequestParserImpl();
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void stop() {
        this.isAlive = false;
    }

    public void run() {
        this.isAlive = true;
        System.out.println("Client Request Processor started for client");

        while (isAlive()) {
            try {
                String instructionString = bufferedReaderStream.readLine();
                System.out.println("Instruction: " + instructionString);

                try {
                    Request request = requestParser.parseFromString(instructionString);

                    if (request.isDisconnectionRequest()) {
                        stop();
                        return;
                    }

                    Double quote = pricingService.getQuotePrice(request);
                    System.out.println("Calculated Quote: " + quote.toString());
                    writeToOutputStream(quote.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    writeToOutputStream(e.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                stop();
            }
        }
    }

    private void writeToOutputStream(String s) throws IOException {
        dataOutputStream.writeBytes(s + "\n\r");
        dataOutputStream.flush();
    }

}
