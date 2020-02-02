package test;

import com.example.marketmaker.*;
import com.example.marketmaker.common.Request;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PricingServiceTest {
    static int securityID = 123;
    private ReferencePriceSource referencePriceSourceMock = mock(ReferencePriceSource.class);
    private QuoteCalculationEngine quoteCalculationEngineMock = mock(QuoteCalculationEngine.class);
    private IPricingService pricingServiceUnderTest;

    @BeforeEach
    void setUp() {
        pricingServiceUnderTest = new PricingService(quoteCalculationEngineMock, referencePriceSourceMock);
    }

    @Test
    void shouldGetPriceAndComputeQuoteCorrectly() {
        Request testRequest = createRequest(5, true);
        when(referencePriceSourceMock.get(securityID)).thenReturn(1.12);
        when(quoteCalculationEngineMock.calculateQuotePrice(securityID, 1.12, true, 5)).thenReturn(11.2);

        double quote = pricingServiceUnderTest.getQuotePrice(testRequest);
        assertEquals(11.2, quote);
        verify(referencePriceSourceMock, times(1)).subscribe(any(ReferencePriceSourceListener.class));
    }

    @Test
    void shouldSubscribeToReferencePriceSouceOncePerSecurityId() {
        when(referencePriceSourceMock.get(securityID)).thenReturn(1.12);

        pricingServiceUnderTest.getReferencePrice(securityID);
        pricingServiceUnderTest.getReferencePrice(securityID);

        verify(referencePriceSourceMock, times(1)).subscribe(any());
    }

    @Test
    void shouldUpdateTheCacheOnAPriceUpdate() {
        double firstPrice = 1.12;
        double secondPrice = 1.14;

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ReferencePriceSourceListener callback = (ReferencePriceSourceListener) invocation.getArguments()[0];
                callback.referencePriceChanged(securityID, secondPrice);
                assertEquals(secondPrice, pricingServiceUnderTest.getReferencePrice(securityID));
                return null;
            }
        }).when(referencePriceSourceMock).subscribe(any(ReferencePriceSourceListener.class));
        when(referencePriceSourceMock.get(securityID)).thenReturn(firstPrice);

        assertEquals(firstPrice, pricingServiceUnderTest.getReferencePrice(securityID));

        verify(referencePriceSourceMock, times(1)).get(securityID);
        verify(referencePriceSourceMock, times(1)).subscribe(any(ReferencePriceSourceListener.class));
    }

    @ParameterizedTest
    @ValueSource(doubles = {
            -1.0,
            0.0,
            -0.000001
    })
    void shouldNotUpdateTheCacheOnInvalidPriceUpdates(double invalidPriceUpdate) {
        double firstPrice = 1.12;

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ReferencePriceSourceListener callback = (ReferencePriceSourceListener) invocation.getArguments()[0];
                callback.referencePriceChanged(securityID, invalidPriceUpdate);
                return null;
            }
        }).when(referencePriceSourceMock).subscribe(any(ReferencePriceSourceListener.class));
        when(referencePriceSourceMock.get(securityID)).thenReturn(firstPrice);
        assertEquals(firstPrice, pricingServiceUnderTest.getReferencePrice(securityID));


        assertEquals(firstPrice, pricingServiceUnderTest.getReferencePrice(securityID));
        verify(referencePriceSourceMock, times(1)).get(securityID);
        verify(referencePriceSourceMock, times(1)).subscribe(any(ReferencePriceSourceListener.class));
    }


    private Request createRequest(int quantity, boolean isBuy) {
        return new Request(securityID, quantity, isBuy);
    }

}