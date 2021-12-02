package it.unibo.oop.lab.workers02;

import java.util.ArrayList;
import java.util.List;

/**
 * Standard implementation.
 */
public class MultiThreadedSumMatrix implements SumMatrix {

    private final int nthread;

    /**
     * @param nthread
     *                    number of threads.
     */
    public MultiThreadedSumMatrix(final int nthread) {
        this.nthread = nthread;
    }

    private static class Worker extends Thread {

        private final int startRow;
        private final int endRow;
        private final double[][] matrix;
        private double res;

        /**
         * Build a new worker.
         * 
         * @param matrix
         *            the matrix to sum
         * @param startRow
         *            the starting row for this worker
         * @param endRow
         *            the ending row for this worker
         */
        Worker(final double[][] matrix, final int startRow, final int endRow) {
            super();
            this.startRow = startRow;
            this.endRow = endRow;
            this.matrix = matrix;
        }

        @Override
        public void run() {
            System.out.println("Working from position " + startRow + " to position " + (startRow + endRow - 1));
            for (int i = startRow; i < matrix.length && i < startRow + endRow; i++) {
                for (int j = 0; j < matrix[0].length; j++) {
                    this.res += this.matrix[i][j];
                }
            }
        }

        /**
         * Returns the result of summing up the integers within the list.
         * 
         * @return the sum of every element in the array
         */
        public double getResult() {
            return this.res;
        }
    }

    /**
     * 
     */
    @Override
    public double sum(final double[][] matrix) {
        final int size = matrix.length % nthread + matrix.length / nthread;
        /*
         * Build a list of workers
         */
        final List<Worker> workers = new ArrayList<>(nthread);
        for (int start = 0; start < matrix.length; start += size) {
            workers.add(new Worker(matrix, start, size));
        }
        /*
         * Start them
         */
        for (final Worker w: workers) {
            w.start();
        }
        /*
         * Wait for every one of them to finish. This operation is _way_ better done by
         * using barriers and latches, and the whole operation would be better done with
         * futures.
         */
        double sum = 0;
        for (final Worker w: workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        /*
         * Return the sum
         */
        return sum;
    }

}
