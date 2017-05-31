package demo;

import com.google.common.base.Stopwatch;
import spinacht.data.*;
import spinacht.dbscan.PaperDBScanner;
import spinacht.index.Index;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.IntStream;


public class DBSCANLameFile {

    public static void main(String[] args) throws IOException {

        String fname = args[0];
        int ndims = Integer.parseInt(args[1]);
        double eps = Double.parseDouble(args[2]);
        int minPts = Integer.parseInt(args[3]);
        Stopwatch timer = Stopwatch.createStarted();

        SimpleDatabase db = DataUtil.fromLameFile(ndims, Paths.get(fname));

        Collection<Subset> clusters = (new PaperDBScanner(eps, minPts, new Index(db))).dbscan(new SubspaceWrapper(IntStream.range(0, ndims).boxed().iterator()), new Subset(db));

        System.out.println(clusters.size() + " clusters in " + timer.stop());

      }

}
