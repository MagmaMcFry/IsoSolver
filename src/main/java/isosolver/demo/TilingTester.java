package isosolver.demo;

import isosolver.IsohedralTilingSolver;
import isosolver.Polyform;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TilingTester {
	public static void main(String[] args) {
		int NUM_POLYGON_SIDES = 3;
		int NUM_POLYGON_TILES = 9;
		int TILING_ORDER = 7;

		long startNanos = System.nanoTime();
		List<Polyform> polyformList = Polyform.getAllPolyforms(NUM_POLYGON_SIDES, NUM_POLYGON_TILES);
		System.out.println(String.format(Locale.ROOT,
				"Testing all %d abstract %s",
				polyformList.size(),
				Polyform.getPolyformName(NUM_POLYGON_SIDES, NUM_POLYGON_TILES, true)
		));

		List<Polyform> solvingPolyforms = new ArrayList<>();
		for (Polyform p : polyformList) {
			//System.out.println("Searching order-" + TILING_ORDER + " regular tilings using " + p + ":");
			IsohedralTilingSolver solver = new IsohedralTilingSolver(p.getVertexOrders(), TILING_ORDER, false);
			solver.solve();
			//System.out.println("Found " + solver.getSolutionCount() + " solutions");
			if (solver.getSolutionCount() > 0) {
				solvingPolyforms.add(p);
			}
		}
		long endNanos = System.nanoTime();
		System.out.println("Time to test: " + ((endNanos - startNanos) / 1_000_000L) + " ms");
		System.out.println(String.format(Locale.ROOT,
				"Found %d %s tiling {%d,%d}",
				solvingPolyforms.size(),
				Polyform.getPolyformName(NUM_POLYGON_SIDES, NUM_POLYGON_TILES, solvingPolyforms.size() != 1),
				NUM_POLYGON_SIDES, TILING_ORDER
		));
	}
}
