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

/**
 * The {@link Message} is responsible for notifications
 *
 * @author Thomas Wang-Nielsen - Initial contribution
 */
public abstract class Message {

    MessageRawRequest request;

    Message(MessageRawRequest request) {
        this.request = request;
    }

    public byte[] buildMessageRawRequest() {
        return this.request.build();
    }

    public boolean waitForResponse() {
        return false;
    }

    public void putResponseByte(byte b) {
    }

    public void done() {
    }

    public void waiting() {
    }

    public boolean isDone() {
        return false;
    }
}
