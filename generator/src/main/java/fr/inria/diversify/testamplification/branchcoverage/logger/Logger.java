package fr.inria.diversify.testamplification.branchcoverage.logger;

import java.util.HashMap;

/**
 * User: Simon
 * Date: 15/04/15
 */
public class Logger {
    private static HashMap<Thread, LogWriter> logs = null;

    /**
     * This is an option. By the default the verbose log is used.
     * @param log
     */
    public static void setLog(HashMap<Thread, LogWriter> log) {
        Logger.logs = log;
    }

    protected static LogWriter getLog() {
        return getLog(Thread.currentThread());
    }

    protected static LogWriter getLog(Thread thread) {
        if ( logs == null ) { logs = new HashMap<Thread, LogWriter>(); }
        if ( logs.containsKey(thread) ) {
            return logs.get(thread);
        } else {
            LogWriter l = new LogWriter(thread);
            logs.put(thread, l);
            return l;
        }
    }

    public static void branch(Thread thread, String id) {
        getLog().branch(id);
    }

    public static void methodIn(Thread thread, String id) {
        getLog().methodIn(id);
    }

    public static void methodOut(Thread thread, String id) {
        getLog().methodOut(id);
    }


    public  static void close() {
        for ( LogWriter l : logs.values() ) {
            l.close();
        }
    }
}
