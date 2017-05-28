package spinacht.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DataUtil {

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
