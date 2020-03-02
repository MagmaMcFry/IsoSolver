package isosolver2.demo;

import isosolver2.IsohedralTilingSolver2;
import isosolver2.IsohedralTilingSolver2Data;

import java.util.List;

public class IsohedralSolver2Demo {

	public static void main(String[] args) {
		System.out.println("Isohedral Solver 2 Demo");
		IsohedralTilingSolver2DataBuilder builder = new IsohedralTilingSolver2DataBuilder(4, true);
		builder.addEdgeCycle(new int[]{1,2,1,3,1,1,3,2,1,1,2,2,3,1});
		IsohedralTilingSolver2Data data = builder.build();
		IsohedralTilingSolver2 solver = new IsohedralTilingSolver2(data);
		long startNanos = System.nanoTime();
		List<int[]> solutions = solver.findAllSolutions();
		long endNanos = System.nanoTime();
		System.out.println("Time to solve: " + ((endNanos - startNanos) / 1_000_000L) + " ms");
		System.out.println("Number of solutions: " + solutions.size());
		String[] names = builder.getNames(IsohedralTilingSolver2DataBuilder.EdgeNameFormat.PRIMES);
		int[] mirror = data.getEdgeMirrors();
		for (int[] solution : solutions) {
			System.out.println(FormatUtils.formatSolution(solution, mirror, names));
		}
		System.out.println("Number of steps taken: " + solver.getStepsTaken());
	}

}
