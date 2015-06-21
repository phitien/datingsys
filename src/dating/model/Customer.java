package dating.model;

import dating.util.Model;

public abstract class Customer implements Model {

	/**
	 * Constructor
	 * 
	 * Automatically add to dating.service.Customer
	 */
	public Customer(String username) {
		this.username = username;
		dating.service.Customer.customers.add(this);
	}

	/**
	 * Type of customer (advertiser or responder)
	 * 
	 * @author sgp0458
	 *
	 */
	public enum Type {
		ADVERTISER, RESPONDER
	}

	/**
	 * Gender enum
	 * 
	 * @author sgp0458
	 *
	 */
	public enum Gender {
		MALE, FEMALE
	}

	protected String username;

	public String getUsername() {
		return username;
	}

	protected Type type;

	public Type getType() {
		return type;
	}

	public String password = "";
	public Gender gender;
	public float age = -1;
	public IncomeRange incomeRange = new IncomeRange();

	public boolean isValid() {
		if (this.gender == null || this.age <= 0) {
			return false;
		}
		return true;
	}
}
