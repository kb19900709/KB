package com.test;

import java.lang.reflect.Method;

public class MusicProvider implements ProxyInvoke{
	
	private static final MusicProvider SELF = new MusicProvider();
	
	public static Music getPinao(){
		return new ProxyUtils<Music>(SELF).getInstance(Piano.class);
	}
	
	public static Music getSpeaker(){
		return new ProxyUtils<Music>(SELF).getInstance(Speaker.class);
	}

	@Override
	public Object invoke(Object proxyInstance, Object proxy, Method method,
			Object[] args) throws Throwable {
		Object result = null;
		if("execute".equals(method.getName())){
			System.out.println(proxyInstance.getClass().getName()+" >>> now execute");
			result = method.invoke(proxyInstance,args);
			System.out.println("\n");
		}
		return result;
	}
}
