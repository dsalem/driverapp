package model.model.backend;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.RequiresApi;

import model.model.datasource.Firebase_DBManager;
import model.model.entities.Driver;

public interface Backend {
    void addDriver(final Driver Driver, final Firebase_DBManager.Action<Long> action);
}
