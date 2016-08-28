package com.sababado.mcpubs.backend.utils;

import com.sababado.mcpubs.backend.models.Pub;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by robert on 8/28/16.
 */
public class PubSorter {
    public static <E extends List<Pub>> E sortByFullCode(E list) {
        Collections.sort(list, new FullCodeSorter());
        return list;
    }

    static class FullCodeSorter implements Comparator<Pub> {
        @Override
        public int compare(Pub o1, Pub o2) {
            String o1FullCode = o1.getFullCode();
            String o2FullCode = o2.getFullCode();
            return o1FullCode.compareTo(o2FullCode);
        }
    }
}