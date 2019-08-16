package isosolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Finds isohedral tilings of polyforms in regular grids, using a fast algorithm based on dancing links techniques.
 */
public class IsohedralTilingSolver {
	private final int VERTEX_COUNT;
	private final EdgeType[] EDGE_TYPES;
	private final int[] VERTEX_ORDERS;
	private final int TILING_ORDER;
	private final boolean KEEP_SOLUTIONS;
	private int steps;

	private enum TrivialEdgeType implements EdgeType {
		EDGE;
		@Override
		public EdgeType reverse() { return this; }
		@Override
		public EdgeType opposite() { return this; }
	}

	private static EdgeType[] makeTrivialEdgeTypeArray(int length) {
		EdgeType[] types = new EdgeType[length];
		Arrays.fill(types, TrivialEdgeType.EDGE);
		return types;
	}

	/**
	 * @param vertexOrders The interior angle at each interior vertex, measured in elementary polygons.
	 * @param tilingOrder The number of elementary polygons desired around each point of the completed tiling.
	 * @param keepSolutions Whether to store solutions in memory. If this is false, solutions are only counted.
	 */
	public IsohedralTilingSolver(int[] vertexOrders, int tilingOrder, boolean keepSolutions) {
		this(vertexOrders, tilingOrder, keepSolutions, makeTrivialEdgeTypeArray(vertexOrders.length));
	}

	/**
	 * @param vertexOrders The interior angle at each interior vertex, measured in elementary polygons.
	 * @param tilingOrder The number of elementary polygons desired around each point of the completed tiling.
	 * @param keepSolutions Whether to store solutions in memory. If this is false, solutions are only counted.
	 * @param edgeTypes (Optional) Edges can be given edge types to specify which edges can be glued to which other edges.
	 *                  Useful e.g. for semiregular grids. Edge 0 is the edge between vertex 0 and vertex 1.
	 */
	public IsohedralTilingSolver(int[] vertexOrders, int tilingOrder, boolean keepSolutions, EdgeType[] edgeTypes) {
		if (edgeTypes.length != vertexOrders.length) {
			throw new IllegalArgumentException("List of vertex orders must have same length as list of edge types");
		}
		this.VERTEX_COUNT = vertexOrders.length;
		this.EDGE_TYPES = edgeTypes;
		this.VERTEX_ORDERS = vertexOrders;
		this.TILING_ORDER = tilingOrder;
		this.KEEP_SOLUTIONS = keepSolutions;
	}

	private LinkedEdge sentinel;
	private int numSolutions;
	private List<Gluing> solution;
	private List<List<Gluing>> solutions;

	public void solve() {
		init();
		solveStep();
	}

	public void init() {
		sentinel = new LinkedEdge();
		// Create edges and link them together properly
		LinkedEdge[] forwardsEdges = new LinkedEdge[VERTEX_COUNT];
		for (int i = 0; i < VERTEX_COUNT; ++i) {
			forwardsEdges[i] = new LinkedEdge(i, (i + 1) % VERTEX_COUNT, VERTEX_ORDERS[i], EDGE_TYPES[i], false, sentinel);
		}
		for (int i = 0; i < VERTEX_COUNT; ++i) {
			forwardsEdges[i].prevEdge = forwardsEdges[(i + VERTEX_COUNT - 1) % VERTEX_COUNT];
			forwardsEdges[i].nextEdge = forwardsEdges[(i + 1) % VERTEX_COUNT];
			forwardsEdges[i].reverseEdge = new LinkedEdge(forwardsEdges[i].secondVertex, forwardsEdges[i].firstVertex, forwardsEdges[i].nextEdge.firstVertexOrder, forwardsEdges[i].edgeType.reverse(), true, sentinel);
			forwardsEdges[i].reverseEdge.reverseEdge = forwardsEdges[i];
		}
		for (int i = 0; i < VERTEX_COUNT; ++i) {
			forwardsEdges[i].reverseEdge.nextEdge = forwardsEdges[i].prevEdge.reverseEdge;
			forwardsEdges[i].reverseEdge.prevEdge = forwardsEdges[i].nextEdge.reverseEdge;
		}
		solution = new ArrayList<>();
		solutions = new ArrayList<>();
		steps = 0;
	}

