package isosolver2.demo;

import isosolver2.IsohedralTilingSolver2;
import isosolver2.IsohedralTilingSolver2Data;

import java.util.List;

public class MirrorSymmetryDemo {
	public static void main(String[] args) {
		String[] names = {"0", "1", "1", "0", "0'", "1'", "2'", "0'", "1'", "2'"};
		IsohedralTilingSolver2Data data = new IsohedralTilingSolver2Data(10);
		data.setDesiredVertexWeight(4);
		data.setEdgeRightNeighbors(new int[]{1, 2, 3, 0, 5, 6, 4, 9, 7, 8});
		data.setEdgeLeftNeighbors(new int[]{3, 0, 1, 2, 6, 4, 5, 8, 9, 7});
		data.setEdgeMirrors(new int[]{3, 2, 1, 0, 7, 8, 9, 4, 5, 6});
		data.setLeftVertexWeights(new int[]{2, 1, 2, 1, 1, 1, 1, 1, 1, 1});
		data.setEdgeTypeSimple();
		data.setEnsureConnectivity(true);
		data.printDebugInfo();
		IsohedralTilingSolver2 solver = new IsohedralTilingSolver2(data);

		List<int[]> solutions = solver.findAllSolutions();
		System.out.println("Solutions: " + solutions.size());
		for (int[] solution : solutions) {
			System.out.println(FormatUtils.stringify(solution) + ": " + FormatUtils.formatSolution(solution, data.getEdgeMirrors(), names));
		}
		System.out.println();

		List<int[]> symmetries = data.getSymmetries();
		System.out.println("Symmetries: " + symmetries.size());
		for (int[] symmetry : symmetries) {
			System.out.println(FormatUtils.stringify(symmetry) + ": " + FormatUtils.formatPermutationCycles(symmetry));
		}
		System.out.println();

		List<int[]> canonicalSolutions = data.canonicalizeSolutions(solutions);
		System.out.println("Canonical solutions: " + canonicalSolutions.size());
		for (int[] canonicalSolution : canonicalSolutions) {
			System.out.println(FormatUtils.stringify(canonicalSolution) + ": " + FormatUtils.formatSolution(canonicalSolution, data.getEdgeMirrors(), names));
		}
	}

}
