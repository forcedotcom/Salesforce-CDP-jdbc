package com.salesforce.cdp.queryservice.util.internal;

import lombok.extern.slf4j.Slf4j;

import javax.net.SocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Default Wrapper for SocketFactory.
 */
@Slf4j
public class SFDefaultSocketFactoryWrapper extends SocketFactory {

    private final boolean isSocksProxyDisabled;
    private final SocketFactory socketFactory;

    public SFDefaultSocketFactoryWrapper(boolean isSocksProxyDisabled) {
        this(isSocksProxyDisabled, SocketFactory.getDefault());
    }

    public SFDefaultSocketFactoryWrapper(boolean isSocksProxyDisabled, SocketFactory socketFactory) {
        super();
        this.isSocksProxyDisabled = isSocksProxyDisabled;
        this.socketFactory = socketFactory;
    }

    /**
     * When <code>isSocksProxyDisabled</code> then, socket backed by plain socket impl is returned.
     * Otherwise, delegates the socket creation to specified socketFactory
     * @return socket
     * @throws IOException when socket creation fails
     */
    public Socket createSocket() throws IOException {
        // avoid creating SocksSocket when SocksProxyDisabled
        // this is the method called by okhttp
        return isSocksProxyDisabled? new Socket(Proxy.NO_PROXY): this.socketFactory.createSocket();
    }

    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return this.socketFactory.createSocket(host, port);
    }

    public Socket createSocket(InetAddress address, int port) throws IOException {
        return this.socketFactory.createSocket(address, port);
    }

    public Socket createSocket(String host, int port,
                               InetAddress clientAddress, int clientPort
    ) throws IOException, UnknownHostException {
        return this.socketFactory.createSocket(host, port, clientAddress, clientPort);
    }

    public Socket createSocket(InetAddress address, int port,
                               InetAddress clientAddress, int clientPort
    ) throws IOException {
        return this.socketFactory.createSocket(address, port, clientAddress, clientPort);
    }
}
