package spinacht.dbscan;

import java.util.*;
import java.util.stream.Collectors;

/**
 * DBSCAN as implemented by the original paper.
 * Created by nachtm on 5/15/17.
 */
public class PaperDBScanner implements DBSCANNER{

    public static final int CLUSTER_START = 0;
    public static final int UNCLASSIFIED = -1;
    public static final int NOISE = -2;
    private double eps;
    private int minPts;

    public PaperDBScanner(double eps, int minPts){
        this.eps = eps;
        this.minPts = minPts;
    }


    //TODO: implement rangeTrees and insert here.
    @Override
    public Iterable<Cluster> dbscan(Subspace space, Set<Point> setOfPoints) {
        return clusterify(new DBScannerInstance(null, space, setOfPoints, eps, minPts).dbscan());
    }

    private static List<Cluster> clusterify(Map<Point, Integer> labels){
        Map<Integer, List<Point>> reverse = new HashMap<>();
        for(Point p : labels.keySet()){
            if(reverse.containsKey(labels.get(p))){
                reverse.get(labels.get(p)).add(p);
            } else{
                List<Point> pointList = new LinkedList<>();
                pointList.add(p);
                reverse.put(labels.get(p), pointList);
            }
        }

        return reverse.keySet().stream().map(k -> new ListCluster(reverse.get(k))).collect(Collectors.toList());
    }

    private class DBScannerInstance {

        RangeTrees rt;
        Subspace space;
        Set<Point> setOfPoints;
        double eps;
        int minPts;
        Map<Point, Integer> labels;

        DBScannerInstance(RangeTrees rangeTrees, Subspace space, Set<Point> setOfPoints, double eps, int minPts){
            rt = rangeTrees;
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
            List<Point> seeds = rt.getWithinRangeFilteredBy(space, p, eps, setOfPoints);
            assert seeds.contains(p); //O(seeds.size())
            if(seeds.size() < minPts){
                labels.put(p, NOISE);
            } else{
                changeIds(seeds, clusterId);
            }
            seeds.remove(p); //O(seeds.size())
            while(!seeds.isEmpty()){
                Point currP = seeds.get(0);
                List<Point> result = rt.getWithinRangeFilteredBy(space, currP, eps, setOfPoints);
                if(result.size() >= minPts){
                    for(int i = 0; i < result.size(); i++){
                        Point resultP = result.get(i);
                        int resultPLabel = labels.get(resultP);
                        if(resultPLabel == UNCLASSIFIED || resultPLabel == NOISE){
                            if(resultPLabel == UNCLASSIFIED){
                                seeds.add(resultP);
                            }
                            labels.put(resultP, clusterId);
                        }
                    }
                }
                seeds.remove(currP); //O(seeds.size())
            }
            return true;
        }

        void changeIds(List<Point> points, int toChange){
            for(Point p : points){
                labels.put(p, toChange);
            }
        }
    }
}
