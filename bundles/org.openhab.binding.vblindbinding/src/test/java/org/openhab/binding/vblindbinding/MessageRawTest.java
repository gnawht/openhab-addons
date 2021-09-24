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
package org.openhab.binding.vblindbinding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.openhab.core.util.HexUtils.bytesToHex;
import static org.openhab.core.util.HexUtils.hexToBytes;

import org.junit.jupiter.api.Test;
import org.openhab.binding.vblindbinding.internal.controller.message.MessageRawRequest;
import org.openhab.binding.vblindbinding.internal.controller.message.MessageRawResponse;
import org.openhab.binding.vblindbinding.internal.controller.message.NoResponseAvailable;

/**
 * The {@link MessageRawTest} is responsible for notifications
 *
 * @author Thomas Wang-Nielsen - Initial contribution
 */
public class MessageRawTest {
    @Test
    public void testRequestBuild() {
        MessageRawRequest request001 = MessageRawRequest.createQueryPos(10, 1);
        assertEquals("55 0A 01 01 02 01 04 83", bytesToHex(request001.build(), " "));
    }

    @Test
    public void testResponseParse() {
        try {
            byte[] buffer = hexToBytes("55 0C 01 01 01 00 4D B3", " ");
            MessageRawResponse response = MessageRawResponse.parseFromBuffer(buffer);
            assertEquals(0x55, response.getId());
        } catch (NoResponseAvailable e) {
            fail(e.getMessage());
        }

        try {
            byte[] buffer = hexToBytes("55 0C 01 01 01 00 4D", " ");
            MessageRawResponse response = MessageRawResponse.parseFromBuffer(buffer);
        } catch (NoResponseAvailable e) {
            assertEquals("invalid crc", e.getMessage());
        }

        try {
            byte[] buffer = hexToBytes("55 0C 01 01 01 00", " ");
            MessageRawResponse response = MessageRawResponse.parseFromBuffer(buffer);
        } catch (NoResponseAvailable e) {
            assertEquals("invalid crc", e.getMessage());
        }

        try {
            byte[] buffer = hexToBytes("56 0C 01 01 01 00", " ");
            MessageRawResponse response = MessageRawResponse.parseFromBuffer(buffer);
        } catch (NoResponseAvailable e) {
            assertEquals("invalid id", e.getMessage());
        }

        try {
            byte[] buffer = hexToBytes("55 0C 01 01 01", " ");
            MessageRawResponse response = MessageRawResponse.parseFromBuffer(buffer);
        } catch (NoResponseAvailable e) {
            assertEquals("invalid length", e.getMessage());
        }

        try {
            byte[] buffer = hexToBytes("56 0C 01 01 01", " ");
            MessageRawResponse response = MessageRawResponse.parseFromBuffer(buffer);
        } catch (NoResponseAvailable e) {
            assertEquals("invalid length", e.getMessage());
        }

        try {
            byte[] buffer = hexToBytes("", " ");
            MessageRawResponse response = MessageRawResponse.parseFromBuffer(buffer);
        } catch (NoResponseAvailable e) {
            assertEquals("invalid length", e.getMessage());
        }

        try {
            byte[] buffer = hexToBytes("55 0A 01 01 03 B4 01 00 52 EB", " ");
            MessageRawResponse response = MessageRawResponse.parseFromBuffer(buffer);
        } catch (NoResponseAvailable e) {
            assertEquals("invalid length", e.getMessage());
        }
    }
}
