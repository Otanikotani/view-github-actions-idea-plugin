package org.github.otanikotani.ui;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.github.otanikotani.ui.toolwindow.ChecksToolWindowContent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ChecksToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ChecksToolWindowContent checksToolWindowContent = new ChecksToolWindowContent();
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(checksToolWindowContent, "Checks", false);
        toolWindow.getContentManager().addContent(content);

        refreshChecksResultTable(project);
    }

    private void refreshChecksResultTable(@NotNull Project project) {
        AnAction refreshAction = ActionManager.getInstance().getAction("ChecksPluginOpenMainPanel");
        DataContext dataContext = dataId -> Objects.equals(dataId, "project") ? project : null;
        AnActionEvent event = new AnActionEvent(null, dataContext, ActionPlaces.UNKNOWN,
                new Presentation(), ActionManager.getInstance(), 0);
        refreshAction.actionPerformed(event);
    }
}
