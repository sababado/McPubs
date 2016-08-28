package com.sababado.mcpubs.backend.factory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by robert on 8/27/16.
 */
public class SimpleNetworkProvider implements NetworkProvider {
    SimpleNetworkProvider() {
        //private
    }

    public Document doNetworkCall(String url) throws IOException {
        return Jsoup.connect(url).get();
    }
}
