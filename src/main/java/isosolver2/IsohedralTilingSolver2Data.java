package isosolver2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class IsohedralTilingSolver2Data {

	private static final int MAX_NUM_EDGES = Integer.MAX_VALUE / 2;
	private static final int MAX_VERTEX_WEIGHT = Integer.MAX_VALUE / 2;

	/**
	 * Number of edges in the configuration
	 */
	final int numEdges;

	/**
	 * Weight that final vertices should end up with (a factor of this weight is also permitted)
	 */
	int desiredVertexWeight;

	/**
	 * edgeRightNeighbor[i] = Index of the right neighbor of edge number i
	 */
	int[] edgeRightNeighbors;

	/**
	 * edgeLeftNeighbor[i] = Index of the left neighbor of edge number i
	 */
	int[] edgeLeftNeighbors;

	/**
	 * edgeMirror[i] = Index of the mirror of edge number i
	 */
	int[] edgeMirrors;

	/**
	 * leftVertexWeight[i] = Current weight of the left vertex of edge number i
	 */
	int[] leftVertexWeights;

	/**
	 * edgeType[i] = Type of edge number i
	 */
	int[] edgeTypes;

	/**
	 * matchType[i] = Type of an edge that may be glued to edge number i
	 */
	int[] matchTypes;

	/**
	 * Whether to allow cone points in the gluing resulting from the solver (default: true)
	 */
	boolean conePointsAllowed = true;

	/**
	 * Whether to discard disconnected solutions (default: true)
	 */
	boolean ensureConnectivity = true;

	/**
	 * The internal symmetries of the given edge data, as permutations
	 * Will be autogenerated by generateSymmetries
	 */
	private List<int[]> symmetries;

	public IsohedralTilingSolver2Data(int numEdges) {
		this.numEdges = numEdges;
	}

	public int getNumEdges() {
		return numEdges;
	}

	public int getDesiredVertexWeight() {
		return desiredVertexWeight;
	}

	public void setDesiredVertexWeight(int desiredVertexWeight) {
		this.desiredVertexWeight = desiredVertexWeight;
	}

	public int[] getEdgeRightNeighbors() {
		return edgeRightNeighbors;
	}

	public void setEdgeRightNeighbors(int[] edgeRightNeighbors) {
		this.edgeRightNeighbors = edgeRightNeighbors;
	}

	public int[] getEdgeLeftNeighbors() {
		return edgeLeftNeighbors;
	}

	public void setEdgeLeftNeighbors(int[] edgeLeftNeighbors) {
		this.edgeLeftNeighbors = edgeLeftNeighbors;
	}

	public int[] getEdgeMirrors() {
		return edgeMirrors;
	}

	public void setEdgeMirrors(int[] edgeMirrors) {
		this.edgeMirrors = edgeMirrors;
	}

	public int[] getLeftVertexWeights() {
		return leftVertexWeights;
	}

	public void setLeftVertexWeights(int[] leftVertexWeights) {
		this.leftVertexWeights = leftVertexWeights;
	}

	public int[] getEdgeTypes() {
		return edgeTypes;
	}

	public void setEdgeTypes(int[] edgeTypes) {
		this.edgeTypes = edgeTypes;
	}

	public int[] getMatchTypes() {
		return matchTypes;
	}

	public void setMatchTypes(int[] matchTypes) {
		this.matchTypes = matchTypes;
	}

	/**
	 * Sets all edge types and match types to 0.
	 */
	public void setEdgeTypeSimple() {
		this.edgeTypes = new int[numEdges];
		this.matchTypes = new int[numEdges];
	}

	public boolean getConePointsAllowed() {
		return conePointsAllowed;
	}

	public void setConePointsAllowed(boolean conePointsAllowed) {
		this.conePointsAllowed = conePointsAllowed;
	}

	public boolean getEnsureConnectivity() {
		return ensureConnectivity;
	}

	public void setEnsureConnectivity(boolean ensureConnectivity) {
		this.ensureConnectivity = ensureConnectivity;
	}

	/**
	 * Checks given vertex data for the necessary consistency.
	 */
	void validate() {
		if (numEdges < 0) {
			throw new IllegalArgumentException("Number of edges must be nonnegative");
		}
		if (numEdges > MAX_NUM_EDGES) {
			throw new IllegalArgumentException("This implementation only supports up to " + MAX_NUM_EDGES + " edges");
		}
		if (desiredVertexWeight < 0) {
			throw new IllegalArgumentException("Maximum vertex weight must be nonnegative");
		}
		if (desiredVertexWeight > MAX_VERTEX_WEIGHT) {
			throw new IllegalArgumentException("This implementation only supports a desired vertex weight of up to " + MAX_VERTEX_WEIGHT);
		}
		if (edgeRightNeighbors == null) {
			throw new IllegalArgumentException("edgeRightNeighbor is not set");
		}
		if (numEdges != edgeRightNeighbors.length) {
			throw new IllegalArgumentException("edgeRightNeighbor does not have length equal to numEdges");
		}
		if (edgeLeftNeighbors == null) {
			throw new IllegalArgumentException("edgeLeftNeighbor is not set");
		}
		if (numEdges != edgeLeftNeighbors.length) {
			throw new IllegalArgumentException("edgeLeftNeighbor does not have length equal to numEdges");
		}
		if (edgeMirrors == null) {
			throw new IllegalArgumentException("edgeMirror is not set");
		}
		if (numEdges != edgeMirrors.length) {
			throw new IllegalArgumentException("edgeMirror does not have length equal to numEdges");
		}
		if (leftVertexWeights == null) {
			throw new IllegalArgumentException("leftVertexWeight is not set");
		}
		if (numEdges != leftVertexWeights.length) {
			throw new IllegalArgumentException("leftVertexWeight does not have length equal to numEdges");
		}
		if (edgeTypes == null) {
			throw new IllegalArgumentException("edgeType is not set");
		}
		if (numEdges != edgeTypes.length) {
			throw new IllegalArgumentException("edgeType does not have length equal to numEdges");
		}
		if (matchTypes == null) {
			throw new IllegalArgumentException("matchType is not set");
		}
		if (numEdges != matchTypes.length) {
			throw new IllegalArgumentException("matchType does not have length equal to numEdges");
		}
		for (int i = 0; i < numEdges; ++i) {
			if (edgeLeftNeighbors[i] < 0 || edgeLeftNeighbors[i] >= numEdges) {
				throw new IllegalArgumentException("edgeLeftNeighbor[" + i + "] is not in bounds");
			}
			if (edgeRightNeighbors[i] < 0 || edgeRightNeighbors[i] >= numEdges) {
				throw new IllegalArgumentException("edgeRightNeighbor[" + i + "] is not in bounds");
			}
			if (edgeMirrors[i] < 0 || edgeMirrors[i] >= numEdges) {
				throw new IllegalArgumentException("edgeMirror[" + i + "] is not in bounds");
			}
			if (leftVertexWeights[i] <= 0) {
				throw new IllegalArgumentException("Vertex weight inconsistency: all vertex weights must be positive");
			}
			if (leftVertexWeights[i] > MAX_VERTEX_WEIGHT) {
				throw new IllegalArgumentException("This implementation only supports a vertex weight of up to " + MAX_VERTEX_WEIGHT);
			}
		}
		for (int i = 0; i < numEdges; ++i) {
			if (edgeLeftNeighbors[edgeRightNeighbors[i]] != i) {
				throw new IllegalArgumentException("Neighbor inconsistency: left neighbor of right neighbor of edge " + i + " is not edge " + i);
			}
			if (edgeRightNeighbors[edgeLeftNeighbors[i]] != i) {
				throw new IllegalArgumentException("Neighbor inconsistency: right neighbor of left neighbor of edge " + i + " is not edge " + i);
			}
			if (edgeMirrors[edgeMirrors[i]] != i) {
				throw new IllegalArgumentException("Mirror inconsistency: mirror of mirror of edge " + i + " is not edge " + i);
			}
			if (edgeLeftNeighbors[edgeMirrors[i]] != edgeMirrors[edgeRightNeighbors[i]]) {
				throw new IllegalArgumentException("Mirror neighbor inconsistency: left neighbor of mirror of edge " + i + " is not mirror of right neighbor of edge " + i);
			}
			if (edgeRightNeighbors[edgeMirrors[i]] != edgeMirrors[edgeLeftNeighbors[i]]) {
				throw new IllegalArgumentException("Mirror neighbor inconsistency: right neighbor of mirror of edge " + i + " is not mirror of left neighbor of edge " + i);
			}
		}
		for (int i = 0; i < numEdges; ++i) {
			for (int j = 0; j < numEdges; ++j) {
				if (matchTypes[i] == edgeTypes[j] && matchTypes[j] != edgeTypes[i]) {
					throw new IllegalArgumentException("Match inconsistency: edges " + i + " and " + j + " do not consistently match");
				}
				if (matchTypes[i] == edgeTypes[j] && matchTypes[edgeMirrors[i]] != edgeTypes[edgeMirrors[j]]) {
					throw new IllegalArgumentException("Match inconsistency: edges " + i + " and " + j + " match but not their mirrors");
				}
			}
		}
	}

	boolean isSolutionConnected(int[] solution) {
		boolean[] traversed = new boolean[numEdges];
		// Stupid O(numEdges^2) algorithm, but it doesn't hog any stack
		traversed[0] = true;
		for (int iters = 0; iters < numEdges; ++iters) {
			for (int i = 0; i < numEdges; ++i) {
				if (traversed[i]) {
					traversed[solution[i]] = true;
					traversed[edgeLeftNeighbors[i]] = true;
					traversed[edgeRightNeighbors[i]] = true;
					traversed[edgeMirrors[i]] = true;
				}
			}
		}
		for (boolean b : traversed) {
			if (!b) return false;
		}
		return true;
	}

	public List<int[]> getSymmetries() {
		if (symmetries == null) generateSymmetries();
		return new ArrayList<>(symmetries);
	}

	private void generateSymmetries() {
		int[] currentSymmetry = new int[numEdges];
		Arrays.fill(currentSymmetry, -1);
		symmetries = new ArrayList<>();
		findSymmetriesStep(currentSymmetry, symmetries);
	}

	private void findSymmetriesStep(int[] currentSymmetry, List<int[]> foundSymmetries) {
		int sourceEdge = -1;
		for (int edge = 0; edge < numEdges; ++edge) {
			if (currentSymmetry[edge] == -1) {
				sourceEdge = edge;
				break;
			}
		}
		if (sourceEdge == -1) {
			// No edges left to match, found potential symmetry. Remains to check consistency
			if (isSymmetry(currentSymmetry)) {
				foundSymmetries.add(Arrays.copyOf(currentSymmetry, currentSymmetry.length));
			}
			return;
		}
		for (int destEdge = 0; destEdge < numEdges; ++destEdge) {
			if (couldMapRing(sourceEdge, destEdge)) {
				mapRing(currentSymmetry, sourceEdge, destEdge);
				findSymmetriesStep(currentSymmetry, foundSymmetries);
				unmapRing(currentSymmetry, sourceEdge);
			}
		}
	}

	private boolean isPermutation(int[] array) {
		if (array.length != numEdges) return false;
		for (int entry : array) {
			if (entry < 0 || entry >= numEdges) return false;
		}
		INVSEARCH: for (int i = 0; i < numEdges; ++i) {
			for (int j = 0; j < numEdges; ++j) {
				if (array[j] == i) continue INVSEARCH;
			}
			return false;
		}
		return true;
	}

	public boolean isSymmetry(int[] perm) {
		if (!isPermutation(perm)) return false;
		// Neighborhood consistency
		for (int i = 0; i < numEdges; ++i) {
			if (edgeRightNeighbors[perm[i]] != perm[edgeRightNeighbors[i]]) return false;
			if (edgeLeftNeighbors[perm[i]] != perm[edgeLeftNeighbors[i]]) return false;
			if (edgeMirrors[perm[i]] != perm[edgeMirrors[i]]) return false;
			if (leftVertexWeights[perm[i]] != leftVertexWeights[i]) return false;
		}
		// Match consistency
		for (int i = 0; i < numEdges; ++i) {
			for (int j = 0; j < numEdges; ++j) {
				if (edgeTypes[i] == matchTypes[j] && edgeTypes[perm[i]] != matchTypes[perm[j]]) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean couldMapRing(int sourceEdge, int destEdge) {
		int sourceRingEdge = sourceEdge, destRingEdge = destEdge;
		do {
			if (leftVertexWeights[sourceRingEdge] != leftVertexWeights[destRingEdge]) return false;

			sourceRingEdge = edgeRightNeighbors[sourceRingEdge];
			destRingEdge = edgeRightNeighbors[destRingEdge];
		} while (sourceRingEdge != sourceEdge && destRingEdge != destEdge);
		// Ensure both rings have closed at once
		return sourceRingEdge == sourceEdge && destRingEdge == destEdge;
	}

	private void mapRing(int[] currentSymmetry, int sourceEdge, int destEdge) {
		int sourceRingEdge = sourceEdge, destRingEdge = destEdge;
		do {
			currentSymmetry[sourceRingEdge] = destRingEdge;
			sourceRingEdge = edgeRightNeighbors[sourceRingEdge];
			destRingEdge = edgeRightNeighbors[destRingEdge];
		} while (sourceRingEdge != sourceEdge);
	}

	private void unmapRing(int[] currentSymmetry, int sourceEdge) {
		int sourceRingEdge = sourceEdge;
		do {
			currentSymmetry[sourceRingEdge] = -1;
			sourceRingEdge = edgeRightNeighbors[sourceRingEdge];
		} while (sourceRingEdge != sourceEdge);
	}

	// Compares arrays lexicographically. Obsolete for Java 9, just use Arrays.compare().
	private int cmp(int[] a1, int[] a2) {
		for (int i = 0; i < numEdges; ++i) {
			int cmp = Integer.compare(a1[i], a2[i]);
			if (cmp != 0) return cmp;
		}
		return 0;
	}

	private static class ComparableIntArray {
		private final int[] array;

		ComparableIntArray(int[] array) { this.array = array; }
		@Override public int hashCode() { return Arrays.hashCode(array); }
		@Override public boolean equals(Object obj) {
			return obj instanceof ComparableIntArray && Arrays.equals(array, ((ComparableIntArray) obj).array);
		}
	}

	public int[] canonicalizeSolution(int[] solution) {
		if (symmetries == null) {
			generateSymmetries();
		}
		int[] bestConjugate = solution;
		for (int[] symmetry : symmetries) {
			int[] conjugate = conjugate(solution, symmetry);
			if (cmp(conjugate, bestConjugate) < 0) {
				bestConjugate = conjugate;
			}
		}
		return bestConjugate;
	}

	public List<int[]> canonicalizeSolutions(List<int[]> solutions) {
		// The hoops we jump through to deduplicate a list of arrays
		Set<ComparableIntArray> canonicalized = solutions.stream().map(this::canonicalizeSolution).map(ComparableIntArray::new).collect(Collectors.toSet());
		return canonicalized.stream().map(e->e.array).collect(Collectors.toList());
	}

	private int[] conjugate(int[] solution, int[] symmetry) {
		int[] newSolution = new int[solution.length];
		for (int i = 0; i < numEdges; ++i) {
			newSolution[symmetry[i]] = symmetry[solution[i]];
		}
		return newSolution;
	}
}