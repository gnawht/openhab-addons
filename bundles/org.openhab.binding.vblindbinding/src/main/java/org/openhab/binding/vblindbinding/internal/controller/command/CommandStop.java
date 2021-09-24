/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.vblindbinding.internal.controller.command;

import java.util.concurrent.ScheduledExecutorService;

import org.openhab.binding.vblindbinding.internal.controller.message.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link CommandStop} is responsible for notifications
 *
 * @author Thomas Wang-Nielsen - Initial contribution
 */
public class CommandStop extends Command {
    private final Logger logger = LoggerFactory.getLogger(CommandStop.class);

    private final int major;
    private final int minor;

    public CommandStop(int major, int minor, int pos, ScheduledExecutorService scheduler,
            CommandCallbackVBlind commandCallbackVBlind) {
        super(scheduler, commandCallbackVBlind);
        this.major = major;
        this.minor = minor;
    }

    @Override
    public void run() {
        scheduler.execute(() -> {
            logger.debug("run");
            enterStateStop();
        });
    }

    @Override
    public void cancel() {
        super.cancel();
        logger.debug("cancel");
        callback.callbackCanceled();
    }

    private void enterStateStop() {
        logger.debug("enterStateStop");
        Message stop = new MessageStop(major, minor, new MessageCallback() {
            @Override
            public void callbackDone() {
                logger.debug("enterStateCheckPos.response");
                enterStateDone();
            }
        });
        commandCallbackVBlind.commandVBlindSendMessage(stop);
    }

    private void enterStateDone() {
        logger.debug("enterStateDone");
        callback.callbackDone();
    }
}
