package com.sababado.mcpubs.backend.db.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by robert on 9/15/15.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Join {
    String table();

    String fromCol();

    String toCol();
}
