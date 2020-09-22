package uk.ac.ed.inf;

public enum MarkerProperties {

    SAFE1 ("#00ff00", "lighthouse"),
    SAFE2 ("#40ff00", "lighthouse"),
    SAFE3 ("#80ff00", "lighthouse"),
    SAFE4 ("#c0ff00", "lighthouse"),
    DANGER1 ("#ffc000", "danger"),
    DANGER2 ("#ff8000", "danger"),
    DANGER3 ("#ff4000", "danger"),
    DANGER5 ("#ff0000", "danger"),
    LOWBATTERY ("#000000", "cross"),
    NOTVISITED ("#aaaaaa", "");

    private static final String ILLEGAL_AIR_POLLUTION = "Air pollution level has to be between 0 " +
            "and 256!";
    MarkerProperties(String rgbString, String markerSymbol) {
    }

    public static MarkerProperties fromAirPollution(double pollution) {
        if (pollution < 0 || pollution > 256) {
            throw new IllegalArgumentException(ILLEGAL_AIR_POLLUTION);
        }
        if (pollution < 32) {
            return MarkerProperties.SAFE1;
        }
        else if (pollution < 64) {
            return MarkerProperties.SAFE2;
        }
        else if (pollution < 96) {
            return MarkerProperties.SAFE3;
        }
        else if (pollution < 128) {
            return MarkerProperties.SAFE4;
        }
        else if (pollution < 160) {
            return MarkerProperties.DANGER1;
        }
        else if (pollution < 192) {
            return MarkerProperties.DANGER2;
        }
        else if (pollution < 224) {
            return MarkerProperties.DANGER3;
        }
        else {
            return MarkerProperties.DANGER5;
        }
    }
}
