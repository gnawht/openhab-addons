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
package org.openhab.binding.vblindbinding.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link VBlindBindingBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Thomas Wang-Nielsen - Initial contribution
 */
@NonNullByDefault
public class VBlindBindingBindingConstants {

    private static final String BINDING_ID = "vblindbinding";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_BRIDGE = new ThingTypeUID(BINDING_ID, "bridge");
    public static final ThingTypeUID THING_TYPE_VBLIND = new ThingTypeUID(BINDING_ID, "vblind");

    // List of all Channel ids
    public static final String CHANNEL_BRIDGE_STATUS = "status-channel";

    public static final String CHANNEL_VBLIND_POS = "pos_channel";
    public static final String CHANNEL_VBLIND_DIM = "dim_channel";
    public static final String CHANNEL_VBLIND_CONTROL = "control_channel";
    public static final String CHANNEL_VBLIND_POSSTATE = "pos_state_channel";

    public static final String CHANNEL_VBLIND_CONTROL_VALUE_STOP = "stop";
    public static final String CHANNEL_VBLIND_CONTROL_VALUE_GOTO_OPEN = "goto_open";
    public static final String CHANNEL_VBLIND_CONTROL_VALUE_GOTO_CLOSE = "goto_close";
    public static final String CHANNEL_VBLIND_CONTROL_VALUE_GOTO_CLOSE_BLACK = "goto_close_black";
    public static final String CHANNEL_VBLIND_CONTROL_VALUE_GOTO_POS1 = "goto_pos1";
    public static final String CHANNEL_VBLIND_CONTROL_VALUE_GOTO_POS2 = "goto_pos2";
    public static final String CHANNEL_VBLIND_CONTROL_VALUE_GOTO_POS3 = "goto_pos3";
    public static final String CHANNEL_VBLIND_CONTROL_VALUE_PREV = "prev";
    public static final String CHANNEL_VBLIND_CONTROL_VALUE_NEXT = "next";

    public static final String CHANNEL_VBLIND_POSSTATE_VALUE_OPEN = "open";
    public static final String CHANNEL_VBLIND_POSSTATE_VALUE_CLOSE = "close";
    public static final String CHANNEL_VBLIND_POSSTATE_VALUE_CLOSE_BLACK = "close_black";
    public static final String CHANNEL_VBLIND_POSSTATE_VALUE_POS1 = "pos1";
    public static final String CHANNEL_VBLIND_POSSTATE_VALUE_POS2 = "pos2";
    public static final String CHANNEL_VBLIND_POSSTATE_VALUE_POS3 = "pos3";
    public static final String CHANNEL_VBLIND_POSSTATE_VALUE_CHANGE = "change";
    public static final String CHANNEL_VBLIND_POSSTATE_VALUE_UNKNOWN = "unknown";
}
