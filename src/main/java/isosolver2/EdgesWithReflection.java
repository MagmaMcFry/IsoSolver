package isosolver2;

public class EdgesWithReflection {

    final int[] edgeCycle;
    int numEdges;
    final boolean reflection;
    final int reflectionParameter;

    public EdgesWithReflection(int[] edgeCycle, boolean reflection, int reflectionParameter) {
        this.edgeCycle = edgeCycle;
        this.reflection = reflection;
        this.reflectionParameter = reflectionParameter;
    }
}
