package pwr.edu.travels.gui.controller;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pwr.edu.travels.client.model.Result;
import pwr.edu.travels.client.service.parser.Parser;
import pwr.edu.travels.client.service.utils.DataScope;

import java.util.List;

public class ViewController {

    @FXML
    private TableView<Result> dataTable;
    @FXML
    private Pagination tablePagination;
    @FXML
    private ComboBox<String> sectionComboBox;
    @FXML
    private ComboBox<String> variableComboBox;
    @FXML
    private ComboBox<Integer> yearComboBox;
    @FXML
    private Button loadDataButton;

    private final ObservableList<String> variableList = FXCollections.observableArrayList();
    private final ObservableList<String> sectionList = FXCollections.observableArrayList();
    private final ObservableList<Integer> yearList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        populateVariable();
        populateSection();
        populateYear();

        variableComboBox.setItems(variableList);
        sectionComboBox.setItems(sectionList);
        yearComboBox.setItems(yearList);

        dataTable.setPlaceholder(new Label("Brak danych"));

        addListeners();
    }

    // fill all combo boxes
    private void populateVariable() {
        List<String> variables = DataScope.getVariables();

        variableList.addAll(variables);
    }

    private void populateSection() {
        List<String> sections = DataScope.getLastSection();

        sectionList.addAll(sections);
    }

    private void populateYear() {
        int index = sectionComboBox.isDisabled() ? -1 : sectionComboBox.getSelectionModel().getSelectedIndex();
        List<Integer> years = DataScope.getPossibleYears(index);

        yearList.addAll(years);
    }

    // add listeners on combo boxes' changes
    public void addListeners() {
        variableComboBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                sectionComboBox.setDisable(variableComboBox.getSelectionModel().getSelectedIndex() == 0);
                clearYearBox();
                sectionComboBox.getSelectionModel().clearSelection();
                tablePagination.setDisable(true);
            }
        });

        sectionComboBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !sectionComboBox.isDisabled()) {
                clearYearBox();
                tablePagination.setDisable(true);
            }
        });

        yearComboBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (sectionComboBox.getSelectionModel().getSelectedIndex() >= 0 || sectionComboBox.isDisabled()) {
                    loadDataButton.setDisable(false);
                    tablePagination.setDisable(true);
                }
            }
        });

        tablePagination.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> {
            clearTable();
            populateDataTable(newValue.intValue());
        });
    }

    private void clearYearBox() {
        yearList.clear();
        populateYear();
        yearComboBox.setDisable(false);
        yearComboBox.getSelectionModel().clearSelection();
        loadDataButton.setDisable(true);
    }

    private void clearTable() {
        dataTable.getItems().clear();
        dataTable.getColumns().clear();
        dataTable.refresh();
    }

    @FXML
    void onLoadButton(ActionEvent event)  {
        tablePagination.setCurrentPageIndex(0);
        clearTable();
        populateDataTable(0);
        tablePagination.setDisable(false);
    }

    // dynamically populates table
    private void populateDataTable(int pageNumber) {
        int variableIndex = variableComboBox.getSelectionModel().getSelectedIndex();
        int sectionIndex = sectionComboBox.getSelectionModel().getSelectedIndex();
        int year = yearComboBox.getSelectionModel().getSelectedItem();

        List<Result> results = Parser.getResults(variableIndex, sectionIndex, year, pageNumber);

        if (results != null) {
            List<Integer> pageInfo = Parser.getPageData();
            tablePagination.setPageCount(pageInfo.getLast() == 0 ? 1 : pageInfo.getLast());
            List<String> colNames = Parser.getColumnNames();

            populateTableColumns(colNames, results);
        } else {
            clearTable();
        }
    }

    private void populateTableColumns(List<String> colNames, List<Result> results) {
        ObservableList<Result> observableResults = FXCollections.observableArrayList(results);

        for (int i = 0; i < colNames.size(); i++) {
            final int finalIdx = i;
            TableColumn<Result, String> column = new TableColumn<>(colNames.get(i));
            column.setCellValueFactory(data -> {
                List<String> positions = data.getValue().positions();
                return new SimpleStringProperty(positions.get(finalIdx));
            });
            dataTable.getColumns().add(column);
        }

        TableColumn<Result, Number> valueColumn = new TableColumn<>("Wartość" + " [" + Parser.getWayOfPresentation() + "]");
        valueColumn.setCellValueFactory(data -> new SimpleFloatProperty(data.getValue().value()));
        dataTable.getColumns().add(valueColumn);

        TableColumn<Result, String> annotationColumn = new TableColumn<>("Informacje dodatkowe");
        annotationColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().annotation()));
        dataTable.getColumns().add(annotationColumn);

        dataTable.setItems(observableResults);
    }
}