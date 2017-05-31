package spinacht.dbscan;

import spinacht.data.Point;
import spinacht.data.Subset;
import spinacht.data.Subspace;
import spinacht.index.Index;

import java.util.*;

/**
 * DBSCAN as implemented by the original paper.
 * Created by nachtm on 5/15/17.
 */
public class PaperDBScanner implements DBSCANNER {

    private static final int CLUSTER_START = 0;
    private static final int UNCLASSIFIED = -1;

    /**
     * The integer label referring to noise points.
     */
    public static final int NOISE = -2;
    private final double eps;
    private final int minPts;
    private final Index index;

    /**
     * Initialize an instance of PaperDBScanner, with these parameters.
     * @param eps The radius of the epsilon-neighborhood to use in the algorithm
     * @param minPts The minimum number of points a neighborhood needs for a point to be considered a core point
     * @param index An index holding the data we want to use
     */
    public PaperDBScanner(double eps, int minPts, Index index){
        this.eps = eps;
        this.minPts = minPts;
        this.index = index;
    }

    @Override
    public Collection<Subset> dbscan(Subspace space, Subset setOfPoints) {
        return clusterify((new DBScannerInstance(space, setOfPoints, PaperDBScanner.this.eps, PaperDBScanner.this.minPts)).dbscan());
    }

    //Turns a map(Point -> Integer) into a collection of subsets, where each point of the same int
    //is in the same subset
    private static Collection<Subset> clusterify(Map<Point, Integer> labels){
        Map<Integer, Subset> reverse = new HashMap<>();
        for(Map.Entry<Point, Integer> entry : labels.entrySet()) {
            if (entry.getValue() != NOISE) {
                reverse.compute(entry.getValue(), (label, subset) -> {
                    if (subset == null) {
                        subset = new Subset();
                    }
                    subset.add(entry.getKey());
                    return subset;
                });
            }
        }
        return reverse.values();
    }

    //an instance of dbscanner holds state relevant for one run of DBSCAN that might not be relevant for the entire class
    private class DBScannerInstance {

        Subspace space;
        Subset setOfPoints;
        double eps;
        int minPts;
        Map<Point, Integer> labels;

        //Construct an instance of DBScannerInstance
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

        //Run DBSCAN according to the algorithm in the paper.
        Map<Point, Integer> dbscan(){

            //all points should start unclassified
            for(Point p : setOfPoints){
                assert labels.get(p) == UNCLASSIFIED;
            }

            int clusterId = CLUSTER_START;

            //try to "expand" each point to its maximal density-connected set
            for(Point p : setOfPoints){
                if(labels.get(p) == UNCLASSIFIED){
                    if(expandClusters(p, clusterId)){
                        clusterId++;
                    }
                }
            }

            //no labels should be unclassified at the end
            for(Point p : setOfPoints){
                assert labels.get(p) != UNCLASSIFIED;
            }
            return labels;
        }

        //Find the maximal density-connected set of a point
        boolean expandClusters(Point p, int clusterId){
            //get the point's epsilon neighborhood
            Subset seeds = PaperDBScanner.this.index.epsNeighborhood(eps, p, space, setOfPoints);
            Queue<Point> seedQueue = new ArrayDeque<>(seeds);
            assert seedQueue.contains(p);

            //If the point isn't core, it's noise. Otherwise, expand it.
            if(seedQueue.size() < minPts){
                labels.put(p, NOISE);
                return false;
            } else {
                //Set each point in the queue to be of our new cluster
                changeIds(seedQueue, clusterId);

                //we don't need to find p's neighbors again
                seedQueue.remove(p);

                //expand the cluster until we run out of points
                while(!seedQueue.isEmpty()){

                    //try to add currP's neighbors to the cluster
                    Point currP = seedQueue.poll();
                    Subset result = PaperDBScanner.this.index.epsNeighborhood(eps, currP, space, setOfPoints);

                    //but only if currP is itself a core point
                    if(result.size() >= minPts){

                        //add each point to our cluster if it hasn't already been added elsewhere
                        for (Point resultP : result){
                            int resultPLabel = labels.get(resultP);
                            if(resultPLabel == UNCLASSIFIED || resultPLabel == NOISE){
                                if(resultPLabel == UNCLASSIFIED){
                                    seedQueue.add(resultP);
                                }
                                labels.put(resultP, clusterId);
                            }
                        }
                    }
                }
                return true;
            }
        }

        //change the ids of all of the points in an iterable to toChange
        void changeIds(Iterable<Point> points, int toChange){
            for(Point p : points){
                labels.put(p, toChange);
            }
        }
    }

}
