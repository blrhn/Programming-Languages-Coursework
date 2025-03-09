package pwr.edu.carwash.simulation.utils;

import pwr.edu.carwash.simulation.threads.CarThread;

public record CarQueuePair(CarThread carThread, int queueId) {
}
