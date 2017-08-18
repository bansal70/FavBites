package com.favbites.controller;

/*
 * Created by Rishav on 8/18/2017.
 */

public class ModelManager {

    private static ModelManager modelManager;

    public static ModelManager getInstance() {
        if (modelManager == null)
            return modelManager = new ModelManager();
        else
            return modelManager;
    }

    private ModelManager() {

    }
}
