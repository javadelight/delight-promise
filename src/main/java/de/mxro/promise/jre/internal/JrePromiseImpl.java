package de.mxro.promise.jre.internal;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import de.mxro.async.callbacks.ValueCallback;
import de.mxro.async.internal.PromiseImpl;
import de.mxro.async.promise.Deferred;

public class JrePromiseImpl<ResultType> extends PromiseImpl<ResultType> {

	@Override
	public ResultType get() {

		ResultType result = super.get();

		if (result != null) {
			return result;
		}

		final CountDownLatch latch = new CountDownLatch(1);

		get(new ValueCallback<ResultType>() {

			@Override
			public void onFailure(Throwable t) {
				latch.countDown();
			}

			@Override
			public void onSuccess(ResultType value) {
				latch.countDown();
			}
		});

		try {

			latch.await(320000, TimeUnit.MILLISECONDS);
		} catch (final InterruptedException e) {
			throw new RuntimeException(e);
		}

		if (latch.getCount() > 0) {
			throw new RuntimeException(
					"Get call could not be completed in 320 s timeout.");
		}

		return get();

	}

	public JrePromiseImpl(Deferred<ResultType> asyncPromise) {
		super(asyncPromise);
	}

}
