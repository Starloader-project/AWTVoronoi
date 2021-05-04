package be.humphreys.voronoi;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import be.humphreys.simplevoronoi.GraphEdge;
import be.humphreys.simplevoronoi.Voronoi;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for the Voronoi class.
 */
public class AppTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Get a list of edges from the given coordinates.
     * 
     * @param z each 4 successive elements make up an edge.
     * @return a list of edges.
     */
    List<GraphEdge> getEdges(double z[]) {
        int n = z.length / 4;
        ArrayList<GraphEdge> edges = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            GraphEdge e = new GraphEdge();
            e.x1 = z[i * 4];
            e.y1 = z[i * 4 + 1];
            e.x2 = z[i * 4 + 2];
            e.y2 = z[i * 4 + 3];
            edges.add(e);
        }
        return edges;
    }

    // Return true if the given edges are close to each other.
    boolean closeEnough(GraphEdge e, GraphEdge f, double tol) {
        if ((Math.hypot(e.x1 - f.x1, e.y1 - f.y1) < tol) && (Math.hypot(e.x2 - f.x2, e.y2 - f.y2) < tol))
            return true;
        if ((Math.hypot(e.x2 - f.x1, e.y2 - f.y1) < tol) && (Math.hypot(e.x1 - f.x2, e.y1 - f.y2) < tol))
            return true;
        return false;
    }

    /**
     * Verify two list of graph edges match within the given tolerance.
     * 
     * @param a
     * @param b
     * @param tol
     */
    boolean contains(List<GraphEdge> a, List<GraphEdge> b, double tol) {
        for (GraphEdge e : a) {
            boolean found = false;
            for (GraphEdge f : b) {
                if (closeEnough(e, f, tol)) {
                    found = true;
                    break;
                }
            }
            if (!found)
                return false;
        }
        return true;
    }

    /**
     * Run a small test (simple enough to check by hand.
     */
    public void testBasic() {
        boolean DEBUG = false; // for testing the test.
        Voronoi alg = new Voronoi(0.1);
        assertTrue(alg != null);
        double x[] = { -1, 1, 1, -1, 0 };
        double y[] = { 1, 1, -1, -1, 0 };
        double xpct[] = { 0.0, -1.0, -1.0, 0.0, 0.0, -1.0, 1.0, 0.0, -1.0, 0.0, -0.0, 1.0, 1.0, -0.0, 0.0, 1.0, -2.0,
                0.0, -1.0, 0.0, 0.0, 1.0, 0.0, 2.0, 1.0, 0.0, 2.0, 0.0, 0.0, -2.0, 0.0, -1.0 };

        List<GraphEdge> result = alg.generateVoronoi(x, y, -2, 2, -2, 2);
        assertTrue(result != null);

        if (DEBUG) {
            // Print out the edge points.
            System.out.println("testBasic: " + result.size() + " edges");
            for (GraphEdge e : result) {
                System.out.println(
                        "  " + e.x1 + ", " + e.y1 + ",   " + e.x2 + ", " + e.y2 + "  " + e.site1 + ":" + e.site2);
            }
        }
        List<GraphEdge> expected = getEdges(xpct);
        assertEquals("Wrong number of edges", expected.size(), result.size());
        assertTrue(contains(expected, result, 0.05));
        assertTrue(contains(result, expected, 0.05));

    }

    /**
     * Run an odd case. Points all in a line and 2 points on the boundary.
     */
    public void testStripe() {
        boolean DEBUG = false; // for testing the test.
        Voronoi alg = new Voronoi(0.1);
        assertTrue(alg != null);
        double x[] = { -2, -1, 0, 2 };
        double y[] = { -2, -1, 0, 2 };
        double xpct[] = { -1.0, -2.0, -2.0, -1.0, 1.0, -2.0, -2.0, 1.0, 2.0, 0.0, 0.0, 2.0 };

        List<GraphEdge> result = alg.generateVoronoi(x, y, -2, 2, -2, 2);
        assertTrue(result != null);

        // Print out the edge points.
        if (DEBUG) {
            System.out.println("testStripe:");
            for (GraphEdge e : result) {
                System.out.println("  " + e.x1 + ", " + e.y1 + ",   " + e.x2 + ", " + e.y2 + ",");
            }
        }
        List<GraphEdge> expected = getEdges(xpct);
        // Don't check the # edges. Having points on the boundary causes
        // this algorithm to count some edges twice.
        assertTrue(contains(expected, result, 0.05));
        assertTrue(contains(result, expected, 0.05));

    }

    /**
     * Verify the class reacts gracefully to bogus input.
     */
    public void testPathology() {
        // Negative min spacing - same results as zero.
        Voronoi alg = new Voronoi(-1.0);
        assertTrue(alg != null);
        double x[] = { -1, 1, 1, -1, 0 };
        double y[] = { 1, 1, -1, -1, 0 };
        double y2[] = { 45, 55 };
        List<GraphEdge> result = alg.generateVoronoi(x, y, -2, 2, -2, 2);
        assertTrue(result != null);
        assertEquals("'-' spacing wrong number of edges", 8, result.size());

        alg = new Voronoi(0.1);
        assertTrue(alg != null);

        // Zero area boundary - valid (but degenerate) results.
        result = alg.generateVoronoi(x, y, 2, 2, -2, 2);
        assertTrue(result != null);

        // Null input - throw an exception.
        try {
            result = alg.generateVoronoi(x, null, 2, -2, -2, 2);
            fail("Null input should throw an exception");
        } catch (Exception ex) {
        }

        // Mismatched input - throw an exception
        try {
            result = alg.generateVoronoi(x, y2, 2, -2, -2, 2);
            fail("Input with different size should throw an exception");
        } catch (Exception ex) {
        }

    }

    private double timeTrial(int N, int R, boolean DEBUG) {
        Voronoi alg = new Voronoi(0.1);
        assertTrue(alg != null);
        Random rand = new Random(2111956); // authors birthday :)
        double x[] = new double[N];
        double y[] = new double[N];
        double totalTime = 0.0;
        for (int rep = 0; rep < R; rep++) {
            for (int n = 0; n < x.length; n++) {
                x[n] = rand.nextDouble() * 1000.0;
                y[n] = rand.nextDouble() * 1000.0;
            }
            long startTime = System.currentTimeMillis();
            List<GraphEdge> result = alg.generateVoronoi(x, y, 0, 1000, 0, 1000);
            long run = System.currentTimeMillis() - startTime;
            if (DEBUG)
                System.out.println(N + " points in " + run + " msec. " + result.size() + " edges were generated.");
            totalTime += run;
        }
        return totalTime / R;
    }

    double correlation(int x[], int y[]) {
        // Find the correlation coefficient for the runtime.
        double sumx = 0, sumy = 0;
        for (int n = 0; n < x.length; n++) {
            sumx += x[n];
            sumy += y[n];
        }
        double xbar = sumx / x.length;
        double ybar = sumy / x.length;

        double sumxy = 0, sumx2 = 0, sumy2 = 0;
        for (int n = 0; n < x.length; n++) {
            sumxy += (x[n] - xbar) * (y[n] - ybar);
            sumx2 += (x[n] - xbar) * (x[n] - xbar);
            sumy2 += (y[n] - ybar) * (y[n] - ybar);
        }
        return sumxy / Math.sqrt(sumx2 * sumy2);
    }

    /**
     * Run some larger random maps to verify runtime performance.
     */
    public void testPerformance() {
        boolean DEBUG = false; // for testing the test.
        // Choose a range of size where the performance in O(N)
        int x[] = { 1000, 5000, 10000, 20000, 40000 };
        int y[] = new int[x.length];
        for (int n = 0; n < x.length; n++) {
            y[n] = (int) timeTrial(x[n], 5, DEBUG);
            if (DEBUG)
                System.out.println(x[n] + " points in " + y[n] + " msec");
        }
        double r = correlation(x, y);
        if (DEBUG)
            System.out.println("r = " + r);

        assertTrue("Runtime is non-linear", (r > 0.95));
    }

}
