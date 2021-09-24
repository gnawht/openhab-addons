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

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.vblindbinding.internal.controller.BridgeController;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.binding.BaseBridgeHandler;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link VBlindBindingBridgeHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Thomas Wang-Nielsen - Initial contribution
 */
@NonNullByDefault
public class VBlindBindingBridgeHandler extends BaseBridgeHandler implements VBlindBindingNotifyThingStatus {

    private final Logger logger = LoggerFactory.getLogger(VBlindBindingBridgeHandler.class);

    private @Nullable VBlindBindingBridgeConfiguration config;
    private @Nullable BridgeController brideController;

    public VBlindBindingBridgeHandler(Bridge bridge) {
        super(bridge);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (CHANNEL_BRIDGE_STATUS.equals(channelUID.getId())) {
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
        config = getConfigAs(VBlindBindingBridgeConfiguration.class);
        logger.debug("initialize host:{} port:{}", config.host, config.port);
        this.updateStatus(ThingStatus.UNKNOWN);
        this.brideController = new BridgeController(config, this);
        scheduler.execute(() -> {
            this.brideController.start();
        });
    }

    @Override
    public void dispose() {
        logger.debug("dispose");
        this.brideController.stopServer();
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
    }

    @Override
    public void childHandlerInitialized(ThingHandler childHandler, Thing childThing) {
        logger.debug("childHandlerInitialized {}", childThing.getLabel());
    }

    @Override
    public void childHandlerDisposed(ThingHandler childHandler, Thing childThing) {
        logger.debug("childHandlerDisposed {}", childThing.getLabel());
    }

    BridgeController getController() {
        return Objects.requireNonNull(this.brideController);
    }
}
