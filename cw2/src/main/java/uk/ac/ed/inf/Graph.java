package uk.ac.ed.inf;

public interface Graph {

    double getDistance(int fromIdx, int toIdx);
    double getHeuristic(int fromIdx, int toIdx);
    int getSize();
}
