package spinacht.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DataUtil {

    /**
     * Creates a SimpleDatabase from a space-separated file, treating lines that begin with # as comments
     * @param ndims The number of dimensions of each data point in the file.
     * @param file The path to the actual file
     * @return a SimpleDatabase containing all the points in the file specified
     * @throws IOException
     */
    public static SimpleDatabase fromLameFile(int ndims, Path file) throws IOException {
        return new SimpleDatabase(ndims, Files.lines(file)
                .filter(line -> line.charAt(0) != '#')
                .map(line -> line.split(" "))
                .map(row -> {
                    double[] pt = new double[ndims];
                    for (int i = 0; i < ndims; i++) {
                        pt[i] = Double.parseDouble(row[i]);
                    }
                    return new SimpleDatabase.SimplePoint(pt);
                })
                .iterator()
        );
    }

    /**
     * Createds a SimpleLabeledDatabase<String> from a space-separated file, treating lines that begin with # as comments
     * and the last element in the row as the label
     * @param ndims The number of dimensions of each data point in the file
     * @param file The path to the actual file
     * @return a SimpleLabeledDatabase containing all the points in the file specified, with labels equal to the last column
     * @throws IOException
     */
    public static SimpleLabeledDatabase<String> fromELKIFile(int ndims, Path file) throws IOException {
        return new SimpleLabeledDatabase<String>(ndims, Files.lines(file)
                .filter(line -> line.charAt(0) != '#')
                .map(line -> line.split(" "))
                .map(row -> {
                    double[] pt = new double[ndims];
                    for (int i = 0; i < ndims; i++) {
                        pt[i] = Double.parseDouble(row[i]);
                    }
                    return new SimpleLabeledDatabase.SimpleLabeledPoint<>(pt, row[ndims]);
                })
                .iterator()
        );
    }

}
