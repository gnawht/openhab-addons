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

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link MessageRawResponse} is responsible for notifications
 *
 * @author Thomas Wang-Nielsen - Initial contribution
 */
public class MessageRawResponse extends MessageRaw {
    private final Logger logger = LoggerFactory.getLogger(MessageRawResponse.class);

    private final byte id;
    private final byte major;
    private final byte minor;
    private final byte command;
    private final byte[] data;
    private final byte[] crc;
    private final byte[] raw;

    MessageRawResponse(byte id, byte major, byte minor, byte command, byte[] data, byte[] crc, byte[] raw) {
        this.id = id;
        this.major = major;
        this.minor = minor;
        this.command = command;
        this.data = data;
        this.crc = crc;
        this.raw = raw;
    }

    public byte getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }

    public byte[] getRaw() {
        return raw;
    }

    static public MessageRawResponse parseFromBuffer(byte[] buffer) throws NoResponseAvailable {
        return parseFromBuffer(buffer, buffer.length);
    }

    static public MessageRawResponse parseFromBuffer(byte[] buffer, int size) throws NoResponseAvailable {
        if (size >= 6) {
            byte id = buffer[0];
            if (id != 0x55) {
                throw new NoResponseAvailable("invalid id");
            }
            byte major = buffer[1];
            byte minor = buffer[2];
            byte command = buffer[3];

            byte[] crc = ByteBuffer.allocate(2).array();
            crc[0] = buffer[size - 2];
            crc[1] = buffer[size - 1];

            byte[] crcCalc = calcCRC16(buffer, size - 2);
            if (crc[0] != crcCalc[0] || crc[1] != crcCalc[1]) {
                throw new NoResponseAvailable("invalid crc");
            }
            byte[] data = ByteBuffer.allocate(0).array();
            if (size > 6) {
                data = Arrays.copyOfRange(buffer, 4, 4 + size - 6);
            }
            return new MessageRawResponse(id, major, minor, command, data, crc,
                    ByteBuffer.wrap(buffer, 0, size).array());
        }
        throw new NoResponseAvailable("invalid length");
    }
}
