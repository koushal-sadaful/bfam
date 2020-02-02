import marketmaker.PricingServiceImpl;
import marketmaker.RequestProcessorImpl;
import marketmaker.common.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class RequestProcessorImplTest {

    private BufferedReader bufferedReaderMock = mock(BufferedReader.class);
    private PricingServiceImpl pricingServiceImplMock = mock(PricingServiceImpl.class);
    private RequestProcessorImpl requestProcessorImplUnderTest;
    private ByteArrayOutputStream outputStreamFake;

    @BeforeEach
    void setUp() {
        outputStreamFake = new ByteArrayOutputStream();
        DataOutputStream dataOutputStreamFake = new DataOutputStream(outputStreamFake);
        requestProcessorImplUnderTest = new RequestProcessorImpl(bufferedReaderMock, dataOutputStreamFake, pricingServiceImplMock);
    }

    @Test
    void shouldProcessInstructionAndReturnQuote() {
        Double expectedQuote = 11.23;
        try {
            when(bufferedReaderMock.readLine()).thenReturn("123 BUY 100");
            when(pricingServiceImplMock.getQuotePrice(any(Request.class))).thenReturn(expectedQuote);
            requestProcessorImplUnderTest.run();
            Double result = getDoubleFromOutputStream(outputStreamFake);
            assertEquals(11.23, result);
        } catch (Exception e) {
            fail("Should not have thrown any exception");
            e.printStackTrace();
        }
    }

    @Test
    void shouldHandleExceptionWhenIfCannotParseRequestStringInstruction_InvalidAction() {
        try {
            when(bufferedReaderMock.readLine()).thenReturn("123 XXS 100");
            when(pricingServiceImplMock.getQuotePrice(any(Request.class))).thenReturn(11.3);
            requestProcessorImplUnderTest.run();
            String errorMessage = getStringFromOutputStream();
            assertEquals("Invalid arguments supplied: {Invalid Instruction. (BUY|SELL) only allowed! }", errorMessage);
        } catch (Exception e) {
            fail("Should not have thrown any exception");
            e.printStackTrace();
        }
    }

    @Test
    void shouldHandleExceptionWhenIfCannotParseRequestStringInstruction_BadFormat() {
        try {
            when(bufferedReaderMock.readLine()).thenReturn("123");
            when(pricingServiceImplMock.getQuotePrice(any(Request.class))).thenReturn(11.3);
            requestProcessorImplUnderTest.run();
            String errorMessage = getStringFromOutputStream();
            assertEquals("Invalid arguments supplied: {Instruction format is incorrect: {security ID} (BUY|SELL) {quantity} }", errorMessage);
        } catch (Exception e) {
            fail("Should not have thrown any exception");
            e.printStackTrace();
        }
    }

    @Test
    void shouldHandleExceptionAndStopThreadWhenFailsToReadNextInstruction() {
        try {
            when(bufferedReaderMock.readLine()).thenThrow(new IOException("something blew up the input stream"));
            when(pricingServiceImplMock.getQuotePrice(any(Request.class))).thenReturn(11.3);
            requestProcessorImplUnderTest.run();
            assertThreadTerminatedAndOutputStreamEmpty();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldStopThreadWhenReceiveTerminateInstruction() {
        try {
            when(bufferedReaderMock.readLine()).thenReturn("quit");
            when(pricingServiceImplMock.getQuotePrice(any(Request.class))).thenReturn(11.3);
            requestProcessorImplUnderTest.run();
            assertThreadTerminatedAndOutputStreamEmpty();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldHandleExceptionAndStopThreadWhenFailsToWriteOutput() {
        try {
            DataOutputStream dataOutputStreamMock = mock(DataOutputStream.class);
            requestProcessorImplUnderTest = new RequestProcessorImpl(bufferedReaderMock, dataOutputStreamMock, pricingServiceImplMock);
            when(bufferedReaderMock.readLine()).thenReturn("123 BUY 100");
            when(pricingServiceImplMock.getQuotePrice(any(Request.class))).thenReturn(11.3);
            requestProcessorImplUnderTest.run();
            assertThreadTerminatedAndOutputStreamEmpty();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Double getDoubleFromOutputStream(ByteArrayOutputStream outputStream) {
        try {
            return ByteBuffer.wrap(outputStreamFake.toByteArray()).getDouble();
        } catch (Exception e) {
            System.out.println("Cant parse output stream to double");
        }
        return null;
    }

    private String getStringFromOutputStream() {
        try {
            return new String(outputStreamFake.toByteArray());
        } catch (Exception e) {
            System.out.println("Cant parse output stream to string");
        }
        return "";
    }

    private void assertThreadTerminatedAndOutputStreamEmpty() {
        assertEquals(0, outputStreamFake.toByteArray().length);
        assertFalse(requestProcessorImplUnderTest.isAlive());
    }

}