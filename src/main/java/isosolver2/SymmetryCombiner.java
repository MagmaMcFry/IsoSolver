package isosolver2;

import isosolver2.demo.FormatUtils;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.*;

public class SymmetryCombiner {

    public ArrayList<IsohedralTilingSolver2Data> combinedList = new ArrayList<>();
    int sizeOfList;
    ArrayList<String[]> namespaces = new ArrayList<>();

    public int getSizeOfList() {
        return sizeOfList;
    }

    int firstMirror(int number, int[] mirrors) {
        for (int i = 0; i < mirrors.length; i++) {
            if (mirrors[i] == number) {
                return Math.min(i, number);
            }
        }
        return -1;
    }

    public void combineList(Symmetry listOne, Symmetry listTwo, int vertexWeight) {
        for (int i = 0; i < listOne.getSymmetryClasses(); i++) {
            int lengthOne = listOne.symmetryList.get(i).getNumEdges();
            for (int j = 0; j < listTwo.getSymmetryClasses(); j++) {
                int[] newRightNeighbors = listTwo.symmetryList.get(j).getEdgeRightNeighbors().clone();
                int[] newLeftNeighbors = listTwo.symmetryList.get(j).getEdgeLeftNeighbors().clone();
                int[] newMirrors = listTwo.symmetryList.get(j).getEdgeMirrors().clone();
                for (int k = 0; k < listTwo.symmetryList.get(j).getNumEdges(); k++) {
                    newRightNeighbors[k] += lengthOne;
                    newLeftNeighbors[k] += lengthOne;
                    newMirrors[k] += lengthOne;
                }
                int combinedLength = lengthOne + listTwo.symmetryList.get(j).getNumEdges();
                IsohedralTilingSolver2Data combo = new IsohedralTilingSolver2Data(combinedLength);
                combo.setDesiredVertexWeight(vertexWeight);

                int[] combinedRightNeighbors = new int[combinedLength];
                int[] combinedLeftNeighbors = new int[combinedLength];
                int[] combinedMirrors = new int[combinedLength];
                int[] combinedLeftVertexWeights = new int[combinedLength];

                System.arraycopy(listOne.symmetryList.get(i).getEdgeRightNeighbors(),0,combinedRightNeighbors,0,lengthOne);
                System.arraycopy(newRightNeighbors,0,combinedRightNeighbors,lengthOne,listTwo.symmetryList.get(j).getNumEdges());

                System.arraycopy(listOne.symmetryList.get(i).getEdgeLeftNeighbors(),0,combinedLeftNeighbors,0,lengthOne);
                System.arraycopy(newLeftNeighbors,0,combinedLeftNeighbors,lengthOne,listTwo.symmetryList.get(j).getNumEdges());

                System.arraycopy(listOne.symmetryList.get(i).getEdgeMirrors(),0,combinedMirrors,0,lengthOne);
                System.arraycopy(newMirrors,0,combinedMirrors,lengthOne,listTwo.symmetryList.get(j).getNumEdges());

                System.arraycopy(listOne.symmetryList.get(i).getLeftVertexWeights(),0,combinedLeftVertexWeights,0,lengthOne);
                System.arraycopy(listTwo.symmetryList.get(j).getLeftVertexWeights(),0,combinedLeftVertexWeights,lengthOne,listTwo.symmetryList.get(j).getNumEdges());

                combo.setEdgeRightNeighbors(combinedRightNeighbors);
                combo.setEdgeLeftNeighbors(combinedLeftNeighbors);
                combo.setEdgeMirrors(combinedMirrors);
                combo.setLeftVertexWeights(combinedLeftVertexWeights);
                combo.setEdgeTypeSimple();
                combo.setEnsureConnectivity(true);

                combinedList.add(combo);
                String[] names = new String[combinedLength];
                for (int k = 0; k < combinedLength; k++) {
                    int index = firstMirror(k, combo.getEdgeMirrors());
                    if (index < lengthOne) {
                        names[k] = Integer.toString(index);
                    }
                    else {
                        names[k] = Integer.toString(index - lengthOne) + "'";
                    }
                }
                namespaces.add(names);
            }
        }
        sizeOfList = combinedList.size();
    }

