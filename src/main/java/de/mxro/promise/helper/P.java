package de.mxro.promise.helper;

import de.mxro.async.Operation;
import de.mxro.fn.Closure;

public interface P<ResultType> extends Operation<ResultType> {

    public ResultType get();

    public void catchExceptions(Closure<Throwable> closure);

    public void get(Closure<ResultType> closure);

}
