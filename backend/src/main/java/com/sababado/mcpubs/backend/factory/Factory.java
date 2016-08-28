package com.sababado.mcpubs.backend.factory;

/**
 * Created by robert on 8/27/16. Useful to help with injecting test classes
 */
public class Factory {
    static NetworkProvider networkProvider;

    public static NetworkProvider getNetworkProvider() {
        if (networkProvider == null) {
            networkProvider = new SimpleNetworkProvider();
        }
        return networkProvider;
    }

    static void setNetworkProvider(NetworkProvider np) {
        networkProvider = np;
    }
}
