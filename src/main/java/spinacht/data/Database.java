package spinacht.data;

import java.util.Collection;


public interface Database extends Collection<Point> {

  public int getDimensionality();

  public Point get(int i);

}
