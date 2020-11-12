package uk.ac.ed.inf;

import org.locationtech.jts.geom.Coordinate;

/**
 * Utility class to handle operations involving angles.
 */
public class Angles {

    /**
     *
     * @param from The coordinate from which we want to start.
     * @param to THe coordinate where we want to go.
     * @return Angle between from and to, rounded to the nearest multiple of 10.
     */
    public static int calculateAngle(Coordinate from, Coordinate to) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        double angle = Math.toDegrees(Math.atan2(dy, dx));
        angle += angle < 0 ? 360 : 0;
        return (int) Math.round(angle / 10) * 10;
    }


    /**
     * Calculates a new coordinate we get if we move from a coordinate in a specified angle and
     * distance .
     * @param distance The distance between the new coordinate and the returned coordinate.
     * @param angle The angle between the new coordinate and the returned coordinate.
     * @return A new coordinate
     */
    public static Coordinate calculateNewCoordinate(Coordinate from, double distance, int angle) {
        double new_x = from.x + Math.cos(Math.toRadians(angle)) * distance;
        double new_y = from.y + Math.sin(Math.toRadians(angle)) * distance;
        return new Coordinate(new_x, new_y);
    }


    /**
     * Adjusts the initialAngle by either adding or subtracting the otherAngle.
     */
    public static int adjustAngle(int initialAngle, int otherAngle, boolean subtract) {
        int newAngle = subtract ? initialAngle - otherAngle : initialAngle + otherAngle;
        newAngle = newAngle < 0 ? (newAngle % 360) + 360 : newAngle % 360;
        return newAngle;
    }
}
