package com.test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Cross DB transaction support without JTA 
 * @author KB
 */
@Target(java.lang.annotation.ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CrossSup {
	String[] tmIDs() default {};
}
