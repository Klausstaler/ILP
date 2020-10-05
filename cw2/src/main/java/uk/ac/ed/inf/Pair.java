package uk.ac.ed.inf;

public class PathInfo<T, U> {
    public final T path;
    public final U distance;

    public PathInfo(T path, U distance) {
        this.path = path;
        this.distance = distance;
    }
}
