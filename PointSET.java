/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Stack;

public class PointSET {
    private SET<Point2D> pSet;  // balanced BST

    public PointSET() { //Construct empty set of points
        pSet = new SET<Point2D>();
    }

    public boolean isEmpty() {
        return pSet.isEmpty();
    }

    public int size() {
        return pSet.size();
    }

    public void insert(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException();

        if (!contains(p))   // logarithmic due to balanced BST
            pSet.add(p);
    }

    public boolean contains(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException();

        return pSet.contains(p);    // logarithmic due to balanced BST
    }

    public void draw() {
        if (pSet.isEmpty()) return;

        for (Point2D p : pSet)
            p.draw();
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (pSet == null) return null;

        Stack<Point2D> pStack = new Stack<Point2D>();   // Return any iterable DS
        for (Point2D p : pSet) { // Time prop. to # items in set
            if (rect.contains(p)) {
                pStack.push(p);
            }
        }

        return pStack;
    }

    public Point2D nearest(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException();

        if (pSet == null)
            return null;

        Point2D champion = null;
        double minDist = Double.POSITIVE_INFINITY;
        for (Point2D setPoint : pSet) {      // Time prop. to # items in set
            if (setPoint.distanceTo(p) < minDist) {
                champion = setPoint;
            }
        }

        return champion;
    }

    public static void main(String[] args) {

    }
}
