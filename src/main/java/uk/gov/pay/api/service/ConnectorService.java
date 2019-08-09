package uk.gov.pay.api.service;

import uk.gov.pay.api.auth.Account;
import uk.gov.pay.api.exception.GetChargeException;
import uk.gov.pay.api.exception.GetEventsException;
import uk.gov.pay.api.exception.GetRefundsException;
import uk.gov.pay.api.model.Charge;
import uk.gov.pay.api.model.ChargeFromResponse;
import uk.gov.pay.api.model.PaymentEvents;
import uk.gov.pay.api.model.Refund;
import uk.gov.pay.api.model.RefundsFromConnector;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.http.HttpStatus.SC_OK;

public class ConnectorService {
    private final Client client;
    private final ConnectorUriGenerator connectorUriGenerator;

    @Inject
    public ConnectorService(Client client, ConnectorUriGenerator connectorUriGenerator) {
        this.client = client;
        this.connectorUriGenerator = connectorUriGenerator;
    }

    public Charge getCharge(Account account, String paymentId) {
        Response response = client
                .target(connectorUriGenerator.chargeURI(account, paymentId))
                .request()
                .get();

        if (response.getStatus() == SC_OK) {
            ChargeFromResponse chargeFromResponse = response.readEntity(ChargeFromResponse.class);
            return Charge.from(chargeFromResponse);
        }

        throw new GetChargeException(response);
    }

    public PaymentEvents getChargeEvents(Account account, String paymentId) {
        Response connectorResponse = client
                .target(connectorUriGenerator.chargeEventsURI(account, paymentId))
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();

        if (connectorResponse.getStatus() == SC_OK) {
            return connectorResponse.readEntity(PaymentEvents.class);
        }

        throw new GetEventsException(connectorResponse);
    }

    public List<Refund> getPaymentRefunds(String accountId, String paymentId) {
        Response connectorResponse = client
                .target(connectorUriGenerator.refundsForPaymentURI(accountId, paymentId))
                .request()
                .get();

        if (connectorResponse.getStatus() == SC_OK) {
            RefundsFromConnector refundsFromConnector = connectorResponse.readEntity(RefundsFromConnector.class);
            return refundsFromConnector
                    .getEmbedded()
                    .getRefunds()
                    .stream()
                    .map(refundFromConnector ->
                            Refund.valueOf(refundFromConnector))
                    .collect(Collectors.toList());
        }

        throw new GetRefundsException(connectorResponse);
    }
}
