import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.List;

/**
 * Created by nazwa on 2018-03-18.
 */
public class KDTree {

    private class KDNode{
        private KDNode left;
        private KDNode right;
        private int vertical;
        private Point2D point;

        private KDNode(KDNode left, KDNode right, int vertical, Point2D point) {
            this.left = left;
            this.right = right;
            this.vertical = vertical;
            this.point = point;
        }
    }

    private KDNode root;
    private int depth;
    private CoordinateComparator comparator;
    public KDTree() {
        this.root = null;
        this.depth = 0;
        this.comparator = new CoordinateComparator(this.depth);
    }

    public  KDTree(List<Point2D.Double> pointList, int depth){
        this.depth = depth;
        this.comparator = new CoordinateComparator(depth);
        pointList.sort(comparator);
        int middle = pointList.size()/2 + 1;


    }

    private class CoordinateComparator implements Comparator<Point2D.Double> {
        int depth;
        public CoordinateComparator(int depth) {
            this.depth = depth;
        }

        @Override
        public int compare(Point2D.Double p1, Point2D.Double p2) {
            if(depth %2 == 0) {
                if (p1.getX() < p2.getX())
                    return -1;
                if (p1.getX() > p2.getX())
                    return 1;
                if (p1.getY() < p2.getY())
                    return -1;
                if (p1.getY() > p2.getY())
                    return 1;
            }else{
                if (p1.getY() < p2.getY())
                    return -1;
                if (p1.getY() > p2.getY())
                    return 1;
                if (p1.getX() < p2.getX())
                    return -1;
                if (p1.getX() > p2.getX())
                    return 1;
            }
            return 0;
        }


    }

    public static void main(String args[]){

    }
}
