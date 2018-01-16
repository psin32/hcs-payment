package co.uk.app.commerce.payment.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "card")
public class CardDetails {

	@Id
	private String id;

	@JsonIgnore
	private String token;

	@JsonIgnore
	private String userId;

	private String cardId;

	private String cardNumber;

	private String expiryDate;
	
	private String cardType;
}
