package com.test.constraint;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.CustomConstraint;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.SimpleConstraint;
import org.zkoss.zul.impl.InputElement;

/**
 * 
 * @author Kevin Liao
 */

public class BasicConstraint extends SimpleConstraint implements CustomConstraint{
	
	private static final long serialVersionUID = 20150512L;
	
	public static final String errorBorderStyle = "border-color: red";

	public BasicConstraint(String constraints){
		super(constraints);
	}

	@Override
	public void showCustomError(Component paramComponent,WrongValueException paramWrongValueException) {}
	
	/**
	 * 
	 * @param inputBox
	 * @param popup
	 * <p>是否跳出該欄位不符合檢核規則的訊息 default:true</p>
	 */
	public void setInputComponent(InputElement inputBox,final boolean popup){
		inputBox.setConstraint(this);
		inputBox.addEventListener("onBlur", new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				if(Executions.getCurrent().getDesktop().getAttribute("waring")!=null){
					Executions.getCurrent().getDesktop().removeAttribute("waring");
				}else{
					InputElement inputBox = (InputElement) event.getTarget();	
					if(!inputBox.isValid()){
						inputBox.setStyle(errorBorderStyle);
						Executions.getCurrent().getDesktop().setAttribute("waring", true);
						if(popup){
							Messagebox.show("此欄位"+inputBox.getErrorMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
						}
					}else{
						inputBox.setStyle("");
					}	
				}	
			}
		});
	}
}
