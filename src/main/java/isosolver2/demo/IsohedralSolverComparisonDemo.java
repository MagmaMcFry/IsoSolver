package isosolver2.demo;

import isosolver.IsohedralTilingSolver;
import isosolver2.IsohedralTilingSolver2;

import java.util.Arrays;
import java.util.stream.Collectors;

public class IsohedralSolverComparisonDemo {
	public static void main(String[] args) {
		int desiredVertexWeight = 12;
		int[] polyhedron = new int[]{1,2,1,3,1,1,3,2,2,1,1,2,2,2,3,1};
		System.out.println("Desired vertex weight: " + desiredVertexWeight);
		System.out.println("Polyform: (" + Arrays.stream(polyhedron).mapToObj(Integer::toString).collect(Collectors.joining(", ")) + ")");
		runSolver1(desiredVertexWeight, polyhedron);
		runSolver2(desiredVertexWeight, polyhedron);
	}

	private static void runSolver1(int desiredVertexWeight, int[] polyhedron) {
		System.out.println("Running Isohedral Solver 1");
		IsohedralTilingSolver s = new IsohedralTilingSolver(desiredVertexWeight, false);
		s.addPolyhedron(polyhedron);
		long startNanos = System.nanoTime();
		s.solve();
		long endNanos = System.nanoTime();
		System.out.println("Time to solve: " + ((endNanos - startNanos) / 1_000_000L) + " ms");
		System.out.println("Number of solutions: " + s.getSolutionCount());
	}

	public static void runSolver2(int desiredVertexWeight, int[] polyhedron) {
		System.out.println("Running Isohedral Solver 2");
		IsohedralTilingSolver2DataBuilder builder = new IsohedralTilingSolver2DataBuilder(desiredVertexWeight, true);
		builder.addEdgeCycle(polyhedron);
		IsohedralTilingSolver2 solver = new IsohedralTilingSolver2(builder.build());
		long startNanos = System.nanoTime();
		long solutions = solver.countSolutions();
		long endNanos = System.nanoTime();
		System.out.println("Time to solve: " + ((endNanos - startNanos) / 1_000_000L) + " ms");
		System.out.println("Number of solutions: " + solutions);
	}
}
