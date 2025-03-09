package pwr.edu.carwash.simulation.threads;

import pwr.edu.carwash.simulation.enums.ControllerState;
import pwr.edu.carwash.simulation.shared.Queues;
import pwr.edu.carwash.simulation.shared.WashStation;
import pwr.edu.carwash.simulation.utils.CarQueuePair;

import java.util.List;

public class ControllerThread extends BaseThread {
    private final Queues queues;
    private final List<WashStation> washStations;
    private ControllerState controllerState;
    public ControllerThread(String signature, long delay, Queues queues, List<WashStation> washStations, Updatable updatable) {
        super(signature, delay, updatable);
        this.queues = queues;
        this.washStations = washStations;
    }

    @Override
    public void run() {
        try {
            while (!BaseThread.isFinished()) {
                assignToStation();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // the controller assigns the taken car from the queue to any free wash station
    private void assignToStation() throws InterruptedException {
        randomDelay(1000);
        WashStation chosenStation = chooseStation();

        if (chosenStation != null) {
            CarQueuePair carQueuePair = queues.takeFromQueue();

            ControllerState state = carQueuePair.queueId() == 0 ? ControllerState.up : ControllerState.down;
            setControllerState(state);

            carQueuePair.carThread().setWashStation(chosenStation);
        } else {
            randomDelay(200);
        }
    }

    private WashStation chooseStation() {
        for (WashStation washStation : washStations) {
            if (washStation.isFree()) {
                return washStation;
            }
        }

        return null;
    }

    public void setControllerState(ControllerState state) {
        this.controllerState = state;

        if (getUpdatable() != null && !BaseThread.isFinished()) {
            getUpdatable().update();
        }
    }

    public ControllerState getControllerState() {
        return controllerState;
    }
}