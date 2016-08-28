package com.sababado.mcpubs.backend.cron;

import com.sababado.mcpubs.backend.factory.FactoryHelper;
import com.sababado.mcpubs.backend.models.Pub;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

/**
 * Created by robert on 8/27/16.
 */
public class PubCheckTest {
    @Before
    public void setup() {
        FactoryHelper.setMockNetworkHelper();
    }

    @Test
    public void testGetPubsFromSearch() {
        String rootCode = "3500";
        PubCheck pubCheck = new PubCheck();
        List<Pub> pubs = pubCheck.getPubsFromSearch(rootCode, Pub.MCO);
        assertNotNull(pubs);
        assertEquals(57, pubs.size());
        //checking inequality of random elements in list.
        assertNotSame(pubs.get(0), pubs.get(25));
        assertNotSame(pubs.get(22), pubs.get(51));

        // test sort
    }

    @After
    public void teardown() {
        FactoryHelper.removeMockNetworkHelper();
    }
}
