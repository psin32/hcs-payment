package co.uk.app.commerce.payment.bean;

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
public class Items {

	private String partnumber;

	private String name;

	private String image;

	private Double listprice;

	private String currency;

	private Double offerprice;

	private Integer quantity;

	private Double itemtotal;

	private String formattedListprice;

	private String formattedOfferprice;

	private String formattedItemtotal;

	private String url;

}
