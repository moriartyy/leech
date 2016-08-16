package org.leech.parse;

import com.google.inject.Inject;
import org.leech.common.component.AbstractLifecyleComponent;
import org.leech.settings.Settings;

/**
 * @author Loster on 2016/8/16.
 */
public class ParseService extends AbstractLifecyleComponent {

    @Inject
    public ParseService(Settings settings) {
        super(settings);
    }

    @Override
    protected void doStart() {

    }

    @Override
    protected void doStop() {

    }

    public void submit(ParseTask parseTask) {

    }
}
