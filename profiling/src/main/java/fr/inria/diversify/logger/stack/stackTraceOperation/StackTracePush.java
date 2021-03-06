package fr.inria.diversify.logger.stack.stackTraceOperation;

import fr.inria.diversify.logger.stack.stackElement.StackTraceCall;

/**
 * Created by Simon on 17/04/14.
 */
public class StackTracePush implements StackTraceOperation {
    protected StackTraceCall elem;


    public StackTracePush(StackTraceCall elem) {
        this.elem = elem;
    }

    @Override
    public void apply(StackTrace stackTrace) {
        stackTrace.deep++;
        stackTrace.stackTraceCalls.push(elem);
    }

    @Override
    public void restore(StackTrace stackTrace) {
        stackTrace.deep--;
        stackTrace.stackTraceCalls.pop();
    }
}
