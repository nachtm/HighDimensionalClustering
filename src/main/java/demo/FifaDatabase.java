package demo;

import spinacht.Params;
import spinacht.data.Clustering;
import spinacht.data.Database;
import spinacht.data.Point;
import spinacht.data.Subspace;
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
 * Created by Micah on 5/24/2017.
 */
public class FifaDatabase extends ArrayList<Point> implements Database {

	private int ndims;

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


	public static final String OUT_FILE = "data/out.txt";

	public static void main(String[] args){
		FifaDatabase db = null;
		try {
			db = new FifaDatabase(Paths.get("data/fifa_small.csv"));
		}
		catch(IOException e){
			System.out.println(e);
			System.exit(1);
		}
		assert db != null;

		Clustering clustering = SUBCLU.go(new Params(5, 100, db), true);

		clustering.forEachCluster(subspace -> {
			File out = new File(OUT_FILE);
			try(FileWriter fw = new FileWriter(out, true)){
				fw.append(Subspace.pprint(subspace) + "\n");
			} catch(IOException e){
				System.err.println(e);
				System.exit(1);
			}
			return subset -> {
				try(FileWriter fw = new FileWriter(out, true)){
					fw.append("\t" + subset.toString() + "\n");
				} catch(IOException e){
					System.err.println(e);
					System.exit(1);
				}
			};
		});

		clustering.forEachCluster(subspace -> {

					return subset -> {

						Map<String, Integer> counts = new HashMap<>();
						subset.stream().map(p -> ((Player) p)
								.getPrefPos())
								.forEach(p -> counts.compute(p, (key, val) -> val == null ? 1 : val + 1));
						int maxCount = counts.keySet().stream().mapToInt(k -> counts.get(k)).max().orElse(-1);
						System.out.println(((double)maxCount) / subset.size());
					};
				});
		System.out.println();
	}
}
