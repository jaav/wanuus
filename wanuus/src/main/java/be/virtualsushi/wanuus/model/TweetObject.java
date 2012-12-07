package be.virtualsushi.wanuus.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
public class TweetObject extends BaseEntity implements HasQuantity {

	private static final long serialVersionUID = 2576971152325807097L;

	@Column(name = "VALUE")
	private String value;

	@Column(name = "QUANTITY")
	private int quantity;

	@Column(name = "TYPE")
	@Enumerated(EnumType.STRING)
	private TweetObjectTypes type;

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public TweetObjectTypes getType() {
		return type;
	}

	public void setType(TweetObjectTypes type) {
		this.type = type;
	}

	@Override
	public void increaseQuantity(int amount) {
		quantity += amount;
	}

}
