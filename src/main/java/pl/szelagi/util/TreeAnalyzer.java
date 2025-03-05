package pl.szelagi.util;

import pl.szelagi.component.baseComponent.BaseComponent;

import java.util.*;

@Deprecated
public class TreeAnalyzer {
    private final Map<Integer, List<BaseComponent>> layers;

    public TreeAnalyzer(BaseComponent root) {
        this.layers = analyzeTree(root);
    }

    // sprawdzone: daje poprawne wyniki
    private Map<Integer, List<BaseComponent>> analyzeTree(BaseComponent root) {
        var layers = new HashMap<Integer, List<BaseComponent>>();
        Queue<BaseComponent> queue = new LinkedList<>();
        queue.add(root);
        int level = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            List<BaseComponent> layer = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                BaseComponent current = queue.poll();
                layer.add(current);
                queue.addAll(current.children());
            }

            layers.put(level, layer);
            level++;
        }

        return layers;
    }

    public Map<Integer, List<BaseComponent>> layers() {
        return layers;
    }

    public int numberOfLayers() {
        return layers.size();
    }

    // sprawdzone: daje poprawne wyniki
    public Iterable<BaseComponent> iterateOldToYoung() {
        return () -> layers.values().stream().flatMap(List::stream).iterator();
    }

    // sprawdzone: daje poprawne wyniki
    public Iterable<BaseComponent> iterateOldToYoungNoRoot() {
        return () -> layers.values().stream().skip(1).flatMap(List::stream).iterator();
    }

    // sprawdzone: daje poprawne wyniki
    public Iterable<BaseComponent> iterableYoungToOld() {
        var rootStream = layers.values().stream();
        return () -> ReverseStream.reverse(rootStream.flatMap(List::stream)).iterator();
    }

    // sprawdzone: daje poprawne wyniki
    public Iterable<BaseComponent> iterableYoungToOldNoRoot() {
        var noRootStream = layers.values().stream().skip(1);
        return () -> ReverseStream.reverse(noRootStream.flatMap(List::stream)).iterator();
    }


}