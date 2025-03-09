package pwr.edu.carwash.simulation.threads;

public abstract class BaseThread extends Thread {
    private final String signature;
    private final long delay;
    private final Updatable updatable;
    private static boolean isFinished;

    public BaseThread(String signature, long delay, Updatable updatable) {
        super(signature);
        this.signature = signature;
        this.delay = delay;
        this.updatable = updatable;
        isFinished = false;
        setDaemon(true);
    }

    public String getSignature() {
        return signature;
    }

    public Updatable getUpdatable() {
        return updatable;
    }

    public void randomDelay(int scope) {
        try {
            Thread.sleep(delay + (long)(Math.random() * scope));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public synchronized static void setFinished(boolean isFinished) {
        BaseThread.isFinished = isFinished;
    }

    public synchronized static boolean isFinished() {
        return isFinished;
    }
}
