package co.uk.app.commerce.payment.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Orders {

	private String id;

	private String ordersId;

	private String usersId;

	private String status;

	private Address shippingaddress;

	private Address billingaddress;

	private List<Items> items;

	private String shippingmethod;

	private Double shippingcharges;

	private Double subtotal;

	private Double totaldiscount;

	private Double ordertotal;

	private String formattedShippingcharges;

	private String formattedSubtotal;

	private String formattedTotaldiscount;

	private String formattedOrdertotal;

	private String timeplaced;
}
