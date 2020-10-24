package uk.ac.ed.inf;

import org.locationtech.jts.geom.Coordinate;

public class Angles {

    public static int calculateAngle(Coordinate from, Coordinate to) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        double angle = Math.toDegrees(Math.atan2(dy, dx));

        if (angle < 0) {
            angle += 360;
        }
        return (int) Math.round(angle / 10) * 10;
    }
}
