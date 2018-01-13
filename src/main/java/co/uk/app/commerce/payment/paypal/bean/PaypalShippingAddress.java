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
public class PaypalShippingAddress {

	private String recipient_name;

	private String line1;

	private String line2;

	private String city;

	private String country_code;

	private String postal_code;

	private String state;
}
