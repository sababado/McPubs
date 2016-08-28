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
        Collections.sort(list, new AlphanumericComparator());
        return list;
    }
}