package marketmaker;

import marketmaker.*;
import marketmaker.common.Request;
import marketmaker.common.RequestImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PricingServiceImplTest {
    static int securityID = 123;
    private ReferencePriceSource referencePriceSourceMock = mock(ReferencePriceSource.class);
    private QuoteCalculationEngine quoteCalculationEngineMock = mock(QuoteCalculationEngine.class);
    private PricingService pricingServiceUnderTest;

    @BeforeEach
    void setUp() {
        pricingServiceUnderTest = new PricingServiceImpl(quoteCalculationEngineMock, referencePriceSourceMock);
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

    @Test
    void shouldGetPriceAndComputeQuoteCorrectlyIfSecurityIdNotInCache() {
        Request testRequest = createRequest(5, true);
        when(referencePriceSourceMock.get(securityID)).thenReturn(1.12);
        when(quoteCalculationEngineMock.calculateQuotePrice(securityID, 1.12, true, 5)).thenReturn(11.2);

        double quote = pricingServiceUnderTest.getQuotePrice(testRequest);
        assertEquals(11.2, quote);
        verify(referencePriceSourceMock, times(1)).subscribe(any(ReferencePriceSourceListener.class));
    }

    @Test
    void shouldUseCachedReferencePriceOnSecondRequest() {
        Request firstRequest = createRequest(5, true);
        Request secondRequest = createRequest(15, false);
        double firstPrice = 1.12;
        double secondPrice = 1.14;
        double expectedFirstQuote = 0.01;
        double expectedSecondQuote = 10.01;

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ReferencePriceSourceListener callback = (ReferencePriceSourceListener) invocation.getArguments()[0];
                callback.referencePriceChanged(securityID, secondPrice);

                double secondFirstQuote = pricingServiceUnderTest.getQuotePrice(secondRequest);
                assertEquals(expectedSecondQuote, secondFirstQuote);
                return null;
            }
        }).when(referencePriceSourceMock).subscribe(any(ReferencePriceSourceListener.class));
        when(referencePriceSourceMock.get(securityID)).thenReturn(firstPrice);
        when(quoteCalculationEngineMock.calculateQuotePrice(securityID, firstPrice, firstRequest.isBuy(), firstRequest.getOrderQuantity())).thenReturn(expectedFirstQuote);
        when(quoteCalculationEngineMock.calculateQuotePrice(securityID, secondPrice, secondRequest.isBuy(), secondRequest.getOrderQuantity())).thenReturn(expectedSecondQuote);

        double actualFirstQuote = pricingServiceUnderTest.getQuotePrice(firstRequest);
        assertEquals(expectedFirstQuote, actualFirstQuote);
    }

    private Request createRequest(int quantity, boolean isBuy) {
        return new RequestImpl(securityID, quantity, isBuy);
    }

}