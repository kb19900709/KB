package com.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyUtils<T> implements InvocationHandler {
	
	T proxyInstance;
	
	ProxyInvoke proxyInvoke;
	
	ProxyUtils(ProxyInvoke proxyInvoke){
		this.proxyInvoke = proxyInvoke;
	} 

	/**
	 * 回傳代理物件，須注意必須回傳interface不然會有問題
	 * 
	 * This is document explain ...
	 * Returns an instance of a proxy class for the specified interfaces
     * that dispatches method invocations to the specified invocation
     * handler.
     *
	 * @param clazz
	 * @return
	 */
	public T getInstance(Class<? extends T> clazz) {
		try {
			this.proxyInstance = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
		
		return (T)Proxy.newProxyInstance(
				proxyInstance.getClass().getClassLoader(),
				proxyInstance.getClass().getInterfaces(), 
				this);
	}

	//方法攔截
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		return proxyInvoke.invoke(proxyInstance, proxy, method, args);
	}

}
