package org.antarcticgardens.cna;

import com.zurrtum.create.client.ponder.foundation.PonderIndex;
import org.antarcticgardens.cna.content.ponders.CNAPonders;

public abstract class CreateNewAgeClient {
    protected void initialize() {
        PonderIndex.addPlugin(new CNAPonders());
    }
}
