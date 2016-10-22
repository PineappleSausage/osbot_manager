package gui.dialogues.input_dialog;

import bot_parameters.configuration.WorldType;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import bot_parameters.account.RunescapeAccount;
import bot_parameters.configuration.Configuration;
import bot_parameters.proxy.Proxy;
import bot_parameters.script.Script;
import javafx.scene.control.cell.CheckBoxListCell;

import java.util.ArrayList;

public final class ConfigurationDialog extends InputDialog<Configuration> {

    private final ComboBox<RunescapeAccount> accountSelector;
    private final ComboBox<Script> scriptSelector;
    private final ComboBox<Proxy> proxySelector;
    private final TextField memoryAllocation;
    private final CheckBox collectData;
    private final CheckBox debugMode;
    private final TextField debugPort;
    private final CheckBox lowResourceMode;
    private final CheckBox lowCpuMode;
    private final ChoiceBox<WorldType> worldTypeSelector;
    private final CheckBox randomizeWorld;
    private final ChoiceBox<Integer> worldSelector;


    public ConfigurationDialog(ObservableList<RunescapeAccount> accountList, ObservableList<Script> scriptList, ObservableList<Proxy> proxyList) {

        setHeaderText("Add A Run Configuration");

        accountSelector = new ComboBox<>(accountList);
        accountSelector.setPromptText("Account");

        scriptSelector = new ComboBox<>(scriptList);
        scriptSelector.setPromptText("Script");

        proxySelector = new ComboBox<>(proxyList);
        proxySelector.setPromptText("(Optional) Proxy");

        memoryAllocation = new TextField();
        memoryAllocation.setPromptText("(Optional) Memory Allocation");
        memoryAllocation.setTextFormatter(new TextFormatter<>(change -> change.getText().matches("\\d*") ? change : null));

        collectData = new CheckBox("Allow data collection");

        debugMode = new CheckBox("Debug mode");

        debugPort = new TextField();
        debugPort.setPromptText("Debug port");
        debugPort.setTextFormatter(new TextFormatter<>(change -> change.getText().matches("\\d*") ? change : null));

        lowResourceMode = new CheckBox("Low resource mode");

        lowCpuMode = new CheckBox("Low cpu mode");

        worldTypeSelector = new ChoiceBox<>(FXCollections.observableArrayList(WorldType.values()));

        worldSelector = new ChoiceBox<>(FXCollections.observableArrayList(WorldType.F2P.worlds));
        worldSelector.getSelectionModel().select(0);

        worldTypeSelector.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            worldSelector.getItems().setAll(worldTypeSelector.getItems().get((Integer) newValue).worlds);
            worldSelector.getSelectionModel().select(0);
        });

        randomizeWorld = new CheckBox("Randomize");

        grid.add(new Label("Account:"), 0, 0);
        grid.add(accountSelector, 1, 0);

        grid.add(new Label("Script:"), 0, 1);
        grid.add(scriptSelector, 1, 1);

        grid.add(new Label("World Type: "), 0, 2);
        grid.add(worldTypeSelector, 1, 2);
        grid.add(randomizeWorld, 2, 2);
        grid.add(new Label("World: "), 3, 2);
        grid.add(worldSelector, 4, 2);

        grid.add(new Label("Proxy:"), 0, 3);
        grid.add(proxySelector, 1, 3);

        grid.add(new Label("Memory:"), 0, 4);
        grid.add(memoryAllocation, 1, 4);

        grid.add(collectData, 1, 5);
        grid.add(debugMode, 1, 6);
        grid.add(debugPort, 2, 6);
        grid.add(lowResourceMode, 1, 7);
        grid.add(lowCpuMode, 1, 8);

        accountSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(accountSelector.getSelectionModel().getSelectedItem() == null ||
                                scriptSelector.getSelectionModel().getSelectedItem() == null);
        });

        scriptSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(accountSelector.getSelectionModel().getSelectedItem() == null ||
                                scriptSelector.getSelectionModel().getSelectedItem() == null);
        });

        Platform.runLater(accountSelector::requestFocus);
    }

    @Override
    public void setValues(final Configuration existingItem) {
        if(existingItem == null) {
            accountSelector.setValue(null);
            scriptSelector.setValue(null);
            proxySelector.setValue(null);
            memoryAllocation.setText("");
            collectData.setSelected(false);
            debugMode.setSelected(false);
            debugPort.setText("");
            lowResourceMode.setSelected(false);
            lowCpuMode.setSelected(false);
            worldTypeSelector.getSelectionModel().select(WorldType.F2P);
            worldSelector.getSelectionModel().select(0);
            randomizeWorld.setSelected(false);
            return;
        }
        accountSelector.setValue(existingItem.getRunescapeAccount());
        scriptSelector.setValue(existingItem.getScript());
        proxySelector.setValue(existingItem.getProxy());
        memoryAllocation.setText(String.valueOf(existingItem.getMemoryAllocation()));
        collectData.setSelected(existingItem.isCollectData());
        debugMode.setSelected(existingItem.isDebugMode());
        debugPort.setText(String.valueOf(existingItem.getDebugPort()));
        lowResourceMode.setSelected(existingItem.isLowResourceMode());
        lowCpuMode.setSelected(existingItem.isLowCpuMode());
        worldTypeSelector.getSelectionModel().select(existingItem.getWorldType());
        worldSelector.getSelectionModel().select(existingItem.getWorld());
        randomizeWorld.setSelected(existingItem.isRandomizeWorld());
    }

    @Override
    protected final Configuration onAdd() {
        Configuration configuration = new Configuration(accountSelector.getSelectionModel().getSelectedItem(), scriptSelector.getSelectionModel().getSelectedItem());
        if (proxySelector.getSelectionModel().getSelectedItem() != null) {
            configuration.setProxy(proxySelector.getSelectionModel().getSelectedItem());
        }
        if (!memoryAllocation.getText().trim().isEmpty()) {
            configuration.setMemoryAllocation(Integer.parseInt(memoryAllocation.getText().trim()));
        }
        if (debugMode.isSelected() && !debugPort.getText().trim().isEmpty()) {
            configuration.setDebugMode(true);
            configuration.setDebugPort(Integer.parseInt(debugPort.getText().trim()));
        }
        if (collectData.isSelected()) {
            configuration.setCollectData(true);
        }
        if (lowCpuMode.isSelected()) {
            configuration.setLowCpuMode(true);
        }
        if (lowResourceMode.isSelected()) {
            configuration.setLowResourceMode(true);
        }
        configuration.setWorldType(worldTypeSelector.getValue());
        configuration.setWorld(worldSelector.getValue());
        configuration.setRandomizeWorld(randomizeWorld.isSelected());

        return configuration;
    }

    @Override
    protected Configuration onEdit(final Configuration existingItem) {
        existingItem.setRunescapeAccount(accountSelector.getSelectionModel().getSelectedItem());
        existingItem.setScript(scriptSelector.getSelectionModel().getSelectedItem());
        existingItem.setProxy(proxySelector.getSelectionModel().getSelectedItem());
        if (!memoryAllocation.getText().trim().isEmpty()) {
            existingItem.setMemoryAllocation(Integer.parseInt(memoryAllocation.getText().trim()));
        } else {
            existingItem.setMemoryAllocation(null);
        }
        if (debugMode.isSelected() && !debugPort.getText().trim().isEmpty()) {
            existingItem.setDebugMode(true);
            existingItem.setDebugPort(Integer.parseInt(debugPort.getText().trim()));
        } else {
            existingItem.setDebugMode(false);
            existingItem.setDebugPort(null);
        }
        existingItem.setCollectData(collectData.isSelected());
        existingItem.setLowCpuMode(lowCpuMode.isSelected());
        existingItem.setLowResourceMode(lowResourceMode.isSelected());
        existingItem.setWorldType(worldTypeSelector.getValue());
        existingItem.setWorld(worldSelector.getValue());
        existingItem.setRandomizeWorld(randomizeWorld.isSelected());
        return existingItem;
    }
}
