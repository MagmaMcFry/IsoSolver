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
		int NUM_POLYGON_SIDES = 4;
		int NUM_POLYGON_TILES = 1;
		int TILING_ORDER = 4;

		long startNanos = System.nanoTime();
		if (NUM_POLYGON_TILES >= TILING_ORDER) {
			for (int sym = TILING_ORDER; sym > 1; --sym) {
				if ((TILING_ORDER % sym == 0) && (NUM_POLYGON_TILES % sym == 0)) {
					List<Polyform> polyformList = Polyform.getAllVSPolyforms(NUM_POLYGON_SIDES, NUM_POLYGON_TILES, TILING_ORDER, sym);
					System.out.println(String.format(Locale.ROOT,
							"Testing all %d abstract %s with %d-fold vertex symmetry",
							polyformList.size(),
							Polyform.getPolyformName(NUM_POLYGON_SIDES, NUM_POLYGON_TILES, true),
							sym
					));

					Map<Polyform, Integer> solutionCounts = new HashMap<>();
					List<Polyform> solvingPolyforms = new ArrayList<>();
					List<Polyform> nonSolvingPolyforms = new ArrayList<>();
					for (Polyform p : polyformList) {
						//System.out.println("Searching order-" + TILING_ORDER + " regular tilings using " + p + ":");
						IsohedralTilingSolver solver = new IsohedralTilingSolver(p.getVertexOrders(), TILING_ORDER, false);
						solver.solve();
						//System.out.println("Found " + solver.getSolutionCount() + " solutions");
						int count = solver.getSolutionCount();
						if (count > 0) {
							solvingPolyforms.add(p);
							if (count == 1) {
								System.out.println(p.toString() + " - unique solution");
							}
						} else {
							nonSolvingPolyforms.add(p);
						}
						solutionCounts.put(p, count);
					}
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
		}
		for (int sym = NUM_POLYGON_SIDES; sym > 1; --sym) {
			if ((NUM_POLYGON_SIDES % sym == 0) && (NUM_POLYGON_TILES % sym == 1)) {
				List<Polyform> polyformList = Polyform.getAllCSPolyforms(NUM_POLYGON_SIDES, NUM_POLYGON_TILES, sym);
				System.out.println(String.format(Locale.ROOT,
						"Testing all %d abstract %s with %d-fold tile-center symmetry",
						polyformList.size(),
						Polyform.getPolyformName(NUM_POLYGON_SIDES, NUM_POLYGON_TILES, true),
						sym
				));

				Map<Polyform, Integer> solutionCounts = new HashMap<>();
				List<Polyform> solvingPolyforms = new ArrayList<>();
				List<Polyform> nonSolvingPolyforms = new ArrayList<>();
				for (Polyform p : polyformList) {
					//System.out.println("Searching order-" + TILING_ORDER + " regular tilings using " + p + ":");
					IsohedralTilingSolver solver = new IsohedralTilingSolver(p.getVertexOrders(), TILING_ORDER, false);
					solver.solve();
					//System.out.println("Found " + solver.getSolutionCount() + " solutions");
					int count = solver.getSolutionCount();
					if (count > 0) {
						solvingPolyforms.add(p);
						if (count == 1) {
							System.out.println(p.toString() + " - unique solution");
						}
					} else {
						nonSolvingPolyforms.add(p);
					}
					solutionCounts.put(p, count);
				}
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
		if (NUM_POLYGON_TILES % 2 == 0) {
			List<Polyform> polyformList = Polyform.getAllESPolyforms(NUM_POLYGON_SIDES, NUM_POLYGON_TILES);
			System.out.println(String.format(Locale.ROOT,
					"Testing all %d abstract %s with edge symmetry",
					polyformList.size(),
					Polyform.getPolyformName(NUM_POLYGON_SIDES, NUM_POLYGON_TILES, true)
			));

			Map<Polyform, Integer> solutionCounts = new HashMap<>();
			List<Polyform> solvingPolyforms = new ArrayList<>();
			List<Polyform> nonSolvingPolyforms = new ArrayList<>();
			for (Polyform p : polyformList) {
				//System.out.println("Searching order-" + TILING_ORDER + " regular tilings using " + p + ":");
				IsohedralTilingSolver solver = new IsohedralTilingSolver(p.getVertexOrders(), TILING_ORDER, false);
				solver.solve();
				//System.out.println("Found " + solver.getSolutionCount() + " solutions");
				int count = solver.getSolutionCount();
				if (count > 0) {
					solvingPolyforms.add(p);
					if (count == 1) {
						System.out.println(p.toString() + " - unique solution");
					}
				} else {
					nonSolvingPolyforms.add(p);
				}
				solutionCounts.put(p, count);
			}
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
			IsohedralTilingSolver solver = new IsohedralTilingSolver(p.getVertexOrders(), TILING_ORDER, false);
			solver.solve();
			//System.out.println("Found " + solver.getSolutionCount() + " solutions");
			int count = solver.getSolutionCount();
			if (count > 0) {
				solvingPolyforms.add(p);
				if (count == 1) {
					System.out.println(p.toString() + " - unique solution");
				}
			} else {
				nonSolvingPolyforms.add(p);
			}
			solutionCounts.put(p, count);
		}
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
		long endNanos = System.nanoTime();
		System.out.println("Time to test: " + ((endNanos - startNanos) / 1_000_000L) + " ms");
	}
}
