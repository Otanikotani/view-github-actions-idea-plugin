package org.github.otanikotani.ui.toolwindow;

import com.intellij.util.messages.Topic;

import java.util.EventListener;

public interface ChecksRefreshedListener extends EventListener {

    Topic<ChecksRefreshedListener> CHECKS_REFRESHED = Topic
        .create("GitHub checks refreshed", ChecksRefreshedListener.class);

    void checksRefreshed();
}
