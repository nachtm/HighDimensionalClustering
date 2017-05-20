package demo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.*;

import com.opencsv.CSVReader;

import spinacht.data.*;


public class SimpleDatabase extends ArrayList<Point> implements Database {

  private final int ndims;

  private static class SimplePoint implements Point {

    final double[] point;

    SimplePoint(double[] point) {
      this.point = point;
    }

    public double get(int i) {
      return point[i];
    }

    public String toString() {
      return "[" + Arrays.stream(this.point).mapToObj(x -> Double.toString(x)).collect(Collectors.joining(", ")) + "]";
    }

  }

  public SimpleDatabase(File file) throws FileNotFoundException, IOException {
    super();
    CSVReader reader = new CSVReader(new FileReader(file));
    int n = -1;
    while (true) {
      String[] read = reader.readNext();
      if (read == null) {
        break;
      }
      n = read.length;
      double[] curr = new double[read.length];
      for (int i = 0; i < read.length; i++) {
        curr[i] = Double.parseDouble(read[i]);
      }
      this.add(new SimplePoint(curr));
    }
    ndims = n;
  }

  public int getDimensionality() {
    return ndims;
  }

}
