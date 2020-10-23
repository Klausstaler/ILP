package uk.ac.ed.inf;

import org.locationtech.jts.geom.Coordinate;

import java.io.IOException;

public class FlightPathLogger extends DroneLogger {

    private int lineNbr = 1;

    public FlightPathLogger(Coordinate initialPos, String date) throws IOException {
        super(initialPos, "flightpath-"+date+".txt");
        System.out.println("Flightpath logger initialized...");
    }

    @Override
    public void log(Coordinate newPos, Sensor read_sensor) {
        int direction = this.calculateAngle(newPos);
        if (direction > 350 || direction < 0 || direction % 10 != 0) {
            throw new IllegalArgumentException("The direction of the drone is not a multiple of " +
                    "10!");
        }
        String location = read_sensor == null ? "null" : read_sensor.getLocation();
        // replace comma decimal sep by dot
        String currX = String.valueOf(this.position.getX()).replace(',', '.');
        String currY = String.valueOf(this.position.getY()).replace(',', '.');
        String newX = String.valueOf(newPos.getX()).replace(',', '.');
        String newY = String.valueOf(newPos.getY()).replace(',', '.');

        String line = String.format("%d,%s,%s,%d,%s,%s,%s",this.lineNbr,
                currX, currY, direction, newX, newY, location);
        if (this.lineNbr != 1) {
            line = "\n" + line;
        }

        try {
            this.file.write(line);
        }
        catch (IOException exception) {
            System.out.println("ERROR SAVING FLIGHT PATH!");
        }

        this.position = newPos;
        this.lineNbr++;
    }

    @Override
    public void close() {
        System.out.println("CLOSING FILE");
        try {
            this.file.close();
        }
        catch (Exception e) {
            System.out.println("ERROR CLOSING FLIGHTPATHLOGGER");
        }
    }

    private int calculateAngle(Coordinate otherPos) {
        // TODO: extract this method and put it in appropriate class
        double dx = otherPos.getX() - this.position.getX();
        double dy = otherPos.getY() - this.position.getY();
        int angle = (int) Math.round(Math.toDegrees(Math.atan2(dy, dx)));

        if(angle < 0){
            angle += 360;
        }

        return angle;
    }
}