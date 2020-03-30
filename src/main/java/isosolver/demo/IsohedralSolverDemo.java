package isosolver.demo;

import isosolver.IsohedralTilingSolver;

import java.util.Collections;
import java.util.List;

public class IsohedralSolverDemo {

	public static void main(String[] args) {
		System.out.println("Isohedral Solver Demo");
		IsohedralTilingSolver s = new IsohedralTilingSolver(4, true);
		s.addPolyhedron(new int[]{1,2,1,3,1,1,3,2,1,1,2,2,3,1});

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
		System.out.println("Number of steps taken: " + s.getStepCount());
	}

	private static class GlueNotation implements Comparable<GlueNotation> {
		private final int firstEdge, secondEdge;
		private final int firstPolygon, secondPolygon;
		private final boolean flip;

		GlueNotation(IsohedralTilingSolver.Gluing g) {
			int e1 = g.isFirstEdgeReversed() ? g.firstEdgeFirstVertex() : g.firstEdgeSecondVertex();
			int e2 = g.isSecondEdgeReversed() ? g.secondEdgeFirstVertex() : g.secondEdgeSecondVertex();
			int p1 = g.firstEdgePolygon();
			int p2 = g.secondEdgePolygon();
			if (p1 < p2 || (p1 == p2 && e1 < e2)) {
				firstEdge = e1;
				firstPolygon = p1;
				secondEdge = e2;
				secondPolygon = p2;
			} else {
				firstEdge = e2;
				firstPolygon = p2;
				secondEdge = e1;
				secondPolygon = p1;
			}
			flip = g.isFirstEdgeReversed() != g.isSecondEdgeReversed();
		}

		@Override
		public int compareTo(GlueNotation o) {
			int c = Integer.compare(firstPolygon, o.firstPolygon);
			if (c != 0) return c;
			return Integer.compare(firstEdge, o.firstEdge);
		}

		@Override
		public String toString() {
			String firstString = firstEdge + primes(firstPolygon);
			String secondString = secondEdge + primes(secondPolygon);
			return (flip ? "[" : "(") + ((firstPolygon == secondPolygon && firstEdge == secondEdge) ? "" + firstString : firstString + " " + secondString) + (flip ? "]" : ")");
		}

		private static String primes(int n) {
			return String.join("", Collections.nCopies(n, "'"));
		}
	}
}
