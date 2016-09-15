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
        return Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                .referrer("http://www.google.com")
                .timeout(1000 * 5) //it's in milliseconds, so this means 5 seconds.
                .get();
    }
}
