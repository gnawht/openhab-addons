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

    public static final String CHANNEL_VBLIND_POS = "pos-channel";
    public static final String CHANNEL_VBLIND_DIM = "dim-channel";
    public static final String CHANNEL_VBLIND_CONTROL = "control-channel";
}
