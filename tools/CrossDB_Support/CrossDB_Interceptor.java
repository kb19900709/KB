package com.test;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;

import com.annotations.CrossSup;

/**
 * @author KB
 * @see org.aopalliance.intercept.MethodInterceptor
 */
public class CrossDB_Interceptor implements MethodInterceptor{
	
	private static Logger logger = LoggerFactory.getLogger(CrossDB_Interceptor.class);
	
	@Autowired
	CrossDB crossDB;

	@Override
	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		Object accessibleObj = methodInvocation.getThis();
		Method proxyMethod = methodInvocation.getMethod();
		
		//確認原始呼叫類別
		if(accessibleObj == null){
			String errorMsg = "Can not support current method if the accessible object is static，"
					+ "direct method is "+proxyMethod.getName();
			logger.error(errorMsg);
			throw new Exception(errorMsg);
		}
		
		/**
		 * 從該類別取得方法並透過 isTheSameMethod 辨識是否為同一個方法
		 * 理由為如果是代理方法無法取得 @interface > @Retention > RetentionPolicy.RUNTIME 的註解識別
		 */
		Method originalMethod = null;
		for(Method method : accessibleObj.getClass().getMethods()){
			if(isTheSameMethod(method,proxyMethod)) originalMethod = method;
		};

		//若攔截的方法沒有 @CrossSup 註解，則執行該方法
		CrossSup crossSup = originalMethod.getAnnotation(CrossSup.class);
		if(crossSup == null) return methodInvocation.proceed();

		logger.info("CrossDB_Interceptor catch method invoke " 
				+ accessibleObj.getClass()+"."+originalMethod.getName());
		
		//使用 CrossDB 完成跨DB transactions 控制
		List<TransactionStatus> openTM = null;
		Object result = null;
		try{
			//open transactions
			openTM = crossDB.openExtraTM(crossSup.tmIDs());
			//execute the method
			result = methodInvocation.proceed();
			//commit transactions
			crossDB.commit(openTM);
		}catch(RuntimeException re){
			//default if throw RuntimeException
			crossDB.rollback(openTM,re);
		}
		return result;
	}
	
	/**
	 * prevent overloading
	 * @param original
	 * @param proxy
	 * @return
	 */
	private boolean isTheSameMethod(Method original,Method proxy){
		//名字是否一致
		if(!original.getName().equals(proxy.getName())) return false;
		
		Type[] originalParameterTypes = original.getGenericParameterTypes();
		Type[] proxyParameterTypes = proxy.getGenericParameterTypes();
		
		//public method 參數陣列長度是否一致
		if(originalParameterTypes.length != proxyParameterTypes.length) return false;

		int typeCount = originalParameterTypes.length;	
		for(int i=0;i<typeCount;i++){
			//public method 參數陣列中每個元素的型態是否一致
			if(!originalParameterTypes[i].getClass().equals(proxyParameterTypes[i].getClass())) 
				return false;
		}
		
		return true;
	}

}
