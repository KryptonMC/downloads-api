package org.kryptonmc.downloads.shell;

import org.jline.reader.impl.history.DefaultHistory;
import org.springframework.stereotype.Component;

@Component
public class NoSaveHistory extends DefaultHistory {

    @Override
    public void save() {
        // Don't save history anywhere - we don't care about it
    }
}
