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
public class PaypalResponse {

	private String id;

	private String intent;

	private String state;

	private String cart;

	private List<PaypalLinks> links;

	private List<PaypalTransactions> transactions;

	private PaypalPayer payer;

	private String created_time;

	private String updated_time;
}
