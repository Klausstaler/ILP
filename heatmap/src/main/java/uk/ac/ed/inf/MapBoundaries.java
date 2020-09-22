package uk.ac.ed.inf;

public enum MapBoundaries {

    NORTHWEST (55.946233, -3.192473),
    NORTHEAST (55.946233, -3.184319),
    SOUTHWEST ( 55.942617, -3.192473),
    SOUTHEAST (55.942617, -3.184319);

    private final double latitude;
    private final double longitude;
    MapBoundaries(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
