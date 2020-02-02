package com.example.marketmaker;

import com.example.marketmaker.common.IRequest;
import com.example.marketmaker.common.IRequestParser;
import com.example.marketmaker.common.RequestParser;

import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.DataOutputStream;

public class RequestProcessor implements IRequestProcessor {

    private IPricingService pricingService;
    private DataOutputStream dataOutputStream;
    private BufferedReader bufferedReaderStream;
    private IRequestParser requestParser;
    private boolean isTerminated = false;

    public RequestProcessor(BufferedReader readStream, DataOutputStream writeStream, IPricingService pricingService) {
        this.pricingService = pricingService;
        this.dataOutputStream = writeStream;
        this.bufferedReaderStream = readStream;
        this.requestParser = new RequestParser();
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
                    IRequest request = requestParser.parseFromString(instructionString);

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
