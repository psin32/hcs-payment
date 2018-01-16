package co.uk.app.commerce.payment.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingenico.connect.gateway.sdk.java.Client;
import com.ingenico.connect.gateway.sdk.java.CommunicatorConfiguration;
import com.ingenico.connect.gateway.sdk.java.Factory;
import com.ingenico.connect.gateway.sdk.java.domain.definitions.AmountOfMoney;
import com.ingenico.connect.gateway.sdk.java.domain.definitions.PaymentProductFilter;
import com.ingenico.connect.gateway.sdk.java.domain.hostedcheckout.CreateHostedCheckoutRequest;
import com.ingenico.connect.gateway.sdk.java.domain.hostedcheckout.CreateHostedCheckoutResponse;
import com.ingenico.connect.gateway.sdk.java.domain.hostedcheckout.GetHostedCheckoutResponse;
import com.ingenico.connect.gateway.sdk.java.domain.hostedcheckout.definitions.HostedCheckoutSpecificInput;
import com.ingenico.connect.gateway.sdk.java.domain.hostedcheckout.definitions.PaymentProductFiltersHostedCheckout;
import com.ingenico.connect.gateway.sdk.java.domain.payment.definitions.Customer;
import com.ingenico.connect.gateway.sdk.java.domain.payment.definitions.Order;
import com.ingenico.connect.gateway.sdk.java.domain.token.TokenResponse;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Details;
import com.paypal.api.payments.Item;
import com.paypal.api.payments.ItemList;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.ShippingAddress;
import com.paypal.api.payments.Transaction;
import com.paypal.api.payments.Transactions;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

import co.uk.app.commerce.payment.bean.Orders;
import co.uk.app.commerce.payment.constant.PaymentConstants;
import co.uk.app.commerce.payment.document.CardDetails;
import co.uk.app.commerce.payment.document.PaymentDetails;
import co.uk.app.commerce.payment.exception.PaymentApplicationException;
import co.uk.app.commerce.payment.globalcollect.bean.GlobalCollectPayment;
import co.uk.app.commerce.payment.globalcollect.bean.GlobalCollectResponseBean;
import co.uk.app.commerce.payment.paypal.bean.PaymentStatus;
import co.uk.app.commerce.payment.paypal.bean.PaymentType;
import co.uk.app.commerce.payment.paypal.bean.PaypalResponse;
import co.uk.app.commerce.payment.repository.CardRepository;
import co.uk.app.commerce.payment.repository.PaymentRepository;

@Component
public class PaymentServiceImpl implements PaymentService {

	@Value("${payment.paypal.configuration.client_id}")
	private String clientId;

	@Value("${payment.paypal.configuration.client_secret}")
	private String clientSecret;

	@Value("${payment.paypal.configuration.cancel.url}")
	private String cancelURL;

	@Value("${payment.paypal.configuration.return.url}")
	private String returnURL;

	@Value("${payment.paypal.configuration.mode}")
	private String paypalMode;

	@Value("${payment.globalcollect.configuration.merchantId}")
	private String globalCollectMerchantId;

	@Value("${payment.globalcollect.configuration.apiKeyId}")
	private String globalCollectApiKey;

	@Value("${payment.globalcollect.configuration.secretApiKey}")
	private String globalCollectSecretKey;

	@Value("${payment.globalcollect.configuration.hostedcheckout.variantId}")
	private String globalCollectVariantId;

	@Value("${payment.globalcollect.configuration.return.url}")
	private String globalCollectReturnUrl;

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private CardRepository cardRepository;

