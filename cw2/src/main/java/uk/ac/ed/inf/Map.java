package uk.ac.ed.inf;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.operation.polygonize.Polygonizer;
import uk.ac.ed.inf.backend.ObstacleService;

import java.util.*;

public class Map {

    private final GeometryFactory geometryFactory = new GeometryFactory();
    private Polygon playArea;


    public Map(ObstacleService obstacleService) {

        Coordinate[] boundaries = {new Coordinate(-3.192473, 55.946233), //NW
                new Coordinate(-3.184319, 55.946233), // NE
                new Coordinate(-3.184319, 55.942617), // SE
                new Coordinate(-3.192473, 55.942617), // SW
                new Coordinate(-3.192473, 55.946233)}; // NW
        LinearRing shell = this.geometryFactory.createLinearRing(boundaries);
        this.playArea = this.geometryFactory.createPolygon(shell);

        LinearRing[] obstacles = obstacleService.getObstacles().toArray(new LinearRing[0]);
        this.addObstacles(obstacles);

        System.out.println(this.playArea.isValid());
        System.out.println("Map init ended");
    }

    public boolean inAllowedArea(com.mapbox.geojson.Point position) {
        Point point = this.geometryFactory.createPoint(new Coordinate(position.longitude(),
                        position.latitude()));

        return this.inAllowedArea(point);
    }

    public boolean inAllowedArea(Point position) {
        return this.playArea.intersects(position);
    }

    public void addObstacles(Coordinate[]... obstacles) {

        List<LinearRing> newObstacles = new ArrayList<>();
        for (Coordinate[] obstacle: obstacles) {
            newObstacles.add(this.geometryFactory.createLinearRing(obstacle));
        }
        this.addObstacles(newObstacles.toArray(new LinearRing[0]));
    }

    public void addObstacles(LinearRing... obstacles) {
        obstacles = this.fitObstacles(obstacles);
        Geometry boundaries = this.playArea.getBoundary();
        LinearRing shell = (LinearRing) boundaries.getGeometryN(0);
        List<LinearRing> holes = new ArrayList<>(Arrays.asList(obstacles));

        for(int i = 1; i < boundaries.getNumGeometries(); i++) {
            holes.add((LinearRing) boundaries.getGeometryN(i));
        }

        this.playArea = this.geometryFactory.createPolygon(shell, holes.toArray(new LinearRing[0]));
    }

    private LinearRing[] fitObstacles(LinearRing... obstacles) {
        List<LinearRing> obstaclesInBounds = new ArrayList<>();

        for (LinearRing obstacle : obstacles) {
            if (this.playArea.covers(obstacle)) {
                obstaclesInBounds.add(obstacle);
            }
            else {
                this.alignBoundaries(obstacle);
            }
        }
        return obstaclesInBounds.toArray(new LinearRing[0]);
    }

    private void alignBoundaries(LinearRing obstacle) {
        double EPSILON = 0.000001;
        LinearRing currentShell = (LinearRing) this.playArea.getBoundary().getGeometryN(0);
        MultiLineString allBounds = (MultiLineString) this.playArea.intersection(obstacle);
        List<Coordinate> coordinates = new ArrayList<>(Arrays.asList(currentShell.getCoordinates()));
        coordinates.remove(coordinates.size()-1); // remove last element as its same as first

        List<LineString> orderedBounds = new ArrayList<>();
        for(int i = 0; i < allBounds.getNumGeometries(); i++) {
            LineString currBound = (LineString) allBounds.getGeometryN(i);
            Coordinate[] currCoords = currBound.getCoordinates();
            LineString prevBound = (orderedBounds.size() > 0) ?
            orderedBounds.get(orderedBounds.size()-1) : null;
            Coordinate[] prevCoords = prevBound != null ? prevBound.getCoordinates() : null;
            if (prevCoords == null || prevCoords[prevCoords.length-1].distance(currCoords[0]) < EPSILON)
                orderedBounds.add(currBound);
            else if (prevCoords[0].distance(currCoords[currCoords.length-1]) < EPSILON) {
                orderedBounds.remove(orderedBounds.size()-1);
                orderedBounds.add(currBound);
                orderedBounds.add(prevBound);
            }
        }

        List<Coordinate> boundCoords = new ArrayList<>();
        for(int i = 0; i < orderedBounds.size(); i++) {
            LineString bound = orderedBounds.get(i);
            if (i%2 == 0 ) {
                boundCoords.addAll(Arrays.asList(bound.getCoordinates()));
            }
            else {
                Coordinate[] wantedCoords = Arrays.copyOfRange(bound.getCoordinates(), 1,
                        bound.getCoordinates().length);
                boundCoords.addAll(Arrays.asList(wantedCoords));
            }
        }

        int closestIdx = 0;
        double minDist = 999999;
        for(int i = 0; i < coordinates.size(); i++) {
            Coordinate currCoord = coordinates.get(i);
            if (currCoord.distance(boundCoords.get(0)) < minDist) {
                closestIdx = i;
                minDist = currCoord.distance(boundCoords.get(0));
            }
        }
        for(Coordinate newCoord : boundCoords) {
            coordinates.add(++closestIdx, newCoord);
        }
        coordinates.add(coordinates.get(0));
        System.out.println("FINAL RING " + coordinates);
        Polygon test = this.geometryFactory.createPolygon(coordinates.toArray(new Coordinate[0]));
        
    }

