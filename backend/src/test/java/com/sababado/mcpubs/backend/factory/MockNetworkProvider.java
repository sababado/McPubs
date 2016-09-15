package com.sababado.mcpubs.backend.factory;

import com.sababado.mcpubs.backend.FileUtils;
import com.sababado.mcpubs.backend.utils.StringUtils;

import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by robert on 8/27/16.
 */
public class MockNetworkProvider implements NetworkProvider {
    @Override
    public Document doNetworkCall(String url) throws IOException {
        if(StringUtils.isEmptyOrWhitespace(url)) {
            return null;
        }
        if(url.equals("http://www.marines.mil/News/Publications/ELECTRONIC-LIBRARY/Custompubtype/2005/Search/3500/?Page=1")) {
            return FileUtils.parseFile("paginatedSearch/page1.html");
        }
        if(url.equals("http://www.marines.mil/News/Publications/ELECTRONIC-LIBRARY/Custompubtype/2005/Search/3500/?Page=2")) {
            return FileUtils.parseFile("paginatedSearch/page2.html");
        }
        if(url.equals("http://www.marines.mil/News/Publications/ELECTRONIC-LIBRARY/Custompubtype/2005/Search/3500/?Page=3")) {
            return FileUtils.parseFile("paginatedSearch/page3.html");
        }

        return null;
    }
}
