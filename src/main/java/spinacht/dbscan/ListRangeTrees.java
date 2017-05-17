package spinacht.dbscan;

import com.sun.xml.internal.bind.v2.util.CollisionCheckStack;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by nachtm on 5/15/17.
 */
public class ListRangeTrees implements RangeTrees {

    List<RangeTree<Point>> rts;
    public ListRangeTrees(int numDimensions, Database db){
        for(int i = 0; i < numDimensions; i++){
            final int currDim = i;
            RangeTree<Point> toAdd = new RangeTree<Point>((p1, p2) -> (int)(p1.get(currDim) - (p2.get(currDim))));
            for(Point p : db){
                toAdd.insert(p);
            }
            rts.add(toAdd);
        }
    }

    @Override
    public List<Point> getWithinRangeFilteredBy(Subspace s, Point p, double epsilon, Set<Point> superset) {
        return getWithinRange(s, p, epsilon).stream().filter(point -> superset.contains(point)).collect(Collectors.toList());
    }

    private List<Point> getWithinRange(Subspace s, Point p, double epsilon){
        Set<Point> toReturn = new HashSet<>();
        boolean firstDim = true;
        for(int dim : s){
            Set<Point> currDim = new HashSet<>();
            Point lo = p.addToDimension(dim, -1 * epsilon);
            Point hi = p.addToDimension(dim, 1 * epsilon);
            rts.get(dim).forEachInRange(lo, hi, point -> currDim.add(p));
            //first pass
            if(firstDim){
                firstDim = false;
                toReturn.addAll(currDim);
            } else{
                toReturn.retainAll(currDim);
            }

        }

        //TODO: filter out the ones that are actually too far away.
        return null;
    }
}
