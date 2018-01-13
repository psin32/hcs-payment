package co.uk.app.commerce.payment.constant;

public interface PaymentConstants {

	String CURRENCY_UK = "GBP";

	String PAYMENT_STATUS_CREATED = "created";

	String PAYMENT_STATUS_APPROVED = "approved";

	String USER_TYPE_GUEST = "G";

	String USER_TYPE_REGISTER = "R";

	String PAYPAL_PAYMENT_NAME = "paypal";

	String PAYPAL_PAYMENT_INTENT = "sale";

	String REQUEST_HEADER_GUEST_USER_ID = null;

	Object JWT_CLAIM_USER_ID = null;

	String REQUEST_HEADER_USER_ID = null;

	String REQUEST_HEADER_REGISTER_TYPE = null;

	Object JWT_CLAIM_REGISTER_TYPE = null;

	String GLOBALCOLLECT_CONNECT_API_APIKEYID = "connect.api.apiKeyId";

	String GLOBALCOLLECT_CONNECT_API_SECRETAPIKEY = "connect.api.secretApiKey";

	String GLOBALCOLLECT_CONFIGURATION_FILE = "/global-collect.properties";

	String LOCALE_UK = "en_GB";

}
