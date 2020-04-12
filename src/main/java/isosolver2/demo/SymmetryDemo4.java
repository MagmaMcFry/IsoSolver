package isosolver2.demo;

import isosolver2.Symmetry;
import isosolver2.SymmetryCombiner;

public class SymmetryDemo4 {

    public static void main(String[] args) {
        int vertexWeight = 21;
        int[] cycle1 = new int[]{4,4,4};
        int[] cycle2 = new int[]{4,4,4};
        int[] cycle3 = new int[]{5,5,5,5};
        int[] cycle4 = new int[]{6,6,6,6,6};
        Symmetry symmetryOne = new Symmetry(cycle1);
        Symmetry symmetryTwo = new Symmetry(cycle2);
        Symmetry symmetryThree = new Symmetry(cycle3);
        Symmetry symmetryFour = new Symmetry(cycle4);

        symmetryOne.createAllSymmetryCycles(vertexWeight);
        symmetryTwo.createAllSymmetryCycles(vertexWeight);
        symmetryThree.createAllSymmetryCycles(vertexWeight);
        symmetryFour.createAllSymmetryCycles(vertexWeight);

        SymmetryCombiner combinedSymmetry = new SymmetryCombiner();
        combinedSymmetry.combineLists(vertexWeight, symmetryOne, symmetryTwo, symmetryThree, symmetryFour);
        combinedSymmetry.solveCombinations();
    }
}
