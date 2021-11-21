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

/**
 * The {@link LH4WiegandConfiguration} class contains fields mapping thing configuration parameters.
 *
 * @author Thomas Wang-Nielsen - Initial contribution
 */
public class LH4WiegandConfiguration {

    /**
     * Sample configuration parameters. Replace with your own.
     */
    public String host;
    public int port;
    public int accessTimeoutSec;
    public String encryptionKey;
}
