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
 * The {@link CommandSetAndWaitForPos} is responsible for notifications
 *
 * @author Thomas Wang-Nielsen - Initial contribution
 */
public class CommandSetAndWaitForPos extends Command {
    private final Logger logger = LoggerFactory.getLogger(CommandSetAndWaitForPos.class);

    private final int major;
    private final int minor;
    private final int pos;

    private ScheduledFuture schedule;

    public CommandSetAndWaitForPos(int major, int minor, int pos, ScheduledExecutorService scheduler,
            CommandCallbackVBlind commandCallbackVBlind) {
        super(scheduler, commandCallbackVBlind);
        this.major = major;
        this.minor = minor;
        this.pos = pos;
    }

    @Override
    public void run() {
        scheduler.execute(() -> {
            logger.debug("run");
            enterStateCheckPos(pos);
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

    @Override
    public String getInfo() {
        return "CommandSetAndWaitForPos";
    }

    private void enterStateCheckPos(int checkValue) {
        logger.debug("enterStateCheckPos checkValue:{}", checkValue);
        Message queryPos = new MessageQueryPos(major, minor, new MessageCallback() {
            @Override
            public void callbackDoneInt(int value) {
                logger.debug("enterStateCheckPos.response value:{}", value);
                commandCallbackVBlind.commandVBlindUpdatePos(value);
                if (value == checkValue) {
                    enterStateDone();
                } else {
                    enterStateSetPos(checkValue);
                }
            }
        });
        commandCallbackVBlind.commandVBlindSendMessage(queryPos);
    }

    private void enterStateSetPos(int setValue) {
        logger.debug("enterStateSetPos setValue:{}", setValue);
        Message setPos = new MessageSetPos(major, minor, setValue, new MessageCallback() {
            @Override
            public void callbackDone() {
                logger.debug("enterStateSetPos.response");
                enterStateGetPos(setValue);
            }
        });
        commandCallbackVBlind.commandVBlindSendMessage(setPos);
    }

    private void enterStateGetPos(int waitForValue) {
        logger.debug("enterStateGetPos waitForValue:{}", waitForValue);
        Message queryPos = new MessageQueryPos(major, minor, new MessageCallback() {
            @Override
            public void callbackDoneInt(int value) {
                logger.debug("enterStateGetPos.response value:{}", value);
                commandCallbackVBlind.commandVBlindUpdatePos(value);
                if (value == waitForValue) {
                    enterStateDone();
                } else {
                    schedule = scheduler.schedule(() -> {
                        enterStateGetPos(waitForValue);
                    }, 4, TimeUnit.SECONDS);
                }
            }
        });
        commandCallbackVBlind.commandVBlindSendMessage(queryPos);
    }

    private void enterStateDone() {
        logger.debug("enterStateDone");
        callback.callbackDone();
    }
}
