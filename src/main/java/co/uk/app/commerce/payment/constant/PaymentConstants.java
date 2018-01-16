package co.uk.app.commerce.payment.constant;

public interface PaymentConstants {

	String CURRENCY_UK = "GBP";

	String PAYMENT_STATUS_CREATED = "created";

	String PAYMENT_STATUS_APPROVED = "approved";

	String USER_TYPE_GUEST = "G";

	String USER_TYPE_REGISTER = "R";

	String PAYPAL_PAYMENT_NAME = "paypal";

	String PAYPAL_PAYMENT_INTENT = "sale";

	String JWT_CLAIM_USER_ID = "userId";

	String JWT_CLAIM_ORIGINATED_BY = "originatedBy";

	String JWT_CLAIM_REGISTER_TYPE = "registertype";

	String REQUEST_HEADER_USER_ID = "USER_ID";

	String REQUEST_HEADER_ORIGINATED_BY = "ORIGINATED_BY";

	String REQUEST_HEADER_REGISTER_TYPE = "REGISTER_TYPE";

	String GLOBALCOLLECT_CONNECT_API_APIKEYID = "connect.api.apiKeyId";

	String GLOBALCOLLECT_CONNECT_API_SECRETAPIKEY = "connect.api.secretApiKey";

	String GLOBALCOLLECT_CONFIGURATION_FILE = "/global-collect.properties";

	String LOCALE_UK = "en_GB";

	String GLOBALCOLLECT_STATUS_IN_PROGRESS = "IN_PROGRESS";

	String GLOBALCOLLECT_STATUS_PAYMENT_CREATED = "PAYMENT_CREATED";

	String REQUEST_ORIGINATED_BY_ORDER_APPLICATION = "ORDER_APPLICATION";

}
