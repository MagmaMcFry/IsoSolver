package isosolver2.demo;

import isosolver2.IsohedralTilingSolver2Data;

import java.util.ArrayList;
import java.util.List;

public class IsohedralTilingSolver2DataBuilder {
	private final int desiredVertexWeight;
	private final boolean buildFromRightVertexWeights;

	private int numEdges = 0;
	private int numEdgeGroups = 0;
	private final List<Integer> edgeGroupNumber = new ArrayList<>();
	private final List<Integer> edgeNumberWithinGroup = new ArrayList<>();
	private final List<Integer> edgeRightNeighbors = new ArrayList<>();
	private final List<Integer> edgeLeftNeighbors = new ArrayList<>();
	private final List<Integer> edgeMirrors = new ArrayList<>();
	private final List<Integer> leftVertexWeights = new ArrayList<>();
	private final List<Integer> edgeTypes = new ArrayList<>();
	private final List<Integer> matchTypes = new ArrayList<>();

	public IsohedralTilingSolver2DataBuilder(int desiredVertexWeight, boolean buildFromRightVertexWeights) {
		this.desiredVertexWeight = desiredVertexWeight;
		this.buildFromRightVertexWeights = buildFromRightVertexWeights;
	}

	public void addEdgeCycle(int[] vertexWeights) {
		int[] defaultTypes = new int[vertexWeights.length];
		addEdgeCycle(vertexWeights, defaultTypes, defaultTypes, defaultTypes, defaultTypes);
	}

	/**
	 * Adds an edge cycle to the edge data. The new edges will be numbered consecutively, followed by the mirrored edges consecutively in the same order.
	 * @param vertexWeights Weights of the vertex associated to each edge (left or right depending on {@link IsohedralTilingSolver2DataBuilder#buildFromRightVertexWeights})
	 * @param edgeTypes Types of each edge
	 * @param matchTypes Types matching the type of each edge
	 * @param mirrorTypes Types of each mirrored edge
	 * @param mirrorMatchTypes Types matching the mirrored type of each edge
	 */
	public void addEdgeCycle(int[] vertexWeights, int[] edgeTypes, int[] matchTypes, int[] mirrorTypes, int[] mirrorMatchTypes) {
		int cycleLength = vertexWeights.length;
		if (edgeTypes.length != cycleLength
				|| mirrorTypes.length != cycleLength
				|| matchTypes.length != cycleLength
				|| mirrorMatchTypes.length != cycleLength
		) {
			throw new IllegalArgumentException("Arrays of unequal length passed to addEdgeCycle");
		}

		int startOffset = numEdges;
		int mirrorOffset = numEdges + cycleLength;
		for (int i = 0; i < cycleLength; ++i) {
			edgeGroupNumber.add(numEdgeGroups);
			edgeNumberWithinGroup.add(i);
			edgeRightNeighbors.add(startOffset + (i + 1) % cycleLength);
			edgeLeftNeighbors.add(startOffset + (i + cycleLength - 1) % cycleLength);
			edgeMirrors.add(mirrorOffset + i);
			this.leftVertexWeights.add(buildFromRightVertexWeights ? vertexWeights[(i+cycleLength-1) % cycleLength] : vertexWeights[i]);
			this.edgeTypes.add(edgeTypes[i]);
			this.matchTypes.add(matchTypes[i]);
		}
		for (int i = 0; i < cycleLength; ++i) {
			edgeGroupNumber.add(numEdgeGroups);
			edgeNumberWithinGroup.add(i);
			edgeRightNeighbors.add(mirrorOffset + (i + cycleLength - 1) % cycleLength);
			edgeLeftNeighbors.add(mirrorOffset + (i + 1) % cycleLength);
			edgeMirrors.add(startOffset + i);
			this.leftVertexWeights.add(buildFromRightVertexWeights ? vertexWeights[i] : vertexWeights[(i + 1) % cycleLength]);
			this.edgeTypes.add(mirrorTypes[i]);
			this.matchTypes.add(mirrorMatchTypes[i]);
		}
		numEdges += 2*cycleLength;
		++numEdgeGroups;
	}

	// TODO add semicycles and a way to represent symmetries of polyforms


	public IsohedralTilingSolver2Data build() {
		IsohedralTilingSolver2Data data = new IsohedralTilingSolver2Data(numEdges);
		data.setDesiredVertexWeight(desiredVertexWeight);
		data.setEdgeRightNeighbors(toIntArray(edgeRightNeighbors));
		data.setEdgeLeftNeighbors(toIntArray(edgeLeftNeighbors));
		data.setEdgeMirrors(toIntArray(edgeMirrors));
		data.setLeftVertexWeights(toIntArray(leftVertexWeights));
		data.setEdgeTypes(toIntArray(edgeTypes));
		data.setMatchTypes(toIntArray(matchTypes));
		return data;
	}

	private int[] toIntArray(List<Integer> edgeRightNeighbor) {
		return edgeRightNeighbor.stream().mapToInt(i->i).toArray();
	}

	public String[] getNames(EdgeNameFormat format) {
		String[] names = new String[numEdges];
		int maxGroup = edgeGroupNumber.stream().max(Integer::compareTo).orElse(0);
		for (int i = 0; i < numEdges; ++i) {
			names[i] = getName(i, maxGroup, format);
		}
		return names;
	}

	private String getName(int index, int maxGroup, EdgeNameFormat format) {
		StringBuilder sb = new StringBuilder();

		int innerNumber = edgeNumberWithinGroup.get(index);
		sb.append(innerNumber);

		int groupNumber = edgeGroupNumber.get(index);
		boolean useNumbers = (format == EdgeNameFormat.NUMBERS)
				|| (format == EdgeNameFormat.SMART && maxGroup >= 3)
				|| (format == EdgeNameFormat.MIXED && groupNumber >= 3);
		if (useNumbers) {
			sb.append('@').append(groupNumber);
		} else {
			for (int i = 0; i < groupNumber; ++i) {
				sb.append('\'');
			}
		}

		return sb.toString();
	}

	public enum EdgeNameFormat {
		/** Prime formatting. Shape number is indicated by the number of primes: {@code 3, 3', 3'', 3'''} */
		PRIMES,
		/** Number formatting. Uses an {@code @} symbol followed by the shape number: {@code 3@0, 3@1, 3@2, 3@3} */
		NUMBERS,
		/** Smart formatting. Uses number formatting if there are more than three shapes, prime formatting otherwise. */
		SMART,
		/** Mixed formatting. Uses primes for shapes 0-2 and continues with numbers for the remaining shapes: {@code 3, 3', 3'', 3@3} */
		MIXED
	}
}
