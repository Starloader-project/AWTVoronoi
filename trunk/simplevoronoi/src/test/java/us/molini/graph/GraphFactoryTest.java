package us.molini.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import us.molini.graph.GraphFactory;
import us.molini.graph.Neighborhood;

import be.humphreys.simplevoronoi.GraphEdge;
import be.humphreys.simplevoronoi.Point;
import be.humphreys.simplevoronoi.Site;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class GraphFactoryTest extends TestCase {
	
	/* Create and initialize a sample graph. 
	 * 9 points in the 1st quadrant, mostly in a line.
	 */
	GraphFactory setup1(boolean DEBUG) {
		GraphFactory alg = new GraphFactory(0.1);
        assertTrue( alg != null );
        double x[] = { 3, 5,13, 0, 5, 6,11,20, 7};
        double y[] = { 3, 2, 6,15, 5, 9,11,15, 4};
       
        Point p[] = new Point[x.length];
        for(int n=0; n<x.length; n++) {
        	p[n] = new Point(x[n],y[n]);
        }

    	List<GraphEdge> result = alg.generateVoronoi(p, -1, 21, -1, 16);
    	assertTrue( result != null);
    	
    	if (DEBUG) {
    		// Print out the edge points.
    		System.out.println("setup1: "+result.size()+" edges");
    		for (GraphEdge e: result) {
    			System.out.println("  "+e.x1+", "+e.y1+",   "+e.x2+", "+e.y2+"  "+e.site1+":"+e.site2);
    		}
    	}	
    	return alg;
	}

	/** 
	 * Test the adjacent points are correctly unraveled.
	 */
	public void testGet() {
		boolean DEBUG = false; // for testing the test.
		GraphFactory alg = setup1(DEBUG);
			
		Neighborhood n = alg.getNeighborhood(0);
		if (DEBUG) {
    		// Print out the neighborhood.
    		System.out.print("Site 0 next to: ");
    		for (Site s:n.neighbor) {
    			System.out.print(s.sitenbr);
    		}
    		System.out.println(" ");
    	}	
		assertEquals("Site 0 neighbor count",n.neighbor.size(),3);
		n = alg.getNeighborhood(2);
		assertEquals("Site 2 neighbors",n.neighbor.size(),5);
	}

	/** 
	 * Test that the radius search is working.
	 */
	public void testCloseTo() {
		boolean DEBUG = false; // for testing the test.
		GraphFactory alg = setup1(DEBUG);
		List<Site> adj = alg.closeTo(0,5);
		if (DEBUG) {
			for(Site s: adj) {
				System.out.println("Site 0 close to "+s.sitenbr);
			}
		}	
		assertEquals("Site 0 region",adj.size(),3);
		adj = alg.closeTo(8,10);
		if (DEBUG) {
			for(Site s: adj) {
				System.out.println("Site 8 close to "+s.sitenbr);
			}
		}
		assertEquals("Site 8 region",adj.size(),6);
	}



    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( GraphFactoryTest.class );
    }
    
    /**
     * Get a list of edges from the given coordinates.
     * @param z each 4 successive elements make up an edge.
     * @return a list of edges.
     */
    List<GraphEdge> getEdges(double z[]) {
    	int n = z.length/4;
    	ArrayList<GraphEdge> edges = new ArrayList<GraphEdge> ();
    	for(int i=0; i<n; i++) {
    		GraphEdge e = new GraphEdge();
    		e.x1 = z[i*4]; e.y1 = z[i*4+1];
    		e.x2 = z[i*4+2]; e.y2 = z[i*4+3];
    		edges.add(e);
    	}
    	return edges;
    }
    // Return true if the given edges are close to each other.
    boolean closeEnough(GraphEdge e, GraphEdge f, double tol) {
    	if  ((Math.hypot(e.x1-f.x1, e.y1-f.y1)<tol) && 
    		(Math.hypot(e.x2-f.x2, e.y2-f.y2)<tol)) return true;
    	if  ((Math.hypot(e.x2-f.x1, e.y2-f.y1)<tol) && 
        		(Math.hypot(e.x1-f.x2, e.y1-f.y2)<tol)) return true;
    	return false;
    }
    
    /** Verify two list of graph edges match within the given tolerance.
     * 
     * @param a
     * @param b
     * @param tol
     */
    boolean contains(List<GraphEdge> a, List<GraphEdge> b, double tol) {
    	for (GraphEdge e:a ) {
    		boolean found = false;
    		for (GraphEdge f:b) {
    			if (closeEnough(e,f,tol)) {
    				found = true;
    				break;
    			}
    		}
    		if (!found) return false;
    	}
    	return true;
    }
    

    /**
     * Run a small test using the Point generator.
     */
    public void testBasic()
    {
    	boolean DEBUG = false; // for testing the test.
    	GraphFactory alg = new GraphFactory(0.1);
        assertTrue( alg != null );
        double x[] = { -1,  1,  1, -1, 0 };
        double y[] = {  1,  1, -1, -1, 0 };
        double xpct[] = { 0.0, -1.0,  -1.0, 0.0,  0.0, -1.0,   1.0, 0.0,
        		         -1.0, 0.0,   -0.0, 1.0,  1.0, -0.0,   0.0, 1.0,
        		         -2.0, 0.0,   -1.0, 0.0,  0.0, 1.0,    0.0, 2.0,
        		          1.0, 0.0,    2.0, 0.0,  0.0, -2.0,   0.0, -1.0};
        Point p[] = new Point[x.length];
        for(int n=0; n<x.length; n++) {
        	p[n] = new Point(x[n],y[n]);
        }

    	List<GraphEdge> result = alg.generateVoronoi(p, -2, 2, -2, 2);
    	assertTrue( result != null);
    	
    	if (DEBUG) {
    		// Print out the edge points.
    		System.out.println("testBasic: "+result.size()+" edges");
    		for (GraphEdge e: result) {
    			System.out.println("  "+e.x1+", "+e.y1+",   "+e.x2+", "+e.y2+"  "+e.site1+":"+e.site2);
    		}
    	}
    	List<GraphEdge> expected = getEdges(xpct);
    	assertEquals("Wrong number of edges", expected.size(), result.size());
    	assertTrue(contains(expected,result, 0.05));
    	assertTrue(contains(result,expected, 0.05));
    	
     }
    
    /**
     * Run an odd case. Use the super-class (X,Y) generator.
     */
    public void testStripe()
    {
    	boolean DEBUG = false; // for testing the test.
    	GraphFactory alg = new GraphFactory(0.1);
        assertTrue( alg != null );
        double x[] = { -2,  -1, 0, 2 };
        double y[] = { -2,  -1, 0, 2 };
        double xpct[] = { -1.0, -2.0,   -2.0, -1.0,  1.0, -2.0,   -2.0, 1.0,
        		  2.0, 0.0,   0.0, 2.0};

    	List<GraphEdge> result = alg.generateVoronoi(x, y, -2, 2, -2, 2);
    	assertTrue( result != null);
    	
    	// Print out the edge points.
    	if (DEBUG) {
    		System.out.println("testStripe:");
    		for (GraphEdge e: result) {
    			System.out.println("  "+e.x1+", "+e.y1+",   "+e.x2+", "+e.y2+",");
    		}
    	}
    	List<GraphEdge> expected = getEdges(xpct);
    	// Don't check the # edges. Having points on the boundary causes
    	// this algorithm to count some edges twice.
    	assertTrue(contains(expected,result, 0.05));
    	assertTrue(contains(result,expected, 0.05));
    	
     }
    
  

}
