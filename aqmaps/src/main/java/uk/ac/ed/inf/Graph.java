package uk.ac.ed.inf;

import org.locationtech.jts.geom.Coordinate;

public interface Graph {

    double getDistance(int fromIdx, int toIdx);

    void setDistance(int fromIdx, int toIdx, double distance);

    Coordinate getCoordinate(int i);

    int getSize();
}
