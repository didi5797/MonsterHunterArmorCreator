package com.example.monsterhunter;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;

public class CheckBoxListCell<T> extends ListCell<T> {
    private final CheckBox checkBox = new CheckBox();

    public CheckBoxListCell() {
        // Update the selected state of the item when the checkbox is clicked
        checkBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                getListView().getSelectionModel().select(getItem());
            } else {
                getListView().getSelectionModel().clearSelection(getIndex());
            }
        });
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            setText(item.toString()); // Display the name of the ArmorSet
            setGraphic(checkBox);

            // Bind the checkbox's selected state to the item's selection state
            checkBox.setSelected(getListView().getSelectionModel().isSelected(getIndex()));
        }
    }
}