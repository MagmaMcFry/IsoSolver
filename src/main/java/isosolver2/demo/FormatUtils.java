package isosolver2.demo;

import java.util.Arrays;
import java.util.stream.Collectors;

public class FormatUtils {

	public static String formatSolution(int[] solution, int[] mirrors, String[] names) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < solution.length; ++i) {
			int j = solution[i];
			if (mirrors[i] < i || j < i || mirrors[j] < i) continue;
			String iName = names[i];
			String jName = names[j];
			boolean useBraces = mirrors[j] < j;
			if (useBraces) {
				sb.append("[").append(iName);
				if (!iName.equals(jName)) {
					sb.append(" ").append(jName);
				}
				sb.append("]");
			} else {
				sb.append("(").append(iName);
				if (!iName.equals(jName)) {
					sb.append(" ").append(jName);
				}
				sb.append(")");
			}
		}
		return sb.toString();
	}

	public static String formatPermutationCycles(int[] permutation) {
		boolean[] covered = new boolean[permutation.length];
		StringBuilder sb = new StringBuilder();
		boolean nontrivial = false;
		for (int i = 0; i < permutation.length; ++i) {
			if (!covered[i]) {
				covered[i] = true;
				if (permutation[i] != i) {
					nontrivial = true;
					sb.append("(").append(i);
					for (int j = permutation[i]; j != i; j = permutation[j]) {
						covered[j] = true;
						sb.append(" ").append(j);
					}
					sb.append(")");
				}
			}
		}
		if (!nontrivial) {
			sb.append("(id)");
		}
		return sb.toString();
	}

	public static String stringify(int[] array) {
		return "[" + Arrays.stream(array).mapToObj(String::valueOf).collect(Collectors.joining(",")) + "]";
	}

}