	private void solveStep() {
		++steps;
		if (steps % 10_000_000 == 0) {
			System.out.println("Steps elapsed: " + steps);
		}
		if (sentinel.iterNextEdge == sentinel) {
			// We glued all edges together
			++numSolutions;
			if (numSolutions % 100_000 == 0) {
				System.out.println("Solutions found so far: " + numSolutions);
			}
			if (KEEP_SOLUTIONS) {
				solutions.add(new ArrayList<>(solution));
			}
			return;
		}
		LinkedEdge bestCandidate = sentinel.iterNextEdge;
		int highestOrder = bestCandidate.firstVertexOrder;
		for (LinkedEdge e = sentinel.iterNextEdge; e != sentinel; e = e.iterNextEdge) {
			if (e.firstVertexOrder > highestOrder) {
				bestCandidate = e;
				highestOrder = e.firstVertexOrder;
			}
		}
		if (highestOrder > TILING_ORDER) {
			// Vertex order is already too large and will not become smaller
			return;
		}
		LinkedEdge firstEdge = bestCandidate;
		for (LinkedEdge secondEdge = sentinel.iterNextEdge; secondEdge != sentinel; secondEdge = secondEdge.iterNextEdge) {
			if (mayGlue(firstEdge, secondEdge)) {
				solution.add(new Gluing(firstEdge, secondEdge));
				if (firstEdge == secondEdge) {
					glueSelf(firstEdge);
					if (mayGlue(firstEdge.reverseEdge, firstEdge.reverseEdge)) {
						glueSelf(firstEdge.reverseEdge);
						solveStep();
						unglueSelf(firstEdge.reverseEdge);
					}
					unglueSelf(firstEdge);
				} else if (firstEdge == secondEdge.reverseEdge) {
					glue(firstEdge, secondEdge);
					solveStep();
					unglue(firstEdge, secondEdge);
				} else {
					glue(firstEdge, secondEdge);
					if (mayGlue(firstEdge.reverseEdge, secondEdge.reverseEdge)) {
						glue(firstEdge.reverseEdge, secondEdge.reverseEdge);
						solveStep();
						unglue(firstEdge.reverseEdge, secondEdge.reverseEdge);
					}
					unglue(firstEdge, secondEdge);
				}
				solution.remove(solution.size() - 1);
			}
		}
	}

	private boolean mayGlue(LinkedEdge firstEdge, LinkedEdge secondEdge) {

		// Cone point around first vertex of first edge
		if (firstEdge.prevEdge == secondEdge && illegal_order(firstEdge.firstVertexOrder)) {
			return false;
		}
		// Cone point around first vertex of second edge
		if (secondEdge.prevEdge == firstEdge && illegal_order(secondEdge.firstVertexOrder)) {
			return false;
		}
		// Length 1 crosscap
		if (firstEdge.nextEdge == firstEdge && firstEdge == secondEdge && illegal_order(firstEdge.firstVertexOrder)) {
			return false;
		}
		// Torus connection
		if (firstEdge != secondEdge && firstEdge.nextEdge == firstEdge && secondEdge.nextEdge == secondEdge && illegal_order(firstEdge.firstVertexOrder + secondEdge.firstVertexOrder)) {
			return false;
		}

		return firstEdge.edgeType.equals(secondEdge.edgeType.opposite());
	}

	/** For debugging purposes. */
	public void tryGlueManually(String s1, String s2) {
		boolean found1 = false, found2 = false;
		for (LinkedEdge e1 = sentinel.iterNextEdge; e1 != sentinel; e1 = e1.iterNextEdge) {
			if (s1.equals(e1.toString())) {
				found1 = true;
				for (LinkedEdge e2 = sentinel.iterNextEdge; e2 != sentinel; e2 = e2.iterNextEdge) {
					if (s2.equals(e2.toString())) {
						found2 = true;
						System.out.println("Vertex orders of e1: " + e1.firstVertexOrder + ", " + e1.nextEdge.firstVertexOrder);
						System.out.println("Vertex orders of e2: " + e2.firstVertexOrder + ", " + e2.nextEdge.firstVertexOrder);
						if (mayGlue(e1, e2)) {
							if (e1 == e2) {
								glueSelf(e1);
							} else {
								glue(e1, e2);
							}
							System.out.println("Successfully glued");
						} else {
							System.out.println("Gluing failed, may not glue");
						}
					}
				}
			}
		}
		if (!found1) System.out.println("No edge " + s1 + " found");
		if (!found2) System.out.println("No edge " + s2 + " found");
	}

