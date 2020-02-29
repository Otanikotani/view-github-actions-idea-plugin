package org.github.otanikotani.ui.toolwindow;

@FunctionalInterface
public interface WorkflowsListener {

    void onLocationChange(WorkflowsLocation coordinates);
}
