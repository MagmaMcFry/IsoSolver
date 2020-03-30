package isosolver2.demo;

import isosolver2.IsohedralTilingSolver2;
import isosolver2.IsohedralTilingSolver2Data;
import isosolver2.Symmetry;
import isosolver2.SymmetryCombiner;

import java.util.List;

public class SymmetryDemo {
	public static void main(String[] args) {
		int vertexWeight = 19;
		int[] cycle1 = new int[]{5,5,5,5,5};
		int[] cycle2 = new int[]{4,4,4};
		Symmetry symmetryOne = new Symmetry(cycle1);
		Symmetry symmetryTwo = new Symmetry(cycle2);

		symmetryOne.createAllSymmetryCycles(vertexWeight);
		symmetryTwo.createAllSymmetryCycles(vertexWeight);

		SymmetryCombiner combinedSymmetry = new SymmetryCombiner();
		combinedSymmetry.combineList(symmetryOne, symmetryTwo, vertexWeight);
		combinedSymmetry.solveCombinations(vertexWeight);
	}

}
