package org.github.otanikotani.ui.toolwindow;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import org.github.otanikotani.action.RefreshAction;

import javax.swing.JPanel;
import java.awt.BorderLayout;

import static java.util.Objects.isNull;

public class ChecksToolbar extends JPanel {
    private static final String REFRESH_ACTION_ID = "GHChecks.Action.Refresh";
    private static final String GHCHECKS_ACTION_GROUP_ID = "GHChecks.ActionGroup";

    public ChecksToolbar(ActionManager actionManager, Runnable refreshChecksTable) {
        super(new BorderLayout());
        if (isNull(actionManager)) {
            return;
        }
        actionManager.registerAction(
            REFRESH_ACTION_ID,
            new RefreshAction(refreshChecksTable)
        );
        DefaultActionGroup checksActionGroup = (DefaultActionGroup) actionManager.getAction(GHCHECKS_ACTION_GROUP_ID);
        checksActionGroup.add(actionManager.getAction(REFRESH_ACTION_ID));

        ActionToolbar actionToolbar = actionManager.createActionToolbar(ActionPlaces.TOOLBAR, checksActionGroup, false);
        add(actionToolbar.getComponent(), BorderLayout.PAGE_START);
    }
}
