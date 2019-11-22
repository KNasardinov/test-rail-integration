package com.knasardinov.idea.utils;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;

@State(name = "Test",
        storages = {
        @Storage(file = "$APP_CONFIG$/testpersist.xml")
})
public class Settings implements PersistentStateComponent<Settings> {

    String testRailAnnotation = "";

    public Settings() {
    }

    public String getTestRailAnnotation() {
        return testRailAnnotation;
    }

    public void setTestRailAnnotation(String testRailAnnotation) {
        this.testRailAnnotation = testRailAnnotation;
    }

    public void loadState(Settings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public Settings getState() {
        return this;
    }
}
