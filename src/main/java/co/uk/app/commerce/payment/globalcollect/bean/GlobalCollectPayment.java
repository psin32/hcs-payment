package co.uk.app.commerce.payment.globalcollect.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ingenico.connect.gateway.sdk.java.domain.hostedcheckout.CreateHostedCheckoutResponse;
import com.ingenico.connect.gateway.sdk.java.domain.hostedcheckout.GetHostedCheckoutResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GlobalCollectPayment {

	private CreateHostedCheckoutResponse createHostedCheckoutResponse;

	private GetHostedCheckoutResponse getHostedCheckoutResponse;

	private String status;
}
