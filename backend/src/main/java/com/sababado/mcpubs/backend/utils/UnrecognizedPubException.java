package com.sababado.mcpubs.backend.utils;

/**
 * Created by robert on 8/27/16.
 */
public class UnrecognizedPubException extends Exception {

    private static final String ERR_MSG = "%%% Unrecognized Pub --- ";

    public UnrecognizedPubException(String pubInfo) {
        super(ERR_MSG + pubInfo);
    }

    public UnrecognizedPubException(String pubInfo, Throwable cause) {
        super(ERR_MSG + pubInfo, cause);
    }
}
