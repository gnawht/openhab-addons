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
package org.openhab.binding.vblindbinding.internal.controller.message;

import static org.openhab.core.util.HexUtils.bytesToHex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link MessageQueryInt} is responsible for notifications
 *
 * @author Thomas Wang-Nielsen - Initial contribution
 */
public class MessageQueryInt extends MessageWithResponse {
    private final Logger logger = LoggerFactory.getLogger(MessageQueryInt.class);
    private MessageCallback callback;

    MessageQueryInt(MessageRawRequest messageRawRequest, MessageCallback callback) {
        super(messageRawRequest);
        this.callback = callback;
    }

    @Override
    public void callbackDone(MessageRawResponse response) {
        byte[] data = response.getData();
        if (data.length > 1) {
            this.callback.callbackDoneInt(Byte.toUnsignedInt(data[1]));
        } else {
            logger.error("Invalid response {}", bytesToHex(response.getData(), ""));
            this.callback.callbackError("Invalid data in response");
        }
    }

    @Override
    public void callbackTimeout() {
        this.callback.callbackTimeout();
    }

    @Override
    public void callbackWaiting() {
    }
}
