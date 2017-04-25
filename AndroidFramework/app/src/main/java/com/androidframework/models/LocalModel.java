package com.androidframework.models;

/**
 * This is to keep the static members
 * Singleton class
 */
public class LocalModel {
    public static LocalModel sINSTANCE;

    //TODO: put your members here.. they will become static automatically

    private LocalModel() {
    }

    public static LocalModel getINSTANCE() {
        if (sINSTANCE == null) {
            sINSTANCE = new LocalModel();
        }
        return sINSTANCE;
    }


}
