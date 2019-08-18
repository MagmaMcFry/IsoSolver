package isosolver.demo;

import isosolver.IsohedralTilingSolver;

import java.util.Arrays;

enum SimpleEdgeType implements IsohedralTilingSolver.EdgeType {

	// Standard edge type with 4-way symmetry
	I0,
	I1,
	I2,

	// Edge type with perpendicular symmetry
	M0, W0,
	M1, W1,
	M2, W2,

	// Edge type with point symmetry
	S0, Z0,
	S1, Z1,
	S2, Z2,

	// Edge type with parallel symmetry
	C0, D0,
	C1, D1,
	C2, D2,

	// Asymmetric edge type
	b0, d0, p0, q0,
	b1, d1, p1, q1,
	b2, d2, p2, q2,

	;

	static {
		I0.reverse = I0; I0.opposite = I0;
		I1.reverse = I1; I1.opposite = I1;
		I2.reverse = I2; I2.opposite = I2;

		M0.reverse = M0; M0.opposite = W0; W0.reverse = W0; W0.opposite = M0;
		M1.reverse = M1; M1.opposite = W1; W1.reverse = W1; W1.opposite = M1;
		M2.reverse = M2; M2.opposite = W2; W2.reverse = W2; W2.opposite = M2;

		S0.reverse = Z0; S0.opposite = S0; Z0.reverse = S0; Z0.opposite = Z0;
		S1.reverse = Z1; S1.opposite = S1; Z1.reverse = S1; Z1.opposite = Z1;
		S2.reverse = Z2; S2.opposite = S2; Z2.reverse = S2; Z2.opposite = Z2;

		C0.reverse = D0; C0.opposite = D0; D0.reverse = C0; D0.opposite = C0;
		C1.reverse = D1; C1.opposite = D1; D1.reverse = C1; D1.opposite = C1;
		C2.reverse = D2; C2.opposite = D2; D2.reverse = C2; D2.opposite = C2;

		b0.reverse = d0; b0.opposite = q0; d0.reverse = b0; d0.opposite = p0; p0.reverse = q0; p0.opposite = d0; q0.reverse = p0; q0.opposite = b0;
		b1.reverse = d1; b1.opposite = q1; d1.reverse = b1; d1.opposite = p1; p1.reverse = q1; p1.opposite = d1; q1.reverse = p1; q1.opposite = b1;
		b2.reverse = d2; b2.opposite = q2; d2.reverse = b2; d2.opposite = p2; p2.reverse = q2; p2.opposite = d2; q2.reverse = p2; q2.opposite = b2;
	}

	private SimpleEdgeType reverse, opposite;

	@Override
	public SimpleEdgeType reverse() {
		return reverse;
	}

	@Override
	public SimpleEdgeType opposite() {
		return opposite;
	}

	public static SimpleEdgeType[] repeat(int count, SimpleEdgeType... edges) {
		SimpleEdgeType[] newEdges = new SimpleEdgeType[count*edges.length];
		for (int i = 0; i < count; ++i) {
			System.arraycopy(edges, 0, newEdges, i*edges.length, edges.length);
		}
		return newEdges;
	}
}
