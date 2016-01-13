import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.SimpleConstraint;
import org.zkoss.zul.impl.InputElement;

/**
 * 
 * @author Kevin Liao
 */
public class BasicValidate {
	
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 20150410L;
	
	private List<InputElement> compList;
	
	/**
	 * 
	 * @param view
	 * @param popup
	 * <p>是否跳出該欄位不符合檢核規則的訊息 default:true</p>
	 */
	public BasicValidate(Component view,boolean popup){
		setCompList(getAllInputComponent(view));
		initInputConstraint(popup);
	}

	private List<InputElement> getAllInputComponent(Component comp){
		List<InputElement> compList = new ArrayList<InputElement>();
		for(Component tempComp:comp.getChildren()){
			if(tempComp instanceof InputElement){
				compList.add((InputElement)tempComp);
			}		
			if(tempComp.getChildren()!=null && tempComp.getChildren().size()>0){
				compList.addAll(getAllInputComponent(tempComp));
			}
		}
		return compList;
	}
	
	private void initInputConstraint(boolean popup){
		SimpleConstraint constraint = null;
		BasicConstraint KBConstraint = null;
		for(InputElement input:compList){
			if(input.getConstraint()!=null){
				constraint =  (SimpleConstraint)input.getConstraint();
				KBConstraint = new BasicConstraint(constraint.getClientConstraint().replace("'", ""));
				KBConstraint.setInputComponent(input,popup);
			}	
		}
	}
	
	public void validateInputData() throws WrongValueException{
		boolean falg = false;
		for(InputElement input:compList){
			if(!input.isValid()){
				input.setStyle(BasicConstraint.errorBorderStyle);
				falg = true;
			}		
		}
		if(falg){
			throw new WrongValueException("請核對各欄位是否輸入正確");
		}
	}

	public List<InputElement> getCompList() {
		return compList;
	}
	private void setCompList(List<InputElement> compList) {
		this.compList = compList;
	}
}