	@Override
	public PaypalResponse createPaypalPayment(Orders orders) throws PaymentApplicationException {
		Payment createdPayment = null;
		PaypalResponse paypalResponse = null;
		try {
			APIContext apiContext = new APIContext(clientId, clientSecret, paypalMode);

			Payer payer = new Payer();
			payer.setPaymentMethod(PaymentConstants.PAYPAL_PAYMENT_NAME);

			List<Item> listOfItem = setItems(orders);
			ShippingAddress shippingAddress = setShippingAddress(orders);

			ItemList itemList = new ItemList();
			itemList.setItems(listOfItem);
			itemList.setShippingAddress(shippingAddress);
			itemList.setShippingMethod(orders.getShippingmethod());

			Details details = setDetails(orders);

			Amount amount = setAmount(orders, details);

			Transaction transaction = setTransaction(orders, itemList, amount);

			List<Transaction> transactions = new ArrayList<Transaction>();
			transactions.add(transaction);

			RedirectUrls redirectUrls = new RedirectUrls();
			redirectUrls.setCancelUrl(cancelURL);
			redirectUrls.setReturnUrl(returnURL);

			Payment payment = setPayment(payer, transactions, redirectUrls);

			createdPayment = payment.create(apiContext);

			if (null != createdPayment) {
				paypalResponse = convertPaypalResponse(createdPayment);

				PaymentDetails paymentDetails = paymentRepository.findByOrderId(orders.getOrdersId());

				if (paymentDetails == null) {
					paymentDetails = new PaymentDetails();
					paymentDetails.setOrderId(orders.getOrdersId());
					paymentDetails.setPaymentType(PaymentType.PAYPAL);
				}
				paymentDetails.setPaypal(paypalResponse);
				paymentDetails.setStatus(PaymentStatus.CREATED);
				paymentDetails.setGlobalcollect(null);

				paymentRepository.save(paymentDetails);
			}
		} catch (PayPalRESTException e) {
			throw new PaymentApplicationException("Exception occured while creating paypal payment - ", e);
		}
		return paypalResponse;
	}

	@Override
	public PaypalResponse getPaypalPaymentDetails(PaymentDetails paymentDetails) throws PaymentApplicationException {
		Payment payment = null;
		PaypalResponse paypalResponseBean = null;
		try {
			APIContext apiContext = new APIContext(clientId, clientSecret, paypalMode);
			payment = Payment.get(apiContext, paymentDetails.getPaypal().getId());

			if (null != payment) {
				paypalResponseBean = convertPaypalResponse(payment);

				paymentDetails.setPaypal(paypalResponseBean);
				paymentDetails.setStatus(PaymentStatus.CREATED);
				paymentDetails.setGlobalcollect(null);

				paymentRepository.save(paymentDetails);
			}
		} catch (PayPalRESTException e) {
			throw new PaymentApplicationException("Exception occured while fetching paypal payment - ", e);
		}
		return paypalResponseBean;
	}

	@Override
	public PaypalResponse executePaypalPayment(String paymentId, Orders orders) throws PaymentApplicationException {

		APIContext apiContext = new APIContext(clientId, clientSecret, paypalMode);

		PaypalResponse paypalResponseBean = null;
		Payment payment = null;

		Transactions transactions = new Transactions();

		Details details = setDetails(orders);

		Amount amount = setAmount(orders, details);

		transactions.setAmount(amount);

		List<Transactions> transactionList = new ArrayList<Transactions>();
		transactionList.add(transactions);

		Payment updatedPayment = null;
		try {
			payment = Payment.get(apiContext, paymentId);

			PaymentExecution paymentExecution = new PaymentExecution();
			paymentExecution.setPayerId(payment.getPayer().getPayerInfo().getPayerId());
			paymentExecution.setTransactions(transactionList);

			updatedPayment = payment.execute(apiContext, paymentExecution);

			if (null != updatedPayment) {
				paypalResponseBean = convertPaypalResponse(updatedPayment);

				PaymentDetails paymentDetails = paymentRepository.findByOrderId(orders.getOrdersId());

				paymentDetails.setPaypal(paypalResponseBean);
				paymentDetails.setGlobalcollect(null);

				if (null != updatedPayment.getState()
						&& updatedPayment.getState().equalsIgnoreCase(PaymentConstants.PAYMENT_STATUS_APPROVED)) {
					paymentDetails.setStatus(PaymentStatus.COMPLETED);
					paymentRepository.save(paymentDetails);
				}
			}
		} catch (PayPalRESTException e) {
			throw new PaymentApplicationException("Exception occured while executing paypal payment - ", e);
		}
		return paypalResponseBean;
	}

	@Override
	public PaymentDetails getPaymentDetailsByOrderId(String orderId) {
		return paymentRepository.findByOrderId(orderId);
	}

	private Payment setPayment(Payer payer, List<Transaction> transactions, RedirectUrls redirectUrls) {
		Payment payment = new Payment();
		payment.setIntent(PaymentConstants.PAYPAL_PAYMENT_INTENT);
		payment.setPayer(payer);
		payment.setTransactions(transactions);
		payment.setRedirectUrls(redirectUrls);
		return payment;
	}

