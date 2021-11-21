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
package org.openhab.binding.lh4.wiegand.internal;

import static org.openhab.binding.lh4.wiegand.internal.LH4WiegandBindingConstants.CHANNEL_ACCESS;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.lh4.wiegand.internal.controller.Controller;
import org.openhab.binding.lh4.wiegand.internal.controller.ControllerCallback;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link LH4WiegandHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Thomas Wang-Nielsen - Initial contribution
 */
@NonNullByDefault
public class LH4WiegandHandler extends BaseThingHandler implements ControllerCallback {

    private final Logger logger = LoggerFactory.getLogger(LH4WiegandHandler.class);

    private @Nullable LH4WiegandConfiguration config;
    private @Nullable Controller controller;
    private @Nullable ScheduledFuture<?> notifyAccessSchedule;

    public LH4WiegandHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    @Override
    public void initialize() {
        logger.debug("initialize");
        config = getConfigAs(LH4WiegandConfiguration.class);
        updateStatus(ThingStatus.UNKNOWN);
        controller = new Controller(config, this, scheduler);
        scheduler.execute(() -> this.controller.start());
    }

    @Override
    public void dispose() {
        logger.debug("dispose");
        if (controller != null) {
            controller.stopClient();
        }
    }

    @Override
    public void notifyOnline() {
        logger.debug("notifyOnline");
        updateStatus(ThingStatus.ONLINE);
    }

    @Override
    public void notifyOffline() {
        logger.debug("notifyOffline");
        updateStatus(ThingStatus.OFFLINE);
    }

    @Override
    public void notifyAccess(String access) {
        if (notifyAccessSchedule != null) {
            notifyAccessSchedule.cancel(true);
        }
        updateState(CHANNEL_ACCESS, new StringType(access));
        logger.debug("notifyAccess access:{}", access);
        if (config.accessTimeoutSec > 0) {
            notifyAccessSchedule = scheduler.schedule(() -> {
                updateState(CHANNEL_ACCESS, new StringType(""));
                logger.debug("notifyAccess access.cleared");
            }, config.accessTimeoutSec, TimeUnit.SECONDS);
        } else {
            logger.debug("notifyAccess access.clear.disabled");
        }
    }
}
