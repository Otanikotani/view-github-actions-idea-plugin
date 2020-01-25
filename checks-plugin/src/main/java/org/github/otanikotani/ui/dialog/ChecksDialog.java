package org.github.otanikotani.ui.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ChecksDialog extends DialogWrapper {

    protected ChecksDialog(@Nullable Project project) {
        super(true);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return null;
    }
}
