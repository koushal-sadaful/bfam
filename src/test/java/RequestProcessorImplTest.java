import marketmaker.PricingServiceImpl;
import marketmaker.RequestProcessorImpl;
import marketmaker.common.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class RequestProcessorImplTest {

    private DataOutputStream dataOutputStreamFake;
    private BufferedReader bufferedReaderMock = mock(BufferedReader.class);
    private PricingServiceImpl pricingServiceImplMock = mock(PricingServiceImpl.class);
    private RequestProcessorImpl requestProcessorImplUnderTest;
    private String testDataFileOutput = "test-result.dat";

    @BeforeEach
    void setUp() {
        try {
            dataOutputStreamFake = new DataOutputStream(new FileOutputStream(testDataFileOutput));
        } catch (FileNotFoundException e) {
            System.out.println("Failed to initialize Output Stream");
        }

        requestProcessorImplUnderTest = new RequestProcessorImpl(bufferedReaderMock, dataOutputStreamFake, pricingServiceImplMock);
    }

    @Test
    void shouldProcessInstructionAndReturnQuote() {
        Double expectedQuote = 11.23;
        try {
            when(bufferedReaderMock.readLine()).thenReturn("123 BUY 100");
            when(pricingServiceImplMock.getQuotePrice(any(Request.class))).thenReturn(expectedQuote);
            requestProcessorImplUnderTest.run();
            double result = getDoubleFromDataOutputStreamResult();
            assertEquals(11.23, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldHandleExceptionWhenIfCannotParseRequestStringInstruction() {
        try {
            when(bufferedReaderMock.readLine()).thenReturn("123 XXS 100");
            when(pricingServiceImplMock.getQuotePrice(any(Request.class))).thenReturn(11.3);
            requestProcessorImplUnderTest.run();
            String errorMessage = getStringFromDataOutputStreamResult();
            assertEquals("Invalid arguments supplied: {Invalid Instruction. (BUY|SELL) only allowed! }", errorMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double getDoubleFromDataOutputStreamResult() {
        try {
            DataInputStream din = new DataInputStream(new FileInputStream("test-result.dat"));
            return din.readDouble();
        } catch (Exception e) {
            System.out.println("Error Occurred in fetching result");
        }
        return 0;
    }

    private String getStringFromDataOutputStreamResult() {
        try {
            DataInputStream din = new DataInputStream(new FileInputStream(testDataFileOutput));
            return din.readLine();
        } catch (Exception e) {
            System.out.println("Error Occurred in fetching result");
        }
        return "";
    }

    // Cannot read
    // Cannot write
    // pricing service throws error
    // terminate instruction
    //
}