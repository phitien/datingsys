package dating.util;

public class FloatRange {
	public float from = -1;
	public float to = -1;

	public boolean isValid() {
		return (from > 0 && to < 0)// only set the value of from
				|| (from < 0 && to > 0)// only set the value of to
				|| (from > 0 && to > from);// set both the values of from & to;
	}

	public boolean isInRange(float point) {
		if (point < 0) {
			return false;
		}
		if (this.isValid()) {
			if (from < 0) {
				return point < to;
			} else if (to < 0) {
				return point > from;
			} else {
				return point > from && point < to;
			}
		}
		return true;
	}

	public boolean hasIntersection(FloatRange range) {
		if (!range.isValid()) {
			return false;
		}
		if (this.isValid()) {
			if (from < 0) {
				return range.isInRange(to);
			} else if (to < 0) {
				return range.isInRange(from);
			} else {
				return this.isInRange(range.from) || this.isInRange(range.to);
			}
		}
		return true;
	}
}
