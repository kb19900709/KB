package com.test;

import java.util.List;

import org.springframework.transaction.TransactionStatus;

/**
 * @author KB
 */
public interface CrossDB {

	/**
	 * 需要額外開啟的 Transaction manager ID
	 * @param extraOpenTxID
	 * @return
	 * @throws RuntimeException
	 */
	List<TransactionStatus> openExtraTM(String... extraOpenTxID);
	
	void commit(List<TransactionStatus> statusRecord);

	/**
	 * 使用於 openExtraTM RuntimeException 的錯誤捕捉區塊
	 * @param statusRecord
	 * @param re
	 */
	void rollback(List<TransactionStatus> statusRecord,RuntimeException re);
}
