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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link CommandList} is responsible for notifications
 *
 * @author Thomas Wang-Nielsen - Initial contribution
 */
public class CommandList extends Command implements CommandCallback {
    private final Logger logger = LoggerFactory.getLogger(CommandList.class);

    private List<Command> commands;
    private List<Command> commandsDone;
    private Command currentCommand;

    public CommandList(List<Command> commands, ScheduledExecutorService scheduler,
            CommandCallbackVBlind commandCallbackVBlind, CommandCallback callback) {
        super(scheduler, commandCallbackVBlind);
        setCallback(callback);
        this.commands = commands;
        this.commandsDone = new ArrayList<Command>();
    }

    @Override
    public void run() {
        scheduler.execute(() -> {
            logger.debug("run size:{}", commands.size());
            runNextCommand();
        });
    }

    @Override
    public void cancel() {
        super.cancel();
        logger.debug("cancel");
        if (currentCommand != null) {
            currentCommand.cancel();
        } else {
            callback.callbackCanceled();
        }
    }

    @Override
    public String getInfo() {
        final String[] result = { "CommandList commands:[" };
        this.commands.forEach(command -> {
            result[0] += command.getInfo();
        });
        result[0] += "]";
        result[0] += " commandsDone:[";
        this.commandsDone.forEach(command -> {
            result[0] += command.getInfo();
            result[0] += ", ";
        });
        result[0] += "]";
        return result[0];
    }

    private void runNextCommand() {
        synchronized (this) {
            logger.debug("runNextCommand size:{}", commands.size());
            if (commands.isEmpty()) {
                enterStateDone();

            } else {
                currentCommand = commands.remove(0);
                currentCommand.setCallback(this);
                currentCommand.run();
            }
        }
    }

    private void enterStateDone() {
        logger.debug("enterStateDone");
        callback.callbackDone();
    }

    @Override
    public void callbackTimeout() {
        callback.callbackTimeout();
    }

    @Override
    public void callbackError(String message) {
        callback.callbackError(message);
    }

    @Override
    public void callbackDone() {
        logger.debug("callbackDone");
        commandsDone.add(currentCommand);
        runNextCommand();
    }

    @Override
    public void callbackCanceled() {
        logger.debug("callbackCanceled");
        callback.callbackCanceled();
    }
}