    public List<LinearRing> getObstacles() {
        List<LinearRing> obstacles = new ArrayList<>();
        for(int i = 1; i < this.playArea.getBoundary().getNumGeometries(); i++ ) {
            obstacles.add( (LinearRing) this.playArea.getBoundary().getGeometryN(i));
        }
        return obstacles;
    }

    public static Geometry validate(Geometry geom){
        if(geom instanceof Polygon){
            if(geom.isValid()){
                geom.normalize(); // validate does not pick up rings in the wrong order - this will fix that
                return geom; // If the polygon is valid just return it
            }
            Polygonizer polygonizer = new Polygonizer();
            addPolygon((Polygon)geom, polygonizer);
            return toPolygonGeometry(polygonizer.getPolygons(), geom.getFactory());
        }else if(geom instanceof MultiPolygon){
            if(geom.isValid()){
                geom.normalize(); // validate does not pick up rings in the wrong order - this will fix that
                return geom; // If the multipolygon is valid just return it
            }
            Polygonizer polygonizer = new Polygonizer();
            for(int n = geom.getNumGeometries(); n-- > 0;){
                addPolygon((Polygon)geom.getGeometryN(n), polygonizer);
            }
            return toPolygonGeometry(polygonizer.getPolygons(), geom.getFactory());
        }else{
            return geom; // In my case, I only care about polygon / multipolygon geometries
        }
    }

    /**
     * Add all line strings from the polygon given to the polygonizer given
     *
     * @param polygon polygon from which to extract line strings
     * @param polygonizer polygonizer
     */
    static void addPolygon(Polygon polygon, Polygonizer polygonizer){
        addLineString(polygon.getExteriorRing(), polygonizer);
        for(int n = polygon.getNumInteriorRing(); n-- > 0;){
            addLineString(polygon.getInteriorRingN(n), polygonizer);
        }
    }

    /**
     * Add the linestring given to the polygonizer
     *
     * @param linestring line string
     * @param polygonizer polygonizer
     */
    static void addLineString(LineString lineString, Polygonizer polygonizer){

        if(lineString instanceof LinearRing){ // LinearRings are treated differently to line strings : we need a LineString NOT a LinearRing
            lineString = lineString.getFactory().createLineString(lineString.getCoordinateSequence());
        }

        // unioning the linestring with the point makes any self intersections explicit.
        Point point = lineString.getFactory().createPoint(lineString.getCoordinateN(0));
        Geometry toAdd = lineString.union(point);

        //Add result to polygonizer
        polygonizer.add(toAdd);
    }

    /**
     * Get a geometry from a collection of polygons.
     *
     * @param polygons collection
     * @param factory factory to generate MultiPolygon if required
     * @return null if there were no polygons, the polygon if there was only one, or a MultiPolygon containing all polygons otherwise
     */
    static Geometry toPolygonGeometry(Collection<Polygon> polygons, GeometryFactory factory){
        switch(polygons.size()){
            case 0:
                return null; // No valid polygons!
            case 1:
                return polygons.iterator().next(); // single polygon - no need to wrap
            default:
                //polygons may still overlap! Need to sym difference them
                Iterator<Polygon> iter = polygons.iterator();
                Geometry ret = iter.next();
                while(iter.hasNext()){
                    ret = ret.symDifference(iter.next());
                }
                return ret;
        }
    }
}
