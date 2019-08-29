package uk.gov.pay.api.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.pay.api.auth.Account;
import uk.gov.pay.api.filter.ratelimit.RateLimitException;
import uk.gov.pay.api.filter.ratelimit.RateLimiter;
import uk.gov.pay.api.model.TokenPaymentType;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RateLimiterFilterTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private String authorization = "Bearer whateverAuthorizationToken";
    private RateLimiterFilter rateLimiterFilter;
    private RateLimiter rateLimiter;
    @Mock
    private ContainerRequestContext mockContainerRequestContext;

    @Before
    public void setup() {
        rateLimiter = mock(RateLimiter.class);
        rateLimiterFilter = new RateLimiterFilter(rateLimiter, new ObjectMapper());

        Account account = new Account("account-id", TokenPaymentType.CARD);
        SecurityContext mockSecurityContext = mock(SecurityContext.class);
        when(mockSecurityContext.getUserPrincipal()).thenReturn(account);

        when(mockContainerRequestContext.getSecurityContext()).thenReturn(mockSecurityContext);

        when(mockContainerRequestContext.getHeaderString("Authorization")).thenReturn(authorization);
        when(mockContainerRequestContext.getMethod()).thenReturn("GET");
    }

    @Test
    public void shouldCheckRateLimitsWhenFilterIsInvoked() throws Exception {
        rateLimiterFilter.filter(mockContainerRequestContext);
        verify(rateLimiter).checkRateOf(any(), any(), any());
    }

    @Test
    public void shouldSendErrorResponse_whenRateLimitExceeded() throws Exception {
        doThrow(RateLimitException.class).when(rateLimiter).checkRateOf("account-id", "GET-" + authorization, "GET");

        try {
            rateLimiterFilter.filter(mockContainerRequestContext);
        } catch (WebApplicationException e) {
            Response response = e.getResponse();
            assertEquals(429, response.getStatus());
            assertEquals("application/json", response.getHeaderString("Content-Type"));
            assertEquals("utf-8", response.getHeaderString("Content-Encoding"));
            assertEquals("{\"code\":\"P0900\",\"description\":\"Too many requests\"}", response.getEntity());
        }
    }
}