    public void combineList3(Symmetry listOne, Symmetry listTwo, Symmetry listThree, int vertexWeight) {
        for (int i = 0; i < listOne.getSymmetryClasses(); i++) {
            int lengthOne = listOne.symmetryList.get(i).getNumEdges();
            for (int j = 0; j < listTwo.getSymmetryClasses(); j++) {
                int lengthTwo = listTwo.symmetryList.get(j).getNumEdges();
                int[] newRightNeighborsTwo = listTwo.symmetryList.get(j).getEdgeRightNeighbors().clone();
                int[] newLeftNeighborsTwo = listTwo.symmetryList.get(j).getEdgeLeftNeighbors().clone();
                int[] newMirrorsTwo = listTwo.symmetryList.get(j).getEdgeMirrors().clone();
                for (int l = 0; l < listTwo.symmetryList.get(j).getNumEdges(); l++) {
                    newRightNeighborsTwo[l] += lengthOne;
                    newLeftNeighborsTwo[l] += lengthOne;
                    newMirrorsTwo[l] += lengthOne;
                }
                for (int k = 0; k < listThree.getSymmetryClasses(); k++) {
                    int lengthThree = listThree.symmetryList.get(k).getNumEdges();
                    int[] newRightNeighborsThree = listThree.symmetryList.get(k).getEdgeRightNeighbors().clone();
                    int[] newLeftNeighborsThree = listThree.symmetryList.get(k).getEdgeLeftNeighbors().clone();
                    int[] newMirrorsThree = listThree.symmetryList.get(k).getEdgeMirrors().clone();
                    for (int l = 0; l < listThree.symmetryList.get(k).getNumEdges(); l++) {
                        newRightNeighborsThree[l] += lengthOne+lengthTwo;
                        newLeftNeighborsThree[l] += lengthOne+lengthTwo;
                        newMirrorsThree[l] += lengthOne+lengthTwo;
                    }
                    int combinedLength = lengthOne + lengthTwo + lengthThree;
                    IsohedralTilingSolver2Data combo = new IsohedralTilingSolver2Data(combinedLength);
                    combo.setDesiredVertexWeight(vertexWeight);

                    int[] combinedRightNeighbors = new int[combinedLength];
                    int[] combinedLeftNeighbors = new int[combinedLength];
                    int[] combinedMirrors = new int[combinedLength];
                    int[] combinedLeftVertexWeights = new int[combinedLength];

                    System.arraycopy(listOne.symmetryList.get(i).getEdgeRightNeighbors(),0,combinedRightNeighbors,0,lengthOne);
                    System.arraycopy(newRightNeighborsTwo,0,combinedRightNeighbors,lengthOne,lengthTwo);
                    System.arraycopy(newRightNeighborsThree,0,combinedRightNeighbors,lengthOne+lengthTwo,lengthThree);

                    System.arraycopy(listOne.symmetryList.get(i).getEdgeLeftNeighbors(),0,combinedLeftNeighbors,0,lengthOne);
                    System.arraycopy(newLeftNeighborsTwo,0,combinedLeftNeighbors,lengthOne,lengthTwo);
                    System.arraycopy(newLeftNeighborsThree,0,combinedLeftNeighbors,lengthOne+lengthTwo,lengthThree);

                    System.arraycopy(listOne.symmetryList.get(i).getEdgeMirrors(),0,combinedMirrors,0,lengthOne);
                    System.arraycopy(newMirrorsTwo,0,combinedMirrors,lengthOne,lengthTwo);
                    System.arraycopy(newMirrorsThree,0,combinedMirrors,lengthOne+lengthTwo,lengthThree);

                    System.arraycopy(listOne.symmetryList.get(i).getLeftVertexWeights(),0,combinedLeftVertexWeights,0,lengthOne);
                    System.arraycopy(listTwo.symmetryList.get(j).getLeftVertexWeights(),0,combinedLeftVertexWeights,lengthOne,lengthTwo);
                    System.arraycopy(listThree.symmetryList.get(k).getLeftVertexWeights(),0,combinedLeftVertexWeights,lengthOne+lengthTwo,lengthThree);

                    combo.setEdgeRightNeighbors(combinedRightNeighbors);
                    combo.setEdgeLeftNeighbors(combinedLeftNeighbors);
                    combo.setEdgeMirrors(combinedMirrors);
                    combo.setLeftVertexWeights(combinedLeftVertexWeights);
                    combo.setEdgeTypeSimple();
                    combo.setEnsureConnectivity(true);

                    combinedList.add(combo);
                    String[] names = new String[combinedLength];
                    for (int l = 0; l < combinedLength; l++) {
                        int index = firstMirror(l, combo.getEdgeMirrors());
                        if (index < lengthOne) {
                            names[l] = Integer.toString(index);
                        }
                        else if (index < lengthOne+lengthTwo) {
                            names[l] = Integer.toString(index - lengthOne) + "'";
                        }
                        else {
                            names[l] = Integer.toString(index - lengthOne - lengthTwo) + "''";
                        }
                    }
                    namespaces.add(names);
                }
            }
        }
        sizeOfList = combinedList.size();
    }

