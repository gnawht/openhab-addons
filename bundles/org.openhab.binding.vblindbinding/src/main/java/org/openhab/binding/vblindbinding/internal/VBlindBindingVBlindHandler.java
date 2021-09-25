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
package org.openhab.binding.vblindbinding.internal;

import static org.openhab.binding.vblindbinding.internal.VBlindBindingBindingConstants.*;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.vblindbinding.internal.controller.VBlindController;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link VBlindBindingVBlindHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Thomas Wang-Nielsen - Initial contribution
 */
@NonNullByDefault
public class VBlindBindingVBlindHandler extends BaseThingHandler implements VBlindBindingNotifyThingStatus {

    private final Logger logger = LoggerFactory.getLogger(VBlindBindingVBlindHandler.class);

    private @Nullable VBlindBindingVBlindConfiguration config;
    private @Nullable VBlindController controller;

    public VBlindBindingVBlindHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (CHANNEL_VBLIND_POS.equals(channelUID.getId())) {
            String posStr = command.toString();
            logger.debug("handleCommand.pos major:{} minor:{} pos:{}", config.major, config.minor, posStr);
            if (command instanceof RefreshType) {
                logger.debug("handleCommand.pos.refresh major:{} minor:{}", config.major, config.minor);
                return;
            }
            controller.setPos(Integer.parseInt(posStr));
        } else if (CHANNEL_VBLIND_DIM.equals(channelUID.getId())) {
            String posStr = command.toString();
            logger.debug("handleCommand.dim major:{} minor:{} pos:{}", config.major, config.minor, posStr);
            if (command instanceof RefreshType) {
                logger.debug("handleCommand.dim.refresh major:{} minor:{}", config.major, config.minor);
                return;
            }
            controller.setDim(Integer.parseInt(posStr));
        } else if (CHANNEL_VBLIND_CONTROL.equals(channelUID.getId())) {
            String state = command.toString();
            logger.debug("handleCommand.control major:{} minor:{} command:{}", config.major, config.minor, state);
            if (command instanceof RefreshType) {
                logger.debug("handleCommand.control.refresh major:{} minor:{} command:{}", config.major, config.minor,
                        state);
                return;
            }
            controller.enterState(state);
        }
    }

    @Override
    public void initialize() {
        config = getConfigAs(VBlindBindingVBlindConfiguration.class);
        logger.debug("initialize major:{} minor:{}", config.major, config.minor);
        updateStatus(ThingStatus.UNKNOWN);
        controller = new VBlindController(config, this,
                ((VBlindBindingBridgeHandler) getBridge().getHandler()).getController(), scheduler);
    }

    @Override
    public void dispose() {
        logger.debug("dispose major:{} minor:{}", config.major, config.minor);
        controller.stop();
    }

    @Override
    public void notifyOnline() {
        updateStatus(ThingStatus.ONLINE);
    }

    @Override
    public void notifyOffline() {
        updateStatus(ThingStatus.OFFLINE);
    }

    @Override
    public void notifyOnChange() {
        scheduler.execute(() -> {
            updateState(CHANNEL_VBLIND_POS, new DecimalType(controller.getCurrentPos()));
            updateState(CHANNEL_VBLIND_DIM, new DecimalType(controller.getCurrentDim()));
            updateState(CHANNEL_VBLIND_POSSTATE, new StringType(controller.getPosState()));
        });
    }

    public void bridgeOnline() {
        notifyOnline();
        controller.start();
    }

    public void bridgeOffline() {
        notifyOffline();
        controller.stop();
    }
}
