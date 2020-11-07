package uk.ac.ed.inf;

import org.locationtech.jts.geom.Coordinate;

import java.io.IOException;

public class FlightPathLogger extends DroneLogger {

    private int lineNbr = 1;

    public FlightPathLogger(Coordinate initialPos, String date) throws IOException {
        super(initialPos, "flightpath-" + date + ".txt");
        System.out.println("Flightpath logger initialized...");
    }

    @Override
    public void log(Coordinate newPos, Sensor read_sensor) throws IOException {
        int direction = Angles.calculateAngle(this.position, newPos);
        if (direction > 350 || direction < 0 || direction % 10 != 0) {
            throw new IllegalArgumentException("The direction of the drone is not a multiple of " +
                    "10!");
        }
        String location = read_sensor == null ? "null" : read_sensor.getLocation();
        String line = this.format(newPos, direction, location);
        this.file.write(line);

        this.position = newPos;
        this.lineNbr++;
    }

    @Override
    public void close() throws IOException {
        System.out.println("FlightPathLogger closing file..");
        System.out.println("Written " + (this.lineNbr - 1) + " lines!");
        this.file.close();
    }

    private String format(Coordinate newPos, int direction, String location) {
        // replace comma decimal sep by dot
        String currX = String.valueOf(this.position.getX()).replace(',', '.');
        String currY = String.valueOf(this.position.getY()).replace(',', '.');
        String newX = String.valueOf(newPos.getX()).replace(',', '.');
        String newY = String.valueOf(newPos.getY()).replace(',', '.');

        return String.format("%d,%s,%s,%d,%s,%s,%s\n", this.lineNbr,
                currX, currY, direction, newX, newY, location);
    }
}