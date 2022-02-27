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
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;

public final class CarSession extends Session {

    public static float _acceleration = 0;
    public static float _speed = 0;
    public static float _fuel = 0;
    public static String _make = "";
    public static String _model = "";
    public static int _year = 0;

    private final Queue<Float> accQueue = new LinkedList<>();

    // Below this acceleration, the car is maintaining a speed
    // Above, the car is attempting to increase its speed
    // The time spent increasing speed should be minimised
    private final float A_DELTA = 0.4f;

    // Each time the A_DELTA threshold passed for a prolonged time,
    // A strike will be added to the journey
    public static int strikes = 0;

    public static long strikeTime = System.currentTimeMillis();

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

                if (make.getValue() != null && make.getStatus() == CarValue.STATUS_SUCCESS)
                    _make = make.getValue();
                if (model.getValue() != null && model.getStatus() == CarValue.STATUS_SUCCESS)
                    _model = model.getValue();
                if (year.getValue() != null && year.getStatus() == CarValue.STATUS_SUCCESS)
                    _year = year.getValue();
            });

            carSensors.addAccelerometerListener(CarSensors.UPDATE_RATE_NORMAL, executor, (data) -> {
                CarValue<List<Float>> acceleration = data.getForces();
                if (acceleration.getValue() != null && acceleration.getStatus() == CarValue.STATUS_SUCCESS) {
                    List<Float> accXYZ = acceleration.getValue();
                    _acceleration = accXYZ.get(0);
                } else {
                    screen.updateScreen("Acceleration read error");
                }
            });

            carInfo.addSpeedListener(executor, (data) -> {
                CarValue<Float> displaySpeed = data.getDisplaySpeedMetersPerSecond();
                if (displaySpeed.getValue() != null && displaySpeed.getStatus() == CarValue.STATUS_SUCCESS) {
                    _speed = displaySpeed.getValue();
                } else {
                    screen.updateScreen("Speed read error");
                    // Display error
                }
            });

            carInfo.addEnergyLevelListener(executor, (data) -> {
                CarValue<Float> fuelLevel = data.getFuelPercent();
                if (fuelLevel.getValue() != null && fuelLevel.getStatus() == CarValue.STATUS_SUCCESS) {
                    _fuel = fuelLevel.getValue();
                } else {
                    screen.updateScreen("Fuel read error");
                    // Display error
                }
            });

            // Periodically sample acceleration to assign strikes
            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    // Amount of samples to take
                    // 10 seconds at lower speeds, 20 seconds above 60 kmh
                    int samples = _speed <= 16.7 ? 100 : 200;
                    if (accQueue.size() >= 200)
                        accQueue.remove();
                    accQueue.add(_acceleration);
                    int i = 0;
                    float accAve = 0;
                    for (float acc : accQueue) {
                        if (i >= samples)
                            break;
                        accAve += acc;
                        i++;
                    }
                    accAve /= samples;
                    // If average acceleration is above threshold, penalise
                    if (Math.abs(accAve) > A_DELTA) {
                        strikes++;
                        // Clear the queue
                        for (int j = 0; i < 20; i++) {
                            accQueue.poll();
                        }
                    }
                }
            }, 0, 100);

        });
        return screen;
    }
}
