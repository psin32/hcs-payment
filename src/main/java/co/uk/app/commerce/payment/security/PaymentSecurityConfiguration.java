package co.uk.app.commerce.payment.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Component
@PropertySource("classpath:application.properties")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSecurityConfiguration {

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Value("${jwt.payment.expiration.time}")
	private int jwtPaymentExpirationTime;

	@Value("${jwt.token.prefix}")
	private String jwtTokenPrefix;

	@Value("${jwt.header}")
	private String jwtHeader;

	@Value("${jwt.guest.token.header}")
	private String jwtGuestTokenHeader;

	@Value("${jwt.audience}")
	private String jwtAudience;

}
