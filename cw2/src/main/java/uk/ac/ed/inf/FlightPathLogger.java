package uk.ac.ed.inf;

import com.mapbox.geojson.Point;

import java.io.IOException;

public class FlightPathLogger extends DroneLogger {

    private int lineNbr = 1;

    public FlightPathLogger(Point initialPos, String date) throws IOException {
        super(initialPos, "flightpath"+date+".txt");
    }

    @Override
    public void log(Point newPos, Sensor read_sensor) throws IOException {
        int direction = this.calculateAngle(newPos);
        System.out.println("Direction is:" + direction);
        if (direction > 350 || direction < 0 || direction % 10 != 0) {
            throw new IllegalArgumentException("The direction of the drone is not a multiple of " +
                    "10!");
        }

        String line = String.format("%d,%f,%f,%d,%f,%f,%s\n",this.lineNbr,
                this.position.longitude(), this.position.latitude(), direction, newPos.longitude(),
                newPos.latitude(), read_sensor.getLocation());
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

    private int calculateAngle(Point otherPos) {
        // TODO: extract this method and put it in appropriate class
        double dy = otherPos.longitude() - this.position.longitude();
        double dx = otherPos.latitude() - this.position.latitude();
        int angle = (int) Math.round(Math.toDegrees(Math.atan2(dy, dx)));

        if(angle < 0){
            angle += 360;
        }

        return angle;
    }
}