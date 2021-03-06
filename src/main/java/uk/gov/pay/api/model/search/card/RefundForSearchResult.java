package uk.gov.pay.api.model.search.card;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import uk.gov.pay.api.model.links.RefundLinksForSearch;

import java.util.List;

@ApiModel
public class RefundForSearchResult {

    @ApiModelProperty(name = "payment_id", example = "hu20sqlact5260q2nanm0q8u93")
    @Schema(name = "payment_id", example = "hu20sqlact5260q2nanm0q8u93")
    private String paymentId;
    @ApiModelProperty(name = "_links")
    @Schema(name = "_links")
    private RefundLinksForSearch links;
    @JsonProperty(value = "_embedded")
    @Schema(name = "_embedded")
    private Embedded embedded;

    @ApiModel(value = "EmbeddedRefunds")
    @Schema(name = "EmbeddedRefunds")
    public class Embedded {
        @ApiModelProperty
        private List<RefundResult> refunds;

        public List<RefundResult> getRefunds() {
            return refunds;
        }
    }

    public String getPaymentId() {
        return paymentId;
    }

    public RefundLinksForSearch getLinks() {
        return links;
    }

    public Embedded getEmbedded() {
        return embedded;
    }
}