    public void combineList4(Symmetry listOne, Symmetry listTwo, Symmetry listThree, Symmetry listFour, int vertexWeight) {
        for (int i = 0; i < listOne.getSymmetryClasses(); i++) {
            int lengthOne = listOne.symmetryList.get(i).getNumEdges();
            for (int j = 0; j < listTwo.getSymmetryClasses(); j++) {
                int lengthTwo = listTwo.symmetryList.get(j).getNumEdges();
                int[] newRightNeighborsTwo = listTwo.symmetryList.get(j).getEdgeRightNeighbors().clone();
                int[] newLeftNeighborsTwo = listTwo.symmetryList.get(j).getEdgeLeftNeighbors().clone();
                int[] newMirrorsTwo = listTwo.symmetryList.get(j).getEdgeMirrors().clone();
                for (int l = 0; l < listTwo.symmetryList.get(j).getNumEdges(); l++) {
                    newRightNeighborsTwo[l] += lengthOne;
                    newLeftNeighborsTwo[l] += lengthOne;
                    newMirrorsTwo[l] += lengthOne;
                }
                for (int k = 0; k < listThree.getSymmetryClasses(); k++) {
                    int lengthThree = listThree.symmetryList.get(k).getNumEdges();
                    int[] newRightNeighborsThree = listThree.symmetryList.get(k).getEdgeRightNeighbors().clone();
                    int[] newLeftNeighborsThree = listThree.symmetryList.get(k).getEdgeLeftNeighbors().clone();
                    int[] newMirrorsThree = listThree.symmetryList.get(k).getEdgeMirrors().clone();
                    for (int l = 0; l < listThree.symmetryList.get(k).getNumEdges(); l++) {
                        newRightNeighborsThree[l] += lengthOne+lengthTwo;
                        newLeftNeighborsThree[l] += lengthOne+lengthTwo;
                        newMirrorsThree[l] += lengthOne+lengthTwo;
                    }
                    for (int l = 0; l < listFour.getSymmetryClasses(); l++) {
                        int lengthFour = listFour.symmetryList.get(l).getNumEdges();
                        int[] newRightNeighborsFour = listFour.symmetryList.get(l).getEdgeRightNeighbors().clone();
                        int[] newLeftNeighborsFour = listFour.symmetryList.get(l).getEdgeLeftNeighbors().clone();
                        int[] newMirrorsFour = listFour.symmetryList.get(l).getEdgeMirrors().clone();
                        for (int m = 0; m < listThree.symmetryList.get(l).getNumEdges(); m++) {
                            newRightNeighborsFour[m] += lengthOne+lengthTwo+lengthThree;
                            newLeftNeighborsFour[m] += lengthOne+lengthTwo+lengthThree;
                            newMirrorsFour[m] += lengthOne+lengthTwo+lengthThree;
                        }
                        int combinedLength = lengthOne + lengthTwo + lengthThree + lengthFour;
                        IsohedralTilingSolver2Data combo = new IsohedralTilingSolver2Data(combinedLength);
                        combo.setDesiredVertexWeight(vertexWeight);

                        int[] combinedRightNeighbors = new int[combinedLength];
                        int[] combinedLeftNeighbors = new int[combinedLength];
                        int[] combinedMirrors = new int[combinedLength];
                        int[] combinedLeftVertexWeights = new int[combinedLength];

                        System.arraycopy(listOne.symmetryList.get(i).getEdgeRightNeighbors(),0,combinedRightNeighbors,0,lengthOne);
                        System.arraycopy(newRightNeighborsTwo,0,combinedRightNeighbors,lengthOne,lengthTwo);
                        System.arraycopy(newRightNeighborsThree,0,combinedRightNeighbors,lengthOne+lengthTwo,lengthThree);
                        System.arraycopy(newRightNeighborsFour,0,combinedRightNeighbors,lengthOne+lengthTwo+lengthThree,lengthFour);

                        System.arraycopy(listOne.symmetryList.get(i).getEdgeLeftNeighbors(),0,combinedLeftNeighbors,0,lengthOne);
                        System.arraycopy(newLeftNeighborsTwo,0,combinedLeftNeighbors,lengthOne,lengthTwo);
                        System.arraycopy(newLeftNeighborsThree,0,combinedLeftNeighbors,lengthOne+lengthTwo,lengthThree);
                        System.arraycopy(newLeftNeighborsFour,0,combinedLeftNeighbors,lengthOne+lengthTwo+lengthThree,lengthFour);

                        System.arraycopy(listOne.symmetryList.get(i).getEdgeMirrors(),0,combinedMirrors,0,lengthOne);
                        System.arraycopy(newMirrorsTwo,0,combinedMirrors,lengthOne,lengthTwo);
                        System.arraycopy(newMirrorsThree,0,combinedMirrors,lengthOne+lengthTwo,lengthThree);
                        System.arraycopy(newMirrorsFour,0,combinedMirrors,lengthOne+lengthTwo+lengthThree,lengthFour);

                        System.arraycopy(listOne.symmetryList.get(i).getLeftVertexWeights(),0,combinedLeftVertexWeights,0,lengthOne);
                        System.arraycopy(listTwo.symmetryList.get(j).getLeftVertexWeights(),0,combinedLeftVertexWeights,lengthOne,lengthTwo);
                        System.arraycopy(listThree.symmetryList.get(k).getLeftVertexWeights(),0,combinedLeftVertexWeights,lengthOne+lengthTwo,lengthThree);
                        System.arraycopy(listFour.symmetryList.get(l).getLeftVertexWeights(),0,combinedLeftVertexWeights,lengthOne+lengthTwo+lengthThree,lengthFour);

                        combo.setEdgeRightNeighbors(combinedRightNeighbors);
                        combo.setEdgeLeftNeighbors(combinedLeftNeighbors);
                        combo.setEdgeMirrors(combinedMirrors);
                        combo.setLeftVertexWeights(combinedLeftVertexWeights);
                        combo.setEdgeTypeSimple();
                        combo.setEnsureConnectivity(true);

                        combinedList.add(combo);
                        String[] names = new String[combinedLength];
                        for (int m = 0; m < combinedLength; m++) {
                            int index = firstMirror(m, combo.getEdgeMirrors());
                            if (index < lengthOne) {
                                names[m] = Integer.toString(index);
                            }
                            else if (index < lengthOne+lengthTwo) {
                                names[m] = Integer.toString(index - lengthOne) + "'";
                            }
                            else if (index < lengthOne+lengthTwo+lengthThree) {
                                names[m] = Integer.toString(index - lengthOne - lengthTwo) + "''";
                            }
                            else {
                                names[m] = Integer.toString(index - lengthOne - lengthTwo - lengthThree) + "'''";
                            }
                        }
                        namespaces.add(names);
                    }
                }
            }
        }
        sizeOfList = combinedList.size();
    }

