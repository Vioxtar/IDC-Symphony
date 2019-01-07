package idc.symphony.music.conducting;

/**
 * Prioritization adapter for sorting commands in an adjustable order
 * @param <T> Value type to be prioritized
 */
public class Prioritized<T> implements Comparable<Prioritized<T>> {
    public T value;
    public int priority;

    public Prioritized(int priority, T value) {
        this.value = value;
        this.priority = priority;
    }

    @Override
    public int compareTo(Prioritized<T> o) {
        return priority - o.priority;
    }
}
