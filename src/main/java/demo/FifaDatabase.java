package demo;

import spinacht.Params;
import spinacht.data.Database;
import spinacht.data.Point;
import spinacht.data.Subspace;
import spinacht.subclu.SUBCLU;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created by Micah on 5/24/2017.
 */
public class FifaDatabase extends ArrayList<Point> implements Database<Point> {

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
	}

	public FifaDatabase(Path f) throws IOException {
		Files.lines(f)
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

	public static void main(String[] args){
		FifaDatabase db = null;
		try {
			db = new FifaDatabase(Paths.get(args[0]));
		}
		catch(IOException e){
			System.out.println(e);
			System.exit(1);
		}
		assert db != null;

		SUBCLU.go(new Params(100, 10, db)).forEachCluster(subspace -> {
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
	}
}
