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

/**
 * The {@link Message} is responsible for notifications
 *
 * @author Thomas Wang-Nielsen - Initial contribution
 */
public abstract class Message {

    private MessageRawRequest request;
    private boolean isDoneFlag;

    Message(MessageRawRequest request) {
        this.request = request;
        this.isDoneFlag = false;
    }

    public byte[] buildMessageRawRequest() {
        return this.request.build();
    }

    public MessageRawRequest getRequest() {
        return request;
    }

    public long getTimeoutSec() {
        return 4;
    }

    public void done() {
        this.isDoneFlag = true;
    }

    public void error(String error) {
        done();
    }

    public void timeout() {
        done();
    }

    public boolean isDone() {
        return isDoneFlag;
    }

    public abstract boolean waitForResponse();

    public abstract void putResponseByte(byte b);

    public abstract void waiting();
}
