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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link Command} is responsible for notifications
 *
 * @author Thomas Wang-Nielsen - Initial contribution
 */
public abstract class Command {
    private final Logger logger = LoggerFactory.getLogger(Command.class);
    protected CommandCallback callback;
    protected CommandCallbackVBlind commandCallbackVBlind;
    protected ScheduledExecutorService scheduler;

    private boolean cancled;

    Command(ScheduledExecutorService scheduler, CommandCallbackVBlind commandCallbackVBlind) {
        this.scheduler = scheduler;
        this.commandCallbackVBlind = commandCallbackVBlind;
        this.cancled = false;
    }

    public void setCallback(CommandCallback callback) {
        this.callback = callback;
    }

    public abstract void run();

    public void cancel() {
        cancled = true;
    }

    public abstract String getInfo();
}
