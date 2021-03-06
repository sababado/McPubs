package com.sababado.mcpubs.backend.models;

import com.sababado.mcpubs.backend.utils.UnrecognizedPubException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by robert on 8/27/16.
 */
public class PubTest {
    @Test
    public void parsePubTest() {
        //Doctrine pub
        try {
            Pub pub = new Pub("FMFRP 12-27", "THE PATTERN OF WAR", true, 2002);
            assertEquals(pub.getTitle(), "FMFRP 12-27");
            assertEquals(pub.getReadableTitle(), "THE PATTERN OF WAR");
            assertEquals(pub.isActive(), true);
            assertEquals(pub.getFullCode(), "12-27");
            assertEquals(pub.getRootCode(), "12-27");
            assertEquals(pub.getCode(), -1);
            assertEquals(pub.getVersion(), null);
        } catch (UnrecognizedPubException e) {
            fail(e.getMessage());
        }

        // MCO
        try {
            Pub pub = new Pub("MCO 3500.24A", "POLICY FOR THE FEDERAL BUREAU OF INVESTIGATION (FBI) TRAINING ASSISTANCE TO THE MARINE CORPS", false, Pub.MCO);
            assertEquals(pub.getFullCode(), "3500.24");
            assertEquals(pub.getRootCode(), "3500");
            assertEquals(pub.getCode(), 24);
            assertEquals(pub.getVersion(), "A");
        } catch (UnrecognizedPubException e) {
            fail(e.getMessage());
        }

        // MCO P
        try {
            Pub pub = new Pub("MCO P10110.42B", "ARMED FORCES RECIPE SERVICE (THE COMPLETE COLLECTION)", true, Pub.MCO_P);
            assertEquals(pub.getFullCode(), "P10110.42");
            assertEquals(pub.getRootCode(), "P10110");
            assertEquals(pub.getCode(), 42);
            assertEquals(pub.getVersion(), "B");
        } catch (UnrecognizedPubException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void parseDoctrine() {
        try {
            Pub pub = new Pub("MCDP 1", "WARFIGHTING", true, 2002);
            assertEquals(pub.getTitle(), "MCDP 1");
            assertEquals(pub.getReadableTitle(), "WARFIGHTING");
            assertEquals(pub.isActive(), true);
            assertEquals(pub.getFullCode(), "1");
            assertEquals(pub.getRootCode(), "1");
            assertEquals(pub.getCode(), -1);
            assertEquals(pub.getVersion(), null);
        } catch (UnrecognizedPubException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void parseDirectives() {
        try {
            Pub pub = new Pub("NAVMC DIR 3500.104", "AH-1Z TRAINING AND READINESS MANUAL", true, Pub.NAVMC_DIR);
            assertEquals(pub.getTitle(), "NAVMC DIR 3500.104");
            assertEquals(pub.getReadableTitle(), "AH-1Z TRAINING AND READINESS MANUAL");
            assertEquals(pub.isActive(), true);
            assertEquals(pub.getFullCode(), "3500.104");
            assertEquals(pub.getRootCode(), "3500");
            assertEquals(pub.getCode(), 104);
            assertEquals(pub.getVersion(), null);
        } catch (UnrecognizedPubException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void unrecognizedPubParseTest() {
        try {
            new Pub("U.S. MARINES IN THE KOREAN WAR PT 12", "U.S. MARINES IN THE KOREAN WAR", true, 2003);
            fail("This pub should not have passed.");
        } catch (UnrecognizedPubException e) {
            assertTrue(true);
        }
    }

    @Test
    public void parseSingleDigitMco() {
        try {
            new Pub("MCO 124", "AAATest", true, 2005);
        } catch (UnrecognizedPubException e) {
            fail();
        }
    }
}
