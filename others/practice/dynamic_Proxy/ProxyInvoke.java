package com.test;

import java.lang.reflect.Method;

public interface ProxyInvoke {
	Object invoke(Object proxyInstance,Object proxy, Method method, Object[] args) throws Throwable;
}