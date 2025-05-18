package com.petclinic.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Objects;

public class FXMLLoaderUtil {
    
    public static Pane loadFXML(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(FXMLLoaderUtil.class.getResource(fxmlPath));

        return loader.load();

    }
    
    public static <T> T loadFXMLWithController(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(FXMLLoaderUtil.class.getResource(fxmlPath)));
        loader.load();
        return loader.getController();
    }
}