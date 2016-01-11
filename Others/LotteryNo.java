package com.test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class LotteryNo {
	private static Set<Integer> randomNumber = new HashSet<Integer>();
	private static final Integer count = 6;
	private static final Integer maxNo = 46;
	
	private static Integer getRandomValue(){
		Double randomResult = java.lang.Math.random()*100;
		if(randomResult <= maxNo){
			return new BigDecimal(randomResult).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
		}
		return getRandomValue();
	}
	
	public static void main(String[] args) throws Exception{
		Integer number = null;
		while(randomNumber.size()<count){
			number = getRandomValue();
			randomNumber.add(number);
		}
		
		Iterator<Integer> randomNumberIt = randomNumber.iterator();
		int currentNo = 1;
		while(randomNumberIt.hasNext()){
			number = randomNumberIt.next();
			if(currentNo != randomNumber.size()){
				System.out.println("no"+currentNo+":"+number);
			}else{
				System.out.println("specila no:"+number);
			}
			currentNo++;
		}
	}
}
