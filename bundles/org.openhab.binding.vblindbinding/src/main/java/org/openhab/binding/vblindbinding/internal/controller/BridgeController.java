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
package org.openhab.binding.vblindbinding.internal.controller;

import static org.openhab.core.util.HexUtils.bytesToHex;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.vblindbinding.internal.VBlindBindingBridgeConfiguration;
import org.openhab.binding.vblindbinding.internal.VBlindBindingNotifyThingStatus;
import org.openhab.binding.vblindbinding.internal.controller.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link BridgeController} is responsible for notifications
 *
 * @author Thomas Wang-Nielsen - Initial contribution
 */
public class BridgeController extends Thread implements BridgeControllerVBlindCallback {

    private final Logger logger = LoggerFactory.getLogger(BridgeController.class);
    private final VBlindBindingBridgeConfiguration config;
    private final VBlindBindingNotifyThingStatus notifyThingStatus;

    private @Nullable ServerSocket serverSocket;
    private @Nullable Socket socket;
    private boolean stop;

    private Queue<Message> messageQueue = new LinkedList<Message>();
    private @Nullable Message currentMessage;

    ScheduledExecutorService scheduler;

    public BridgeController(VBlindBindingBridgeConfiguration config, VBlindBindingNotifyThingStatus notifyThingStatus,
            ScheduledExecutorService scheduler) {
        this.config = config;
        this.notifyThingStatus = notifyThingStatus;
        this.scheduler = scheduler;
        this.stop = false;
    }

    public void run() {
        logger.debug("run");
        this.startServerDelayed();
        logger.debug("run.end");
    }

    public void stopServer() {
        logger.debug("stopServer");
        this.stop = true;
        this.cleanUp();
    }

    private void cleanUp() {
        if (this.socket != null) {
            try {
                this.socket.close();
                this.socket = null;
            } catch (IOException e) {
                logger.error("Exception.3:{}", e.getMessage());
            }
        }
        if (this.serverSocket != null) {
            try {
                this.serverSocket.close();
                this.serverSocket = null;
            } catch (IOException e) {
                logger.error("Exception.3:{}", e.getMessage());
            }
        }
    }

    private void startServer() {
        try {
            serverSocket = new ServerSocket(this.config.port, 1, InetAddress.getByName(this.config.host));
            serverSocket.setReuseAddress(true);
            logger.debug("Listening on {}:{}", config.host, config.port);

            socket = serverSocket.accept();
            notifyThingStatus.notifyOnline();
            logger.debug("Client connected from:{}", this.socket.getRemoteSocketAddress().toString());

            try {
                InputStream input = this.socket.getInputStream();
                int c;
                while (!this.stop) {
                    c = input.read();
                    if (c != -1) {
                        this.handleByte((byte) c);
                    } else {
                        break;
                    }
                }
                logger.debug("Client closed by remote");
            } catch (IOException ex) {
                logger.error("Exception.1:{}", ex.getMessage());
            }
        } catch (IOException ex) {
            logger.error("Exception.2:{}", ex.getMessage());
        }
        if (this.stop) {
            logger.debug("startServer.stopped");
        } else {
            this.startServerDelayed();
        }
    }

    private synchronized void handleByte(byte b) {
        if (this.currentMessage != null) {
            this.currentMessage.putResponseByte(b);
            if (this.currentMessage.isDone()) {
                handleMessageQueueNext();
            }
        } else {
            logger.warn("handleByte.no current message to receive data b:{} ignored", Integer.toHexString(b & 0xFF));
        }
    }

    private void startServerDelayed() {
        notifyThingStatus.notifyOffline();
        cleanUp();
        try {
            logger.debug("startServerDelayed waiting");
            TimeUnit.SECONDS.sleep(5);
            startServer();
        } catch (InterruptedException e) {
            logger.error("startServerDelayed.Exception:{}", e.getMessage());
        }
    }

    private synchronized void handleMessageQueue() {
        logger.debug("handleMessageQueue queueSize:{} hasCurrentMessage:{}", this.messageQueue.size(),
                this.currentMessage != null);
        if (this.currentMessage == null && !this.messageQueue.isEmpty()) {
            Message nextMessage = this.messageQueue.remove();
            if (this.socket != null) {
                try {
                    byte[] messageRaw = nextMessage.buildMessageRawRequest();
                    logger.trace("handleMessageQueue.send wait:{} raw:{}", nextMessage.waitForResponse(),
                            bytesToHex(messageRaw, " "));
                    this.socket.getOutputStream().write(messageRaw);
                } catch (IOException e) {
                    logger.error("sendMessage Exception:{}", e.getMessage());
                    nextMessage.error("Error sending message e:" + e.getMessage());
                }
                if (nextMessage.waitForResponse()) {
                    this.currentMessage = nextMessage;
                    nextMessage.waiting();
                    scheduler.schedule(() -> {
                        if (!nextMessage.isDone()) {
                            nextMessage.timeout();
                            handleMessageQueueNext();
                        }
                    }, nextMessage.getTimeoutSec(), TimeUnit.SECONDS);
                } else {
                    nextMessage.done();
                }
            } else {
                logger.error("sendMessage.no socket");
                nextMessage.error("Error sending message, no socket");
            }
        }
    }

    private synchronized void handleMessageQueueNext() {
        this.currentMessage = null;
        try {
            TimeUnit.MILLISECONDS.sleep(1000);
        } catch (InterruptedException e) {
        }
        this.handleMessageQueue();
    }

    @Override
    public synchronized void vblindSendMessage(Message message) {
        logger.debug("vblindSendMessage");
        this.messageQueue.add(message);
        this.handleMessageQueue();
    }

    @Override
    public boolean vblindIsOnline() {
        if (this.socket != null) {
            return this.socket.isConnected();
        }
        return false;
    }
}
