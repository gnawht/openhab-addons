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
import static org.openhab.core.util.HexUtils.hexToBytes;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link MessageRawRequest} is responsible for notifications
 *
 * @author Thomas Wang-Nielsen - Initial contribution
 */
public class MessageRawRequest extends MessageRaw {
    private final Logger logger = LoggerFactory.getLogger(MessageRawRequest.class);

    private final byte major;
    private final byte minor;
    private final byte command;
    private final byte[] buffer;

    MessageRawRequest(byte major, byte minor, byte command, byte[] buffer) {
        this.major = major;
        this.minor = minor;
        this.command = command;
        this.buffer = buffer;
    }

    public byte getMinor() {
        return minor;
    }

    public byte getMajor() {
        return major;
    }

    public byte[] build() {
        int size = 1 + 2 + 1 + buffer.length + 2;
        byte[] result = new byte[size];
        result[0] = 0x55;
        result[1] = this.major;
        result[2] = this.minor;
        result[3] = this.command;
        System.arraycopy(this.buffer, 0, result, 4, this.buffer.length);
        System.out.println("build.result" + bytesToHex(result, " "));
        byte[] crc = this.calcCRC16(result, 4 + this.buffer.length);
        System.out.println("build.crc" + bytesToHex(crc, " "));
        System.arraycopy(crc, 0, result, result.length - 2, 2);
        return result;
    }

    static public MessageRawRequest createQueryPos(int major, int minor) {
        byte[] subCommand = hexToBytes("02 01", " ");
        return new MessageRawRequest((byte) major, (byte) minor, (byte) 0x01, subCommand);
    }

    static public MessageRawRequest createQueryDim(int major, int minor) {
        byte[] subCommand = hexToBytes("06 03", " ");
        return new MessageRawRequest((byte) major, (byte) minor, (byte) 0x01, subCommand);
    }

    static public MessageRawRequest createSetPos(int major, int minor, int pct) {
        byte[] subCommand = ByteBuffer.allocate(2).array();
        subCommand[0] = (byte) 0x04;
        subCommand[1] = (byte) pct;
        return new MessageRawRequest((byte) major, (byte) minor, (byte) 0x03, subCommand);
    }

    static public MessageRawRequest createSetDim(int major, int minor, int dim) {
        byte[] subCommand = ByteBuffer.allocate(3).array();
        subCommand[0] = (byte) 0x04;
        subCommand[1] = (byte) 0xFF;
        subCommand[2] = (byte) dim;
        return new MessageRawRequest((byte) major, (byte) minor, (byte) 0x03, subCommand);
    }

    static public MessageRawRequest createOpen(int major, int minor) {
        byte[] subCommand = ByteBuffer.allocate(3).array();
        subCommand[0] = (byte) 0x01;
        return new MessageRawRequest((byte) major, (byte) minor, (byte) 0x03, subCommand);
    }

    static public MessageRawRequest createClose(int major, int minor) {
        byte[] subCommand = ByteBuffer.allocate(3).array();
        subCommand[0] = (byte) 0x02;
        return new MessageRawRequest((byte) major, (byte) minor, (byte) 0x03, subCommand);
    }

    static public MessageRawRequest createStop(int major, int minor) {
        byte[] subCommand = ByteBuffer.allocate(3).array();
        subCommand[0] = (byte) 0x03;
        return new MessageRawRequest((byte) major, (byte) minor, (byte) 0x03, subCommand);
    }
}
