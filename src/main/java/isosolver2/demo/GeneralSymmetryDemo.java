package isosolver2.demo;

import isosolver2.SymmetryCombiner;

public class GeneralSymmetryDemo {
    public static void main(String[] args) {
        new SymmetryCombiner(21,
                new int[]{4,4,4},
                new int[]{4,4,4},
                new int[]{5,5,5,5},
                new int[]{6,6,6,6,6}
        ).solveCombinations();
    }
}
