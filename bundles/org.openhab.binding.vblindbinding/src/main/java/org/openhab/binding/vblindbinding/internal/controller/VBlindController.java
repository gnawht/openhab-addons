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
package org.openhab.binding.vblindbinding.internal.controller;

import static org.openhab.binding.vblindbinding.internal.VBlindBindingBindingConstants.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.openhab.binding.vblindbinding.internal.VBlindBindingNotifyThingStatus;
import org.openhab.binding.vblindbinding.internal.VBlindBindingVBlindConfiguration;
import org.openhab.binding.vblindbinding.internal.controller.command.*;
import org.openhab.binding.vblindbinding.internal.controller.message.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link VBlindController} is responsible for notifications
 *
 * @author Thomas Wang-Nielsen - Initial contribution
 */
public class VBlindController implements CommandCallbackVBlind {
    private final Logger logger = LoggerFactory.getLogger(VBlindController.class);
    private final VBlindBindingVBlindConfiguration config;
    private final VBlindBindingNotifyThingStatus notifyThingStatus;
    private final BridgeControllerVBlindCallback bridgeControllerVBlindCallback;
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture schedule;
    private int currentPos;
    private int currentDim;
    private Command currentCommand;

    public VBlindController(VBlindBindingVBlindConfiguration config, VBlindBindingNotifyThingStatus notifyThingStatus,
            BridgeControllerVBlindCallback bridgeControllerVBlindCallback, ScheduledExecutorService scheduler) {
        this.config = config;
        this.notifyThingStatus = notifyThingStatus;
        this.bridgeControllerVBlindCallback = bridgeControllerVBlindCallback;
        this.scheduler = scheduler;
    }

    public void start() {
        logger.debug("start major:{} minor:{}", config.major, config.minor);
        stop();
        updateStateStart();
    }

    public void stop() {
        if (schedule != null) {
            schedule.cancel(true);
        }
        if (currentCommand != null) {
            currentCommand.cancel();
        }
        logger.debug("stop major:{} minor:{}", config.major, config.minor);
    }

    private void updateStateStart() {
        schedule = scheduler.scheduleAtFixedRate(() -> {
            updateState();
        }, 5, 60, TimeUnit.SECONDS);
    }

    private void updateState() {
        logger.debug("updateState major:{} minor:{}", config.major, config.minor);
        Message queryDim = new MessageQueryDim(config.major, config.minor, new MessageCallback() {
            @Override
            public void callbackDoneInt(int value) {
                logger.debug("MessageQueryDim.response {}", value);
                updateDim(value);
            }

            @Override
            public void callbackTimeout() {
                logger.debug("MessageQueryDim.timeout");
            }
        });

        Message queryPct = new MessageQueryPos(config.major, config.minor, new MessageCallback() {
            @Override
            public void callbackDoneInt(int value) {
                logger.debug("MessageQueryPct.response {}", value);
                updatePos(value);
            }

            @Override
            public void callbackTimeout() {
                logger.debug("MessageQueryPct.timeout");
            }
        });
        this.bridgeControllerVBlindCallback.vblindSendMessage(queryDim);
        this.bridgeControllerVBlindCallback.vblindSendMessage(queryPct);
    }

    private void updatePos(int pos) {
        logger.debug("updatePos major:{} minor:{} pos:{}", config.major, config.minor, pos);
        currentPos = pos;
        notifyThingStatus.notifyOnChange();
    }