	private boolean illegal_order(int n) {
		return TILING_ORDER % n != 0;
	}

	private void glueSelf(LinkedEdge edge) {
		joinOrders(edge, edge);
		hideFromIteration(edge);
		if (edge.nextEdge != edge) {
			edge.nextEdge.prevEdge = edge.prevEdge;
			edge.prevEdge.nextEdge = edge.nextEdge;
		}
	}

	private void unglueSelf(LinkedEdge edge) {
		if (edge.nextEdge != edge) {
			edge.prevEdge.nextEdge = edge;
			edge.nextEdge.prevEdge = edge;
		}
		unhideFromIteration(edge);
		unjoinOrders(edge, edge);
	}

	private void glue(LinkedEdge e1, LinkedEdge e2) {
		joinOrders(e1, e2);
		hideFromIteration(e1);
		hideFromIteration(e2);
		if (e1.prevEdge != e1 && e1.prevEdge != e2) {
			e1.prevEdge.nextEdge = (e2.nextEdge == e2) ? e1.nextEdge : e2.nextEdge;
		}
		if (e1.nextEdge != e1 && e1.nextEdge != e2) {
			e1.nextEdge.prevEdge = (e2.prevEdge == e2) ? e1.prevEdge : e2.prevEdge;
		}
		if (e2.prevEdge != e1 && e2.prevEdge != e2) {
			e2.prevEdge.nextEdge = (e1.nextEdge == e1) ? e2.nextEdge : e1.nextEdge;
		}
		if (e2.nextEdge != e1 && e2.nextEdge != e2) {
			e2.nextEdge.prevEdge = (e1.prevEdge == e1) ? e2.prevEdge : e1.prevEdge;
		}
	}

	private void unglue(LinkedEdge e1, LinkedEdge e2) {
		if (e2.nextEdge != e1 && e2.nextEdge != e2) {
			e2.nextEdge.prevEdge = e2;
		}
		if (e2.prevEdge != e1 && e2.prevEdge != e2) {
			e2.prevEdge.nextEdge = e2;
		}
		if (e1.nextEdge != e1 && e1.nextEdge != e2) {
			e1.nextEdge.prevEdge = e1;
		}
		if (e1.prevEdge != e1 && e1.prevEdge != e2) {
			e1.prevEdge.nextEdge = e1;
		}
		unhideFromIteration(e2);
		unhideFromIteration(e1);
		unjoinOrders(e1, e2);
	}

	private void joinOrders(LinkedEdge e1, LinkedEdge e2) {
		if (e1 == e2) {
			if (e1.nextEdge != e1) {
				e1.nextEdge.firstVertexOrder += e1.firstVertexOrder;
			}
		} else {
			if (e1.nextEdge != e1 && e1.nextEdge != e2) {
				e1.nextEdge.firstVertexOrder += e2.firstVertexOrder;
				if (e2.nextEdge == e2) {
					e1.nextEdge.firstVertexOrder += e1.firstVertexOrder;
				}
			}
			if (e2.nextEdge != e1 && e2.nextEdge != e2) {
				e2.nextEdge.firstVertexOrder += e1.firstVertexOrder;
				if (e1.nextEdge == e1) {
					e2.nextEdge.firstVertexOrder += e2.firstVertexOrder;
				}
			}
		}
	}

