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
package org.openhab.binding.vblindbinding.internal.controller.command;

import org.openhab.binding.vblindbinding.internal.controller.message.Message;

/**
 * The {@link CommandCallbackVBlind} is responsible for notifications
 *
 * @author Thomas Wang-Nielsen - Initial contribution
 */
public interface CommandCallbackVBlind {
    public void commandVBlindSendMessage(Message message);

    public void commandVBlindUpdatePos(int pos);

    public void commandVBlindUpdateDim(int dim);
}
