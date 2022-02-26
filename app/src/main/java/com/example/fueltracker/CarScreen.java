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
    protected CarScreen(@NonNull CarContext carContext) {
        super(carContext);
    }

    private String aMessage = "Hello, world!";

    @NonNull
    @Override
    public Template onGetTemplate() {
        Row row = new Row.Builder().setTitle(aMessage).build();
//        Row row = new Row.Builder().setTitle(Integer.toString(i)).build();
        Pane pane = new Pane.Builder().addRow(row).build();
        return new PaneTemplate.Builder(pane)
                .setHeaderAction(Action.APP_ICON)
                .build();
    }

    public void updateScreen(String message) {
        aMessage = message;
        invalidate();
    }
}