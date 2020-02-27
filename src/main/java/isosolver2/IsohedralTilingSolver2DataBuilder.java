package isosolver2;

import java.util.ArrayList;
import java.util.List;

public class IsohedralTilingSolver2DataBuilder {
	private final int desiredVertexWeight;

	private int numEdges = 0;
	private int numEdgeGroups = 0;
	private List<Integer> edgeGroupNumber = new ArrayList<>();
	private List<Integer> edgeNumberWithinGroup = new ArrayList<>();
	private List<Integer> edgeRightNeighbors = new ArrayList<>();
	private List<Integer> edgeLeftNeighbors = new ArrayList<>();
	private List<Integer> edgeMirrors = new ArrayList<>();
	private List<Integer> leftVertexWeights = new ArrayList<>();
	private List<Integer> edgeTypes = new ArrayList<>();
	private List<Integer> matchTypes = new ArrayList<>();

	public IsohedralTilingSolver2DataBuilder(int desiredVertexWeight) {
		this.desiredVertexWeight = desiredVertexWeight;
	}

	public void addEdgeCycle(int[] leftVertexWeights) {
		int[] defaultTypes = new int[leftVertexWeights.length];
		addEdgeCycle(leftVertexWeights, defaultTypes, defaultTypes, defaultTypes, defaultTypes);
	}

	/**
	 * Adds an edge cycle to the edge data. The new edges will be numbered consecutively, followed by the mirrored edges consecutively in the same order.
	 * @param leftVertexWeights Weights of the left vertices of each edge
	 * @param edgeTypes Types of each edge
	 * @param matchTypes Types matching the type of each edge
	 * @param mirrorTypes Types of each mirrored edge
	 * @param mirrorMatchTypes Types matching the mirrored type of each edge
	 */
	public void addEdgeCycle(int[] leftVertexWeights, int[] edgeTypes, int[] matchTypes, int[] mirrorTypes, int[] mirrorMatchTypes) {
		int cycleLength = leftVertexWeights.length;
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
			this.leftVertexWeights.add(leftVertexWeights[i]);
			this.edgeTypes.add(edgeTypes[i]);
			this.matchTypes.add(matchTypes[i]);
		}
		for (int i = 0; i < cycleLength; ++i) {
			edgeGroupNumber.add(numEdgeGroups);
			edgeNumberWithinGroup.add(i);
			edgeRightNeighbors.add(mirrorOffset + (i + cycleLength - 1) % cycleLength);
			edgeLeftNeighbors.add(mirrorOffset + (i + 1) % cycleLength);
			edgeMirrors.add(startOffset + i);
			this.leftVertexWeights.add(leftVertexWeights[(i + 1) % cycleLength]);
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
	public String[] getNames() {
		String[] names = new String[numEdges];
		for (int i = 0; i < numEdges; ++i) {
			StringBuilder sb = new StringBuilder();
			sb.append(edgeNumberWithinGroup.get(i));
			int g = edgeGroupNumber.get(i);
			for (int j = 0; j < g; ++j) {
				sb.append("'");
			}
			names[i] = sb.toString();
		}
		return names;
	}
}
