package isosolver.demo;

import isosolver.IsohedralTilingSolver;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class IsohedralSolverDemo {

	public static void main(String[] args) {

		IsohedralTilingSolver s = new IsohedralTilingSolver(
				//new int[]{1,1,3,1,1,3,1,1,3,1,1,3}, 4, true // X pentomino
				//new int[]{1,1,2,1,3,1,1,3,1,1,2,3}, 4, true // F pentomino
				//new int[]{1,1,2,1,3,1,1,3,1,1,2,3}, 4, true, OrientedEdgeType.makePositiveArray(12) // F pentomino, no reflections
				//new int[]{1,2,3,1,2,3}, 5, true // Straight tetriamond
				//new int[]{1,2,3,1,2,3}, 4, true // Straight tetriamond
				//new int[]{1,1,3,1,3,1,2,1,1,3,1,3,1,2}, 12, false // Zigzag hexomino
				//new int[]{1,1,2,1,3,1,1,3,1,1,2,3}, 16, false // F pentomino
				//new int[]{1,5,1,2,4,1,2,4,1,3}, 7, true // That one octiamond
				//new int[]{1,1,2,2,1,2,2,1,1,2,3,2}, 7, true // V pentomino
				//new int[]{1,1,2,3,1,1,2,1,3,1,1,3}, 7, true // F pentomino
				//new int[]{1,1,2,1,2,2,2,2,2,2,1,1,2,2,2,2,2,3}, 11, false // L octomino
				//new int[]{1,3,1,4,1,3,1,4}, 7, true // H hexiamond
				//new int[]{1,3,1,4}, 7, true // Half H hexiamond
				new int[]{1,1,2,2,1,1,2,2}, 5, true // Straight tromino
				//new int[]{1,1,2,2}, 5, true // Half straight tromino
		);

		long startNanos = System.nanoTime();
		s.solve();
		long endNanos = System.nanoTime();
		System.out.println("Time to solve: " + ((endNanos - startNanos) / 1_000_000L) + " ms");
		System.out.println("Number of solutions: " + s.getSolutionCount());

		if (s.getSolutions() != null && s.getSolutionCount() < 30) {
			for (List<IsohedralTilingSolver.Gluing> solution : s.getSolutions()) {
				solution.stream().map(GlueNotation::new).sorted().forEach(System.out::print);
				System.out.println();
			}
		}
	}

	private enum OrientedEdgeType implements IsohedralTilingSolver.EdgeType {
		POSITIVE, NEGATIVE;

		public static OrientedEdgeType[] makePositiveArray(int length) {
			OrientedEdgeType[] array = new OrientedEdgeType[length];
			Arrays.fill(array, POSITIVE);
			return array;
		}

		@Override
		public OrientedEdgeType reverse() {
			switch(this) {
				case POSITIVE:
					return NEGATIVE;
				case NEGATIVE:
					return POSITIVE;
			}
			return POSITIVE;
		}

		@Override
		public OrientedEdgeType opposite() {
			return this;
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
