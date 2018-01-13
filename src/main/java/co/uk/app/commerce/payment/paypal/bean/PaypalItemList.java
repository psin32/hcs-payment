package co.uk.app.commerce.payment.paypal.bean;

import java.util.List;

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
public class PaypalItemList {

	private List<PaypalItems> items;
	
	private PaypalShippingAddress shipping_address;
	
	private String shipping_method;
}
