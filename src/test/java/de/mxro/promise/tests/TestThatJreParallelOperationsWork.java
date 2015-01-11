package de.mxro.promise.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import de.mxro.async.Operation;
import de.mxro.async.callbacks.ValueCallback;
import de.mxro.promise.Promise;
import de.mxro.promise.jre.Promises;

public class TestThatJreParallelOperationsWork {

    private final class RandomlyDelayedPromise implements Operation<String> {
        @Override
        public void apply(final ValueCallback<String> callback) {
            new Thread() {

                @Override
                public void run() {
                    final int delay = new Random().nextInt(10) + 1;
                    try {
                        Thread.sleep(delay);
                    } catch (final InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    callback.onSuccess("Completed after delay: " + delay);
                }

            }.start();
        }
    }

    @Test
    public void test_it() {

        final List<Promise<String>> promises = new ArrayList<Promise<String>>();

        for (int i = 1; i <= 50; i++) {
            final Promise<String> p = Promises.create(new RandomlyDelayedPromise());
            promises.add(p);
        }

        Promises.parallel(promises);

    }
}
