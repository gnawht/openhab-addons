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
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.openhab.binding.vblindbinding.internal.controller.message.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link CommandSetAndWaitForDim} is responsible for notifications
 *
 * @author Thomas Wang-Nielsen - Initial contribution
 */
public class CommandSetAndWaitForDim extends Command {
    private final Logger logger = LoggerFactory.getLogger(CommandSetAndWaitForDim.class);

    private final int major;
    private final int minor;
    private final int dim;

    ScheduledFuture schedule;

    public CommandSetAndWaitForDim(int major, int minor, int dim, ScheduledExecutorService scheduler,
            CommandCallbackVBlind commandCallbackVBlind) {
        super(scheduler, commandCallbackVBlind);
        this.major = major;
        this.minor = minor;
        this.dim = dim;
    }

    @Override
    public void run() {
        scheduler.execute(() -> {
            logger.debug("run");
            enterStateCheckDim(dim);
        });
    }

    @Override
    public void cancel() {
        super.cancel();
        logger.debug("cancel");
        if (schedule != null) {
            schedule.cancel(false);
        }
        callback.callbackCanceled();
    }

    private void enterStateCheckDim(int checkValue) {
        logger.debug("enterStateCheckDim checkValue:{}", checkValue);
        Message queryDim = new MessageQueryDim(major, minor, new MessageCallback() {
            @Override
            public void callbackDoneInt(int value) {
                logger.debug("enterStateCheckDim.response value:{}", value);
                commandCallbackVBlind.commandVBlindUpdateDim(value);
                if (value == checkValue) {
                    enterStateDone();
                } else {
                    enterStateSetDim(checkValue);
                }
            }
        });
        commandCallbackVBlind.commandVBlindSendMessage(queryDim);
    }

    private void enterStateSetDim(int setValue) {
        logger.debug("enterStateSetDim setValue:{}", setValue);
        Message setDim = new MessageSetDim(major, minor, setValue, new MessageCallback() {
            @Override
            public void callbackDone() {
                logger.debug("enterStateSetDim.response");
                enterStateGetDim(setValue);
            }
        });
        commandCallbackVBlind.commandVBlindSendMessage(setDim);
    }

    private void enterStateGetDim(int waitForValue) {
        logger.debug("enterStateGetDim waitForValue:{}", waitForValue);
        Message queryDim = new MessageQueryDim(major, minor, new MessageCallback() {
            @Override
            public void callbackDoneInt(int value) {
                logger.debug("enterStateGetDim.response value:{}", value);
                commandCallbackVBlind.commandVBlindUpdateDim(value);
                if (value == waitForValue) {
                    enterStateDone();
                } else {
                    schedule = scheduler.schedule(() -> {
                        enterStateGetDim(waitForValue);
                    }, 4, TimeUnit.SECONDS);
                }
            }
        });
        commandCallbackVBlind.commandVBlindSendMessage(queryDim);
    }

    private void enterStateDone() {
        logger.debug("enterStateDone");
        callback.callbackDone();
    }
}
