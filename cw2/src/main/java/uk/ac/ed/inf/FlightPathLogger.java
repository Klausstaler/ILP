package uk.ac.ed.inf;

import org.locationtech.jts.geom.Coordinate;

import java.io.IOException;

public class FlightPathLogger extends DroneLogger {

    private int lineNbr = 1;

    public FlightPathLogger(Coordinate initialPos, String date) throws IOException {
        super(initialPos, "flightpath"+date+".txt");
        System.out.println("Flightpath logger initialized...");
    }

    @Override
    public void log(Coordinate newPos, Sensor read_sensor) throws IOException {
        int direction = this.calculateAngle(newPos);
        System.out.println("Direction is:" + direction);
        if (direction > 350 || direction < 0 || direction % 10 != 0) {
            throw new IllegalArgumentException("The direction of the drone is not a multiple of " +
                    "10!");
        }
        String location = read_sensor == null ? null : read_sensor.getLocation();

        String line = String.format("%d,%f,%f,%d,%f,%f,%s\n",this.lineNbr,
                this.position.getX(), this.position.getY(), direction, newPos.getX(),
                newPos.getY(), location);
        if (this.lineNbr != 1) {
            line = "\n" + line;
        }
        this.file.write(line);

        this.position = newPos;
        this.lineNbr++;
    }

    @Override
    public void close() throws IOException {
        this.file.close();
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