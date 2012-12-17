package be.virtualsushi.wanuus.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
public class TweetObject extends BaseEntity implements HasQuantity {

	private static final long serialVersionUID = 2576971152325807097L;

	private static final int IMAGE_QUANTITY_FACTOR = 3;
	private static final int URL_QUANTITY_FACTOR = 2;
	private static final int HASHTAG_QUANTITY_FACTOR = 1;

	@Column(name = "VALUE", length = 2048)
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

	public int getQuantityFactor() {
		switch (type) {
		case IMAGE:
			return IMAGE_QUANTITY_FACTOR;
		case URL:
			return URL_QUANTITY_FACTOR;
		default:
			return HASHTAG_QUANTITY_FACTOR;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		TweetObject other = (TweetObject) obj;
		if (type != other.type)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
