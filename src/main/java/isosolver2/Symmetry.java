package isosolver2;

import isosolver2.demo.FormatUtils;

import java.util.ArrayList;

public class Symmetry {

    final int[] edgeCycle;
    final int numEdges;
    int fundamentalRotation;
    int fundamentalRotationGroup;
    boolean fundamentalReflection;
    int fundamentalAxes;
    public ArrayList<IsohedralTilingSolver2Data> symmetryList = new ArrayList<>();
    int symmetryClasses;

    public Symmetry(int[] edgeCycle) {
        this.edgeCycle = edgeCycle;
        this.numEdges = edgeCycle.length;
    }

    int computeFundamentalRotation (int[] edgeCycle) {
        aa:
        for (int i = 1; i <= edgeCycle.length ; i++) {
            for (int j = 0; j < edgeCycle.length; j++) {
                if (edgeCycle[j] != edgeCycle[Math.floorMod(i+j, edgeCycle.length)]) {
                    continue aa;
                }
            }
            return i;
        }
        return 0;
    }

    void setFundamentalRotation() {
        fundamentalRotation = computeFundamentalRotation(edgeCycle);
    }

    void setFundamentalRotationGroup() {
        fundamentalRotationGroup = numEdges / fundamentalRotation;
    }

    int[] reverseArray(int[] edgeCycle) {
        int[] reverseCycle = new int[edgeCycle.length];
        for (int i = 0; i < reverseCycle.length; i++) {
            reverseCycle[i] = edgeCycle[edgeCycle.length - 1 - i];
        }
        return reverseCycle;
    }

    boolean computeFundamentalReflection(int[] edgeCycle) {
        int[] reverseCycle = reverseArray(edgeCycle);
        aa:
        for (int i = 0; i < reverseCycle.length ; i++) {
            for (int j = 0; j < reverseCycle.length; j++) {
                if (edgeCycle[j] != reverseCycle[Math.floorMod(i+j, reverseCycle.length)]) {
                    continue aa;
                }
            }
            return true;
        }
        return false;
    }

    void setFundamentalReflection() {
        fundamentalReflection = computeFundamentalReflection(edgeCycle);
    }

    int computeFundamentalAxes(boolean fundamentalReflection, int fundamentalRotationGroup) {
        if (fundamentalReflection) {
            return fundamentalRotationGroup;
        }
        else {
            return 0;
        }
    }

    void setFundamentalAxes() {
        fundamentalAxes = computeFundamentalAxes(fundamentalReflection, fundamentalRotationGroup);
    }

    IsohedralTilingSolver2Data createSymmetryCycle(int vertexWeight, int[] edgeCycle, boolean fundamentalReflection, int reflectionParameter) {
        if (!fundamentalReflection) {
            IsohedralTilingSolver2Data data = new IsohedralTilingSolver2Data(edgeCycle.length * 2);
            data.setDesiredVertexWeight(vertexWeight);
            int[] rightNeighbors = new int[edgeCycle.length*2];
            int[] leftNeighbors = new int[edgeCycle.length*2];
            int[] leftVertexWeights = new int[edgeCycle.length*2];
            int[] edgeMirrors = new int[edgeCycle.length*2];
            for (int i = 0; i < edgeCycle.length; i++) {
                rightNeighbors[Math.floorMod(i - 1, edgeCycle.length)] = i;
                rightNeighbors[Math.floorMod(i + 1, edgeCycle.length) + edgeCycle.length] = i + edgeCycle.length;
                leftNeighbors[Math.floorMod(i + 1, edgeCycle.length)] = i;
                leftNeighbors[Math.floorMod(i - 1, edgeCycle.length) + edgeCycle.length] = i + edgeCycle.length;
                leftVertexWeights[Math.floorMod(i + 1, edgeCycle.length)] = edgeCycle[i];
                leftVertexWeights[i + edgeCycle.length] = edgeCycle[i];
                edgeMirrors[i] = i + edgeCycle.length;
                edgeMirrors[i + edgeCycle.length] = i;
            }
            data.setEdgeRightNeighbors(rightNeighbors);
            data.setEdgeLeftNeighbors(leftNeighbors);
            data.setLeftVertexWeights(leftVertexWeights);
            data.setEdgeMirrors(edgeMirrors);
            data.setEdgeTypeSimple();
            data.setEnsureConnectivity(true);
            return data;
        }
        else {
            IsohedralTilingSolver2Data data = new IsohedralTilingSolver2Data(edgeCycle.length);
            data.setDesiredVertexWeight(vertexWeight);
            int[] rightNeighbors = new int[edgeCycle.length];
            int[] leftNeighbors = new int[edgeCycle.length];
            int[] leftVertexWeights = new int[edgeCycle.length];
            int[] edgeMirrors = new int[edgeCycle.length];
            int[] reverseCycle = new int[edgeCycle.length];
            for (int i = 0; i < edgeCycle.length; i++) {
                reverseCycle[i] = edgeCycle.length - i - 1;
            }
            for (int i = 0; i < edgeCycle.length; i++) {
                rightNeighbors[Math.floorMod(i - 1, edgeCycle.length)] = i;
                leftNeighbors[Math.floorMod(i + 1, edgeCycle.length)] = i;
                leftVertexWeights[Math.floorMod(i + 1, edgeCycle.length)] = edgeCycle[i];
                edgeMirrors[i] = reverseCycle[Math.floorMod(i + reflectionParameter - 1,edgeCycle.length)];
            }
            data.setEdgeRightNeighbors(rightNeighbors);
            data.setEdgeLeftNeighbors(leftNeighbors);
            data.setLeftVertexWeights(leftVertexWeights);
            data.setEdgeMirrors(edgeMirrors);
            data.setEdgeTypeSimple();
            data.setEnsureConnectivity(true);
            return data;
        }
    }

    public void createAllSymmetryCycles(int vertexWeight) {
        setFundamentalRotation();
        setFundamentalRotationGroup();
        setFundamentalReflection();
        setFundamentalAxes();

        for (int i = fundamentalRotation; i <= numEdges; i += fundamentalRotation) {
            if (numEdges % i == 0) {
                int[] quotient = new int[i];
                System.arraycopy(edgeCycle, 0, quotient, 0, i);
                if (fundamentalReflection) {
                    int numAxes = fundamentalAxes*i/numEdges;
                    int[] axis = new int[2];
                    int[] reverseCycle = reverseArray(quotient);
                    int axisIndex = 0;
                    int parity = Math.floorMod(numAxes, 2);
                    aa:
                    for (int j = 0; j < i; j++) {
                        for (int k = 0; k < i; k++) {
                            if (quotient[k] != reverseCycle[Math.floorMod(j+k, i)]) {
                                continue aa;
                            }
                        }
                        axis[axisIndex] = j;
                        axisIndex++;
                        if (axisIndex >= 2) {
                            break;
                        }
                    }
                    symmetryList.add(createSymmetryCycle(vertexWeight, quotient, true, axis[0]));
                    if (parity == 0) {
                        symmetryList.add(createSymmetryCycle(vertexWeight, quotient, true, axis[1]));
                    }
                }
                symmetryList.add(createSymmetryCycle(vertexWeight, quotient, false, 0));
            }
        }
        symmetryClasses = symmetryList.size();
    }

    public int getSymmetryClasses() {
        return symmetryClasses;
    }
}
