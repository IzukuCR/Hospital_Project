package front.util;

import java.util.Map;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

public class ThreadDiagnostics {
    private ThreadDiagnostics() {}

    public static void dumpAllThreads() {
        System.err.println("==== THREAD DUMP START ====");
        for (Map.Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()) {
            Thread th = entry.getKey();
            System.err.println("Thread: " + th.getName() + " state=" + th.getState());
            for (StackTraceElement st : entry.getValue()) {
                System.err.println("  at " + st.toString());
            }
            System.err.println();
        }
        System.err.println("==== THREAD DUMP END ====");
    }

    public static <T> T createWithTimeout(Supplier<T> supplier, String name, long timeoutMs) {
        FutureTask<T> task = new FutureTask<>(supplier::get);
        Thread t = new Thread(task, "init-" + name);
        t.setDaemon(true);
        t.start();
        try {
            return task.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (TimeoutException te) {
            System.err.println(name + " creation timed out after " + timeoutMs + " ms");
            dumpAllThreads();
            throw new RuntimeException(name + " init timed out", te);
        } catch (Exception e) {
            System.err.println("Error creating " + name + ": " + e);
            throw new RuntimeException(e);
        }
    }
}
