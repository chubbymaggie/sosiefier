package fr.inria.diversify.sosie;

import fr.inria.diversify.sosie.logger.ShutdownHookLog;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 * Abstract classes for all loggers
 *
 * Created by marodrig on 25/06/2014.
 */
public abstract class InstruLogWriter {

    ///The call deep is how deep in the stack are we.
    protected Map<Thread, Integer> callDeep;

    ///Directory where the log is being stored
    protected File dir;

    ///Dictionary indicating if the methods of a thread are to be logged.
    protected Map<Thread, Boolean> logMethod;

    ///Semaphores for locking output streams
    protected Map<String, Semaphore> semaphores;



    public void InstrulLogWriter() {
        if (dir == null) initDir();
        semaphores = new HashMap<String, Semaphore>();
        logMethod = new HashMap<Thread, Boolean>();
        ShutdownHookLog shutdownHook = new ShutdownHookLog();
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    /**
     * Log a call to a method
     * @param thread Thread where the call is invoked
     * @param methodSignatureId Signature of the method
     */
    public abstract void methodCall(Thread thread, String methodSignatureId);

    /**
     * Method that logs and return from a method
     * @param thread Thread where the method returns
     */
    public void methodOut(Thread thread) {
        decCallDepth(thread);
    }

    public abstract void writeTestStart(Thread thread, String testSignature);

    public abstract void writeAssert(int id, Thread thread, String className,
                                     String methodSignature, String assertName, Object... var);

    public abstract void writeVar(int id, Thread thread, String methodSignatureId, Object... var);

    public abstract void writeException(int id, Thread thread,
                                        String className, String methodSignature, Object exception);

    public abstract void writeCatch(int id, Thread thread, String className, String methodSignature, Object exception);

    public  abstract void close();

    /**
     * Increases the depth of the stack for a given thread
     * @param thread Thread to increase depth
     */
    protected int incCallDepth(Thread thread) {
        if (callDeep == null)
            callDeep = new HashMap<Thread, Integer>();
        if (callDeep.containsKey(thread)) {
            int c = callDeep.get(thread) + 1;
            callDeep.put(thread, c);
            return c;
        }
        else {
            callDeep.put(thread, 1);
            return 1;
        }
    }

    /**
     * Resets the depth of the stack for a given thread
     * @param thread Thread to reset depth
     */
    protected void resetCallDepth(Thread thread) {
        if (callDeep != null && callDeep.containsKey(thread))
            callDeep.remove(thread);
    }

    /**
     * Decreases the depth of the stack for a given thread
     * @param thread Thread to decrease depth
     */
    protected int decCallDepth(Thread thread) {
        int deep = callDeep.get(thread);
        if (deep > 0) {
            deep--;
            callDeep.put(thread, deep);
            return deep;
        }
        return 0;
    }

    /**
     * Gets a boolean value indicating if the methods of a thread are to be logged.
     * @param thread Log this thread?
     * @return True if log, false otherwise
     */
    protected boolean getLogMethod(Thread thread) {
        return logMethod == null || !logMethod.containsKey(thread) || logMethod.get(thread);
    }

    protected void stopLogMethod(Thread thread) {
        logMethod.put(thread, false);
    }

    protected void startLogMethod(Thread thread) {
        logMethod.put(thread, true);
    }

    /**
     * Initializes the directory where the files for each thread are going to be stored
     */
    protected void initDir() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("LogDirName"));
            dir = new File("log" + reader.readLine());
        } catch (IOException e) {
            dir = new File("log");
        }
        dir.mkdir();
    }

    /**
     * Returns the file name of the file where this thread's log is being stored
     * @param thread
     * @return Relative filename of the file where this thread's log is being stored
     */
    protected String getThreadFileName(Thread thread) {
        return "log" + thread.getName();
    }

    /**
     * A print string representation for an Object o
     * @param o Object that is going to be printed
     * @return A string representation of the object
     */
    protected String printString(Object o) {
        String string;
        if (o == null)
            string = "null";
        else if (o instanceof Object[]) {
            Object[] array = (Object[]) o;
            int iMax = array.length - 1;
            if (iMax == -1)
                return "[]";

            StringBuilder b = new StringBuilder();
            b.append('[');
            for (int i = 0; ; i++) {
                b.append(printString(array[i]));
                if (i == iMax)
                    return b.append(']').toString();
                b.append(", ");
            }
        } else
            string = o.toString();
        return string;
    }

}