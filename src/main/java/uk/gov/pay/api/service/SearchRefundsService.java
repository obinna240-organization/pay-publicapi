package uk.gov.pay.api.service;

import uk.gov.pay.api.auth.Account;
import uk.gov.pay.api.model.search.card.SearchRefunds;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static uk.gov.pay.api.validation.RefundSearchValidator.validateSearchParameters;

public class SearchRefundsService {

    private static final String PAGE = "page";
    private static final String DISPLAY_SIZE = "display_size";
    private static final String DEFAULT_PAGE = "1";
    private static final String DEFAULT_DISPLAY_SIZE = "500";
    private static final String FROM_DATE = "from_date";
    private static final String TO_DATE = "to_date";
    
    private final SearchRefunds searchRefunds;

    @Inject
    public SearchRefundsService(SearchRefunds searchRefunds) {
        this.searchRefunds = searchRefunds;
    }

    public Response getAllRefunds(Account account, RefundsParams params) {
        validateSearchParameters(params);
        Map<String, String> queryParams = buildQueryString(params);
        return searchRefunds.getSearchResponse(account, queryParams);
    }

    private Map<String, String> buildQueryString(RefundsParams params) {
        Map<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put(FROM_DATE, params.getFromDate());
        queryParams.put(TO_DATE, params.getToDate());
        queryParams.put(PAGE, Optional.ofNullable(params.getPage()).orElse(DEFAULT_PAGE));
        queryParams.put(DISPLAY_SIZE, Optional.ofNullable(params.getDisplaySize()).orElse(DEFAULT_DISPLAY_SIZE));
        return queryParams;
    }
}
