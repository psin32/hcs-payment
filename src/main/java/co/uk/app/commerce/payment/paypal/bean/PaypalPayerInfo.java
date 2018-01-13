package co.uk.app.commerce.payment.paypal.bean;

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
public class PaypalPayerInfo {

	private String email;
	
	private String first_name;
	
	private String last_name;
	
	private String payer_id;
	
	private String country_code;
	
	private PaypalShippingAddress shipping_address;
}
