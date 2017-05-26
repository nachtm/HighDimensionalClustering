package spinacht.dbscan;

import java.awt.print.Paper;
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
    private final double eps;
    private final int minPts;
    private final Index index;

    public PaperDBScanner(double eps, int minPts, Index index){
        this.eps = eps;
        this.minPts = minPts;
        this.index = index;
    }

    @Override
    public Collection<Subset> dbscan(Subspace space, Subset setOfPoints) {
        return clusterify(toBeNamedOrPutIntoTheInnerClassIJustWantedToSimplifyForTheSakeOfSeeingWhetherIHadActuallyFoundTheProblem(space, setOfPoints));
    }

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

    private Map<Point, Integer> toBeNamedOrPutIntoTheInnerClassIJustWantedToSimplifyForTheSakeOfSeeingWhetherIHadActuallyFoundTheProblem(Subspace subspace, Subset subset) {

        Map<Point, Integer> labels = new HashMap<>();

        int clusterId = 0;

        for (Point seed : subset) {
            if (!labels.containsKey(seed)) {
                Subset neighborhood = index.epsNeighborhood(PaperDBScanner.this.eps, seed, subspace, subset);
                System.out.println(PaperDBScanner.this.minPts);
                if (neighborhood.size() < PaperDBScanner.this.minPts) {
                    labels.put(seed, NOISE);
                } else {
                    labels.put(seed, clusterId);
                    Queue<Point> reachable = new LinkedList<>(); // contains only points density-reachable from `point`
                    reachable.addAll(neighborhood);
                    while (!reachable.isEmpty()) {
                        Point subSeed = reachable.poll();
                        labels.put(subSeed, clusterId);
                        Subset subNeighborhood = index.epsNeighborhood(PaperDBScanner.this.eps, subSeed, subspace, subset);
                        if (subNeighborhood.size() >= PaperDBScanner.this.minPts) {
                            for (Point p : subNeighborhood) {
                                if (labels.containsKey(p)) {
                                    if (labels.get(p) == NOISE) {
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
            for(Point p : setOfPoints){
                assert labels.get(p) != UNCLASSIFIED;
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
