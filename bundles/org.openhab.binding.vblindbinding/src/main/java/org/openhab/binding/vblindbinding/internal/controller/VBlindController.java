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

import static org.openhab.core.util.HexUtils.bytesToHex;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.openhab.binding.vblindbinding.internal.VBlindBindingNotifyThingStatus;
import org.openhab.binding.vblindbinding.internal.VBlindBindingVBlindConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link VBlindController} is responsible for notifications
 *
 * @author Thomas Wang-Nielsen - Initial contribution
 */
public class VBlindController implements MessageWithResponseCallback {
    private final Logger logger = LoggerFactory.getLogger(VBlindController.class);
    private final VBlindBindingVBlindConfiguration config;
    private final VBlindBindingNotifyThingStatus notifyThingStatus;
    private final BridgeControllerVBlindCallback bridgeControllerVBlindCallback;
    private final ScheduledExecutorService scheduler;

    public VBlindController(VBlindBindingVBlindConfiguration config, VBlindBindingNotifyThingStatus notifyThingStatus,
            BridgeControllerVBlindCallback bridgeControllerVBlindCallback, ScheduledExecutorService scheduler) {
        this.config = config;
        this.notifyThingStatus = notifyThingStatus;
        this.bridgeControllerVBlindCallback = bridgeControllerVBlindCallback;
        this.scheduler = scheduler;
    }

    public void start() {
        logger.debug("start major:{} minor:{}", config.major, config.minor);
        scheduler.schedule(() -> {
            logger.debug("start.go major:{} minor:{}", config.major, config.minor);
            MessageWithResponse test = new MessageWithResponse(
                    MessageRawRequest.createQueryDim(config.major, config.minor), this);
            this.bridgeControllerVBlindCallback.vblindSendMessage(test);
        }, 20, TimeUnit.SECONDS);
    }

    public void stop() {
        logger.debug("stop major:{} minor:{}", config.major, config.minor);
    }

    @Override
    public void done() {
        logger.debug("done major:{} minor:{}", config.major, config.minor);
    }

    @Override
    public void done(MessageRawResponse response) {
        logger.debug("done.withMessage major:{} minor:{} data:{}", config.major, config.minor,
                bytesToHex(response.getData(), ""));
    }

    @Override
    public void timeout() {
        logger.debug("timeout major:{} minor:{}", config.major, config.minor);
    }

    @Override
    public void waiting() {
        logger.debug("waiting major:{} minor:{}", config.major, config.minor);
    }
}
