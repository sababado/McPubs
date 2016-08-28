package com.sababado.mcpubs.backend.factory;

import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by robert on 8/27/16.
 */
public interface NetworkProvider {
    public Document doNetworkCall(String url) throws IOException;
}
