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

import java.nio.ByteBuffer;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link MessageWithResponse} is responsible for notifications
 *
 * @author Thomas Wang-Nielsen - Initial contribution
 */
public class MessageWithResponse extends Message {
    private final Logger logger = LoggerFactory.getLogger(MessageWithResponse.class);
    private static final int BUFFER_SIZE = 64;

    private MessageWithResponseCallback callback;
    private boolean isDoneCalled;
    private ByteBuffer buffer;
    private Instant tsStart;

    MessageWithResponse(MessageRawRequest request, MessageWithResponseCallback callback) {
        super(request);
        this.callback = callback;
        this.isDoneCalled = false;
        this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
    }

    public void done(MessageRawResponse response) {
        isDoneCalled = true;
        this.callback.done(response);
    }

    public void waiting() {
        this.tsStart = Instant.now();
        this.callback.waiting();
    }

    public boolean waitForResponse() {
        return true;
    }

    public void putResponseByte(byte b) {
        if (this.buffer.position() < (BUFFER_SIZE - 1)) {
            this.buffer.put(b);
        }
        try {
            MessageRawResponse response = MessageRawResponse.parseFromBuffer(this.buffer.array(),
                    this.buffer.position());
            done(response);
        } catch (NoResponseAvailable e) {
            logger.debug("putResponseByte.NoResponseAvailable e:{} {}", e.getMessage(),
                    bytesToHex(this.buffer.array(), ""));
        }
    }

    public boolean isDone() {
        return isDoneCalled;
    }
}
