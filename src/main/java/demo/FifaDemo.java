package demo;

import spinacht.Params;
import spinacht.data.Clustering;
import spinacht.data.Subspace;
import spinacht.dbscan.DBSCANNER;
import spinacht.dbscan.PaperDBScanner;
import spinacht.index.Index;
import spinacht.subclu.SUBCLU;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by nachtm on 5/28/17.
 */
public class FifaDemo {

    public static void main(String[] args){
        double eps = 1;
        int minPts = 50;
        System.out.println("Loading data.");
        FifaDatabase fifaDB = null;
        try {
            fifaDB = new FifaDatabase(Paths.get("data/fifa_small.csv"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert fifaDB != null;

        System.out.println("Clustering.");


    }
}
