import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class KdTree {

    // Tree Data
    private Node root;
    private int size;

    //
    private static class Node {
        Point2D point;
        Node left;
        Node right;
        int level;

        Node(Point2D point, Node left, Node right, int level) {
            this.point = point;
            this.left = left;
            this.right = right;
            this.level = level;
        }

        public static Node create(Point2D point, Node left, Node right, int level) {
            return new Node(point, left, right, level);
        }
    }

    public KdTree() {
        root = null;
        size = 0;
    }


    public boolean isEmpty() {
        return (root == null);
    }

    public int size() {
        return size;
    }

    public void insert(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException();

        if (p == null) {
            root = Node.create(p, null, null, 0);
            size++;
        }
        else if (!contains(p)) {
            insert(root, p, true);
            size++;
        }
    }

    public boolean contains(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException();
        if (isEmpty()) return false;
        return contains(root, p, true);
    }

    public void draw() {
        draw(root, new RectHV(0, 0, 1, 1));
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();
        List<Point2D> result = new LinkedList<>();
        range(this.root, rect, result);
        return result;
    }

    public Point2D nearest(Point2D p) {
        if (p == null || isEmpty()) throw new IllegalArgumentException();
        return nearest(p, root.point, root);
    }

    /* Private helper functions */
    private void insert(Node root, Point2D p, boolean xOrder) {
        Comparator<Point2D> comparator = xOrder ? Point2D.X_ORDER : Point2D.Y_ORDER;
        // If insertion point is smaller, go to the left
        if (comparator.compare(root.point, p) > 0) {
            if (root.left == null) {
                root.left = Node.create(p, null, null, root.level + 1);
            }
            else {
                insert(root.left, p, !xOrder);
            }
        }
        else if (root.right == null) {
            root.right = Node.create(p, null, null, root.level + 1);
        }
        else {
            insert(root.right, p, !xOrder);
        }
    }

    private boolean contains(Node root, Point2D p, boolean xOrder) {
        Comparator<Point2D> comparator = xOrder ? Point2D.X_ORDER : Point2D.Y_ORDER;
        if (root.point.equals(p)) return true;
        if (comparator.compare(root.point, p) > 0) {
            if (root.left == null) {
                return false;
            }
            else {
                return contains(root.left, p, !xOrder);
            }
        }
        else if (root.right == null) {
            return false;
        }
        else {
            return contains(root.right, p, !xOrder);
        }
    }

    private void draw(Node n, RectHV rectHV) {
        if (n == null) {
            return;
        }
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.02);
        n.point.draw();

        StdDraw.setPenRadius(0.001);
        if (n.level % 2 == 0) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(n.point.x(), rectHV.ymin(), n.point.x(), rectHV.ymax());
            draw(n.left, new RectHV(rectHV.xmin(), rectHV.ymin(), n.point.x(), rectHV.ymax()));
            draw(n.right, new RectHV(n.point.x(), rectHV.ymin(), rectHV.xmax(), rectHV.ymax()));
        }
        else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(rectHV.xmin(), n.point.y(), rectHV.xmax(), n.point.y());
            draw(n.left, new RectHV(rectHV.xmin(), rectHV.ymin(), rectHV.xmax(), n.point.y()));
            draw(n.right, new RectHV(rectHV.xmin(), n.point.y(), rectHV.xmax(), rectHV.ymax()));
        }
    }

    private void range(Node n, RectHV rect, List<Point2D> acc) {
        if (n == null) {
            return;
        }

        if (rect.contains(n.point)) {
            acc.add(n.point);
        }

        if (n.level % 2 == 0) { //Vertical segment
            // the vertical line intersects with query rectangle
            if (rect.xmin() <= n.point.x() && n.point.x() <= rect.xmax()) {
                range(n.left, rect, acc);
                range(n.right, rect, acc);
            }
            else if (rect.xmin() > n.point.x()) { // Search right
                range(n.right, rect, acc);
            }
            else { // Search left
                range(n.left, rect, acc);
            }
        }
        else { // Horizontal segment
            // the horizontal line intersects with query rectangle
            if (rect.ymin() <= n.point.y() && n.point.y() <= rect.ymax()) {
                range(n.left, rect, acc);
                range(n.right, rect, acc);
            }
            else if (rect.ymin() > n.point.y()) { // Search up
                range(n.right, rect, acc);
            }
            else { // Search down
                range(n.left, rect, acc);
            }
        }
    }

    private Point2D nearest(Point2D p, Point2D currentNearestPoint, Node n) {

        if (n == null) {
            return currentNearestPoint;
        }
        if (n.level % 2 == 0) {
            if (p.x() > n.point.x()) { // check right sub-tree
                Point2D npr = nearest(p, n.point.distanceTo(p) < currentNearestPoint.distanceTo(p) ?
                                         n.point : currentNearestPoint, n.right);
                if (npr.distanceTo(p) > Math.abs(n.point.x() - p.x())) {
                    Point2D npl = nearest(p, npr, n.left);
                    return npr.distanceTo(p) > npl.distanceTo(p) ? npl : npr;
                }
                else {
                    return npr;
                }
            }
            else { // check left sub-tree
                Point2D npl = nearest(p, n.point.distanceTo(p) < currentNearestPoint.distanceTo(p) ?
                                         n.point : currentNearestPoint, n.left);
                if (npl.distanceTo(p) > Math.abs(n.point.x() - p.x())) {
                    Point2D npr = nearest(p, npl, n.right);
                    return npr.distanceTo(p) > npl.distanceTo(p) ? npl : npr;
                }
                else {
                    return npl;
                }
            }
        }
        else {
            if (p.y() > n.point.y()) { // check up sub-tree
                Point2D npu = nearest(p, n.point.distanceTo(p) < currentNearestPoint.distanceTo(p) ?
                                         n.point : currentNearestPoint, n.right);
                if (npu.distanceTo(p) > Math.abs(n.point.y() - p.y())) {
                    Point2D npd = nearest(p, npu, n.left);
                    return npu.distanceTo(p) > npd.distanceTo(p) ? npd : npu;
                }
                else {
                    return npu;
                }
            }
            else { // check down sub-tree
                Point2D npd = nearest(p, n.point.distanceTo(p) < currentNearestPoint.distanceTo(p) ?
                                         n.point : currentNearestPoint, n.left);
                if (npd.distanceTo(p) > Math.abs(n.point.y() - p.y())) {
                    Point2D npu = nearest(p, npd, n.right);
                    return npu.distanceTo(p) > npd.distanceTo(p) ? npd : npu;
                }
                else {
                    return npd;
                }
            }
        }
    }

    public static void main(String[] args) {

    }
}
