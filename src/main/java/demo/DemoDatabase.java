package demo;

import spinacht.Params;
import spinacht.data.Database;
import spinacht.data.Point;
import spinacht.data.Subspace;
import spinacht.subclu.SUBCLU;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;


public class DemoDatabase extends ArrayList<Point> implements Database<Point> {

  private int ndims;

  private static class DemoPoint implements Point {

    final double[] point;
    final String cluster;

    DemoPoint(double[] point, String cluster) {
      this.point = point;
      this.cluster = cluster;
    }

    public double get(int i) {
      return point[i];
    }

    public String toString() {
      return "[" + Arrays.stream(this.point).mapToObj(Double::toString).collect(Collectors.joining(", ")) + "] " + this.cluster;
    }

  }

  public DemoDatabase(Path file) throws Exception {
    super();
    Files.lines(file)
         .filter(line -> line.charAt(0) != '#')
         .map(line -> line.split(" "))
         .forEach(cells -> {
            int n = cells.length - 1;
            double[] pt = new double[n];
            for (int i = 0; i < n; i++) {
              pt[i] = Double.parseDouble(cells[i]);
            }
            this.add(new DemoPoint(pt, cells[n]));
            this.ndims = n;
         });
  }

  public int getDimensionality() {
    return ndims;
  }

  public static void main(String[] args) throws Exception {
    try {
      Database db = new DemoDatabase(Paths.get(args[0]));
      SUBCLU.go(new Params(.05, 10, db)).forEachCluster(subspace -> {
        System.out.print("SUBSPACE: " + Subspace.pprint(subspace));
        System.out.println();
        return subset -> {
          System.out.println("  SUBSET");
          for (Point p : subset) {
            System.out.println("    " + p);
          }
        };
      });
      System.out.println();
    } catch (Exception e) {
      e.printStackTrace(System.out);
      throw e;
    }

  }

}
