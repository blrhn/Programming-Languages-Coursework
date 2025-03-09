package pwr.edu.carwash.simulation.shared;

import pwr.edu.carwash.simulation.threads.CarThread;

import java.util.ArrayList;
import java.util.List;

// wash station mechanism a la guarded blocks
public class WashStation {
    private CarThread car;
    private boolean isFree;
    private final List<WashDevice> washDevices;

    public WashStation(List<WashDevice> washDevices) {
        this.isFree = true;
        this.washDevices = washDevices;
    }

    public WashStation() {
        washDevices = new ArrayList<>();
    };

    public synchronized boolean isFree() {
        return isFree;
    }

    public synchronized void occupy(CarThread car) {
        while (!isFree) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        isFree = false;
        this.car = car;
        notifyAll();
    }

    public synchronized void setFree() {
        isFree = true;
        car = null;
        notifyAll();
    }

    public synchronized CarThread getCar() {
        return car;
    }

    public synchronized List<WashDevice> getWashDevices() {
        return washDevices;
    }
}
