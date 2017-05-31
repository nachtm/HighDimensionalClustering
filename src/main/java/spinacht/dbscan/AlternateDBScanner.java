package spinacht.dbscan;

import spinacht.data.Point;
import spinacht.data.Subset;
import spinacht.data.Subspace;
import spinacht.index.Index;

import java.util.*;

/**
 * An alternate way of thinking about DBSCANning. Useful to compare against PaperDBScanner in case one of them gets messed up.
 * Not asymptotically better than PaperDBScanner from what we can tell.
 */
public class AlternateDBScanner implements DBSCANNER {

    private final double eps;
    private final int minPts;
    private final Index index;

    /**
     * Initialize an instance of an AlternateDBScanner.
     * @param eps The radius of the epsilon-neighborhood to use in the algorithm
     * @param minPts The minimum number of points a neighborhood needs for a point to be considered a core point
     * @param index An index holding the data we want to use
     */
    public AlternateDBScanner(double eps, int minPts, Index index) {
        this.eps = eps;
        this.minPts = minPts;
        this.index = index;
    }

    @Override
    public Collection<Subset> dbscan(Subspace subspace, Subset subset) {
        Map<Integer, Subset> clusters = new HashMap<>();
        for (Map.Entry<Point, Integer> entry : this.assignLabels(subspace, subset).entrySet()) {
            if (entry.getValue() >= 0) {
                clusters.compute(entry.getValue(), (label, cluster) -> {
                    if (cluster == null) {
                        cluster = new Subset();
                    }
                    cluster.add(entry.getKey());
                    return cluster;
                });
            }
        }
        return clusters.values();
    }

    //cluster using a DFS through the points reachable from the seed point for each cluster.
    private Map<Point, Integer> assignLabels(Subspace subspace, Subset subset) {
        //initialize variables
        Map<Point, Integer> labels = new HashMap<>();
        int clusterId = 0;

        //iterate through all of the points
        for (Point seed : subset) {

            //if we haven't found this point yet
            if (!labels.containsKey(seed)) {
                Subset neighborhood = index.epsNeighborhood(AlternateDBScanner.this.eps, seed, subspace, subset);

                if (neighborhood.size() < AlternateDBScanner.this.minPts) {
                    //this point is noise
                    labels.put(seed, -1);
                } else {
                    //this point can be the start of this cluster
                    labels.put(seed, clusterId);
                    Queue<Point> reachable = new LinkedList<>(); // contains only points density-reachable from `point`
                    reachable.addAll(neighborhood);

                    //do a DFS through all reachable points from the seed, adding them to the queue if they are also core points
                    while (!reachable.isEmpty()) {
                        Point subSeed = reachable.poll();
                        labels.put(subSeed, clusterId);
                        Subset subNeighborhood = index.epsNeighborhood(AlternateDBScanner.this.eps, subSeed, subspace, subset);
                        if (subNeighborhood.size() >= AlternateDBScanner.this.minPts) {
                            for (Point p : subNeighborhood) {
                                if (labels.containsKey(p)) {
                                    if (labels.get(p) < 0) {
                                        labels.put(p, clusterId);
                                    }
                                } else {
                                    reachable.offer(p);
                                    labels.put(p, clusterId);
                                }
                            }
                        }
                    }
                    clusterId++;
                }
            }
        }
        return labels;
    }

}
