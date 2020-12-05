package uk.ac.ed.inf;

import org.locationtech.jts.geom.Coordinate;

import java.io.IOException;

/**
 * Used to log the flight path in txt format.
 */
public class FlightPathLogger extends DroneLogger {

    private static final int MAX_LINES = 150; // maximum number of lines we are allowed to have
    private int lineNbr = 1; // current line number.

    public FlightPathLogger(Coordinate initialPos, String date) throws IOException {
        super(initialPos, "flightpath-" + date + ".txt");
        System.out.println("Flightpath logger initialized...");
    }

    /**
     * Logs the current previous position, current position, as well as the angle between and the
     * location of the sensor if one was read.
     *
     * @param newPos      the new position we need to log
     * @param read_sensor The sensor read during move
     * @throws IOException
     */
    @Override
    public void log(Coordinate newPos, Sensor read_sensor) throws IOException {
        int direction = Angles.calculateAngle(this.position, newPos);
        if (direction > 350 || direction < 0 || direction % 10 != 0) {
            throw new IllegalArgumentException("The direction of the drone is not a multiple of " +
                    "10!");
        }
        if (this.lineNbr > MAX_LINES)
            throw new AssertionError("More than 150 lines need to be written!");
        String sensorLocation = read_sensor == null ? "null" : read_sensor.getLocation();
        String line = this.format(newPos, direction, sensorLocation);
        this.file.write(line);

        this.position = newPos;
        this.lineNbr++;
    }

    /**
     * Closes the file, ends logging.
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        System.out.println("FlightPathLogger closing file..");
        System.out.println("Written " + (this.lineNbr - 1) + " lines!");
        this.file.close();
    }

    /**
     * Formats the logging output correctly.
     *
     * @param newPos         the new position we need to log
     * @param direction      The direction in which we headed
     * @param sensorLocation The what3words address of the sensor read
     * @return A String representing one line of logging output
     */
    private String format(Coordinate newPos, int direction, String sensorLocation) {
        // replace comma decimal sep by dot
        String currX = String.valueOf(this.position.getX()).replace(',', '.');
        String currY = String.valueOf(this.position.getY()).replace(',', '.');
        String newX = String.valueOf(newPos.getX()).replace(',', '.');
        String newY = String.valueOf(newPos.getY()).replace(',', '.');

        return String.format("%d,%s,%s,%d,%s,%s,%s\n", this.lineNbr,
                currX, currY, direction, newX, newY, sensorLocation);
    }
}