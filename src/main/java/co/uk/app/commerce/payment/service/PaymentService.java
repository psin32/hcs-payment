package co.uk.app.commerce.payment.service;

import co.uk.app.commerce.payment.bean.Orders;
import co.uk.app.commerce.payment.document.PaymentDetails;
import co.uk.app.commerce.payment.exception.PaymentApplicationException;
import co.uk.app.commerce.payment.globalcollect.bean.GlobalCollectResponse;
import co.uk.app.commerce.payment.paypal.bean.PaypalResponse;

public interface PaymentService {

	PaypalResponse createPaypalPayment(Orders orders) throws PaymentApplicationException;

	PaypalResponse getPaypalPaymentDetails(PaymentDetails paymentDetails) throws PaymentApplicationException;

	PaypalResponse executePaypalPayment(String paymentId, Orders orders) throws PaymentApplicationException;

	PaymentDetails getPaymentDetailsByOrderId(String orderId);

	GlobalCollectResponse createGlobalCollectHostedPage(Orders orders) throws PaymentApplicationException;
}
