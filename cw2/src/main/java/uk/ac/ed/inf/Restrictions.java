package uk.ac.ed.inf;

public enum Restrictions {
    MIN_BATTERY(10.0);

    private final double value;

    Restrictions(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

}
