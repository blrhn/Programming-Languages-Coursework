package pwr.edu.carwash.gui.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pwr.edu.carwash.simulation.enums.CarState;
import pwr.edu.carwash.simulation.enums.ControllerState;
import pwr.edu.carwash.simulation.shared.Queues;
import pwr.edu.carwash.simulation.shared.WashDevice;
import pwr.edu.carwash.simulation.shared.WashStation;
import pwr.edu.carwash.simulation.threads.BaseThread;
import pwr.edu.carwash.simulation.threads.CarThread;
import pwr.edu.carwash.simulation.threads.ControllerThread;
import pwr.edu.carwash.simulation.threads.Updatable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class ViewController implements Updatable {
    @FXML private VBox controllerVBox;
    @FXML private HBox stationsHBox;
    @FXML private Spinner<Integer> carSpinner;
    @FXML private VBox carsVBox;
    @FXML private HBox queue1HBox;
    @FXML private HBox queue2HBox;
    @FXML private Spinner<Integer> queueSpinner;
    @FXML private Button startButton;
    @FXML private Spinner<Integer> stationSpinner;

    private int carCount;
    private int queueCount;
    private int stationCount;

    private List<CarThread> cars;
    private List<WashStation> washStations;
    private Queues queues;
    private ControllerThread controllerThread;

    public void initialize() {
        initializeSpinners();
    }

    private void initializeSpinners() {
        // initialize spinners for user input
        carSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 10));
        queueSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 5));
        stationSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 8));
    }

    @FXML
    private void onStartButton(ActionEvent event) {
        getSpinnerValues();
        toggleSpinners(true);
        start();
    }

    @FXML
    private void onStopButton(ActionEvent event) {
        stop();
        toggleSpinners(false);
    }

    private void getSpinnerValues() {
        carCount = carSpinner.getValue();
        queueCount = queueSpinner.getValue();
        stationCount = stationSpinner.getValue();
    }

    private void toggleSpinners(boolean disable) {
        carSpinner.setDisable(disable);
        queueSpinner.setDisable(disable);
        stationSpinner.setDisable(disable);
        startButton.setDisable(disable);
    }

    private void start() {
        initializeComponents();
        initializeWashStations();
        createCarThreads();
        createControllerThread();
    }

    private void initializeComponents() {
        queues = new Queues(queueCount);
        cars = new ArrayList<>();
        washStations = new ArrayList<>();
    }

    private void initializeWashStations() {
        List<WashDevice> washDevices = new ArrayList<>();

        for (int i = 0; i < stationCount - 1; i++) {
            washDevices.add(new WashDevice());
        }

        for (int i = 0; i < stationCount; i++) {
            List<WashDevice> tempDevices = new ArrayList<>();
            if (i > 0) {
                tempDevices.add(washDevices.get(i - 1));
            }
            if (i < stationCount - 1) {
                tempDevices.add(washDevices.get(i));
            }

            washStations.add(new WashStation(tempDevices));
        }
    }

    private void createCarThreads() {
        for (int i = 0; i < carCount; i++) {
            CarThread carThread = new CarThread(String.valueOf((char)(i + 'a')),
                    (long) (Math.random() * 1000), queues, this);
            cars.add(carThread);
            carThread.start();
        }
    }

    private void createControllerThread() {
        controllerThread = new ControllerThread("P",
                (long) (Math.random() * 1000), queues, washStations, this);
        controllerThread.start();
    }

    private void stop() {
        BaseThread.setFinished(true);
        cars.clear();
        washStations.clear();
        queues = null;
        reset();
    }

    private void reset() {
        carsVBox.getChildren().clear();
        queue1HBox.getChildren().clear();
        queue2HBox.getChildren().clear();
        controllerVBox.getChildren().clear();
        stationsHBox.getChildren().clear();
    }

    private void refresh() {
        refreshCars();
        refreshQueues();
        refreshController();
        refreshStations();
    }

    private void refreshCars() {
        carsVBox.getChildren().setAll(
                cars.stream()
                    .map(car -> new Label(car.getCarState() == CarState.waitingForQueue ? car.getSignature() : "."))
                    .toList());
    }

    private void refreshQueues() {
        refreshQueue(queue1HBox, queues.getQueues().getFirst());
        refreshQueue(queue2HBox, queues.getQueues().getLast());
    }

    private void refreshQueue(HBox queueBox, BlockingQueue<CarThread> queue) {
        queueBox.getChildren().clear();
        queue.forEach(car -> queueBox.getChildren().add(new Label(car.getSignature())));
        fillEmptySlots(queueBox, queue.size());
    }

    private void fillEmptySlots(HBox queueBox, int occupiedSlots) {
        for (int i = 0; i < queueCount - occupiedSlots; i++) {
            queueBox.getChildren().add(new Label("."));
        }
    }

    private void refreshController() {
        controllerVBox.getChildren().clear();
        ControllerState state = controllerThread.getControllerState();

        if (state == ControllerState.up) {
            controllerVBox.getChildren().addAll(new Label(">"), new Label(" "), new Label("P"));
        } else if (state == ControllerState.down) {
            controllerVBox.getChildren().addAll(new Label("P"), new Label(" "), new Label(">"));
        }
    }

    private void refreshStations() {
        stationsHBox.getChildren().clear();

        stationsHBox.getChildren().add(new Label("|"));
        for (WashStation washStation : washStations) {
            String content = washStation.getCar() != null
                    ? " " + washStation.getCar().getSignature() + getCarSymbol(washStation.getCar()) + " |" : " . |";

            stationsHBox.getChildren().add(new Label(content));
        }
    }

    private String getCarSymbol(CarThread car) {
        return switch (car.getCarState()) {
            case usingWater -> " (W)";
            case usingSoap -> " (S)";
            case waitingForDevice -> " (-)";
            default -> "";
        };
    }

    // refresh gui
    @Override
    public void update() {
        Platform.runLater(this::refresh);
    }
}