package dating.model;

import dating.model.Customer.Gender;


public class PartnerCriteria {
	public Gender gender = Gender.MALE;
	public AgeRange ageRange = new AgeRange();
	public IncomeRange incomeRange = new IncomeRange();

	public boolean match(Responder responder) {
		if (responder.isValid() && this.gender == responder.gender) {
			if (this.ageRange != null
					&& !this.ageRange.isInRange(responder.age)) {
				return false;
			}
			if (this.incomeRange != null
					&& !this.incomeRange.hasIntersection(responder.incomeRange)) {
				return false;
			}
			return true;
		}
		return false;
	}
}
