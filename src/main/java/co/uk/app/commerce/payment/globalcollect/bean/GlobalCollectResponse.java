package co.uk.app.commerce.payment.globalcollect.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GlobalCollectResponse {

	private String hostedCheckoutId;

	private String invalidTokens;

	private String partialRedirectUrl;

	private String returnmac;
}
