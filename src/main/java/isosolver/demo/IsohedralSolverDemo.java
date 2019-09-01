package isosolver.demo;

import isosolver.IsohedralTilingSolver;

import java.util.List;

public class IsohedralSolverDemo {

	public static void main(String[] args) {

		IsohedralTilingSolver s = new IsohedralTilingSolver(4, true);
		s.addPolyhedron(new int[]{3,3,3,1,1,2,1,2,2,1,2,2,1,2,1,1}); // Heptomino with hole
		s.addPolyhedron(new int[]{1,1,1,1}); // Monomino to fill the hole

		long startNanos = System.nanoTime();
		s.solve();
		long endNanos = System.nanoTime();
		System.out.println("Time to solve: " + ((endNanos - startNanos) / 1_000_000L) + " ms");
		System.out.println("Number of solutions: " + s.getSolutionCount());

		if (s.getSolutions() != null && s.getSolutionCount() < 100) {
			for (List<IsohedralTilingSolver.Gluing> solution : s.getSolutions()) {
				solution.stream().map(GlueNotation::new).sorted().forEach(System.out::print);
				System.out.println();
			}
		}
	}

	private static class GlueNotation implements Comparable<GlueNotation> {
		private final int firstEdge, secondEdge;
		private final boolean flip;
		GlueNotation(IsohedralTilingSolver.Gluing g) {
			int e1 = g.isFirstEdgeReversed() ? g.firstEdgeFirstVertex() : g.firstEdgeSecondVertex();
			int e2 = g.isSecondEdgeReversed() ? g.secondEdgeFirstVertex() : g.secondEdgeSecondVertex();
			if (e1 < e2) {
				firstEdge = e1;
				secondEdge = e2;
			} else {
				firstEdge = e2;
				secondEdge = e1;
			}
			flip = g.isFirstEdgeReversed() != g.isSecondEdgeReversed();
		}

		@Override
		public int compareTo(GlueNotation o) {
			return Integer.compare(firstEdge, o.firstEdge);
		}

		@Override
		public String toString() {
			return (flip ? "[" : "(") + (firstEdge == secondEdge ? "" + firstEdge : firstEdge + " " + secondEdge) + (flip ? "]" : ")");
		}
	}
}
