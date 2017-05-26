package spinacht.dbscan;

import java.util.*;

import spinacht.common.*;
import spinacht.data.*;
import spinacht.index.*;

/**
 * DBSCAN as implemented by the original paper.
 * Created by nachtm on 5/15/17.
 */
public class PaperDBScanner implements DBSCANNER {

    public static final int CLUSTER_START = 0;
    public static final int UNCLASSIFIED = -1;
    public static final int NOISE = -2;
    private double eps;
    private int minPts;
    private Index index;

    public PaperDBScanner(double eps, int minPts, Index index){
        this.eps = eps;
        this.minPts = minPts;
        this.index = index;
    }

    @Override
    public Collection<Subset> dbscan(Subspace space, Subset setOfPoints) {
        return clusterify(new DBScannerInstance(space, setOfPoints, eps, minPts).dbscan());
    }

    private static Collection<Subset> clusterify(Map<Point, Integer> labels){
        Map<Integer, Subset> reverse = new HashMap<>();
        for(Point p : labels.keySet()) {
            Integer label = labels.get(p);
            assert label != UNCLASSIFIED;
            if (label != NOISE) {
                if (reverse.containsKey(label)) {
                    reverse.get(label).add(p);
                } else {
                    Subset pointList = new Subset();
                    pointList.add(p);
                    reverse.put(label, pointList);
                }
            }
        }

        return reverse.values();
    }

    private class DBScannerInstance {

        Subspace space;
        Subset setOfPoints;
        double eps;
        int minPts;
        Map<Point, Integer> labels;

        DBScannerInstance(Subspace space, Subset setOfPoints, double eps, int minPts){
            this.space = space;
            this.setOfPoints = setOfPoints;
            this.eps = eps;
            this.minPts = minPts;
            labels = new HashMap<>();
            for(Point p : setOfPoints){
                labels.put(p, UNCLASSIFIED);
            }
        }

        Map<Point, Integer> dbscan(){
            for(Point p : setOfPoints){
                assert labels.get(p) == UNCLASSIFIED;
            }

            int clusterId = CLUSTER_START;
            for(Point p : setOfPoints){
                if(labels.get(p) == UNCLASSIFIED){
                    if(expandClusters(p, clusterId)){
                        clusterId++;
                    }
                }
            }
            return labels;
        }

        boolean expandClusters(Point p, int clusterId){
            Subset seeds = PaperDBScanner.this.index.epsNeighborhood(eps, p, space, setOfPoints);
            assert seeds.contains(p); //O(seeds.size())
            if(seeds.size() < minPts){
                labels.put(p, NOISE);
                return false;
            } else {
                changeIds(seeds, clusterId);
                seeds.remove(p); //O(seeds.size())
                for (Iterator<Point> it = seeds.iterator(); it.hasNext(); ) {
                    Point currP = it.next();
                    it.remove();
                    Subset result = PaperDBScanner.this.index.epsNeighborhood(eps, p, space, setOfPoints);
                    if (result.size() >= minPts) {
                        for (Point resultP : result) {
                            int resultPLabel = labels.get(resultP);
                            if (resultPLabel == UNCLASSIFIED || resultPLabel == NOISE) {
                                if (resultPLabel == UNCLASSIFIED) {
                                    seeds.add(resultP);
                                }
                                labels.put(resultP, clusterId);
                            }
                        }
                    }
                }
                return true;
            }
        }

        void changeIds(Subset points, int toChange){
            for(Point p : points){
                labels.put(p, toChange);
            }
        }
    }
}
