package com.conveyal.r5.profile.mcrr;


import static com.conveyal.r5.profile.mcrr.IntUtils.newIntArray;
import static com.conveyal.r5.profile.mcrr.StopState.NOT_SET;
import static com.conveyal.r5.profile.mcrr.StopState.UNREACHED;

public final class StopStateFlyWeight implements StopStateCollection {
    private int size = 0;

    private final int[][] stateStopIndex;

    private final int[] times;
    private final int[] transitTimes;
    private final int[] previousPatterns;
    private final int[] previousTrips;
    private final int[] boardTimes;
    private final int[] transferTimes;
    private final int[] boardStops;
    private final int[] transferFromStops;


    public StopStateFlyWeight(int rounds, int stops) {
        this.stateStopIndex = new int[rounds][stops];

        final int limit = 3 * stops;

        this.times = newIntArray(limit, UNREACHED);

        this.boardStops = newIntArray(limit, NOT_SET);
        this.transitTimes = newIntArray(limit, UNREACHED);
        this.previousPatterns = newIntArray(limit, NOT_SET);
        this.previousTrips = newIntArray(limit, NOT_SET);
        this.boardTimes = newIntArray(limit, UNREACHED);

        this.transferFromStops = newIntArray(limit, NOT_SET);
        this.transferTimes = newIntArray(limit, NOT_SET);
    }

    @Override
    public void setInitalTime(int round, int stop, int time) {
        final int index = findOrCreateStopIndex(round, stop);
        times[index] = time;
    }

    @Override
    public void transitToStop(int round, int stop, int time, int fromPattern, int boardStop, int tripIndex, int boardTime, boolean bestTime) {
        final int index = findOrCreateStopIndex(round, stop);

        transitTimes[index] = time;
        previousPatterns[index] = fromPattern;
        previousTrips[index] = tripIndex;
        boardTimes[index] = boardTime;
        boardStops[index] = boardStop;

        if(bestTime) {
            times[index] = time;
            transferFromStops[index] = NOT_SET;
        }
    }

    /**
     * Set the time at a transit index iff it is optimal. This sets both the best time and the transfer time
     */
    @Override
    public void transferToStop(int round, int stop, int time, int fromStop, int transferTime) {
        final int index = findOrCreateStopIndex(round, stop);
        times[index] = time;
        transferFromStops[index] = fromStop;
        transferTimes[index] = transferTime;
    }

    public Cursor newCursor() {
        return new Cursor();
    }



    private int nextAvailable() {
        // Skip the first element, index 0 is not used for optimaziations reasons
        return ++size;
    }

    private int findOrCreateStopIndex(final int round, final int stop) {
        if(stateStopIndex[round][stop] == 0) {
            stateStopIndex[round][stop] = nextAvailable();
        }
        return stateStopIndex[round][stop];
    }

    public class Cursor implements StopStateCursor, StopState {
        private int cursor;

        public StopState stop(int round, int stop) {
            this.cursor = stateStopIndex[round][stop];
            return this;
        }

        @Override
        public final int time() {
            return times[cursor];
        }

        @Override
        public int transitTime() {
            return transitTimes[cursor];
        }

        @Override
        public boolean isTransitTimeSet() {
            return transitTimes[cursor] != UNREACHED;
        }

        @Override
        public int previousPattern() {
            return previousPatterns[cursor];
        }

        @Override
        public int previousTrip() {
            return previousTrips[cursor];
        }

        @Override
        public int transferTime() {
            return transferTimes[cursor];
        }

        @Override
        public int boardStop() {
            return boardStops[cursor];
        }

        @Override
        public int boardTime() {
            return boardTimes[cursor];
        }

        @Override
        public int transferFromStop() {
            return transferFromStops[cursor];
        }

        @Override
        public boolean arrivedByTransfer() {
            return transferFromStops[cursor] != NOT_SET;
        }

        @Override
        public String toString() {
            return asString();
        }
    }
}
