package org.github.otanikotani.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.github.otanikotani.ui.toolwindow.ChecksToolWindowContent;
import org.jetbrains.annotations.NotNull;

public class ChecksToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ChecksToolWindowContent checksToolWindowContent = new ChecksToolWindowContent();
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(checksToolWindowContent, "Checks", false);
        toolWindow.getContentManager().addContent(content);
    }
}
