package com.example.fueltracker;

import androidx.annotation.NonNull;
import androidx.car.app.CarContext;
import androidx.car.app.Screen;
import androidx.car.app.model.Action;
import androidx.car.app.model.Pane;
import androidx.car.app.model.PaneTemplate;
import androidx.car.app.model.Row;
import androidx.car.app.model.Template;

public class CarScreen extends Screen {

    private String _message = "";

    protected CarScreen(@NonNull CarContext carContext) {
        super(carContext);
    }

    @NonNull
    @Override
    public Template onGetTemplate() {
        Row row = new Row.Builder().setTitle("Hermes is running").addText(_message).build();
        Pane pane = new Pane.Builder().addRow(row).build();
        return new PaneTemplate.Builder(pane)
                .setHeaderAction(Action.APP_ICON)
                .build();
    }

    public void updateScreen(String message) {
        _message = message;
        invalidate();
    }
}