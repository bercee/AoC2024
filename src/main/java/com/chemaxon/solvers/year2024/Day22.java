package com.chemaxon.solvers.year2024;

import com.chemaxon.Solver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;

public class Day22 extends Solver {

    private static long MOD = 16777216L;
    List<Long> list;

    public Day22(List<String> input) {
        super(input);
        list = input.stream().mapToLong(Long::parseLong).boxed().toList();
    }


    long next(long l) {
        var sec = l;
        sec = prune(mix(sec * 64, sec));
        sec = prune(mix(sec / 32, sec));
        sec = prune(mix(sec * 2048, sec));
        return sec;
    }

    long prune(long l) {
        return l % MOD;
    }

    long mix(long l1, long l2) {
        return l1 ^ l2;
    }

    List<Long> prices(long start) {
        List<Long> ret = new ArrayList<>();
        ret.add(start % 10);
        for (int i = 0; i < 2000; i++) {
            start = next(start);
            ret.add(start % 10);
        }

        return ret;
    }

    List<Long> diffs(List<Long> secrets) {
        List<Long> ret = new ArrayList<>();
        for (int i = 0; i < secrets.size(); i++) {
            if (i == 0) {
                ret.add(null);
            } else {
                ret.add(secrets.get(i) - secrets.get(i - 1));
            }
        }

        return ret;
    }

    @Override
    public String solvePart1() {
        var ret = list.stream().mapToLong(l -> {
            long ll = l;
            for (int i = 0; i < 2000; i++) {
                ll = next(ll);
            }
            return ll;
        }).sum();
        return String.valueOf(ret);
    }

    long totalPrice(List<Long> prices, List<Long> sequence) {
        List<Long> diffs = diffs(prices);
        int i = Collections.indexOfSubList(diffs, sequence);
        if (i == -1) {
            return 0;
        }

        return prices.get(i + 3);
    }

    List<Long> generateNextSequence(List<Long> seq) {
        var lastMaxs = getLastMaxs(seq);
        if (lastMaxs == seq.size()) {
            return null;
        }

        var copy = new ArrayList<>(seq.reversed());
        for (int i = 0; i < lastMaxs; i++) {
            copy.set(i, -9L);
        }
        copy.set(lastMaxs, copy.get(lastMaxs) + 1);
        return copy.reversed();
    }

    private int getLastMaxs(List<Long> seq) {
        var copy = seq.reversed();
        for (int i = 0; i < copy.size(); i++) {
            if (copy.get(i) != 9L) {
                return i;
            }
        }
        return copy.size();
    }

    long totalPriceSum(List<List<Long>> pricesList, List<Long> sequence) {
        return pricesList.stream().mapToLong(prices -> totalPrice(prices, sequence)).sum();
    }

    private List<List<Long>> generateAllSequences(List<Long> initialSequence) {
        List<List<Long>> sequences = new ArrayList<>();
        sequences.add(initialSequence); // Add the first sequence
        List<Long> nextSequence = generateNextSequence(initialSequence);
        while (nextSequence != null) {
            sequences.add(nextSequence);
            nextSequence = generateNextSequence(nextSequence);
        }
        return sequences;
    }

    @Override
    public String solvePart2() {
        List<List<Long>> pricesList = list.stream().map(this::prices).toList();
//        List<Long> sequence = List.of(-9L, -9L, -9L, -9L);
        List<Long> initialSequence = List.of(-9L, -9L, -9L, -9L);

        List<List<Long>> allSequences = generateAllSequences(initialSequence);

        AtomicInteger processedCount = new AtomicInteger(0); // Thread-safe counter for processed sequences
        int totalSequences = allSequences.size();
        long startTime = System.currentTimeMillis();

        ForkJoinPool forkJoinPool = new ForkJoinPool(4); // Use 4 cores
        try {
            long max = forkJoinPool.submit(() ->
                    allSequences.parallelStream()
                            .mapToLong(sequence -> {
                                long result = totalPriceSum(pricesList, sequence);

                                // Increment the counter and log progress
                                int currentCount = processedCount.incrementAndGet();
                                if (currentCount % 100 == 0 || currentCount == totalSequences) {
                                    long elapsedTime = System.currentTimeMillis() - startTime; // Elapsed time in ms
                                    double avgTimePerSequence = elapsedTime / (double) currentCount; // Average time per sequence
                                    long remainingSequences = totalSequences - currentCount;
                                    long estimatedRemainingTime = (long) (remainingSequences * avgTimePerSequence); // Estimated remaining time in ms
                                    System.out.printf("\rProcessed %d/%d sequences (%.2f%%). Remaining: %s", currentCount, totalSequences, (double) currentCount / (double) totalSequences * 100, formatTime(estimatedRemainingTime));
                                }

                                return result;
                            })
                            .max()
                            .orElse(Long.MIN_VALUE)
            ).get();

            return String.valueOf(max);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

//        long max = Long.MIN_VALUE;
//        int c = 0;
//        long startTime = System.currentTimeMillis();
//        int totalTasks = (int) Math.pow(19, 4);
//
//        while (sequence != null) {
//            c++;
//            long elapsedTime = System.currentTimeMillis() - startTime;
//            double avgTimePerTask = (double) elapsedTime / c;
//
//            double estimatedRemainingTime = avgTimePerTask * (totalTasks - c);
//            double status = (double) c / totalTasks * 100;
//
//            if (c % 100 == 0) {
//                long estimatedEndTime = System.currentTimeMillis() + (long) estimatedRemainingTime;
//                String formattedEndTime = new SimpleDateFormat("HH:mm:ss").format(new Date(estimatedEndTime));
//                System.out.printf("\rTask %d/%d completed (%.2f%%). Estimated time remaining: %.2f seconds (%s)",
//                        c, totalTasks, status, estimatedRemainingTime / 1000, formattedEndTime);
//            }
//            var curr = totalPriceSum(pricesList, sequence);
//            if (curr > max) {
//                max = curr;
//            }
//
//            sequence = generateNextSequence(sequence);
//        }


//        return String.valueOf(max);
    }

    private String formatTime(long millis) {
        long seconds = (millis / 1000) % 60;
        long minutes = (millis / (1000 * 60)) % 60;
        long hours = millis / (1000 * 60 * 60);
        return String.format("%02dh %02dm %02ds", hours, minutes, seconds);
    }
}