	private Transaction setTransaction(Orders orders, ItemList itemList, Amount amount) {
		Transaction transaction = new Transaction();
		transaction.setDescription("Order details for order number - " + orders.getOrdersId());
		transaction.setAmount(amount);
		transaction.setInvoiceNumber(orders.getOrdersId());
		transaction.setItemList(itemList);
		return transaction;
	}

	private Amount setAmount(Orders orders, Details details) {
		Amount amount = new Amount();
		amount.setCurrency(PaymentConstants.CURRENCY_UK);
		amount.setTotal(orders.getFormattedOrdertotal());
		amount.setDetails(details);
		return amount;
	}

	private Details setDetails(Orders orders) {
		Details details = new Details();
		details.setShipping(orders.getFormattedShippingcharges());
		details.setSubtotal(orders.getFormattedSubtotal());
		return details;
	}

	private List<Item> setItems(Orders orders) {
		List<Item> listOfItem = new ArrayList<>();
		orders.getItems().stream().forEach(items -> {
			Item item = new Item();
			item.setName(items.getName());
			item.setSku(items.getPartnumber());
			item.setCurrency(PaymentConstants.CURRENCY_UK);
			item.setPrice(items.getFormattedListprice());
			item.setQuantity(String.valueOf(items.getQuantity()));

			listOfItem.add(item);
		});
		return listOfItem;
	}

	private ShippingAddress setShippingAddress(Orders orders) {
		ShippingAddress shippingAddress = new ShippingAddress();
		String name = orders.getShippingaddress().getTitle() + " " + orders.getShippingaddress().getFirstname() + " "
				+ orders.getShippingaddress().getLastname();
		shippingAddress.setRecipientName(name);
		shippingAddress.setLine1(orders.getShippingaddress().getAddress1());
		shippingAddress.setLine2(orders.getShippingaddress().getAddress2());
		shippingAddress.setId(String.valueOf(orders.getShippingaddress().getAddressId()));
		shippingAddress.setPostalCode(orders.getShippingaddress().getZipcode());
		shippingAddress.setCity(orders.getShippingaddress().getCity());
		shippingAddress.setCountryCode("GB");
		return shippingAddress;
	}

	private PaypalResponse convertPaypalResponse(Payment payment) throws PaymentApplicationException {
		ObjectMapper mapper = new ObjectMapper();
		PaypalResponse paypalResponseBean = new PaypalResponse();
		try {
			paypalResponseBean = mapper.readValue(payment.toJSON(), PaypalResponse.class);
		} catch (JsonParseException e) {
			throw new PaymentApplicationException("Exception occured while parsing paypal response - ", e);
		} catch (JsonMappingException e) {
			throw new PaymentApplicationException("Exception occured while parsing paypal response - ", e);
		} catch (IOException e) {
			throw new PaymentApplicationException("Exception occured while parsing paypal response - ", e);
		}
		return paypalResponseBean;
	}

	@Override
	public GlobalCollectResponseBean createGlobalCollectHostedPage(Orders orders) throws PaymentApplicationException {

		// Card card = new Card();
		// card.setCvv("123");
		// card.setCardNumber("4567350000427977");
		// card.setExpiryDate("1220");
		// cardPaymentMethodSpecificInput.setCard(card);

		HostedCheckoutSpecificInput hostedCheckoutSpecificInput = new HostedCheckoutSpecificInput();
		PaymentProductFiltersHostedCheckout paymentProductFiltersHostedCheckout = new PaymentProductFiltersHostedCheckout();

		CreateHostedCheckoutRequest body = generateCreateGlobalCollectRequest(orders, hostedCheckoutSpecificInput,
				paymentProductFiltersHostedCheckout);

		GlobalCollectResponseBean globalCollectResponseBean = new GlobalCollectResponseBean();
		GlobalCollectPayment globalCollectPayment = new GlobalCollectPayment();
		try {
			CreateHostedCheckoutResponse response = getGlobalCollectClient().merchant(globalCollectMerchantId)
					.hostedcheckouts().create(body);
			globalCollectPayment.setCreateHostedCheckoutResponse(response);
			globalCollectPayment.setStatus(PaymentConstants.GLOBALCOLLECT_STATUS_IN_PROGRESS);

			PaymentDetails paymentDetails = paymentRepository.findByOrderId(orders.getOrdersId());

			if (null == paymentDetails) {
				paymentDetails = new PaymentDetails();
			}
			paymentDetails.setOrderId(orders.getOrdersId());
			paymentDetails.setPaymentType(PaymentType.GLOBALCOLLECT);
			paymentDetails.setGlobalcollect(globalCollectPayment);
			paymentDetails.setStatus(PaymentStatus.CREATED);
			paymentDetails.setPaypal(null);

			paymentRepository.save(paymentDetails);

			globalCollectResponseBean
					.setHostedCheckoutId(globalCollectPayment.getCreateHostedCheckoutResponse().getHostedCheckoutId());
			globalCollectResponseBean.setPartialRedirectUrl(
					globalCollectPayment.getCreateHostedCheckoutResponse().getPartialRedirectUrl());
			globalCollectResponseBean.setStatus(PaymentConstants.GLOBALCOLLECT_STATUS_IN_PROGRESS);

		} catch (URISyntaxException e) {
			throw new PaymentApplicationException(
					"Exception occured while creating global collect hosted checkout page - ", e);
		}
		return globalCollectResponseBean;
	}

