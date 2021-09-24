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
 * The {@link MessageSet} is responsible for notifications
 *
 * @author Thomas Wang-Nielsen - Initial contribution
 */
public class MessageSet extends MessageWithResponse {
    private final Logger logger = LoggerFactory.getLogger(MessageSet.class);
    private MessageCallback callback;

    MessageSet(MessageRawRequest messageRawRequest, MessageCallback callback) {
        super(messageRawRequest);
        this.callback = callback;
    }

    @Override
    public void callbackDone(MessageRawResponse response) {
        System.out.println("parseFromBuffer crc     :" + bytesToHex(response.getRaw(), " "));
        this.callback.callbackDone();
    }

    @Override
    public void callbackTimeout() {
        this.callback.callbackTimeout();
    }

    @Override
    public void callbackWaiting() {
    }
}
