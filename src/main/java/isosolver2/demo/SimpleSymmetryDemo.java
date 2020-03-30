package isosolver2.demo;

import isosolver2.IsohedralTilingSolver2;
import isosolver2.Symmetry;
import isosolver2.SymmetryCombiner;

import java.util.List;
import java.util.stream.Collectors;

public class SimpleSymmetryDemo {
	static int firstMirror(int number, int[] mirrors) {
		for (int i = 0; i < mirrors.length; i++) {
			if (mirrors[i] == number) {
				return Math.min(i, number);
			}
		}
		return -1;
	}

	public static void main(String[] args) {
		int vertexWeight = 19;
		int[] cycle1 = new int[]{4,9,9,4,14,5,5,5,5,10,10,5,5,5,5,14};

		Symmetry symmetryOne = new Symmetry(cycle1);

		symmetryOne.createAllSymmetryCycles(vertexWeight);

		for (int i = 0; i < symmetryOne.getSymmetryClasses(); i++) {

			IsohedralTilingSolver2 solver = new IsohedralTilingSolver2(symmetryOne.symmetryList.get(i));
			String[] names = new String[symmetryOne.symmetryList.get(i).getNumEdges()];
			for (int k = 0; k < symmetryOne.symmetryList.get(i).getNumEdges(); k++) {
				int index = firstMirror(k, symmetryOne.symmetryList.get(i).getEdgeMirrors());
				names[k] = Integer.toString(index);
			}

			List<int[]> solutions = solver.findAllSolutions();

			List<int[]> symmetries = symmetryOne.symmetryList.get(i).getSymmetries();

			List<int[]> canonicalSolutions = symmetryOne.symmetryList.get(i).canonicalizeSolutions(solutions);

			if(canonicalSolutions.size() > 0) {
				for (String name : names) {
					System.out.print(name + " ");
				}
				System.out.println();

				symmetryOne.symmetryList.get(i).printDebugInfo();

				System.out.println("Canonical solutions: " + canonicalSolutions.size());

				canonicalSolutions = canonicalSolutions.stream().sorted((o1,o2) -> {
					for (int j = 0; j < o1.length; j++) {
						int c = Integer.compare(o1[j],o2[j]);
						if (c != 0) {
							return c;
						}
					}
					return Integer.compare(o1.length, o2.length);
				}).collect(Collectors.toList());

				for (int[] canonicalSolution : canonicalSolutions) {
					System.out.println(FormatUtils.formatSolution(canonicalSolution, symmetryOne.symmetryList.get(i).getEdgeMirrors(), names));
					ab:
					for (int[] symmetry : symmetries) {
						for (int j = 0; j < canonicalSolution.length; j++) {
							if (canonicalSolution[symmetry[j]] != symmetry[canonicalSolution[j]]) {
								continue ab;
							}
						}
						System.out.println(FormatUtils.stringify(symmetry) + ": " + FormatUtils.formatPermutationCycles(symmetry));
					}
				}
				System.out.println();
			}
		}


	}

}
