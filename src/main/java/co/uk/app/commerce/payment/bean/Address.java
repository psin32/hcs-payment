package co.uk.app.commerce.payment.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Address {

	private String id;
	
	@JsonProperty("address_id")
	private Long addressId;

	@JsonProperty("users_id")
	private Long usersId;

	private String addresstype;

	private String status;

	private Integer isprimary;

	private Integer selfaddress;

	private String title;

	private String firstname;

	private String lastname;

	private String email1;

	private String email2;

	private String phone1;

	private String phone2;

	private String nickname;

	private String address1;

	private String address2;

	private String address3;

	private String city;

	private String state;

	private String zipcode;

	private String country;
}
