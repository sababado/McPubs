package com.sababado.mcpubs.backend.utils;

import com.sababado.mcpubs.backend.models.Pub;

import java.util.List;

/**
 * Created by robert on 8/27/16.
 */
public class PubUtils {
    public static final String PS_NEW = "New";
    public static final String PS_CURRENT = "Current";
    public static final String PS_DELETED = "Deleted";

    public enum UpdateStatus {
        NO_CHANGE,
        UPDATED,
        UPDATED_BUT_DELETED,
        DELETED
    }

    public static boolean parseStatus(String status) {
        return PS_CURRENT.equalsIgnoreCase(status) || PS_NEW.equalsIgnoreCase(status);
    }

    public static Pub findPubByFullCode(List<Pub> pubList, String fullCode) {
        if (pubList != null && pubList.size() > 0) {
            int size = pubList.size();
            for (int i = 0; i < size; i++) {
                if (pubList.get(i).getFullCode().equalsIgnoreCase(fullCode)) {
                    return pubList.get(i);
                }
            }
        }
        return null;
    }

    public static boolean pubInfoEquals(Pub pub1, Pub pub2) {
        boolean rs1 = StringUtils.isEmptyOrWhitespace(pub1.getReadableTitle());
        boolean rs2 = StringUtils.isEmptyOrWhitespace(pub2.getReadableTitle());
        if (rs1 != rs2)
            return false;
        if (rs1 && rs2)
            return true;
        if (!pub1.getReadableTitle().equals(pub2.getReadableTitle()))
            return false;
        return true;
    }

    public static void copyPubInfo(Pub to, Pub from) {
        to.setReadableTitle(from.getReadableTitle());
    }
}
