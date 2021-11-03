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
package org.openhab.binding.lh4.wiegand.internal.controller;

import static org.openhab.core.util.HexUtils.bytesToHex;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.lh4.wiegand.internal.LH4WiegandConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link Controller} is responsible for notifications
 *
 * @author Thomas Wang-Nielsen - Initial contribution
 */
public class Controller extends Thread {

    private final Logger logger = LoggerFactory.getLogger(Controller.class);
    private final LH4WiegandConfiguration config;
    private final ControllerCallback callback;

    private @Nullable Socket socket;
    private boolean stop;

    private static final int BUFFER_SIZE = 100;
    private ByteBuffer buffer;

    ScheduledExecutorService scheduler;

    public Controller(LH4WiegandConfiguration config, ControllerCallback callback, ScheduledExecutorService scheduler) {
        this.config = config;
        this.callback = callback;
        this.scheduler = scheduler;
        this.stop = false;
        clearBuffer();
    }

    public void run() {
        logger.debug("run");
        this.startClientDelayed();
        logger.debug("run.end");
    }

    public void stopClient() {
        logger.debug("stopClient");
        stop = true;
        cleanUp();
    }

    private void cleanUp() {
        logger.debug("cleanUp");
        if (this.socket != null) {
            try {
                this.socket.close();
                this.socket = null;
            } catch (IOException e) {
                logger.error("Exception.3:{}", e.getMessage());
            }
        }
    }

    private void startClientDelayed() {
        callback.notifyOffline();
        cleanUp();
        try {
            logger.debug("startClientDelayed waiting");
            TimeUnit.SECONDS.sleep(5);
            startClient();
        } catch (InterruptedException e) {
            logger.error("startClientDelayed.Exception:{}", e.getMessage());
        }
    }

    private void startClient() {
        clearBuffer();
        try {
            socket = new Socket(this.config.host, this.config.port);
            callback.notifyOnline();
            logger.debug("Client connected to:{}", this.socket.getRemoteSocketAddress().toString());
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
                logger.debug("Closed by remote");
            } catch (IOException ex) {
                logger.error("Exception.1:{}", ex.getMessage());
            }
        } catch (IOException ex) {
            logger.error("Exception.2:{}", ex.getMessage());
        }
        if (this.stop) {
            logger.debug("startServer.stopped");
        } else {
            this.startClientDelayed();
        }
    }

    private synchronized void handleByte(byte b) {
        int number = b & 0xff;
        logger.debug("handleByte b(hex):{} b:{}", Integer.toHexString(number), number);
        if (number == 11) {
            handleBuffer();
        } else if (buffer.position() < (BUFFER_SIZE - 1)) {
            buffer.put(b);
        } else {
            handleBuffer();
        }
    }

    private void handleBuffer() {
        byte[] message = new byte[buffer.position()];
        System.arraycopy(buffer.array(), 0, message, 0, buffer.position());
        String messageStr = bytesToHex(message, " ");
        clearBuffer();
        logger.debug("handleBuffer access: {}", messageStr);
        callback.notifyAccess(encrypt(messageStr));
    }

    private void clearBuffer() {
        buffer = ByteBuffer.allocate(BUFFER_SIZE);
    }

    public boolean isOnline() {
        if (this.socket != null) {
            return this.socket.isConnected();
        }
        return false;
    }

    private static final String SECRET_KEY = "my_super_secret_key_ho_ho_ho";
    private static final String SALT = "ssshhhhhhhhhhh!!!!";

    public static String encrypt(String strToEncrypt) {
        try {
            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }
}
