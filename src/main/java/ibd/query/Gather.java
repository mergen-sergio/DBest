package ibd.query;

import ibd.query.sourceop.ParallelIndexScan;
import ibd.query.unaryop.UnaryOperation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class Gather extends UnaryOperation {

    static final Tuple END = new Tuple();

    Operation[] copies;

    ParallelIndexScan[] scans;

    GatherIterator running = null;

    public Gather(Operation... copies) throws Exception {
        super(copies[0]);
        this.copies = copies;
        for (int i = 1; i < copies.length; i++) {
            copies[i].setParentOperation(this);
            childOperations.add(copies[i]);
        }
        scans = new ParallelIndexScan[copies.length];
        for (int i = 0; i < copies.length; i++) {
            scans[i] = findParallelScan(copies[i]);
            if (scans[i] == null) {
                throw new Exception("Gather copy " + i + " has no parallel index scan");
            }
            scans[i].setPart(i, copies.length);
        }
    }

    private static ParallelIndexScan findParallelScan(Operation op) {
        if (op instanceof ParallelIndexScan) {
            return (ParallelIndexScan) op;
        }
        for (Operation child : op.getChildOperations()) {
            ParallelIndexScan scan = findParallelScan(child);
            if (scan != null) {
                return scan;
            }
        }
        return null;
    }

    @Override
    public boolean canProcessDelegatedFilters() {
        return false;
    }

    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        stop();
        running = new GatherIterator(processedTuples, withFilterDelegation);
        return running;
    }

    void stop() {
        if (running != null) {
            running.stop();
            running = null;
        }
    }

    @Override
    public void close() throws Exception {
        stop();
        for (Operation copy : copies) {
            copy.close();
        }
    }

    @Override
    public String toString() {
        return "Gather (" + copies.length + " threads)";
    }

    public class GatherIterator extends UnpagedOperationIterator {

        Thread[] threads;

        LinkedBlockingQueue<Tuple>[] queues;

        Throwable[] errors;

        volatile boolean stopped = false;

        boolean started = false;

        int current = 0;

        public GatherIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());
            threads = new Thread[copies.length];
            queues = new LinkedBlockingQueue[copies.length];
            errors = new Throwable[copies.length];
            for (int i = 0; i < copies.length; i++) {
                scans[i].setCancelled(false);
                queues[i] = new LinkedBlockingQueue<>();
                int copy = i;
                List<Tuple> ownTuples = new ArrayList<>(processedTuples);
                threads[i] = new Thread(() -> produce(copy, ownTuples), "gather-" + i);
                threads[i].setDaemon(true);
            }
        }

        void produce(int copy, List<Tuple> ownTuples) {
            try {
                Iterator<Tuple> it = copies[copy].lookUp(ownTuples, false);
                while (!stopped && it.hasNext()) {
                    queues[copy].add(it.next());
                }
            } catch (Throwable t) {
                errors[copy] = t;
            }
            queues[copy].add(END);
        }

        @Override
        protected Tuple findNextTuple() {
            if (stopped) {
                throw new IllegalStateException("Iterator was closed");
            }
            if (!started) {
                started = true;
                for (Thread thread : threads) {
                    thread.start();
                }
            }
            while (current < copies.length) {
                Tuple tuple;
                try {
                    tuple = queues[current].take();
                } catch (InterruptedException e) {
                    stop();
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Gather was interrupted", e);
                }
                if (tuple != END) {
                    return tuple;
                }
                if (errors[current] != null) {
                    Throwable error = errors[current];
                    stop();
                    throw new IllegalStateException("Copy " + current + " failed: " + error.getMessage(), error);
                }
                current++;
            }
            return null;
        }

        void stop() {
            stopped = true;
            for (int i = 0; i < copies.length; i++) {
                scans[i].setCancelled(true);
                queues[i].clear();
            }
            boolean interrupted = false;
            for (Thread thread : threads) {
                while (thread.isAlive()) {
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        interrupted = true;
                    }
                }
            }
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
