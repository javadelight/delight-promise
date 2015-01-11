package de.mxro.promise.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import de.mxro.async.Operation;
import de.mxro.async.callbacks.ValueCallback;
import de.mxro.promise.helper.PromiseTemplate;
import de.mxro.promise.jre.PromiseJre;

public class TestThatJreParallelOperationsCatchFailures {

    private final class RandomlyFailingPromise implements Operation<String> {

        @Override
        public void apply(final ValueCallback<String> callback) {

            new Thread() {

                @Override
                public void run() {

                    if (new Random().nextInt(2) == 1) {
                        callback.onSuccess("Got it.");
                        return;
                    }

                    callback.onFailure(new Exception("So failed!"));

                }

            }.start();

        }

    }

    @Test(expected = Exception.class)
    public void test_it() {
        final List<PromiseTemplate<String>> promises = new ArrayList<PromiseTemplate<String>>();

        for (int i = 1; i <= 50; i++) {
            final PromiseTemplate<String> p = PromiseJre.create(new RandomlyFailingPromise());
            promises.add(p);
        }

        PromiseJre.parallel(promises);

    }

}