    public void solveCombinations(int vertexWeight) {
        for (int i = 0; i < getSizeOfList(); i++) {
            String[] names = namespaces.get(i);

            IsohedralTilingSolver2 solver = new IsohedralTilingSolver2(combinedList.get(i));

            System.out.println("combo " + (i+1) + "/" + getSizeOfList() + "(size " + combinedList.get(i).getNumEdges() + ")");
            for (int j = 0; j < combinedList.get(i).getNumEdges(); j++) {
                System.out.print(names[j] + " ");
                int k = j;
                int max = j;
                boolean cycle = false;
                while (!cycle) {
                    k = combinedList.get(i).edgeRightNeighbors[k];
                    if (k > max) {
                        max = k;
                    }
                    if (k == j) {
                        cycle = true;
                    }
                }
                if (max == j) {
                    System.out.print("| ");
                }
            }
            System.out.println();
            //combinedList.get(i).printDebugInfo();

            List<int[]> solutions = solver.findAllSolutions();
            List<int[]> symmetries = combinedList.get(i).getSymmetries();

            List<int[]> canonicalSolutions = combinedList.get(i).canonicalizeSolutions(solutions);

            if(canonicalSolutions.size() > 0) {
                for (int j = 0; j < combinedList.get(i).getNumEdges(); j++) {
                    System.out.print(names[j] + " ");
                    int k = j;
                    int max = j;
                    boolean cycle = false;
                    while (!cycle) {
                        k = combinedList.get(i).edgeRightNeighbors[k];
                        if (k > max) {
                            max = k;
                        }
                        if (k == j) {
                            cycle = true;
                        }
                    }
                    if (max == j) {
                        System.out.print("| ");
                    }
                }
                /*for (String name : names) {
                    System.out.print(name + " ");
                }*/
                System.out.println();

                combinedList.get(i).printDebugInfo();

                canonicalSolutions = canonicalSolutions.stream().sorted((o1,o2) -> {
                    for (int j = 0; j < o1.length; j++) {
                        int c = Integer.compare(o1[j],o2[j]);
                        if (c != 0) {
                            return c;
                        }
                    }
                    return Integer.compare(o1.length, o2.length);
                }).collect(Collectors.toList());

                System.out.println("Canonical solutions: " + canonicalSolutions.size());
                for (int[] canonicalSolution : canonicalSolutions) {
                    System.out.println(FormatUtils.formatSolution(canonicalSolution, combinedList.get(i).getEdgeMirrors(), names));
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
