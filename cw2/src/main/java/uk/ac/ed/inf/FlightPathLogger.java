package uk.ac.ed.inf;

import org.locationtech.jts.geom.Coordinate;

import java.io.IOException;

public class FlightPathLogger extends DroneLogger {

    private int lineNbr = 1;
    private int direction;
    private String location;

    public FlightPathLogger(Coordinate initialPos, String date) throws IOException {
        super(initialPos, "flightpath-" + date + ".txt");
        System.out.println("Flightpath logger initialized...");
    }

    @Override
    public void log(Coordinate newPos, Sensor read_sensor) {
        this.location = read_sensor == null ? "null" : read_sensor.getLocation();
        this.direction = Angles.calculateAngle(this.position, newPos);
        if (direction > 350 || direction < 0 || direction % 10 != 0) {
            throw new IllegalArgumentException("The direction of the drone is not a multiple of " +
                    "10!");
        }
        String line = this.format(newPos);
        if (this.lineNbr != 1) {
            line = "\n" + line;
        }

        try {
            this.file.write(line);
        } catch (IOException exception) {
            System.out.println("ERROR SAVING FLIGHT PATH!");
        }

        this.position = newPos;
        this.lineNbr++;
    }

    private String format(Coordinate newPos) {
        // replace comma decimal sep by dot
        String currX = String.valueOf(this.position.getX()).replace(',', '.');
        String currY = String.valueOf(this.position.getY()).replace(',', '.');
        String newX = String.valueOf(newPos.getX()).replace(',', '.');
        String newY = String.valueOf(newPos.getY()).replace(',', '.');

        return String.format("%d,%s,%s,%d,%s,%s,%s", this.lineNbr,
                currX, currY, this.direction, newX, newY, this.location);
    }

    @Override
    public void close() {
        System.out.println("FlightPathLogger closing file..");
        try {
            this.file.close();
        } catch (Exception e) {
            System.out.println("ERROR CLOSING FLIGHTPATHLOGGER");
        }
    }
}