/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.store;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

/**
 *
 * @author mg
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SerialCollection {

    Class<?> elementType();

    String elementTagName();

    Class<? extends Collection> deserializeAs();
}
