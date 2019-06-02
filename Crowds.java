/**
 * 
 */
package cmpObj;

import java.util.ArrayList;


/**簇类的描述
 * @author Ryan
 *
 */


public class Crowds {
	public final static int SHIFT_IN = 1;                 //移进
	public final static int INDUCE = 0;                   //规约
	public final static int START =-2;             //开始项目
	
	
	public int index;                       //簇编号
	public int objNum;                      //簇包含的项目数目
	public ArrayList<String> obj = new ArrayList<String>();           //簇项目
	public int isShiftIn;                   //是否是移进项目
	
	public ArrayList<String> relation = new ArrayList<String>();   //与其他簇的联系
	
	/**
	 * 构造函数
	 */
	Crowds() {
		
	}
	
	/**
	 * 判断项目是否是移进
	 * @param str
	 * @return
	 */
	public int isShiftIn(String pStr) {
		int maxIndex = pStr.length();
		
		if(pStr.charAt(maxIndex-1) == '@' && pStr.charAt(maxIndex-2) == 'E') {
			return START;
		}
		else if(pStr.charAt(maxIndex-1) == '@') {
			return INDUCE;
		}
		else {
			return SHIFT_IN;
		}
	}
	
	/**
	 * 置类的isShift参数
	 * 
	 */
	public void iniShiftIn() {
		int deter = 0;                                    
		
		//每个项目都应是同一个类型
		for(int i =0;i<this.objNum;i++) {
			int sign = this.isShiftIn(this.obj.get(i));
			deter = deter + sign;
		}
		
		if(deter == 0) {
			this.isShiftIn = INDUCE;
		}
		else if(deter == this.objNum) {
			this.isShiftIn = SHIFT_IN;
		}
		else {
			this.isShiftIn = START;
		}
		
	}
	
	
	
	
	
	

}
