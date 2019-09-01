package isosolver.demo;

import isosolver.IsohedralTilingSolver;
import isosolver.Polyform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

		Map<Polyform, Integer> solutionCounts = new HashMap<>();
		List<Polyform> solvingPolyforms = new ArrayList<>();
		List<Polyform> nonSolvingPolyforms = new ArrayList<>();
		for (Polyform p : polyformList) {
			//System.out.println("Searching order-" + TILING_ORDER + " regular tilings using " + p + ":");
			IsohedralTilingSolver solver = new IsohedralTilingSolver(TILING_ORDER, false);
			solver.addPolyhedron(p.getVertexOrders());
			solver.solve();
			//System.out.println("Found " + solver.getSolutionCount() + " solutions");
			int count = solver.getSolutionCount();
			if (count > 0) {
				solvingPolyforms.add(p);
			} else {
				nonSolvingPolyforms.add(p);
			}
			solutionCounts.put(p, count);
		}
		long endNanos = System.nanoTime();
		System.out.println("Time to test: " + ((endNanos - startNanos) / 1_000_000L) + " ms");
		System.out.println(String.format(Locale.ROOT,
				"Found %d %s tiling {%d,%d}",
				solvingPolyforms.size(),
				Polyform.getPolyformName(NUM_POLYGON_SIDES, NUM_POLYGON_TILES, solvingPolyforms.size() != 1),
				NUM_POLYGON_SIDES, TILING_ORDER
		));
		if (solvingPolyforms.size() > 0 && solvingPolyforms.size() < 20) {
			System.out.println("Tiling polyforms:");
			for (Polyform p : solvingPolyforms) {
				System.out.println(p + " (" + solutionCounts.get(p) + ")");
			}
		}
		if (nonSolvingPolyforms.size() > 0 && nonSolvingPolyforms.size() < 20) {
			System.out.println("Non-tiling polyforms:");
			for (Polyform p : nonSolvingPolyforms) {
				System.out.println(p + " (" + solutionCounts.get(p) + ")");
			}
		}
	}
}
