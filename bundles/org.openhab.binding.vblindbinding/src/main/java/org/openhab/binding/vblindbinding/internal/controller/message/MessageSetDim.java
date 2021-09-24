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
 * The {@link MessageSetDim} is responsible for notifications
 *
 * @author Thomas Wang-Nielsen - Initial contribution
 */
public class MessageSetDim extends MessageSet {
    public MessageSetDim(int major, int minor, int pct, MessageCallback callback) {
        super(MessageRawRequest.createSetDim(major, minor, pct), callback);
    }
}