	private void unjoinOrders(LinkedEdge e1, LinkedEdge e2) {
		if (e1 == e2) {
			if (e1.nextEdge != e1) {
				e1.nextEdge.firstVertexOrder -= e1.firstVertexOrder;
			}
		} else {
			if (e2.nextEdge != e1 && e2.nextEdge != e2) {
				e2.nextEdge.firstVertexOrder -= e1.firstVertexOrder;
				if (e1.nextEdge == e1) {
					e2.nextEdge.firstVertexOrder -= e2.firstVertexOrder;
				}
			}
			if (e1.nextEdge != e1 && e1.nextEdge != e2) {
				e1.nextEdge.firstVertexOrder -= e2.firstVertexOrder;
				if (e2.nextEdge == e2) {
					e1.nextEdge.firstVertexOrder -= e1.firstVertexOrder;
				}
			}
		}
	}

	private void hideFromIteration(LinkedEdge edge) {
		edge.iterPrevEdge.iterNextEdge = edge.iterNextEdge;
		edge.iterNextEdge.iterPrevEdge = edge.iterPrevEdge;
	}

	private void unhideFromIteration(LinkedEdge edge) {
		edge.iterNextEdge.iterPrevEdge = edge;
		edge.iterPrevEdge.iterNextEdge = edge;
	}

	public int getSolutionCount() {
		return numSolutions;
	}

	public List<List<Gluing>> getSolutions() {
		if (!KEEP_SOLUTIONS) return null;
		return solutions;
	}

	private class LinkedEdge {
		final int firstVertex;
		final int secondVertex;
		int firstVertexOrder;
		final boolean isReversed;
		final EdgeType edgeType;
		LinkedEdge reverseEdge;
		LinkedEdge prevEdge, nextEdge;
		LinkedEdge iterPrevEdge, iterNextEdge;

		private LinkedEdge() {
			this.firstVertex = -1;
			this.secondVertex = -1;
			this.firstVertexOrder = TILING_ORDER;
			this.isReversed = false;
			this.edgeType = null;
			this.reverseEdge = null;
			this.prevEdge = null;
			this.nextEdge = null;
			this.iterPrevEdge = this;
			this.iterNextEdge = this;
		}

		private LinkedEdge(int firstVertex, int secondVertex, int firstVertexOrder, EdgeType edgeType, boolean isReversed, LinkedEdge sentinel) {
			this.firstVertex = firstVertex;
			this.secondVertex = secondVertex;
			this.firstVertexOrder = firstVertexOrder;
			this.isReversed = isReversed;
			this.edgeType = edgeType;
			this.reverseEdge = null;
			this.prevEdge = null;
			this.nextEdge = null;
			this.iterNextEdge = sentinel;
			this.iterPrevEdge = sentinel.iterPrevEdge;
			this.iterNextEdge.iterPrevEdge = this;
			this.iterPrevEdge.iterNextEdge = this;
		}

		@Override
		public String toString() {
			return "(" + firstVertex + "," + secondVertex + ")";
		}
	}

	/**
	 * Defines a pair of edges which are glued together.
	 *
	 * Note that the edges are always glued in such a manner that firstEdgeFirstVertex() is glued to secondEdgeSecondVertex(), and
	 * firstEdgeSecondVertex() is glued to secondEdgeFirstVertex().
	 */
	public class Gluing {
		private LinkedEdge e1;
		private LinkedEdge e2;

		private Gluing(LinkedEdge e1, LinkedEdge e2) {
			this.e1 = e1;
			this.e2 = e2;
		}

		public int firstEdgeFirstVertex() {
			return e1.firstVertex;
		}

		public int firstEdgeSecondVertex() {
			return e1.secondVertex;
		}

		public boolean isFirstEdgeReversed() {
			return e1.isReversed;
		}

		public int secondEdgeFirstVertex() {
			return e2.firstVertex;
		}

		public int secondEdgeSecondVertex() {
			return e2.secondVertex;
		}

		public boolean isSecondEdgeReversed() {
			return e2.isReversed;
		}
	}

	/**
	 * Implementations of this interface (preferably enums) should make sure that reverse() and opposite() are commuting involutions.
	 */
	public interface EdgeType {
		/**
		 * Returns the type of the reverse of an edge of this type
		 */
		EdgeType reverse();

		/**
		 * Returns the type of an edge fitting this type
		 */
		EdgeType opposite();
	}
}
