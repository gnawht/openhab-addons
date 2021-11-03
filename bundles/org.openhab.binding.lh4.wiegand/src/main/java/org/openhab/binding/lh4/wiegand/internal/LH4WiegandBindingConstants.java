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
package org.openhab.binding.lh4.wiegand.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link LH4WiegandBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Thomas Wang-Nielsen - Initial contribution
 */
@NonNullByDefault
public class LH4WiegandBindingConstants {

    private static final String BINDING_ID = "lh4-wiegand";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_WIEGAND = new ThingTypeUID(BINDING_ID, "wiegand");

    // List of all Channel ids
    public static final String CHANNEL_ACCESS = "access";
}
