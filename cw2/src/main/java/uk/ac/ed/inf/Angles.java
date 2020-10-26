package uk.ac.ed.inf;

import org.locationtech.jts.geom.Coordinate;

public class Angles {

    public static int calculateAngle(Coordinate from, Coordinate to) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        double angle = Math.toDegrees(Math.atan2(dy, dx));
        angle += angle < 0 ? 360 : 0;
        return (int) Math.round(angle / 10) * 10;
    }

    public static Coordinate calculateNewPos(Coordinate from, double distance, int angle) {
        double new_x = from.x + Math.cos(Math.toRadians(angle)) * distance;
        double new_y = from.y + Math.sin(Math.toRadians(angle)) * distance;
        return new Coordinate(new_x, new_y);
    }

    public static int adjustAngle(int initialAngle, int otherAngle, boolean subtract) {
        int newAngle = subtract ? initialAngle - otherAngle : initialAngle + otherAngle;
        newAngle = newAngle < 0 ? (newAngle % 360) + 360 : newAngle % 360;
        return newAngle;
    }
}
