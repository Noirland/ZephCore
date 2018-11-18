package nz.co.noirland.zephcore;

import com.comphenix.executors.BukkitExecutors;
import com.comphenix.executors.BukkitScheduledExecutorService;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Callable;

/**
 * Bukkit callback utilising Guava's {@link com.google.common.util.concurrent.ListenableFuture}.
 * @param <T>
 */
public abstract class Callback<T> {

    private static final BukkitScheduledExecutorService sync = BukkitExecutors.newSynchronous(ZephCore.inst());
    private static final BukkitScheduledExecutorService async = BukkitExecutors.newAsynchronous(ZephCore.inst());

    /**
     * Registers the given task to be executed async immediately, and then run the callback in sync.
     * @param task
     * @param callback
     */
    public Callback(Callable<T> task, FutureCallback<T> callback) {
        ListenableFuture<T> future = async.submit(task);

        Futures.addCallback(future, callback, sync);
    }
}
