package cmpObj;

import java.util.ArrayList;

import cmpObj.Crowds;
/**
 * 
 * @author Ryan
 *
 */

public class DFAMap {
	
	
//	public int crowdsNum;                     //簇数量
//	public String[][] relationMap;            //关系二维数组
//	public Crowds[] crowdsList;               //簇数组
	
	
	public ArrayList<Crowds> crowdList =new ArrayList<Crowds>();     //
	
	public ArrayList<String> finalSymble = new ArrayList<String>();     //终结符动态数组
	public ArrayList<String> notFinalSymble =new ArrayList<String>();   //非终结符动态数组
	
	
	/**
	 * 构造函数     初始化？
	 */
	DFAMap(){
		
	}
	
	/**
	 * 添加新的簇
	 * @param newCrowd 
	 * 
	 */
	public void addCrowd(Crowds newCrowd) {
//		++this.crowdsNum;
		crowdList.add(newCrowd);
		
	}
	
	/**
	 * 导出关系二维数组
	 * 
	 */
	public String[][] updateRelation() {
		int length = this.crowdList.size();
		
		String[][] relationMap = new String[length][length];
		
		for (int sourceI = 0;sourceI<this.crowdList.size();sourceI++) {
			for(int reNum = 0;reNum<crowdList.get(sourceI).relation.size();reNum++){
				String tmpRelation = crowdList.get(sourceI).relation.get(reNum);
				
				char factor = tmpRelation.charAt(1);
				String sFactor = String.valueOf(factor);                           //识别符
				
				String dstnation = tmpRelation.substring(3, tmpRelation.length()-1);
				int iDes = Integer.parseInt(dstnation);                           //目的簇编号
				
				relationMap[sourceI][iDes] = sFactor;
			}
		}
		
		
		
		return relationMap;
 		
		
	}
	/**
	 * 更新终结符 非终结符列表
	 * @param finSymble
	 * @param nFinSymble
	 */
	public void updateArray(ArrayList<String> finSymble,ArrayList<String> nFinSymble) {
		this.finalSymble = finSymble;
		this.notFinalSymble = nFinSymble;
	}
}
