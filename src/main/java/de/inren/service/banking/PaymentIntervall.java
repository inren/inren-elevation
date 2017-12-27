package de.inren.service.banking;

public enum PaymentIntervall {
	NONE(0), MONTHLY(1), QUARTERLY(3), HALF_YEARLY(6), YEARLY(12);

	private final int value;

	private PaymentIntervall(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

}
