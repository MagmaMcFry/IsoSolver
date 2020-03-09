package isosolver2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IsohedralTilingSolver2 {
	private static final boolean DEBUG = false;

	// Data passed in constructor
	private final IsohedralTilingSolver2Data data;
	// Copies of data entries, to modify as necessary
	private final int numEdges;
	private final int desiredVertexWeight;
	private final int[] edgeRightNeighbors, edgeLeftNeighbors, edgeMirrors;
	private final int[] leftVertexWeights;
	private final int[] edgeTypes, matchTypes;
	private final boolean conePointsAllowed, ensureConnectivity;

	/**
	 * iterPrevious[i] = Previous edge after i to iterate over (index numEdges is a sentinel)
	 */
	private final int[] iterPrevious;

	/**
	 * iterNext[i] = Next edge after i to iterate over (index numEdges is a sentinel)
	 */
	private final int[] iterNext;

	/**
	 * activeSolution[i] = the index of the edge matched to edge i if currently matched, otherwise -1
	 */
	private final int[] activeSolution;

	/**
	 * List of all solutions found so far
	 */
	private final List<int[]> foundSolutions = new ArrayList<>();

	/**
	 * Solution count
	 */
	private long numSolutions;

	/**
	 * Whether to store found solutions
	 */
	private boolean storeSolutions;

	/**
	 * How many solutions to find at most
	 */
	private long maxSolutions;

	/**
	 * How many steps the current solve has taken
	 */
	private long numSteps;

	public IsohedralTilingSolver2(IsohedralTilingSolver2Data data) {

		data.validate();

		this.data = data;
		this.numEdges = data.numEdges;
		this.desiredVertexWeight = data.desiredVertexWeight;
		this.edgeRightNeighbors = Arrays.copyOf(data.edgeRightNeighbors, data.edgeRightNeighbors.length);
		this.edgeLeftNeighbors = Arrays.copyOf(data.edgeLeftNeighbors, data.edgeLeftNeighbors.length);
		this.edgeMirrors = Arrays.copyOf(data.edgeMirrors, data.edgeMirrors.length);
		this.leftVertexWeights = Arrays.copyOf(data.leftVertexWeights, data.leftVertexWeights.length);
		this.edgeTypes = Arrays.copyOf(data.edgeTypes, data.edgeTypes.length);
		this.matchTypes = Arrays.copyOf(data.matchTypes, data.matchTypes.length);
		this.conePointsAllowed = data.conePointsAllowed;
		this.ensureConnectivity = data.ensureConnectivity;

		this.iterPrevious = new int[numEdges + 1];
		this.iterNext = new int[numEdges + 1];
		for (int i = 0; i < numEdges; ++i) {
			iterPrevious[i + 1] = i;
			iterNext[i] = i + 1;
		}
		iterPrevious[0] = numEdges;
		iterNext[numEdges] = 0;

		this.activeSolution = new int[numEdges];
	}

	/**
	 * Finds a gluing with the given edge data.
	 *
	 * @return A list representing a gluing, or null if no valid gluing exists.
	 */
	public int[] findFirstSolution() {
		solve(true, 1L);
		return foundSolutions.isEmpty() ? null : foundSolutions.get(0);
	}

	/**
	 * Finds all possible gluings with the given edge data.
	 *
	 * @return A list of solutions, where a solution consists of an array of gluings.
	 */
	public List<int[]> findAllSolutions() {
		solve(true, Long.MAX_VALUE);
		return foundSolutions;
	}

	/**
	 * Returns the number of gluings with the given edge data.
	 *
	 * @return The number of gluings
	 */
	public long countSolutions() {
		solve(false, Long.MAX_VALUE);
		return numSolutions;
	}

	/**
	 * Checks whether there is any valid gluing with the given edge data.
	 *
	 * @return True if a gluing exists
	 */
	public boolean hasSolution() {
		solve(false, 1L);
		return numSolutions > 0;
	}

	/**
	 * Returns the number of iteration steps taken during the last solve.
	 *
	 * @return The number of steps.
	 */
	public long getStepsTaken() {
		return numSteps;
	}

	private void solve(boolean storeSolutions, long maxSolutions) {
		Arrays.fill(activeSolution, -1);
		foundSolutions.clear();
		numSolutions = 0;
		numSteps = 0;
		this.storeSolutions = storeSolutions;
		this.maxSolutions = maxSolutions;
		try {
			solveStep();
		} catch (SolutionLimitReached ignored) {
		}
	}

	private void checkSolution() {
		if (ensureConnectivity && !data.isSolutionConnected(activeSolution)) {
			return;
		}
		++numSolutions;
		if (storeSolutions) {
			foundSolutions.add(Arrays.copyOf(activeSolution, activeSolution.length));
		}
		if (numSolutions >= maxSolutions) {
			throw new SolutionLimitReached();
		}
	}

	private void solveStep() {
		++numSteps;
		if (iterNext[numEdges] == numEdges) {
			checkSolution();
			return;
		}
		int bestCandidateEdge = -1;
		int bestVertexWeight = -1;
		for (int candidateEdge = iterNext[numEdges]; candidateEdge != numEdges; candidateEdge = iterNext[candidateEdge]) {
			int candidateVertexWeight = leftVertexWeights[candidateEdge];
			if (candidateVertexWeight > bestVertexWeight) {
				bestCandidateEdge = candidateEdge;
				bestVertexWeight = candidateVertexWeight;
			}
		}
		if (bestVertexWeight > desiredVertexWeight) {
			// Left vertex of bestCandidateEdge is overweight, no solutions possible
			return;
		}
		int firstEdge = bestCandidateEdge;
		int firstEdgeType = edgeTypes[firstEdge];
		for (int secondEdge = iterNext[numEdges]; secondEdge != numEdges; secondEdge = iterNext[secondEdge]) {
			if (firstEdgeType == matchTypes[secondEdge]) {
				// Match found
				if (firstEdge == secondEdge && firstEdge == edgeMirrors[firstEdge]) {
					// Gluing a self-mirrored edge to itself
					if (conePointsAllowed) {
						boolean glueSuccess = glueSelf(firstEdge);
						if (glueSuccess) {
							hide(firstEdge);
							if (DEBUG) {
								System.out.println("Gluing " + firstEdge + " to itself");
							}
							solveStep();
							if (DEBUG) {
								System.out.println("Ungluing " + firstEdge + " from itself");
							}
							unhide(firstEdge);
						}
						unglueSelf(firstEdge);
					}
				} else if (firstEdge == secondEdge) {
					// Gluing a non-self-mirrored edge to itself
					if (conePointsAllowed) {
						boolean glueSuccess = glueSelf(firstEdge);
						glueSuccess &= glueSelf(edgeMirrors[firstEdge]);
						if (glueSuccess) {
							hide(firstEdge);
							hide(edgeMirrors[firstEdge]);
							if (DEBUG) {
								System.out.println("Gluing " + firstEdge + " to itself");
							}
							solveStep();
							if (DEBUG) {
								System.out.println("Ungluing " + firstEdge + " from itself");
							}
							unhide(edgeMirrors[firstEdge]);
							unhide(firstEdge);
						}
						unglueSelf(edgeMirrors[firstEdge]);
						unglueSelf(firstEdge);
					}
				} else if (firstEdge == edgeMirrors[secondEdge]) {
					// Gluing a non-self-mirrored edge to its own mirror
					boolean glueSuccess = glue(firstEdge, secondEdge);
					if (glueSuccess) {
						hide(firstEdge);
						hide(secondEdge);
						if (DEBUG) {
							System.out.println("Gluing " + firstEdge + " to " + secondEdge);
						}
						solveStep();
						if (DEBUG) {
							System.out.println("Ungluing " + firstEdge + " from " + secondEdge);
						}
						unhide(secondEdge);
						unhide(firstEdge);
					}
					unglue(firstEdge, secondEdge);
				} else if (firstEdge == edgeMirrors[firstEdge] || secondEdge == edgeMirrors[secondEdge]) {
					// Gluing a self-mirrored edge to another
					// This requires the other edge to be self-mirrored too, otherwise gluing makes no sense
					if (firstEdge == edgeMirrors[firstEdge] && secondEdge == edgeMirrors[secondEdge]) {
						boolean glueSuccess = glue(firstEdge, secondEdge);
						if (glueSuccess) {
							hide(firstEdge);
							hide(secondEdge);
							if (DEBUG) {
								System.out.println("Gluing " + firstEdge + " to " + secondEdge);
							}
							solveStep();
							if (DEBUG) {
								System.out.println("Ungluing " + firstEdge + " from " + secondEdge);
							}
							unhide(secondEdge);
							unhide(firstEdge);
						}
						unglue(firstEdge, secondEdge);
					}
				} else {
					// General case
					boolean glueSuccess = glue(firstEdge, secondEdge);
					glueSuccess &= glue(edgeMirrors[firstEdge], edgeMirrors[secondEdge]);
					if (glueSuccess) {
						hide(firstEdge);
						hide(secondEdge);
						hide(edgeMirrors[firstEdge]);
						hide(edgeMirrors[secondEdge]);
						if (DEBUG) {
							System.out.println("Gluing " + firstEdge + " to " + secondEdge);
						}
						solveStep();
						if (DEBUG) {
							System.out.println("Ungluing " + firstEdge + " from " + secondEdge);
						}
						unhide(edgeMirrors[secondEdge]);
						unhide(edgeMirrors[firstEdge]);
						unhide(secondEdge);
						unhide(firstEdge);
					}
					unglue(edgeMirrors[firstEdge], edgeMirrors[secondEdge]);
					unglue(firstEdge, secondEdge);
				}
			}
		}
	}

	boolean glue(int firstEdge, int secondEdge) {
		if (DEBUG && (activeSolution[firstEdge] != -1 || activeSolution[secondEdge] != -1)) {
			System.err.println("Double occupation");
		}
		boolean success = true;
		if (edgeLeftNeighbors[firstEdge] == secondEdge) {
			success &= isValidWeight(leftVertexWeights[firstEdge]);
		} else {
			edgeRightNeighbors[edgeLeftNeighbors[firstEdge]] = edgeRightNeighbors[secondEdge];
			edgeLeftNeighbors[edgeRightNeighbors[secondEdge]] = edgeLeftNeighbors[firstEdge];
			leftVertexWeights[edgeRightNeighbors[secondEdge]] += leftVertexWeights[firstEdge];
		}
		if (edgeLeftNeighbors[secondEdge] == firstEdge) {
			success &= isValidWeight(leftVertexWeights[secondEdge]);
		} else {
			edgeRightNeighbors[edgeLeftNeighbors[secondEdge]] = edgeRightNeighbors[firstEdge];
			edgeLeftNeighbors[edgeRightNeighbors[firstEdge]] = edgeLeftNeighbors[secondEdge];
			leftVertexWeights[edgeRightNeighbors[firstEdge]] += leftVertexWeights[secondEdge];
		}
		activeSolution[firstEdge] = secondEdge;
		activeSolution[secondEdge] = firstEdge;
		return success;
	}

	void unglue(int firstEdge, int secondEdge) {
		activeSolution[firstEdge] = -1;
		activeSolution[secondEdge] = -1;
		if (edgeLeftNeighbors[secondEdge] != firstEdge) {
			leftVertexWeights[edgeRightNeighbors[firstEdge]] -= leftVertexWeights[secondEdge];
			edgeLeftNeighbors[edgeRightNeighbors[firstEdge]] = firstEdge;
			edgeRightNeighbors[edgeLeftNeighbors[secondEdge]] = secondEdge;
		}
		if (edgeLeftNeighbors[firstEdge] != secondEdge) {
			leftVertexWeights[edgeRightNeighbors[secondEdge]] -= leftVertexWeights[firstEdge];
			edgeLeftNeighbors[edgeRightNeighbors[secondEdge]] = secondEdge;
			edgeRightNeighbors[edgeLeftNeighbors[firstEdge]] = firstEdge;
		}
	}

	boolean glueSelf(int edge) {
		if (DEBUG && (activeSolution[edge] != -1)) {
			System.err.println("Double occupation");
		}
		boolean success = selfGluingAllowed();
		if (edgeLeftNeighbors[edge] == edge) {
			success &= isValidWeight(leftVertexWeights[edge]);
		} else {
			edgeRightNeighbors[edgeLeftNeighbors[edge]] = edgeRightNeighbors[edge];
			edgeLeftNeighbors[edgeRightNeighbors[edge]] = edgeLeftNeighbors[edge];
			leftVertexWeights[edgeRightNeighbors[edge]] += leftVertexWeights[edge];
		}
		activeSolution[edge] = edge;
		return success;
	}

	void unglueSelf(int edge) {
		activeSolution[edge] = -1;
		if (edgeLeftNeighbors[edge] != edge) {
			leftVertexWeights[edgeRightNeighbors[edge]] -= leftVertexWeights[edge];
			edgeLeftNeighbors[edgeRightNeighbors[edge]] = edge;
			edgeRightNeighbors[edgeLeftNeighbors[edge]] = edge;
		}
	}

	private boolean selfGluingAllowed() {
		return true;
	}

	private boolean isValidWeight(int weight) {
		if (conePointsAllowed) {
			return desiredVertexWeight % weight == 0;
		} else {
			return desiredVertexWeight == weight;
		}
	}

	void hide(int edge) {
		iterNext[iterPrevious[edge]] = iterNext[edge];
		iterPrevious[iterNext[edge]] = iterPrevious[edge];
	}

	void unhide(int edge) {
		iterPrevious[iterNext[edge]] = edge;
		iterNext[iterPrevious[edge]] = edge;
	}

	/**
	 * Internal exception that is thrown once per solve, once the desired number of solutions has been found.
	 * This is probably faster than checking for whether we're done in every single call of {@link #solveStep}.
	 */
	private static class SolutionLimitReached extends RuntimeException {
	}
}
