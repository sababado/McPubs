package com.sababado.mcpubs.backend.factory;

/**
 * Created by robert on 8/27/16.
 */
public class FactoryHelper {
    public static void setMockNetworkHelper() {
        Factory.setNetworkProvider(new MockNetworkProvider());
    }

    public static void removeMockNetworkHelper() {
        Factory.setNetworkProvider(null);
    }
}
