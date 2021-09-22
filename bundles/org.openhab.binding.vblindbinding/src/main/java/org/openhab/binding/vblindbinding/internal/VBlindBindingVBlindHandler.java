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
            if (command instanceof RefreshType) {
                // TODO: handle data refresh
            }

            // TODO: handle command

            // Note: if communication with thing fails for some reason,
            // indicate that by setting the status with detail information:
            // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
            // "Could not control device at IP address x.x.x.x");
        } else if (CHANNEL_VBLIND_DIM.equals(channelUID.getId())) {
            if (command instanceof RefreshType) {
                // TODO: handle data refresh
            }

            // TODO: handle command

            // Note: if communication with thing fails for some reason,
            // indicate that by setting the status with detail information:
            // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
            // "Could not control device at IP address x.x.x.x");
        } else if (CHANNEL_VBLIND_CONTROL.equals(channelUID.getId())) {
            if (command instanceof RefreshType) {
                // TODO: handle data refresh
            }

            // TODO: handle command

            // Note: if communication with thing fails for some reason,
            // indicate that by setting the status with detail information:
            // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
            // "Could not control device at IP address x.x.x.x");
        }
    }

    @Override
    public void initialize() {
        config = getConfigAs(VBlindBindingVBlindConfiguration.class);
        logger.debug("initialize major:{} minor:{}", config.major, config.minor);
        this.updateStatus(ThingStatus.UNKNOWN);
        this.controller = new VBlindController(config, this,
                ((VBlindBindingBridgeHandler) getBridge().getHandler()).getController(), scheduler);
        this.controller.start();
    }

    @Override
    public void dispose() {
        logger.debug("dispose");
        this.controller.stop();
    }

    @Override
    public void notifyOnline() {
        updateStatus(ThingStatus.ONLINE);
    }

    @Override
    public void notifyOffline() {
        updateStatus(ThingStatus.OFFLINE);
    }
}
