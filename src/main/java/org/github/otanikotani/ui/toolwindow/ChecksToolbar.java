package org.github.otanikotani.ui.toolwindow;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import javax.swing.JPanel;
import org.jetbrains.annotations.NotNull;

import java.awt.BorderLayout;
import java.util.Objects;

public class ChecksToolbar extends JPanel {

    public ChecksToolbar(@NotNull ActionManager actionManager, ActionGroup checksActionGroup) {
        super(new BorderLayout());
        ActionToolbar actionToolbar = Objects.requireNonNull(actionManager)
            .createActionToolbar(ActionPlaces.TOOLBAR, checksActionGroup, false);
        add(actionToolbar.getComponent(), BorderLayout.PAGE_START);
    }
}
