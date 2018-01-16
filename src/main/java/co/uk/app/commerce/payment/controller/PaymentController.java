package co.uk.app.commerce.payment.controller;

import java.util.Collection;

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
import co.uk.app.commerce.payment.constant.PaymentConstants;
import co.uk.app.commerce.payment.document.CardDetails;
import co.uk.app.commerce.payment.document.PaymentDetails;
import co.uk.app.commerce.payment.exception.PaymentApplicationException;
import co.uk.app.commerce.payment.globalcollect.bean.GlobalCollectResponseBean;
import co.uk.app.commerce.payment.paypal.bean.PaypalResponse;
import co.uk.app.commerce.payment.service.PaymentService;

@RestController
public class PaymentController {

	@Autowired
	private PaymentService paymentService;

	@PostMapping(path = "/paypal/create")
	public ResponseEntity<?> createPaypalPayment(@RequestBody Orders orders, HttpServletRequest request,
			HttpServletResponse response) {
		String originatedBy = String.valueOf(request.getAttribute(PaymentConstants.REQUEST_HEADER_ORIGINATED_BY));
		if (PaymentConstants.REQUEST_ORIGINATED_BY_ORDER_APPLICATION.equalsIgnoreCase(originatedBy)) {
			PaypalResponse paypalResponseBean = null;
			try {
				paypalResponseBean = paymentService.createPaypalPayment(orders);
			} catch (PaymentApplicationException e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}

			if (null != paypalResponseBean) {
				return ResponseEntity.ok(paypalResponseBean);
			}
		} else {
			ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}

	@PostMapping(path = "/paypal/get/{paymentId}")
	public ResponseEntity<?> getPaypalPayment(@PathVariable(value = "paymentId") String paymentId,
			@RequestBody Orders orders, HttpServletRequest request, HttpServletResponse response) {
		String originatedBy = String.valueOf(request.getAttribute(PaymentConstants.REQUEST_HEADER_ORIGINATED_BY));
		if (PaymentConstants.REQUEST_ORIGINATED_BY_ORDER_APPLICATION.equalsIgnoreCase(originatedBy)) {
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
		} else {
			ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}

	@PostMapping(path = "/paypal/execute/{paymentId}")
	public ResponseEntity<?> executePaypalPayment(@PathVariable(value = "paymentId") String paymentId,
			@RequestBody Orders orders, HttpServletRequest request, HttpServletResponse response) {
		String originatedBy = String.valueOf(request.getAttribute(PaymentConstants.REQUEST_HEADER_ORIGINATED_BY));
		if (PaymentConstants.REQUEST_ORIGINATED_BY_ORDER_APPLICATION.equalsIgnoreCase(originatedBy)) {
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
		} else {
			ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}

	@PostMapping(path = "/globalcollect/create")
	public ResponseEntity<?> createGlobalCollectHostedCheckout(@RequestBody Orders orders, HttpServletRequest request,
			HttpServletResponse response) {
		String originatedBy = String.valueOf(request.getAttribute(PaymentConstants.REQUEST_HEADER_ORIGINATED_BY));
		if (PaymentConstants.REQUEST_ORIGINATED_BY_ORDER_APPLICATION.equalsIgnoreCase(originatedBy)) {
			GlobalCollectResponseBean globalCollectResponse = null;
			try {
				globalCollectResponse = paymentService.createGlobalCollectHostedPage(orders);
			} catch (PaymentApplicationException e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}

			if (null != globalCollectResponse) {
				return ResponseEntity.ok(globalCollectResponse);
			}
		} else {
			ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}

	@PostMapping(path = "/globalcollect/create/{cardId}")
	public ResponseEntity<?> createGlobalCollectHostedCheckoutForToken(@RequestBody Orders orders,
			@PathVariable(value = "cardId") String cardId, HttpServletRequest request, HttpServletResponse response) {
		String userId = String.valueOf(request.getAttribute(PaymentConstants.REQUEST_HEADER_USER_ID));
		String originatedBy = String.valueOf(request.getAttribute(PaymentConstants.REQUEST_HEADER_ORIGINATED_BY));
		if (PaymentConstants.REQUEST_ORIGINATED_BY_ORDER_APPLICATION.equalsIgnoreCase(originatedBy)) {
			GlobalCollectResponseBean globalCollectResponse = null;
			try {
				globalCollectResponse = paymentService.createGlobalCollectHostedPageForToken(orders, userId, cardId);
			} catch (PaymentApplicationException e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}

			if (null != globalCollectResponse) {
				return ResponseEntity.ok(globalCollectResponse);
			}
		} else {
			ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}

	@PostMapping(path = "/globalcollect/status/{orderId}/{hostedCheckoutId}")
	public ResponseEntity<?> getGlobalCollectHostedCheckoutStatus(@PathVariable(value = "orderId") String orderId,
			@PathVariable(value = "hostedCheckoutId") String hostedCheckoutId, HttpServletRequest request,
			HttpServletResponse response) {
		String usersId = String.valueOf(request.getAttribute(PaymentConstants.REQUEST_HEADER_USER_ID));
		String registerType = String.valueOf(request.getAttribute(PaymentConstants.REQUEST_HEADER_REGISTER_TYPE));
		String originatedBy = String.valueOf(request.getAttribute(PaymentConstants.REQUEST_HEADER_ORIGINATED_BY));
		if (PaymentConstants.REQUEST_ORIGINATED_BY_ORDER_APPLICATION.equalsIgnoreCase(originatedBy)) {
			GlobalCollectResponseBean globalCollectResponse = null;
			try {
				globalCollectResponse = paymentService.getGlobalCollectCheckoutStatus(orderId, hostedCheckoutId,
						usersId, registerType);
			} catch (PaymentApplicationException e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}

			if (null != globalCollectResponse) {
				return ResponseEntity.ok(globalCollectResponse);
			}
		} else {
			ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}

	@PostMapping(path = "/globalcollect/card")
	public ResponseEntity<?> getGlobalCollectTokenizedCard(HttpServletRequest request, HttpServletResponse response) {
		String userId = String.valueOf(request.getAttribute(PaymentConstants.REQUEST_HEADER_USER_ID));
		Collection<CardDetails> cardDetails = paymentService.getCardToken(userId);
		return ResponseEntity.ok(cardDetails);
	}

}