	@Override
	public GlobalCollectResponseBean createGlobalCollectHostedPageForToken(Orders orders, String userId, String cardId)
			throws PaymentApplicationException {

		HostedCheckoutSpecificInput hostedCheckoutSpecificInput = new HostedCheckoutSpecificInput();
		PaymentProductFiltersHostedCheckout paymentProductFiltersHostedCheckout = new PaymentProductFiltersHostedCheckout();

		CardDetails cardDetails = cardRepository.findByUserIdAndCardId(userId, cardId);

		if (null != cardDetails && null != cardDetails.getToken()) {
			hostedCheckoutSpecificInput.setTokens(cardDetails.getToken());
			paymentProductFiltersHostedCheckout.setTokensOnly(true);
		}

		CreateHostedCheckoutRequest body = generateCreateGlobalCollectRequest(orders, hostedCheckoutSpecificInput,
				paymentProductFiltersHostedCheckout);

		GlobalCollectResponseBean globalCollectResponseBean = new GlobalCollectResponseBean();
		GlobalCollectPayment globalCollectPayment = new GlobalCollectPayment();
		try {
			CreateHostedCheckoutResponse response = getGlobalCollectClient().merchant(globalCollectMerchantId)
					.hostedcheckouts().create(body);
			globalCollectPayment.setCreateHostedCheckoutResponse(response);
			globalCollectPayment.setStatus(PaymentConstants.GLOBALCOLLECT_STATUS_IN_PROGRESS);

			PaymentDetails paymentDetails = paymentRepository.findByOrderId(orders.getOrdersId());

			if (null == paymentDetails) {
				paymentDetails = new PaymentDetails();
			}
			paymentDetails.setOrderId(orders.getOrdersId());
			paymentDetails.setPaymentType(PaymentType.GLOBALCOLLECT);
			paymentDetails.setGlobalcollect(globalCollectPayment);
			paymentDetails.setStatus(PaymentStatus.CREATED);
			paymentDetails.setPaypal(null);

			paymentRepository.save(paymentDetails);

			globalCollectResponseBean
					.setHostedCheckoutId(globalCollectPayment.getCreateHostedCheckoutResponse().getHostedCheckoutId());
			globalCollectResponseBean.setPartialRedirectUrl(
					globalCollectPayment.getCreateHostedCheckoutResponse().getPartialRedirectUrl());
			globalCollectResponseBean.setStatus(PaymentConstants.GLOBALCOLLECT_STATUS_IN_PROGRESS);

		} catch (URISyntaxException e) {
			throw new PaymentApplicationException(
					"Exception occured while creating global collect hosted checkout page - ", e);
		}
		return globalCollectResponseBean;
	}

