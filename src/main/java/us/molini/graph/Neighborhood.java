package us.molini.graph;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import be.humphreys.simplevoronoi.Site;

public class Neighborhood extends Site {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1726045706862326870L;
    int lastVisit; // Site number of list visitor.
    ArrayList<Neighborhood> neighbor;

    Neighborhood(Point2D p, int number) {
        super();
        this.setLocation(p);
        sitenbr = number;
        neighbor = new ArrayList<>();
    }

    public double distance(Site s) {
        double d = Math.hypot(x - s.x, y - s.y);
        // System.out.println("Site "+s.sitenbr+" is "+d+" from site "+sitenbr);
        return d;
    }
}
