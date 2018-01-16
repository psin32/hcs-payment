package co.uk.app.commerce.payment.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import co.uk.app.commerce.payment.globalcollect.bean.GlobalCollectPayment;
import co.uk.app.commerce.payment.paypal.bean.PaymentStatus;
import co.uk.app.commerce.payment.paypal.bean.PaymentType;
import co.uk.app.commerce.payment.paypal.bean.PaypalResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payment")
public class PaymentDetails {

	@Id
	private String id;

	private String orderId;

	private PaymentStatus status;

	private PaymentType paymentType;

	private PaypalResponse paypal;

	private GlobalCollectPayment globalcollect;
}
