package idc.symphony.ui.property;

import java.util.function.Consumer;

/**
 * Interface representing an asynchronous request.
 * May or may not run on executing thread - this is merely a functional interface.
 *
 * @param <T> Request return type
 */
@FunctionalInterface
public interface AsyncRequest<T> {
    void get(Consumer<T> callback);
}
