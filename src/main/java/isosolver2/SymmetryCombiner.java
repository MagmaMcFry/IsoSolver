package isosolver2;

import isosolver2.demo.FormatUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class SymmetryCombiner {

    public ArrayList<IsohedralTilingSolver2Data> combinedList = new ArrayList<>();
    int sizeOfList;
    ArrayList<String[]> namespaces = new ArrayList<>();

    public SymmetryCombiner() {}

    public SymmetryCombiner(int vertexWeight, int[]... cycles) {
        Symmetry[] symmetries = Arrays.stream(cycles).map(cycle -> {
            Symmetry y = new Symmetry(cycle);
            y.createAllSymmetryCycles(vertexWeight);
            return y;
        }).toArray(Symmetry[]::new);
        this.combineLists(vertexWeight, symmetries);
    }

    public int getSizeOfList() {
        return sizeOfList;
    }

    int firstMirror(int number, int[] mirrors) {
        return Math.min(number, mirrors[number]);
    }

    private<T> Iterable<List<T>> iterCartesianProduct(List<List<T>> listOfLists) {
        int[] sizes = listOfLists.stream().mapToInt(List::size).toArray();
        int[] indices = new int[sizes.length];
        return () -> new Iterator<List<T>>(){
            @Override
            public boolean hasNext() {
                return indices[0] < sizes[0];
            }

            @Override
            public List<T> next() {
                List<T> result = new ArrayList<T>(sizes.length);
                for (int i = 0; i < sizes.length; ++i) {
                    result.add(listOfLists.get(i).get(indices[i]));
                }
                indices[indices.length-1]++;
                for (int i = indices.length-1; i > 0; --i) {
                    if (indices[i] >= sizes[i]) {
                        indices[i] = 0;
                        indices[i-1]++;
                    } else {
                        break;
                    }
                }
                return result;
            }
        };
    }

    public void combineLists(int vertexWeight, Symmetry... lists) {
        List<List<IsohedralTilingSolver2Data>> dataLists = Arrays.stream(lists).map(e->e.symmetryList).collect(Collectors.toList());
        for (List<IsohedralTilingSolver2Data> dataCombination : iterCartesianProduct(dataLists)) {
            int combinedLength = dataCombination.stream().mapToInt(IsohedralTilingSolver2Data::getNumEdges).sum();
            int[] edgeRightNeighbors = new int[combinedLength];
            int[] edgeLeftNeighbors = new int[combinedLength];
            int[] edgeMirrors = new int[combinedLength];
            int[] leftVertexWeights = new int[combinedLength];
            String[] names = new String[combinedLength];
            int offset = 0;
            int datasetIndex = 0;
            for (IsohedralTilingSolver2Data data : dataCombination) {
                for (int i = 0; i < data.numEdges; ++i) {
                    edgeRightNeighbors[offset + i] = data.edgeRightNeighbors[i] + offset;
                    edgeLeftNeighbors[offset + i] = data.edgeLeftNeighbors[i] + offset;
                    edgeMirrors[offset + i] = data.edgeMirrors[i] + offset;
                    leftVertexWeights[offset + i] = data.leftVertexWeights[i];
                    names[offset + i] = firstMirror(i, data.edgeMirrors) + getSuffix(datasetIndex);
                }
                offset += data.numEdges;
                datasetIndex++;
            }
            IsohedralTilingSolver2Data combinedData = new IsohedralTilingSolver2Data(combinedLength);
            combinedData.setDesiredVertexWeight(vertexWeight);
            combinedData.setEdgeRightNeighbors(edgeRightNeighbors);
            combinedData.setEdgeLeftNeighbors(edgeLeftNeighbors);
            combinedData.setEdgeMirrors(edgeMirrors);
            combinedData.setLeftVertexWeights(leftVertexWeights);
            combinedData.setEdgeTypeSimple();
            combinedData.setEnsureConnectivity(true);
            combinedList.add(combinedData);
            namespaces.add(names);
        }
        sizeOfList = combinedList.size();
    }

    private String getSuffix(int datasetIndex) {
        switch(datasetIndex) {
            case 0: return "";
            case 1: return "'";
            case 2: return "''";
            case 3: return "'''";
            default: return "(" + datasetIndex + ")";
        }
    }

    public void solveCombinations() {
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
