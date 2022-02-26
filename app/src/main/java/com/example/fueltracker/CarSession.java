package com.example.fueltracker;

import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.car.app.CarContext;
import androidx.car.app.Screen;
import androidx.car.app.Session;
import androidx.car.app.hardware.CarHardwareManager;
import androidx.car.app.hardware.common.CarValue;
import androidx.car.app.hardware.info.CarInfo;
import androidx.car.app.hardware.info.CarSensors;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

public final class CarSession extends Session {

    public static float _acceleration = 0;
    public static float _speed = 0;
    public static float _fuel = 0;
    public static String _make = "";
    public static String _model = "";
    public static int _year = 0;

    @Override
    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.P)
    public Screen onCreateScreen(@NonNull Intent intent) {
        CarContext ctx = getCarContext();
        Executor executor = ctx.getMainExecutor();

        CarScreen screen = new CarScreen(ctx);

        CarHardwareManager hardwareManager = ctx.getCarService(CarHardwareManager.class);
        CarSensors carSensors = hardwareManager.getCarSensors();
        CarInfo carInfo = hardwareManager.getCarInfo();

        List<String> permissions = Arrays.asList(
                "com.google.android.gms.permission.CAR_SPEED",
                "com.google.android.gms.permission.CAR_FUEL",
                "android.permission.ACCESS_COARSE_LOCATION",
                "android.permission.ACCESS_FINE_LOCATION");

        getCarContext().requestPermissions(permissions, executor, (List<String> grantedPermissions, List<String> rejectedPermissions) -> {

            // TODO check granted permissions to make sure data can be read
            // TODO and show error message that user needs to accept permissions

            carInfo.fetchModel(executor, (data) -> {
                CarValue<String> make = data.getManufacturer();
                CarValue<String> model = data.getName();
                CarValue<Integer> year = data.getYear();

                if (make.getValue() != null && make.getStatus() == CarValue.STATUS_SUCCESS) {
                    _make = make.getValue();
                }
                if (model.getValue() != null && model.getStatus() == CarValue.STATUS_SUCCESS) {
                    _model = model.getValue();
                }
                if (year.getValue() != null && year.getStatus() == CarValue.STATUS_SUCCESS) {
                    _year = year.getValue();
                }
            });

            carSensors.addAccelerometerListener(CarSensors.UPDATE_RATE_NORMAL, executor, (data) -> {
                CarValue<List<Float>> acceleration = data.getForces();
                if (acceleration.getValue() != null && acceleration.getStatus() == CarValue.STATUS_SUCCESS) {
                    List<Float> accXYZ = acceleration.getValue();
                    float accX = accXYZ.get(0);
                    screen.updateScreen(Float.toString(accX));
                    _acceleration = accX;
                } else {
                    screen.updateScreen("Acceleration read error");
                }
            });

            carInfo.addSpeedListener(executor, (data) -> {
                CarValue<Float> displaySpeed = data.getDisplaySpeedMetersPerSecond();
                if (displaySpeed.getValue() != null && displaySpeed.getStatus() == CarValue.STATUS_SUCCESS) {
                    float speed = displaySpeed.getValue();
                    screen.updateScreen(Float.toString(speed));
                    _speed = speed;
                } else {
                    screen.updateScreen("Speed read error");
                    // Display error
                }
            });

            carInfo.addEnergyLevelListener(executor, (data) -> {
                CarValue<Float> fuelLevel = data.getFuelPercent();
                if (fuelLevel.getValue() != null && fuelLevel.getStatus() == CarValue.STATUS_SUCCESS) {
                    float fuel = fuelLevel.getValue();
                    screen.updateScreen(Float.toString(fuel));
                    _fuel = fuel;
                } else {
                    screen.updateScreen("Fuel read error");
                    // Display error
                }
            });

        });
        return screen;
    }
}
