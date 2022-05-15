package com.shinleeholdings.coverstar.util;

import com.google.firebase.firestore.FirebaseFirestore;

public class FireBaseHelper {
    private static final String TAG = "FireBaseHelper";
    private static volatile FireBaseHelper instance;
    private final static Object lockObject = new Object();
    FirebaseFirestore database;

    public static FireBaseHelper getSingleInstance() {
        if (instance == null) {
            synchronized (lockObject) {
                if (instance == null) {
                    instance = new FireBaseHelper();
                }
            }
        }

        return instance;
    }

    private FireBaseHelper() {
        database = FirebaseFirestore.getInstance();
    }

    public FirebaseFirestore getDatabase() {
        return database;
    }
}
