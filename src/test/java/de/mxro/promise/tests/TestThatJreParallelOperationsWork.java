package de.mxro.promise.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import de.mxro.async.callbacks.ValueCallback;
import de.mxro.async.jre.AsyncJre;
import de.mxro.async.promise.Deferred;
import de.mxro.async.promise.Promise;
import de.mxro.promise.jre.PromiseJre;

public class TestThatJreParallelOperationsWork {

    private final class RandomlyDelayedPromise implements Deferred<String> {
        @Override
        public void get(final ValueCallback<String> callback) {
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
            final Promise<String> p = PromiseJre.promise(new RandomlyDelayedPromise());
            promises.add(p);
        }

        AsyncJre.parallel(promises);

    }
}
