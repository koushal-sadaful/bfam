import com.sun.javaws.exceptions.InvalidArgumentException;
import marketmaker.common.Request;
import marketmaker.common.RequestParser;
import marketmaker.common.RequestParserImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestParserImplTest {

    private RequestParser requestParserUnderTest;

    @BeforeEach
    void setUp() {
        requestParserUnderTest = new RequestParserImpl();
    }

    @Test
    void canParseValidInstruction() {
        Request userRequest = null;
        try {
            userRequest = requestParserUnderTest.parseFromString("123 BUY 100");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(userRequest.isBuy());
        assertEquals(100, userRequest.getOrderQuantity());
        assertEquals(123, userRequest.getSecurityId());
    }

    @Test
    void canParseValidInstructionCaseInsensitive() {
        Request userRequest = null;
        try {
            userRequest = requestParserUnderTest.parseFromString("123 bUy 100");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(userRequest.isBuy());
        assertEquals(100, userRequest.getOrderQuantity());
        assertEquals(123, userRequest.getSecurityId());
    }


    @Test
    void canParseQuitInstruction() {
        Request userRequest = null;
        try {
            userRequest = requestParserUnderTest.parseFromString("quiT");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(userRequest.isDisconnectionRequest());
    }


    @ParameterizedTest
    @ValueSource(strings = {
            "123",          //Incomplete Instruction
            "  ",           //Incomplete Instruction
            "SELL",         //Incomplete Instruction
            "",             //Empty Instruction
            "123 BUY ",     //Incomplete Instruction
            "BUY 123   ",   //Incomplete Instruction with space
            "123 0BUY 103", //Invalid Instruction
            "123 SELL 0",   //Quantity Cannot Be Zero
            "123 SELL -100", //Quantity Cannot Be Negative
            "123 SELL 1.5",  //Quantity Cannot Be Decimal
            "XXX SELL 100",   //Invalid ID
            "105.5 SELL 100",  //Invalid Number
            "-105 SELL 100",  //Invalid Number
    })
    void parseShouldThrowAnException(String instruction) {
        Assertions.assertThrows(InvalidArgumentException.class, () -> requestParserUnderTest.parseFromString(instruction));
    }
}