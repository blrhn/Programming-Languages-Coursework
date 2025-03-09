package pwr.edu.carwash.simulation.shared;

import pwr.edu.carwash.simulation.threads.CarThread;
import pwr.edu.carwash.simulation.utils.CarQueuePair;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Queues {
    private final List<BlockingQueue<CarThread>> queues;
    private int nextQueueIndex;

    public Queues(int size) {
        this.queues = List.of(new LinkedBlockingQueue<>(size), new LinkedBlockingQueue<>(size));
        this.nextQueueIndex = 0;
    }

    // adds cars to the shorter queue
    public void addToQueue(CarThread car) {
        try {
            BlockingQueue<CarThread> shorterQueue;

            synchronized (queues) {
                shorterQueue = queues.stream().min(Comparator.comparingInt(BlockingQueue::size)).orElseThrow();
            }

            shorterQueue.put(car);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // removes cars from queues alternately
    public CarQueuePair takeFromQueue() {
        try {
            BlockingQueue<CarThread> queue;
            int q;

            synchronized (queues) {
                queue = queues.get(nextQueueIndex);
                q = nextQueueIndex;
            }
            nextQueueIndex = (nextQueueIndex + 1) % queues.size();
            CarThread takenCar = queue.take();

            return new CarQueuePair(takenCar, q);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return null;
    }

    public List<BlockingQueue<CarThread>> getQueues() {
        synchronized (queues) {
            return queues;
        }
    }
}