	@Override
	public GlobalCollectResponseBean getGlobalCollectCheckoutStatus(String orderId, String hostedCheckoutId,
			String userId, String registerType) throws PaymentApplicationException {
		GlobalCollectResponseBean globalCollectResponseBean = new GlobalCollectResponseBean();
		GlobalCollectPayment globalCollectPayment = new GlobalCollectPayment();
		try {
			GetHostedCheckoutResponse response = getGlobalCollectClient().merchant(globalCollectMerchantId)
					.hostedcheckouts().get(hostedCheckoutId);

			if (null != response) {
				PaymentDetails paymentDetails = paymentRepository.findByOrderId(orderId);
				globalCollectPayment = paymentDetails.getGlobalcollect();

				globalCollectPayment.setGetHostedCheckoutResponse(response);
				if (null != response.getStatus() && response.getStatus()
						.equalsIgnoreCase(PaymentConstants.GLOBALCOLLECT_STATUS_PAYMENT_CREATED)) {

					globalCollectPayment.setStatus(PaymentConstants.GLOBALCOLLECT_STATUS_PAYMENT_CREATED);

					if (null != paymentDetails) {
						paymentDetails.setPaymentType(PaymentType.GLOBALCOLLECT);
						paymentDetails.setGlobalcollect(globalCollectPayment);
						if (response.getCreatedPaymentOutput().getPayment().getStatus()
								.equalsIgnoreCase("PENDING_APPROVAL")) {
							paymentDetails.setStatus(PaymentStatus.COMPLETED);
						} else {
							paymentDetails.setStatus(PaymentStatus.FAILED);
						}
						paymentDetails.setPaypal(null);
						paymentRepository.save(paymentDetails);

						setGlobalCollectResponse(globalCollectResponseBean, globalCollectPayment,
								response.getCreatedPaymentOutput().getPayment().getStatus());
					}

					if (PaymentConstants.USER_TYPE_REGISTER.equalsIgnoreCase(registerType)) {
						saveCard(userId, response);
					}
				} else {
					globalCollectResponseBean.setStatus(PaymentConstants.GLOBALCOLLECT_STATUS_IN_PROGRESS);
				}
			}
		} catch (URISyntaxException e) {
			throw new PaymentApplicationException(
					"Exception occured while getting global collect hosted checkout status - ", e);
		}
		return globalCollectResponseBean;
	}

	@Override
	public Collection<CardDetails> getCardToken(String userId) {
		return cardRepository.findByUserId(userId);
	}

	private void saveCard(String userId, GetHostedCheckoutResponse response) throws URISyntaxException {
		String token = response.getCreatedPaymentOutput().getTokens();
		if (null != token) {
			TokenResponse tokenResponse = getGlobalCollectClient().merchant(globalCollectMerchantId).tokens()
					.get(token);

			if (null != tokenResponse) {
				CardDetails cardDetails = cardRepository.findByUserIdAndToken(userId, token);

				if (null == cardDetails) {
					cardDetails = new CardDetails();
					cardDetails.setCardId(UUID.randomUUID().toString());
					cardDetails.setToken(token);
					cardDetails.setUserId(userId);
					cardDetails.setCardNumber(response.getCreatedPaymentOutput().getPayment().getPaymentOutput()
							.getCardPaymentMethodSpecificOutput().getCard().getCardNumber());
					cardDetails.setExpiryDate(response.getCreatedPaymentOutput().getPayment().getPaymentOutput()
							.getCardPaymentMethodSpecificOutput().getCard().getExpiryDate());
					cardDetails
							.setCardType(getGlobalCollectCardMap().get(response.getCreatedPaymentOutput().getPayment()
									.getPaymentOutput().getCardPaymentMethodSpecificOutput().getPaymentProductId()));

					cardRepository.save(cardDetails);
				}
			}
		}
	}

