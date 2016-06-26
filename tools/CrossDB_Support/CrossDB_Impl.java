package com.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * @author KB
 * @see org.springframework.transaction.PlatformTransactionManager
 * @see org.springframework.transaction.TransactionStatus
 */
@Component
public class CrossDB_Impl implements CrossDB{
	
	private static Logger logger = LoggerFactory.getLogger(CrossDB_Impl.class);

	//map TM_ID:TM
	private Map<String,PlatformTransactionManager> tmMap;

	//map TransactionStatus集合:對應的TM集合
	private Map<List<TransactionStatus>,List<PlatformTransactionManager>> currentTransEntry = 
			new HashMap<List<TransactionStatus>,List<PlatformTransactionManager>>();
	
	//map TransactionStatus:TM_ID紀錄
	private Map<List<TransactionStatus>,StringBuffer> currentTransIDRecord = 
			new HashMap<List<TransactionStatus>,StringBuffer>();;
	
	@Autowired
	private void init(ApplicationContext appContext){
		//spring container 初始化時取得所有 type=PlatformTransactionManager.class 的 TM (HibernateTransactionManager)
		this.tmMap = appContext.getBeansOfType(PlatformTransactionManager.class);
	}

	@Override
	public List<TransactionStatus> openExtraTM(String... extraOpenTmIDs) {
		//如果參數為空，則將開啟所有的 TM 控制
		if(ArrayUtils.isEmpty(extraOpenTmIDs)){
			String warnMsg = "CrossDB_Impl.openExtraTM input params is null， CrossDB_Impl will open all TM control";
			logger.info(warnMsg);
			extraOpenTmIDs = getAllTmIDs();
		}
		
		logger.info("CrossDB_Impl.openExtraTM begin");
		
		StringBuffer sb = new StringBuffer();
		List<PlatformTransactionManager> tmRecord = new ArrayList<PlatformTransactionManager>();
		List<TransactionStatus> statusRecord = new ArrayList<TransactionStatus>();
		PlatformTransactionManager currentTM;
		TransactionStatus currentStatus;
		DefaultTransactionDefinition dtd;

		//依據傳入TM_ID 初始化 transaction 必要資訊
		for(String txID:extraOpenTmIDs){
			if(sb.toString().length()>0){
				sb.append(", "+txID);
			}else{
				sb.append(txID);
			}
			currentTM = tmMap.get(txID);
			//初始化預設的交易行為 >>> PROPAGATION_REQUIRED,ISOLATION_DEFAULT
			dtd = new DefaultTransactionDefinition();
			dtd.setName(getClass().getName());
			//開啟 transaction
			currentStatus = currentTM.getTransaction(dtd);
			tmRecord.add(currentTM);
			statusRecord.add(currentStatus);
		}
		
		//因為 transaction 先進後出的特性在此處做集合反轉供後續 commit or rollBack 操作
		Collections.reverse(statusRecord);
		Collections.reverse(tmRecord);
		
		currentTransEntry.put(statusRecord, tmRecord);
		currentTransIDRecord.put(statusRecord, sb);
		
		logger.info("CrossDB_Impl.openExtraTM tm >>> "+sb.toString()+" >>> open transaction finish");
		return statusRecord;
	}

	@Override
	public void commit(List<TransactionStatus> statusRecord) {
		logger.info("CrossDB_Impl.commit begin");
		
		StringBuffer tmRecord = currentTransIDRecord.get(statusRecord);
		executeTrans(statusRecord, true);
		
		logger.info("CrossDB_Impl.commit tm >>> "+tmRecord.toString()+" >>> commit finish");
		
		removeTransCatch(statusRecord);
	}

	@Override
	public void rollback(List<TransactionStatus> statusRecord,RuntimeException re) {
		logger.info("CrossDB_Impl.rollback begin");
		
		StringBuffer tmRecord = currentTransIDRecord.get(statusRecord);	
		executeTrans(statusRecord, false);
		
		logger.info("CrossDB_Impl.rollback tm >>> "+tmRecord.toString()+" >>> roll back finish");
		
		removeTransCatch(statusRecord);
		
		//再拋出 RuntimeException 給呼叫者處理
		throw new RuntimeException(re);
	}
	
	/**
	 * 依據 isTransactionSuccess 執行 commit or rollBack
	 * @param statusRecord
	 * @param isTransactionSuccess
	 */
	private void executeTrans(List<TransactionStatus> statusRecord,boolean isTransactionSuccess){
		List<PlatformTransactionManager> tmRecord = currentTransEntry.get(statusRecord);
		int count = tmRecord.size();
		PlatformTransactionManager tm;
		TransactionStatus status;
		for(int i=0;i<count;i++){
			tm = tmRecord.get(i);
			status = statusRecord.get(i);
			
			if(isTransactionSuccess){
				tm.commit(status);
			}else{
				tm.rollback(status);
			}
		}
	}
	
	/**
	 * 移除辨識紀錄
	 * @param statusRecord
	 */
	private void removeTransCatch(List<TransactionStatus> statusRecord){
		currentTransEntry.remove(statusRecord);
		currentTransIDRecord.remove(statusRecord);
	}
	
	//取得所有TM的ID
	private String[] getAllTmIDs(){
		List<String> tmList = new ArrayList<String>(tmMap.keySet());
		String[] resultArray = new String[tmList.size()];
		return tmList.toArray(resultArray);
	}
	
}
