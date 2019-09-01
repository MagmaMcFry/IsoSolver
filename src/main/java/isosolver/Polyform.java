package isosolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Polyform implements Comparable<Polyform> {
	private final int[] vertexOrders;
	final int length;

	public Polyform(int[] vertexOrders) {
		//if (vertexOrders.length < 2) {
		//	throw new IllegalArgumentException("Polyform must have at least 2 sides");
		//}
		this.vertexOrders = vertexOrders;
		this.length = vertexOrders.length;
	}

	private final static String[] IAMOND_NAMES = new String[]{
			"Moniamond", "Diamond", "Triamond", "Tetriamond", "Pentiamond", "Hexiamond", "Heptiamond", "Octiamond"
	};

	private final static String[] OMINO_NAMES = new String[]{
			"Monomino", "Domino", "Tromino", "Tetromino", "Pentomino", "Hexomino", "Heptomino", "Octomino"
	};

	private final static String[] PENT_NAMES = new String[]{
			"Monopent", "Dipent", "Tripent", "Tetrapent", "Pentapent", "Hexapent", "Heptapent", "Octapent"
	};

	private final static String[] HEX_NAMES = new String[]{
			"Monohex", "Dihex", "Trihex", "Tetrahex", "Pentahex", "Hexahex", "Heptahex", "Octahex"
	};

	private final static String[][] FORM_NAMES = new String[][]{IAMOND_NAMES, OMINO_NAMES, PENT_NAMES, HEX_NAMES};
	private final static String[] FORM_DEFAULTS = new String[]{"iamond", "omino", "pent", "hex"};
	private final static String[] FORM_PLURALS = new String[]{"s", "es", "s", "es"};
	public static String getPolyformName(int num_polygon_sides, int num_polygon_tiles, boolean plural) {
		int form_index = num_polygon_sides - 3;
		if (form_index >= 0 && form_index < FORM_NAMES.length) {
			int count_index = num_polygon_tiles - 1;
			if (count_index >= 0 && count_index < FORM_NAMES[form_index].length) {
				return FORM_NAMES[form_index][count_index] + (plural ? FORM_PLURALS[form_index] : "");
			} else {
				return num_polygon_tiles + "-" + FORM_DEFAULTS[form_index] + (plural ? FORM_PLURALS[form_index] : "");
			}
		} else {
			return num_polygon_tiles + "-(" + num_polygon_sides + "-gon)" + (plural ? "s" : "");
		}
	}

	public Polyform addPolygon(int sides, int position) {
		if (position < 0 || position >= length) {
			throw new IllegalArgumentException("Illegal side index for polygon extension");
		}
		if (sides < 2) {
			throw new IllegalArgumentException("Additional polygon must have at least 2 sides");
		}
		int[] newVertexOrders = new int[length + sides - 2];
		for (int i = 0; i < sides-2; ++i) {
			newVertexOrders[i] = 1;
		}
		for (int j = 0; j < length; ++j) {
			newVertexOrders[j + sides - 2] = vertexOrders[(j + position) % length];
		}
		++newVertexOrders[sides - 2];
		++newVertexOrders[newVertexOrders.length-1];
		return new Polyform(newVertexOrders);
	}

	public Polyform normalize() {
		return new Polyform(normalize(vertexOrders));
	}

	private static int[] normalize(int[] array) {
		int[] minRotated = rotateMinimal(array);
		int[] minRotatedReflected = rotateMinimal(reverse(array));
		for (int i = 0; i < array.length; ++i) {
			if (minRotated[i] != minRotatedReflected[i]) {
				if (minRotated[i] < minRotatedReflected[i]) {
					return minRotated;
				} else {
					return minRotatedReflected;
				}
			}
		}
		return minRotated;
	}

	private static int[] reverse(int[] array) {
		int[] reversedArray = new int[array.length];
		for (int i = 0; i < array.length; ++i) {
			reversedArray[array.length-i-1] = array[i];
		}
		return reversedArray;
	}

	private static int[] rotateMinimal(int[] array) {
		int[] doubleArray = new int[2*array.length];
		for (int i = 0; i < array.length; ++i) {
			doubleArray[i] = array[i];
			doubleArray[i+array.length] = array[i];
		}
		int bestOffset = 0;
		for (int offset = 1; offset < array.length; ++offset) {
			for (int j = 0; j < array.length; ++j) {
				if (doubleArray[bestOffset+j] != doubleArray[offset+j]) {
					if (doubleArray[bestOffset+j] > doubleArray[offset+j]) {
						bestOffset = offset;
					}
					break;
				}
			}
		}
		return Arrays.copyOfRange(doubleArray, bestOffset, bestOffset+array.length);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Polyform polyform = (Polyform) o;
		return Arrays.equals(vertexOrders, polyform.vertexOrders);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(vertexOrders);
	}

	@Override
	public int compareTo(Polyform o) {
		int lengthCmp = Integer.compare(length, o.length);
		if (lengthCmp != 0) return lengthCmp;
		for (int j = 0; j < length; ++j) {
			int jCmp = Integer.compare(vertexOrders[j], o.vertexOrders[j]);
			if (jCmp != 0) return jCmp;
		}
		return 0;
	}

	public static List<Polyform> getAllPolyforms(int polygonSides, int numPolygons) {
		Set<Polyform> oldPolyforms = new HashSet<Polyform>();
		Set<Polyform> newPolyforms = new HashSet<Polyform>();
		oldPolyforms.add(new Polyform(new int[]{0,0})); // Zero-form
		for (int n = 0; n < numPolygons; ++n) {
			for (Polyform p : oldPolyforms) {
				for (int i = 0; i < p.length; ++i) {
					Polyform np = p.addPolygon(polygonSides, i);
					newPolyforms.add(np.normalize());
				}
			}
			Set<Polyform> swap = newPolyforms;
			newPolyforms = oldPolyforms;
			oldPolyforms = swap;
			newPolyforms.clear();
		}
		ArrayList<Polyform> ans = new ArrayList<>(oldPolyforms);
		Collections.sort(ans);
		return ans;
	}
	public static List<Polyform> getAllESPolyforms(int polygonSides, int numPolygons) {
		Set<Polyform> oldPolyforms = new HashSet<Polyform>();
		Set<Polyform> newPolyforms = new HashSet<Polyform>();
		int[] BasicES = new int[polygonSides-1];
		for (int k = 0; k < polygonSides-2; ++k) {
			BasicES[k] = 1;
		}
		BasicES[polygonSides-2] = 2;
		Polyform BasicESPolyform = new Polyform(BasicES);
		oldPolyforms.add(BasicESPolyform); // Di-form
		for (int n = 2; n < numPolygons; n = n+2) {
			for (Polyform p : oldPolyforms) {
				for (int i = 0; i < p.length; ++i) {
					Polyform np = p.addPolygon(polygonSides, i);
					newPolyforms.add(np.normalize());
				}
			}
			Set<Polyform> swap = newPolyforms;
			newPolyforms = oldPolyforms;
			oldPolyforms = swap;
			newPolyforms.clear();
		}
		ArrayList<Polyform> ans = new ArrayList<>(oldPolyforms);
		Collections.sort(ans);
		return ans;
	}
	public static List<Polyform> getAllCSPolyforms(int polygonSides, int numPolygons, int symmetry) {
		Set<Polyform> oldPolyforms = new HashSet<Polyform>();
		Set<Polyform> newPolyforms = new HashSet<Polyform>();
		int PS = polygonSides/symmetry;
		int[] BasicCS = new int[PS];
		for (int k = 0; k < PS; ++k) {
			BasicCS[k] = 1;
		}
		Polyform BasicCSPolyform = new Polyform(BasicCS);
		oldPolyforms.add(BasicCSPolyform); // Mono-form
		for (int n = 1; n < numPolygons; n = n+symmetry) {
			for (Polyform p : oldPolyforms) {
				for (int i = 0; i < p.length; ++i) {
					Polyform np = p.addPolygon(polygonSides, i);
					newPolyforms.add(np.normalize());
				}
			}
			Set<Polyform> swap = newPolyforms;
			newPolyforms = oldPolyforms;
			oldPolyforms = swap;
			newPolyforms.clear();
		}
		ArrayList<Polyform> ans = new ArrayList<>(oldPolyforms);
		Collections.sort(ans);
		return ans;
	}
	public static List<Polyform> getAllVSPolyforms(int polygonSides, int numPolygons, int q, int symmetry) {
		Set<Polyform> oldPolyforms = new HashSet<Polyform>();
		Set<Polyform> newPolyforms = new HashSet<Polyform>();
		int QS = q/symmetry;
		int[] BasicVS = new int[(polygonSides - 2)*QS];
		for (int k = 0; k < QS; ++k) {
			for (int l = 0; l < polygonSides - 2; ++l) {
				BasicVS[(polygonSides - 2) * k + l] = 1;
			}
			BasicVS[(polygonSides - 2) * (k+1) - 1] = 2;
		}
		Polyform BasicVSPolyform = new Polyform(BasicVS);
		oldPolyforms.add(BasicVSPolyform); // Q-form
		for (int n = q; n < numPolygons; n = n+symmetry) {
			for (Polyform p : oldPolyforms) {
				for (int i = 0; i < p.length; ++i) {
					Polyform np = p.addPolygon(polygonSides, i);
					newPolyforms.add(np.normalize());
				}
			}
			Set<Polyform> swap = newPolyforms;
			newPolyforms = oldPolyforms;
			oldPolyforms = swap;
			newPolyforms.clear();
		}
		ArrayList<Polyform> ans = new ArrayList<>(oldPolyforms);
		Collections.sort(ans);
		return ans;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i = 0; i < vertexOrders.length; i++) {
			if (i != 0) sb.append(",");
			sb.append(vertexOrders[i]);
		}
		sb.append("]");
		return sb.toString();
	}

	public int[] getVertexOrders() {
		return vertexOrders;
	}
}
