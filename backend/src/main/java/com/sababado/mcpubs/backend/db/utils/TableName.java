package com.sababado.mcpubs.backend.db.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by robert on 9/15/15.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TableName {
    String value();

    /**
     * This can be a comma separated string to support joining on multiple tables.
     */
    String joinTable() default "";
}
