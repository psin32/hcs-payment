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
public class PaypalItems {

	private String sku;
	
	private String name;
	
	private String quantity;
	
	private String price;
	
	private String currency;
}
