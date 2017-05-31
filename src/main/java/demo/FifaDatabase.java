package demo;

import spinacht.Params;
import spinacht.data.*;
import spinacht.subclu.SUBCLU;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A database designed to hold FIFA players. Includes a main() which runs SUBCLU on an example FIFA dataset.
 * Created by Micah on 5/24/2017.
 */
public class FifaDatabase extends ArrayList<Point> implements Database<Point> {

	private int ndims;

	//A Player is a point with a name and preferred position.
	private static class Player implements Point{

		private double[] data;
		private String name;
		private String prefPos;

		Player(double[] data, String name, String prefPos){
			this.data = data;
			this.name = name;
			this.prefPos = prefPos;
		}

		@Override
		public double get(int i) {
			return data[i];
		}

		@Override
		public String toString(){
			return name + " " + getPrefPos();
		}

		public String getPrefPos(){
			return prefPos;
		}
	}

	/**
	 * Constructs a FifaDatabase from a .csv file formatted like fifa_small.csv (top row is a header with Name as
	 * the first entry, each subsequent row is a player where the first column is their name, second is their
	 * preferred position, and the rest are their attributes).
	 * @param f The Path to the file in question.
	 * @throws IOException
	 */
	public FifaDatabase(Path f) throws IOException {
		Files.lines(f, StandardCharsets.ISO_8859_1)
				.filter(line -> !line.startsWith("Name,"))
				.map(line -> line.split(","))
				.forEach(row -> {
					String name = row[0];
					String prefPos = row[1];
					double[] data = new double[row.length - 2];
					for(int i = 0; i < data.length; i++){
						data[i] = Double.parseDouble(row[i+2]);
					}
					add(new Player(data, name, prefPos));
					ndims = data.length;
				});
	}

	@Override
	public int getDimensionality() {
		return ndims;
	}


	/**
	 * The location to output the file to.
	 */
	public static final String OUT_FILE = "data/out.txt";

	/**
	 * Runs one run of SUBCLU on data/fifa_small.csv and saves stats about the result to OUT_FILE.
	 * Note that currently we just append the results to the end of OUT_FILE, though this could be changed pretty
	 * quickly.
	 * @param args No args.
	 */
	public static void main(String[] args){

		//Load data
		FifaDatabase db = null;
		try {
			db = new FifaDatabase(Paths.get("data/fifa_small.csv"));
		}
		catch(IOException e){
			System.out.println(e);
			System.exit(1);
		}
		assert db != null;

		//Cluster data
		Clustering clustering = SUBCLU.go(new Params(10, 200, db));

		//print stats about the data
		clustering.forEachCluster(subspace -> {
			File out = new File(OUT_FILE);

			return subset -> {
				Stats sim = getClusterSimilarity(subset);
				try(FileWriter fw = new FileWriter(out, true)){
				    fw.append(Subspace.pprint(subspace));
					fw.append(sim.getSim() + "\t" + sim.getPos() + "\n");
					fw.append("\t" + subset.toString() + "\n");
				} catch(IOException e){
					System.err.println(e);
					System.exit(1);
				}
			};
		});

		System.out.println();
	}

	//Gets the single-position similarity of a cluster, defined as the max over all positions of:
	//(Players in cluster who play position)/(total number of players in cluster)
	private static Stats getClusterSimilarity(Subset subset){
		Map<String, Integer> counts = new HashMap<>();
		subset.stream().map(p -> ((Player) p)
				.getPrefPos())
				.forEach(p -> counts.compute(p, (key, val) -> val == null ? 1 : val + 1));
				//int maxCount = counts.keySet().stream().mapToInt(k -> counts.get(k)).max().orElse(-1);
        double maxCount = -1;
        String ppos = null;
        for(Map.Entry<String, Integer> e : counts.entrySet()){
            if(e.getValue() > maxCount){
                ppos = e.getKey();
                maxCount = e.getValue();
            }
        }
		return new Stats((maxCount) / subset.size(), ppos);
	}

	//Holds information about the statistics of a cluster
	private static class Stats{

        double sim;
	    String pos;

	    Stats(double sim, String pos){
	        this.sim = sim;
	        this.pos = pos;
        }

        public double getSim() {
            return sim;
        }

        public String getPos() {
            return pos;
        }

    }
}
