/*
Copyright 2008 WebAtlas
Authors : Mathieu Bastian, Mathieu Jacomy, Julian Bilcke
Website : http://www.gephi.org

This file is part of Gephi.

Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Gephi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gephi.utils.longtask;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.util.Cancellable;

/**
 *
 * @author Mathieu Bastian
 */
public final class LongTaskExecutor {

    private final boolean inBackground;
    private boolean interruptCancel;
    private final long interruptDelay;
    private final String name;
    private RunningLongTask runningTask;
    private ExecutorService executor;
    private Timer cancelTimer;
    private LongTaskListener listener;

    /**
     * Creates a new long task executor.
     * @param doInBackground when <code>true</code>, the task will be executed in a separate thread
     * @param name the name of the executor, used to recognize threads by names
     * @param interruptDelay number of seconds to wait before calling <code>Thread.interrupt()</code> after a cancel request
     */
    public LongTaskExecutor(boolean doInBackground, String name, int interruptDelay) {
        this.inBackground = doInBackground;
        this.name = name;
        this.interruptCancel = true;
        this.interruptDelay = interruptDelay * 1000;
    }

    /**
     * Creates a new long task executor.
     * @param doInBackground doInBackground when <code>true</code>, the task will be executed in a separate thread
     * @param name the name of the executor, used to recognize threads by names
     */
    public LongTaskExecutor(boolean doInBackground, String name) {
        this(doInBackground, name, 0);
        this.interruptCancel = false;
    }

    /**
     * Creates a new long task executor.
     * @param doInBackground doInBackground when <code>true</code>, the task will be executed in a separate thread
     */
    public LongTaskExecutor(boolean doInBackground) {
        this(doInBackground, "LongTaskExecutor");
    }

    /**
     * Execute a long task with cancel and progress support. Task can be <code>null</code>.
     * In this case <code>runnable</code> will be executed normally, but without
     * cancel and progress support.
     * @param task the task to be executed, can be <code>null</code>.
     * @param runnable the runnable to be executed
     * @param taskName the name of the task, is displayed in the status bar if available
     * @throws NullPointerException if <code>runnable</code> or <code>taskName</code> is null
     * @throws IllegalStateException if a task is still executing at this time
     */
    public void execute(LongTask task, final Runnable runnable, String taskName) {
        if (runnable == null || taskName == null) {
            throw new NullPointerException();
        }
        if (runningTask != null) {
            throw new IllegalStateException("A task is still executing");
        }
        if (executor == null) {
            this.executor = Executors.newSingleThreadExecutor(new NamedThreadFactory());
        }
        runningTask = new RunningLongTask(task, runnable, taskName);
        if (inBackground) {
            Future future = executor.submit(runningTask);
            runningTask.future = future;
        } else {
            runnable.run();
        }
    }

    /**
     * Execute a long task with cancel and progress support. Task can be <code>null</code>.
     * In this case <code>runnable</code> will be executed normally, but without
     * cancel and progress support.
     * @param task the task to be executed, can be <code>null</code>.
     * @param runnable the runnable to be executed
     * @throws NullPointerException if <code>runnable</code> is null
     * @throws IllegalStateException if a task is still executing at this time
     */
    public void execute(LongTask task, Runnable runnable) {
        execute(task, runnable, "");
    }

    /**
     * Cancel the current task. If the task fails to cancel itself and if an <code>interruptDelay</code> has been specified,
     * the task will be <b>interrupted</b> after <code>interruptDelay</code>. Using <code>Thread.interrupt()</code> may cause
     * hazardous behaviours and should be avoided. Therefore any task should be cancellable.
     */
    public void cancel() {
        if (runningTask != null) {
            if (runningTask.isCancellable()) {
                if (interruptCancel) {
                    cancelTimer = new Timer(name + "_cancelTimer");
                    cancelTimer.schedule(new InterruptTimerTask(), interruptDelay);
                } else {
                }
            }
        }
    }

    /**
     * Returns <code>true</code> if the executor is executing a task.
     * @return <code>true</code> if a task is running, <code>false</code> otherwise
     */
    public boolean isRunning() {
        return runningTask != null;
    }

    /**
     * Set the listener to this executor. Only a unique listener can be set to this executor. The listener
     * is called when the task terminates normally.
     * @param listener a listener for this executor
     */
    public void setLongTaskListener(LongTaskListener listener) {
        this.listener = listener;
    }

    private void finished() {
        if (cancelTimer != null) {
            cancelTimer.cancel();
        }
        LongTask task = runningTask.task;
        runningTask = null;
        if (listener != null) {
            listener.taskFinished(task);
        }
    }

    /**
     * Inner class for associating a task to its Future instance
     */
    private class RunningLongTask implements Runnable {

        private final LongTask task;
        private final Runnable runnable;
        private Future future;
        private ProgressTicket progress;

        public RunningLongTask(LongTask task, Runnable runnable, String taskName) {
            this.task = task;
            this.runnable = runnable;
            this.progress = new ProgressTicket(taskName, new Cancellable() {

                public boolean cancel() {
                    LongTaskExecutor.this.cancel();
                    return true;
                }
            });
            if (task != null) {
                task.setProgressTicket(progress);
            }
        }

        public void run() {
            runnable.run();
            finished();
            progress.finish();
        }

        public boolean cancel() {
            if (inBackground) {
                if (future.cancel(false)) {
                    return true;
                }
            }
            if (task != null) {
                return task.cancel();
            }
            return false;
        }

        public boolean isCancellable() {
            if (inBackground) {
                if (!future.isCancelled()) {
                    return true;
                }
                return false;
            }
            return true;
        }
    }

    /**
     * Inner class for naming the executor service thread
     */
    private class NamedThreadFactory implements ThreadFactory {

        public Thread newThread(Runnable r) {
            return new Thread(r, name);
        }
    }

    private class InterruptTimerTask extends TimerTask {

        @Override
        public void run() {
            if (runningTask != null) {
                executor.shutdownNow();
            }
        }
    }
}
