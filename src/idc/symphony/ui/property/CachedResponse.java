package idc.symphony.ui.property;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * JavaFX cached request for both synchronous and asynchronous requests
 * Only executes requests if cache is considered dirty.
 *
 * No need for thread-safe implementation as it is intended to only execute on JavaFX thread.
 * @param <T>
 */
public class CachedResponse<T> {
    private T value = null;
    private InvalidationListener invalidator = (dependency) -> dirty = true;
    private boolean dirty = true;

    /**
     * Set cache to dirty upon notifiers' state change
     * @param notifiers Notifier to invalidate cache with
     */
    public void addInvalidator(Observable... notifiers) {
        for (Observable notifier : notifiers) {
            notifier.addListener(invalidator);
        }
    }

    /**
     * Remove previously registered invalidating notifier
     * @param notifiers Notifiers previously used to invalidate cache
     */
    public void removeInvalidator(Observable... notifiers) {
        for (Observable notifier : notifiers) {
            notifier.removeListener(invalidator);
        }
    }

    /**
     * Get value from cache, calculated value via asynchronous request if dirty
     * @param request Asynchronous request. Not necessarily called, only when dirty.
     * @param callback value receiver upon task completion
     */
    public void getAsync(AsyncRequest<T> request, Consumer<T> callback) {
        if (dirty) {
            request.get((newValue) -> {
                updateValue(newValue);
                callback.accept(value);
            });
        } else {
            callback.accept(value);
        }
    }

    /**
     * Update cache value and reset its dirty state
     * Must always occur on FX Application thread to remain thread-safe
     *
     * @param newValue Updated value
     */
    private void updateValue(T newValue) {
        if (!Platform.isFxApplicationThread()) {
            throw new IllegalStateException("Cached requests must occur on FX threads!");
        }

        value = newValue;
        dirty = false;
    }

    /**
     * Get value from cache, calculated via supplier if cache is dirty
     * @param supplier Synchronous request. Not necessarily called, only when dirty.
     * @return cached values
     */
    public T get(Supplier<T> supplier) {
        if (dirty) {
            updateValue(value);
        }

        return value;
    }
}
