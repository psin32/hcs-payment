package co.uk.app.commerce.payment.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import co.uk.app.commerce.payment.bean.Orders;
import co.uk.app.commerce.payment.document.PaymentDetails;
import co.uk.app.commerce.payment.exception.PaymentApplicationException;
import co.uk.app.commerce.payment.globalcollect.bean.GlobalCollectResponse;
import co.uk.app.commerce.payment.paypal.bean.PaypalResponse;
import co.uk.app.commerce.payment.service.PaymentService;

@RestController
public class PaymentController {

	@Autowired
	private PaymentService paymentService;

	@PostMapping(path = "/paypal/create")
	public ResponseEntity<?> createPaypalPayment(@RequestBody Orders orders, HttpServletRequest request,
			HttpServletResponse response) {

		PaypalResponse paypalResponseBean = null;
		try {
			paypalResponseBean = paymentService.createPaypalPayment(orders);
		} catch (PaymentApplicationException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

		if (null != paypalResponseBean) {
			return ResponseEntity.ok(paypalResponseBean);
		}
		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}

	@PostMapping(path = "/paypal/get/{paymentId}")
	public ResponseEntity<?> getPaypalPayment(@PathVariable(value = "paymentId") String paymentId,
			@RequestBody Orders orders, HttpServletRequest request, HttpServletResponse response) {

		PaypalResponse paypalResponseBean = null;
		try {
			PaymentDetails paymentDetails = paymentService.getPaymentDetailsByOrderId(orders.getOrdersId());
			if (null != paymentDetails && paymentId.equalsIgnoreCase(paymentDetails.getPaypal().getId())) {
				paypalResponseBean = paymentService.getPaypalPaymentDetails(paymentDetails);
			} else {
				return ResponseEntity.status(HttpStatus.CONFLICT).build();
			}
		} catch (PaymentApplicationException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

		if (null != paypalResponseBean) {
			return ResponseEntity.ok(paypalResponseBean);
		}
		System.out.println("Payment Id 3");
		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}

	@PostMapping(path = "/paypal/execute/{paymentId}")
	public ResponseEntity<?> executePaypalPayment(@PathVariable(value = "paymentId") String paymentId,
			@RequestBody Orders orders, HttpServletRequest request, HttpServletResponse response) {

		PaypalResponse paypalResponseBean = null;
		try {
			PaymentDetails paymentDetails = paymentService.getPaymentDetailsByOrderId(orders.getOrdersId());
			if (null != paymentDetails && paymentId.equalsIgnoreCase(paymentDetails.getPaypal().getId())) {
				paypalResponseBean = paymentService.executePaypalPayment(paymentId, orders);
			} else {
				return ResponseEntity.status(HttpStatus.CONFLICT).build();
			}
		} catch (PaymentApplicationException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

		if (null != paypalResponseBean) {
			return ResponseEntity.ok(paypalResponseBean);
		}
		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}

	@PostMapping(path = "/globalcollect/create")
	public ResponseEntity<?> createGlobalCollectHostedCheckout(@RequestBody Orders orders, HttpServletRequest request,
			HttpServletResponse response) {

		GlobalCollectResponse globalCollectResponse = null;
		try {
			globalCollectResponse = paymentService.createGlobalCollectHostedPage(orders);
		} catch (PaymentApplicationException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

		if (null != globalCollectResponse) {
			return ResponseEntity.ok(globalCollectResponse);
		}
		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}

}
