package marketmaker;

import marketmaker.common.Request;
import marketmaker.common.RequestParser;
import marketmaker.common.RequestParserImpl;

import java.io.BufferedReader;
import java.io.DataOutputStream;

public class RequestProcessorImpl implements RequestProcessor {

    private PricingService pricingService;
    private DataOutputStream dataOutputStream;
    private BufferedReader bufferedReaderStream;
    private RequestParser requestParser;
    private boolean isTerminated = false;

    public RequestProcessorImpl(BufferedReader readStream, DataOutputStream writeStream, PricingService pricingService) {
        this.pricingService = pricingService;
        this.dataOutputStream = writeStream;
        this.bufferedReaderStream = readStream;
        this.requestParser = new RequestParserImpl();
    }

    public void stop() {
        this.isTerminated = true;
    }

    public void run() {
        System.out.println("Client Request Processor started for client");

        while (!this.isTerminated) {
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
                    System.out.println("Quote: " + quote.toString());
                    dataOutputStream.writeDouble(quote);
                } catch (Exception e) {
                    e.printStackTrace();
                    dataOutputStream.writeBytes(e.getMessage());
                } finally {
                    dataOutputStream.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                stop();
            }
        }
    }

}
