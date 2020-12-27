package route_planner;

import graphs.DGVertex;
import graphs.DirectedGraph;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class Junction implements DGVertex<Road>
        // TODO extend superclass and/or implement interfaces
{
    private String name;            // unique name of the junction
    private double locationX;       // RD x-coordinate in km
    private double locationY;       // RD y-coordinate in km
    private int population;         // indicates importance of the junction, use for graphical purposes only

    Set<Road> roads = new HashSet<>();  // all (one-directional) road segments that start at this junction.

    private double variability;     // technical number to fix variability in actual lengths of roads
    public Junction() {
        // initialise the variability to be used for determining true length of roads around the junction.
        // this is a technical trick to compensate for lack of true authentic data.
        this.variability = RoadMap.randomizer.nextDouble();
    }
    public Junction(String name) {
        this();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLocationX() {
        return locationX;
    }

    public void setLocationX(double locationX) {
        this.locationX = locationX;
    }

    public double getLocationY() {
        return locationY;
    }

    public void setLocationY(double locationY) {
        this.locationY = locationY;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public double getVariability() {
        return variability;
    }

    /**
     * draws the junction onto an svg image with a given colour
     * @param svgWriter
     * @param colour
     */
    public void svgDraw(PrintStream svgWriter, String colour) {
        // calculate the size of the dot relative to population at the junction
        double radius = 0.1 + 0.3 * Math.log(1+this.population/2000);
        //radius = 0.1;
        int fontSize = 3;

        // accounts for the reversed y-direction of the svg coordinate system relative to RD-coordinates
        svgWriter.printf(Locale.ENGLISH,"<circle cx='%.3f' cy='%.3f' r='%.3f' fill='%s'/>\n",
                this.locationX, -this.locationY, radius, colour);
        svgWriter.printf(Locale.ENGLISH,"<text x='%.3f' y='%.3f' font-size='%d' fill='%s' text-anchor='middle'>%s</text>\n",
                this.locationX, -this.locationY-1.3, fontSize, colour, this.name);

    }

    /**
     * draws all road segments starting from the junction with their default colour
     * @param svgWriter
     */
    public void svgDrawRoads(PrintStream svgWriter) {
        for (Road r: this.roads) {
            r.svgDraw(svgWriter, null);
        }
    }

    @Override
    public String toString() {
        return this.name +
                this.roads.stream()
                    .map(Road::toString)
                    .collect(Collectors.joining(" ","[","]"));
    }

    /**
     *  calculates the carthesion distance between two junctions
     * @param target
     * @return
     */
    double getDistance(Junction target) {
        // calculate the cartesion distance between this and the target junction
        // using the locationX and locationY as provided in the dutch RD-coordinate system
        double dX = target.locationX - this.locationX;
        double dY = target.locationY - this.locationY;
        return Math.sqrt(dX*dX + dY*dY);
    }

    @Override
    public String getId() {
        return name;
    }

    @Override
    public Set<Road> getEdges() {
        return roads;
    }

//     TODO more implementations as required for use with DirectedGraph, HashSet and/or HashMap


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Junction junction = (Junction) o;

        return name.equals(junction.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
