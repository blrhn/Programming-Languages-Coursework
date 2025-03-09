package pwr.edu.carwash.simulation.threads;

import pwr.edu.carwash.simulation.enums.CarState;
import pwr.edu.carwash.simulation.shared.Queues;
import pwr.edu.carwash.simulation.shared.WashDevice;
import pwr.edu.carwash.simulation.shared.WashStation;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/locks/Condition.html

public class CarThread extends BaseThread {
    private final Queues queues;
    private WashStation washStation;
    private CarState carState;
    private final Lock lock = new ReentrantLock();
    private final Condition stationAssigned = lock.newCondition();

    public CarThread(String signature, long delay, Queues queues, Updatable updatable) {
        super(signature, delay, updatable);
        this.queues = queues;
    }

    @Override
    public void run() {
        try {
            while (!BaseThread.isFinished()) {
                goIntoQueue();
                waitForAssignment();
                goIntoStation();
                resetState();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void goIntoQueue() {
        updateCarState(CarState.waitingForQueue);
        randomDelay(5000);

        queues.addToQueue(this);
        updateCarState(CarState.inQueue);
    }

    private void goIntoStation() throws InterruptedException {
        if (washStation != null) {
            washStation.occupy(this);
            updateCarState(CarState.inStation);

            randomDelay(5000);
            wash();

            washStation.setFree();
            updateCarState(CarState.finished);
        }
    }

    // the car waits until it gets assigned to a certain wash station by the controller
    private void waitForAssignment() throws InterruptedException {
        lock.lock();

        try {
            while (washStation == null && !BaseThread.isFinished()) {
                stationAssigned.await();
            }
        } finally {
            lock.unlock();
        }
    }

    private void wash() {
        useWater();
        useSoap();
        useWater();
    }

    // only one car can use soap at a time, if it's in use, the car must wait until it's free
    private void useSoap() {
        WashDevice device = null;

        while (device == null && !BaseThread.isFinished()) {
            for (WashDevice washDevice : washStation.getWashDevices()) {
                if (washDevice.tryUseSoap()) {
                    device = washDevice;
                    updateCarState(CarState.usingSoap);
                    randomDelay(5000);
                    washDevice.releaseSoap();

                    break;
                }

                this.updateCarState(CarState.waitingForDevice);
                randomDelay(200);
            }
        }
    }

    // only one car can use water at a time, if it's in use, the car must wait until it's free
    private void useWater() {
        WashDevice device = null;

        while (device == null && !BaseThread.isFinished()) {
            for (WashDevice washDevice : washStation.getWashDevices()) {
                if (washDevice.tryUseWater()) {
                    device = washDevice;
                    updateCarState(CarState.usingWater);
                    randomDelay(5000);
                    washDevice.releaseWater();

                    break;
                }

                updateCarState(CarState.waitingForDevice);
                randomDelay(200);
            }
        }
    }

    public CarState getCarState() {
        return carState;
    }

    public void updateCarState(CarState carState) {
        this.carState = carState;

        if (getUpdatable() != null && !BaseThread.isFinished()) {
            getUpdatable().update();
        }
    }

    public void setWashStation(WashStation washStation) {
        lock.lock();
        try {
            this.washStation = washStation;
            stationAssigned.signal();
        } finally {
            lock.unlock();
        }
    }

    // the car repeats the sequence after it completes all the steps
    private void resetState() {
        updateCarState(CarState.waitingForQueue);
        setWashStation(null);
        randomDelay(5000);
    }
}