	private void setGlobalCollectResponse(GlobalCollectResponseBean globalCollectResponseBean,
			GlobalCollectPayment globalCollectPayment, String paymentResponseStatus) {
		globalCollectResponseBean
				.setHostedCheckoutId(globalCollectPayment.getCreateHostedCheckoutResponse().getHostedCheckoutId());
		globalCollectResponseBean
				.setPartialRedirectUrl(globalCollectPayment.getCreateHostedCheckoutResponse().getPartialRedirectUrl());

		Long amount = globalCollectPayment.getGetHostedCheckoutResponse().getCreatedPaymentOutput().getPayment()
				.getPaymentOutput().getAmountOfMoney().getAmount();
		globalCollectResponseBean.setAmount(String.valueOf(Double.valueOf(amount) / 100));
		globalCollectResponseBean.setPaymentMethod(globalCollectPayment.getGetHostedCheckoutResponse()
				.getCreatedPaymentOutput().getPayment().getPaymentOutput().getPaymentMethod());

		if (globalCollectPayment.getGetHostedCheckoutResponse().getCreatedPaymentOutput().getPayment()
				.getPaymentOutput().getPaymentMethod().equalsIgnoreCase("card")) {
			globalCollectResponseBean.setCardNumber(
					globalCollectPayment.getGetHostedCheckoutResponse().getCreatedPaymentOutput().getPayment()
							.getPaymentOutput().getCardPaymentMethodSpecificOutput().getCard().getCardNumber());
			globalCollectResponseBean.setCardType(getGlobalCollectCardMap()
					.get(globalCollectPayment.getGetHostedCheckoutResponse().getCreatedPaymentOutput().getPayment()
							.getPaymentOutput().getCardPaymentMethodSpecificOutput().getPaymentProductId()));
		}

		globalCollectResponseBean.setCurrency(globalCollectPayment.getGetHostedCheckoutResponse()
				.getCreatedPaymentOutput().getPayment().getPaymentOutput().getAmountOfMoney().getCurrencyCode());

		if (paymentResponseStatus.equalsIgnoreCase("PENDING_APPROVAL")) {
			globalCollectResponseBean.setStatus(PaymentConstants.GLOBALCOLLECT_STATUS_PAYMENT_CREATED);
		} else {
			globalCollectResponseBean.setStatus(PaymentConstants.GLOBALCOLLECT_STATUS_PAYMENT_FAILED);
		}
	}

	private Client getGlobalCollectClient() throws URISyntaxException {
		String apiKeyId = System.getProperty(PaymentConstants.GLOBALCOLLECT_CONNECT_API_APIKEYID, globalCollectApiKey);
		String secretApiKey = System.getProperty(PaymentConstants.GLOBALCOLLECT_CONNECT_API_SECRETAPIKEY,
				globalCollectSecretKey);

		URL propertiesUrl = getClass().getResource(PaymentConstants.GLOBALCOLLECT_CONFIGURATION_FILE);
		CommunicatorConfiguration configuration = Factory.createConfiguration(propertiesUrl.toURI(), apiKeyId,
				secretApiKey);
		return Factory.createClient(configuration);
	}

	private Map<Integer, String> getGlobalCollectCardMap() {
		Map<Integer, String> map = new HashMap<>();
		map.put(1, "Visa");
		map.put(2, "American Express");
		map.put(3, "MasterCard");
		return map;
	}

	private CreateHostedCheckoutRequest generateCreateGlobalCollectRequest(Orders orders,
			HostedCheckoutSpecificInput hostedCheckoutSpecificInput,
			PaymentProductFiltersHostedCheckout paymentProductFiltersHostedCheckout) {
		hostedCheckoutSpecificInput.setLocale(PaymentConstants.LOCALE_UK);
		hostedCheckoutSpecificInput.setVariant(globalCollectVariantId);

		List<Integer> products = new ArrayList<>();
		products.add(1);
		products.add(2);
		products.add(3);

		PaymentProductFilter paymentProductFilter = new PaymentProductFilter();
		paymentProductFilter.setProducts(products);

		paymentProductFiltersHostedCheckout.setRestrictTo(paymentProductFilter);

		hostedCheckoutSpecificInput.setPaymentProductFilters(paymentProductFiltersHostedCheckout);
		hostedCheckoutSpecificInput.setShowResultPage(false);
		hostedCheckoutSpecificInput.setReturnCancelState(true);
		hostedCheckoutSpecificInput.setReturnUrl(globalCollectReturnUrl);

		Double amount = orders.getOrdertotal() * 100;

		AmountOfMoney amountOfMoney = new AmountOfMoney();
		amountOfMoney.setAmount(amount.longValue());
		amountOfMoney.setCurrencyCode(PaymentConstants.CURRENCY_UK);

		com.ingenico.connect.gateway.sdk.java.domain.definitions.Address billingAddress = new com.ingenico.connect.gateway.sdk.java.domain.definitions.Address();
		billingAddress.setCountryCode("GB");

		Customer customer = new Customer();
		customer.setBillingAddress(billingAddress);
		customer.setMerchantCustomerId(String.valueOf(orders.getShippingaddress().getAddressId()));

		Order order = new Order();
		order.setAmountOfMoney(amountOfMoney);
		order.setCustomer(customer);

		CreateHostedCheckoutRequest body = new CreateHostedCheckoutRequest();
		body.setHostedCheckoutSpecificInput(hostedCheckoutSpecificInput);
		body.setOrder(order);
		return body;
	}
}