    private void updateDim(int dim) {
        logger.debug("updateDim major:{} minor:{} dim:{}", config.major, config.minor, dim);
        currentDim = dim;
        notifyThingStatus.notifyOnChange();
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public int getCurrentDim() {
        return currentDim;
    }

    public String getPosState() {
        if (currentCommand != null) {
            return CHANNEL_VBLIND_POSSTATE_VALUE_CHANGE;
        } else if (currentPos == 100 && currentDim == 90) {
            return CHANNEL_VBLIND_POSSTATE_VALUE_OPEN;
        } else if (currentPos == 0 && currentDim == 90) {
            return CHANNEL_VBLIND_POSSTATE_VALUE_CLOSE;
        } else if (currentPos == 0 && currentDim == 0) {
            return CHANNEL_VBLIND_POSSTATE_VALUE_CLOSE_BLACK;
        } else if (currentPos == 0 && currentDim == 180) {
            return CHANNEL_VBLIND_POSSTATE_VALUE_CLOSE_BLACK;
        } else if (checkPosStateForUserPos(currentPos, currentDim, config.pos1)) {
            return CHANNEL_VBLIND_POSSTATE_VALUE_POS1;
        } else if (checkPosStateForUserPos(currentPos, currentDim, config.pos2)) {
            return CHANNEL_VBLIND_POSSTATE_VALUE_POS2;
        } else if (checkPosStateForUserPos(currentPos, currentDim, config.pos3)) {
            return CHANNEL_VBLIND_POSSTATE_VALUE_POS3;
        } else {
            return CHANNEL_VBLIND_POSSTATE_VALUE_UNKNOWN;
        }
    }

    private boolean checkPosStateForUserPos(int currentPos, int currentDim, String configPosStr) {
        String[] configPosStrSplit = configPosStr.split(":");
        if (configPosStrSplit.length == 2) {
            int pos = Integer.parseInt(configPosStrSplit[0].trim());
            int dim = Integer.parseInt(configPosStrSplit[1].trim());
            return pos == currentPos && dim == currentDim;
        }
        return false;
    }

    public void setPos(int pos) {
        Message setPos = new MessageSetPos(config.major, config.minor, pos, new MessageCallback() {
            @Override
            public void callbackDone() {
                logger.debug("MessageSetPos.response");
            }

            @Override
            public void callbackTimeout() {
                logger.debug("MessageSetPos.callbackTimeout");
            }
        });
        this.bridgeControllerVBlindCallback.vblindSendMessage(setPos);
    }

    public void setDim(int dim) {
        Message setDim = new MessageSetDim(config.major, config.minor, dim, new MessageCallback() {
            @Override
            public void callbackDone() {
                logger.debug("MessageSetDim.response");
            }

            @Override
            public void callbackTimeout() {
                logger.debug("MessageSetDim.callbackTimeout");
            }
        });
        this.bridgeControllerVBlindCallback.vblindSendMessage(setDim);
    }

    public void enterState(String state) {
        logger.debug("enterState major:{} minor:{} state:{}", config.major, config.minor, state);
        switch (state.toLowerCase()) {
            case CHANNEL_VBLIND_CONTROL_VALUE_GOTO_OPEN: {
                enterPos(100);
                break;
            }
            case CHANNEL_VBLIND_CONTROL_VALUE_GOTO_CLOSE: {
                enterPos(0);
                break;
            }
            case CHANNEL_VBLIND_CONTROL_VALUE_GOTO_CLOSE_BLACK: {
                enterPosAndDim(0, 0);
                break;
            }
            case CHANNEL_VBLIND_CONTROL_VALUE_GOTO_POS1: {
                enterUserPos(config.pos1);
                break;
            }
            case CHANNEL_VBLIND_CONTROL_VALUE_GOTO_POS2: {
                enterUserPos(config.pos2);
                break;
            }
            case CHANNEL_VBLIND_CONTROL_VALUE_GOTO_POS3: {
                enterUserPos(config.pos3);
                break;
            }
            case CHANNEL_VBLIND_CONTROL_VALUE_STOP: {
                enterStop();
                break;
            }
            case CHANNEL_VBLIND_CONTROL_VALUE_PREV: {
                enterPrev();
                break;
            }
            case CHANNEL_VBLIND_CONTROL_VALUE_NEXT: {
                enterNext();
                break;
            }
        }
    }

    private void enterPos(int pos) {
        logger.debug("enterPos major:{} minor:{} pos:{}", config.major, config.minor, pos);
        Command setDim = new CommandSetAndWaitForDim(config.major, config.minor, 90, scheduler, this);
        Command setPos = new CommandSetAndWaitForPos(config.major, config.minor, pos, scheduler, this);
        List<Command> commands = new ArrayList<Command>();
        commands.add(setDim);
        commands.add(setPos);
        Command command = new CommandList(commands, scheduler, this, new CommandCallback() {
            @Override
            public void callbackTimeout() {
                logger.debug("enterPos.callbackTimeout major:{} minor:{} pos:{}", config.major, config.minor, pos);
                execCommandDone();
            }

            @Override
            public void callbackError(String message) {
                logger.error("enterPos.callbackError major:{} minor:{} pos:{} message:{}", config.major, config.minor,
                        pos, message);
                execCommandDone();
            }

            @Override
            public void callbackDone() {
                logger.debug("enterPos.callbackDone major:{} minor:{} pos:{}", config.major, config.minor, pos);
                execCommandDone();
            }

            @Override
            public void callbackCanceled() {
                logger.debug("enterPos.callbackCanceled major:{} minor:{} pos:{}", config.major, config.minor, pos);
                execCommandDone();
            }
        });
        execCommand(command);
    }

    private void enterPosAndDim(int pos, int dim) {
        logger.debug("enterPosAndDim major:{} minor:{} pos:{}", config.major, config.minor, pos);

        Command setDim = new CommandSetAndWaitForDim(config.major, config.minor, 90, scheduler, this);
        Command setPos = new CommandSetAndWaitForPos(config.major, config.minor, pos, scheduler, this);
        Command setDimEnd = new CommandSetAndWaitForDim(config.major, config.minor, dim, scheduler, this);
        List<Command> commands = new ArrayList<Command>();
        commands.add(setDim);
        commands.add(setPos);
        commands.add(setDimEnd);
        Command command = new CommandList(commands, scheduler, this, new CommandCallback() {
            @Override
            public void callbackTimeout() {
                logger.debug("enterPosAndDim.callbackTimeout major:{} minor:{} pos:{}", config.major, config.minor,
                        pos);
                execCommandDone();
            }

            @Override
            public void callbackError(String message) {
                logger.error("enterPosAndDim.callbackError major:{} minor:{} pos:{} message:{}", config.major,
                        config.minor, pos, message);
                execCommandDone();
            }

            @Override
            public void callbackDone() {
                logger.debug("enterPosAndDim.callbackDone major:{} minor:{} pos:{}", config.major, config.minor, pos);
                execCommandDone();
            }

            @Override
            public void callbackCanceled() {
                logger.debug("enterPosAndDim.callbackCanceled major:{} minor:{} pos:{}", config.major, config.minor,
                        pos);
                execCommandDone();
            }
        });
        execCommand(command);
    }

    private void enterUserPos(String configPosStr) {
        logger.debug("enterUserPos major:{} minor:{} configPosStr:{}", config.major, config.minor, configPosStr);
        if (configPosStr != null) {
            String[] configPosStrSplit = configPosStr.split(":");
            if (configPosStrSplit.length == 2) {
                int pos = Integer.parseInt(configPosStrSplit[0].trim());
                int dim = Integer.parseInt(configPosStrSplit[1].trim());
                enterPosAndDim(pos, dim);
            } else {
                logger.warn("enterUserPos.error invalid config major:{} minor:{} configPosStr:{}", config.major,
                        config.minor, configPosStr);
            }
        } else {
            logger.warn("enterUserPos.error missing config string major:{} minor:{}", config.major, config.minor);
        }
    }

    private void enterStop() {
        logger.debug("enterStop major:{} minor:{}", config.major, config.minor);

        Command command = new CommandStop(config.major, config.minor, 90, scheduler, this);
        command.setCallback(new CommandCallback() {
            @Override
            public void callbackTimeout() {
                logger.debug("enterStop.callbackTimeout major:{} minor:{}", config.major, config.minor);
                execCommandDone();
            }

            @Override
            public void callbackError(String message) {
                logger.error("enterStop.callbackError major:{} minor:{} message:{}", config.major, config.minor,
                        message);
                execCommandDone();
            }

            @Override
            public void callbackDone() {
                logger.debug("enterStop.callbackDone major:{} minor:{}", config.major, config.minor);
                execCommandDone();
            }

            @Override
            public void callbackCanceled() {
                logger.debug("enterStop.callbackCanceled major:{} minor:{}", config.major, config.minor);
                execCommandDone();
            }
        });
        execCommand(command);
    }

    private void enterNext() {
        logger.debug("enterNext major:{} minor:{}", config.major, config.minor);
        switch (getPosState()) {
            case CHANNEL_VBLIND_POSSTATE_VALUE_OPEN:
                enterState(CHANNEL_VBLIND_CONTROL_VALUE_GOTO_CLOSE);
                break;
            case CHANNEL_VBLIND_POSSTATE_VALUE_CLOSE:
                enterState(CHANNEL_VBLIND_CONTROL_VALUE_GOTO_CLOSE_BLACK);
                break;
            default:
                logger.debug("enterNext.ignored major:{} minor:{}", config.major, config.minor);
        }
    }

    private void enterPrev() {
        logger.debug("enterPrev major:{} minor:{}", config.major, config.minor);
        switch (getPosState()) {
            case CHANNEL_VBLIND_POSSTATE_VALUE_CLOSE:
                enterState(CHANNEL_VBLIND_CONTROL_VALUE_GOTO_OPEN);
                break;
            case CHANNEL_VBLIND_POSSTATE_VALUE_CLOSE_BLACK:
                enterState(CHANNEL_VBLIND_CONTROL_VALUE_GOTO_CLOSE);
                break;
            default:
                logger.debug("enterPrev.ignored major:{} minor:{}", config.major, config.minor);
        }
    }

    private void execCommand(Command command) {
        synchronized (this) {
            if (currentCommand != null) {
                currentCommand.cancel();
            }
            currentCommand = command;
            currentCommand.run();
        }
    }

    private void execCommandDone() {
        currentCommand = null;
        notifyThingStatus.notifyOnChange();
    }

    @Override
    public void commandVBlindSendMessage(Message message) {
        scheduler.execute(() -> {
            this.bridgeControllerVBlindCallback.vblindSendMessage(message);
        });
    }

    @Override
    public void commandVBlindUpdatePos(int pos) {
        scheduler.execute(() -> {
            updatePos(pos);
        });
    }

    @Override
    public void commandVBlindUpdateDim(int dim) {
        scheduler.execute(() -> {
            updateDim(dim);
        });
    }
}
