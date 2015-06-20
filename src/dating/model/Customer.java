package dating.model;

import dating.util.Model;


public abstract class Customer implements Model {

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

	public String username;
	public String password = "";
	public Gender gender;
	protected Type type;
	public float age = -1;
	public IncomeRange incomeRange;

	public Type getType() {
		return type;
	}

	public boolean isValid() {
		if (this.username == null || this.gender == null || this.age <= 0) {
			return false;
		}
		return true;
	}
}
