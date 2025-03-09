package pwr.edu.carwash.simulation.shared;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// wash device (soap and water) mechanism using locks
public class WashDevice {
    private final Lock waterLock = new ReentrantLock();
    private final Lock soapLock = new ReentrantLock();

    public boolean tryUseWater() {
        return waterLock.tryLock();
    }

    public void releaseWater() {
        waterLock.unlock();
    }

    public boolean tryUseSoap() {
        return soapLock.tryLock();
    }

    public void releaseSoap() {
        soapLock.unlock();
    }
}
