package model.model.datasource;

import model.model.backend.Backend;

public class BackendFactory {
    private static final Backend ourInstance = new Firebase_DBManager();

    public static Backend getInstance() {
        return ourInstance;
    }

    private BackendFactory() {
    }
}
