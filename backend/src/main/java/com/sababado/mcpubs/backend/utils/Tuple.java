package com.sababado.mcpubs.backend.utils;

/**
 * Created by robert on 8/28/16.
 */
public class Tuple<E, U, V> {
    public final E one;
    public final U two;
    public final V three;

    public Tuple(E one, U two, V three) {
        this.one = one;
        this.two = two;
        this.three = three;
    }
}
