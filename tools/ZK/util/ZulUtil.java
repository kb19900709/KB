package com.test.util;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ext.Disable;

/**
 * 
 * @author Kevin Liao
 */
public class ZulUtil {
	
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 20160223L;

	/**
	 * 
	 * @param comp : 父元件
	 * @param instance : 欲搜尋之子元件class
	 * @return
	 */
	public static <T> List<T> getAllComponentByTyped(Component comp,Class<T> instance){
		List<T> compList = new ArrayList<T>();
		for(Component tempComp:comp.getChildren()){
			if(instance.isAssignableFrom(tempComp.getClass())){
				compList.add((T)tempComp);
			}
			if(tempComp.getChildren()!=null && tempComp.getChildren().size()>0){
				compList.addAll(getAllComponentByTyped(tempComp,instance));
			}
		}
		return compList;
	}
	
	/**
	 * 此元件集合設定全部不可操作(灰底)
	 * @param comps
	 * @return
	 */
	public static List<? extends Disable> setElementDisable(List<? extends Disable> comps){
		for(Disable element:comps){
			element.setDisabled(true);
		}
		return comps;
	}
	
	/**
	 * 此元件集合設定全部隱藏
	 * @param comps
	 * @return
	 */
	public static List<? extends AbstractComponent> setElementUnVisable(List<? extends AbstractComponent> comps){
		for(AbstractComponent element:comps){
			element.setVisible(false);
		}
		return comps;
	}
}