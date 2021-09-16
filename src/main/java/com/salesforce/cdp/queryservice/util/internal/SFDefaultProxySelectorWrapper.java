package com.salesforce.cdp.queryservice.util.internal;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.List;

/**
 * Wrapper for ProxySelector.
 *
 * <p>
 *     This can be used for additional customization on top of provided proxy selector
 * </p>
 */
// for now, being used for debugging
@Slf4j
public class SFDefaultProxySelectorWrapper extends ProxySelector {

    private final ProxySelector proxySelector;

    public SFDefaultProxySelectorWrapper() {
        this(ProxySelector.getDefault());
    }

    public SFDefaultProxySelectorWrapper(ProxySelector proxySelector) {
        this.proxySelector = proxySelector;
    }

    @Override
    public List<Proxy> select(URI uri) {
        List<Proxy> proxies = proxySelector.select(uri);
        // todo: check if logging URI is accepted
        if (!proxies.isEmpty()) {
            log.info("For URI {}, proxy {} is selected", uri, proxies.get(0));
        } else {
            log.warn("No proxy selected for URI {}", uri);
        }
        return proxies;
    }

    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        proxySelector.connectFailed(uri, sa, ioe);
    }
}
