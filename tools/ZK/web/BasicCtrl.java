package com.test.web;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.select.Selectors;

import com.test.constraint.BasicValidate;

public class BasicCtrl  {
	protected BasicValidate validate;

	@AfterCompose
    public void basicAfterCompose(@ContextParam(ContextType.VIEW) Component view){
		try{
			Selectors.wireComponents(view, this, false);
		}catch(Exception e){
			e.printStackTrace();
		}
    }
	
	//common validate append by Kevin 20150410
	/**
	 * <p>初始化檢核類別，務必放在子類別 afterCompose 最後一行，中間也許有手動新增元件的可能...?</P>
	 * @param view
	 * @param popup
	 * <p>是否跳出該欄位不符合檢核規則的訊息 default:true</p>
	 */
	public void initValidate(Component view,boolean popup){
		this.validate = new BasicValidate(view,popup);
	}

	/**
	 * 檢核zul上所有有constraint屬性的input 取值是否正確
	 * @throws WrongValueException
	 */
	public void validateInputData() throws WrongValueException{
		try{
			if(this.validate!=null){
				validate.validateInputData();
			}else{
				throw new WrongValueException("Please check this ctrl`s super.validate");
			}
		}catch(WrongValueException wve){
			throw wve;
		}
	}
}
