package spinacht.data;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.*;

import com.opencsv.CSVReader;


public class SimpleDatabase implements Database {

  private final int ndims;
  private final List<Point> rows;

  private static class SimplePoint implements Point {
    final double[] point;
    SimplePoint(double[] point) {
      this.point = point;
    }
    public double get(int i) {
      return point[i];
    }
  }

  public SimpleDatabase(File file) throws FileNotFoundException, IOException {
    CSVReader reader = new CSVReader(new FileReader("./src/test/resources/test.csv"));
    rows = new ArrayList<Point>();
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
      rows.add(new SimplePoint(curr));
    }
    ndims = n;
  }

  public static void main(String[] args) throws Exception {
    Database db = new SimpleDatabase(new File("test.csv"));
    for (Point pt : db) {
      System.out.println(pt.get(0));
    }
  }

  public int getDimensionality() {
    return ndims;
  }

  public Point get(int i) {
    return rows.get(i);
  }

  @Override
  public int size() {
    return rows.size();
  }

  @Override
  public Iterator<Point> iterator() {
    return rows.iterator();
  }

  @Override public boolean add(Point element) { throw new UnsupportedOperationException(); }
  @Override public boolean addAll(Collection c) { throw new UnsupportedOperationException(); }
  @Override public boolean remove(Object element) { throw new UnsupportedOperationException(); }
  @Override public boolean removeAll(Collection c) { throw new UnsupportedOperationException(); }
  @Override public boolean retainAll(Collection c) { throw new UnsupportedOperationException(); }
  // @Override public int size() { throw new UnsupportedOperationException(); }
  @Override public void clear() { throw new UnsupportedOperationException(); }
  @Override public boolean contains(Object element) { throw new UnsupportedOperationException(); }
  @Override public boolean containsAll(Collection c) { throw new UnsupportedOperationException(); }
  // @Override public Iterator iterator() { throw new UnsupportedOperationException(); }
  @Override public Object[] toArray() { throw new UnsupportedOperationException(); }
  @Override public <T> T[] toArray(T[] a) { throw new UnsupportedOperationException(); }
  @Override public boolean isEmpty() { throw new UnsupportedOperationException(); }
  @Override public boolean equals(Object element) { throw new UnsupportedOperationException(); }
  @Override public int hashCode() { throw new UnsupportedOperationException(); }

}
