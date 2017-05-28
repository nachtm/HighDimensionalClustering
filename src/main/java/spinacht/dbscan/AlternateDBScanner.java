package spinacht.dbscan;

import spinacht.data.Point;
import spinacht.data.Subset;
import spinacht.data.Subspace;
import spinacht.index.Index;

import java.util.*;


public class AlternateDBScanner implements DBSCANNER {

    private final double eps;
    private final int minPts;
    private final Index index;

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

    private Map<Point, Integer> assignLabels(Subspace subspace, Subset subset) {
        Map<Point, Integer> labels = new HashMap<>();
        int clusterId = 0;
        for (Point seed : subset) {
            if (!labels.containsKey(seed)) {
                Subset neighborhood = index.epsNeighborhood(AlternateDBScanner.this.eps, seed, subspace, subset);
                if (neighborhood.size() < AlternateDBScanner.this.minPts) {
                    labels.put(seed, -1);
                } else {
                    labels.put(seed, clusterId);
                    Queue<Point> reachable = new LinkedList<>(); // contains only points density-reachable from `point`
                    reachable.addAll(neighborhood);
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
