package idc.symphony.music.conducting;

import java.util.Objects;

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

    /*@Override
    public boolean equals(Object o) {
        if (o instanceof Prioritized) {
            Prioritized p = (Prioritized)o;
            return Objects.equals(value, p.value);
        }

        return false;
    }*/
}
