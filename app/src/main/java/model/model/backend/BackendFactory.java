package model.model.backend;

import model.model.backend.Backend;
import model.model.datasource.Firebase_DBManager;

public class BackendFactory {
    private static final Backend ourInstance = new Firebase_DBManager();

    public static Backend getInstance() {
        return ourInstance;
    }

    private BackendFactory() {
    }
}
