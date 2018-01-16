package co.uk.app.commerce.payment.service;

import java.util.Collection;

import co.uk.app.commerce.payment.bean.Orders;
import co.uk.app.commerce.payment.document.CardDetails;
import co.uk.app.commerce.payment.document.PaymentDetails;
import co.uk.app.commerce.payment.exception.PaymentApplicationException;
import co.uk.app.commerce.payment.globalcollect.bean.GlobalCollectResponseBean;
import co.uk.app.commerce.payment.paypal.bean.PaypalResponse;

public interface PaymentService {

	PaypalResponse createPaypalPayment(Orders orders) throws PaymentApplicationException;

	PaypalResponse getPaypalPaymentDetails(PaymentDetails paymentDetails) throws PaymentApplicationException;

	PaypalResponse executePaypalPayment(String paymentId, Orders orders) throws PaymentApplicationException;

	PaymentDetails getPaymentDetailsByOrderId(String orderId);

	GlobalCollectResponseBean createGlobalCollectHostedPage(Orders orders) throws PaymentApplicationException;

	GlobalCollectResponseBean getGlobalCollectCheckoutStatus(String orderId, String hostedCheckoutId, String userId,
			String registerType) throws PaymentApplicationException;

	Collection<CardDetails> getCardToken(String userId);

	GlobalCollectResponseBean createGlobalCollectHostedPageForToken(Orders orders, String userId, String cardId)
			throws PaymentApplicationException;
}
