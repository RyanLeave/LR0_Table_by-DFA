package cmpObj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import cmpObj.DFAMap;
import cmpObj.Crowds;

/**
 * 
 * @author Ryan
 *
 */

public class StateConstruct {
	
	final static int FINAL = 0;
	final static int NOTFINAL = 1;
	final static int SEPARATE = 2;
	final static int START_END = 3;
	final static int ERROR = -1;
	public static final int SHIFT_IN = 1; 
	public static final int INDUCE = 0;
	public static final int START = -2;
	
	/**
	 * 查询列表里是否有重复的元素.
	 * 
	 * 
	 * @param sourceList
	 * @param s
	 * @return
	 */
	public static boolean isRepet(ArrayList<String> sourceList, char s) {
		int i = 0;
		while(i<sourceList.size()) {
			if(sourceList.get(i).charAt(0) == s) {
				return true;
			}
			i++;
		}
		return false;
	}
	
	
	/**
	 * 返回单个字符的类型
	 * @param r_char
	 * @return
	 */
	
	public static int charType(char r_char) {
		if(r_char == ' '||r_char == ':'||r_char == '['||r_char == ']') {
			return SEPARATE;
		}
		else if(Character.isLowerCase(r_char) == true) {
			return FINAL;
		}
		else if(Character.isUpperCase(r_char) && r_char != 'S') {
			return NOTFINAL;
		}
		else
			return ERROR;
	}
	
	
	
	/**
	 * 从指定路径中读取DFA
	 * @param filePath
	 * @return
	 * @throws IOException 
	 */
	public static DFAMap readDFA(String filePath) throws IOException {
		
		
		String errorMes = null;
		
		DFAMap DMap = new DFAMap();
		
		File file = new File(filePath);
		String lineReader = null;
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		ArrayList<String> finalList = new ArrayList<String>();          //终结符列表
		ArrayList<String> notFinalList = new ArrayList<String>();       //非终结符列表
		
		finalList.add("#");                                           //将#添加进终结符列表
		
		//每一行即一个簇，将信息写入新的簇类
		while((lineReader = br.readLine())!=null) {
			
			ArrayList<Integer> sprateList = new ArrayList<Integer>();
			
			int lineLength = lineReader.length();
			for (int charCount = 0;charCount<lineLength;++charCount) {
				char singleChar = lineReader.charAt(charCount);       
				
				int char_type = charType(singleChar);                //   char转换成的String与String比较出的结果不同
				switch(char_type) {
					case SEPARATE:
						sprateList.add(charCount);
						break;
					case FINAL:
						if(!isRepet(finalList,singleChar)) {
							finalList.add(String.valueOf(singleChar));
						}
						break;
					case NOTFINAL:
						if(!isRepet(notFinalList,singleChar)) {
							notFinalList.add(String.valueOf(singleChar));
						}
						break;
					case ERROR:
						//...
						break;
					default:
						//...
						break;
				}					
			}

			//新建簇类 并初始化
			Crowds tempCrowd = new Crowds();
			for (int j = 0; j<sprateList.size()-1;j++) {
				if(j == 0) {
					int crowdIndex = Integer.parseInt(lineReader.substring(sprateList.get(j)+1, sprateList.get(j+1)));
					tempCrowd.index = crowdIndex;
				}
				else if(Character.isUpperCase(lineReader.charAt(sprateList.get(j)+1)) == true) {
					String tmpObj = lineReader.substring(sprateList.get(j)+1, sprateList.get(j+1));
					tempCrowd.obj.add(tmpObj);
				}
				else if(lineReader.charAt(sprateList.get(j)+1) == '<') {
					tempCrowd.relation.add(lineReader.substring(sprateList.get(j)+1, sprateList.get(j+1)));
				}
				else
					errorMes = "<"+sprateList.get(j)+","+"格式错误>";
					
			}
			tempCrowd.objNum = tempCrowd.obj.size();
			
			tempCrowd.iniShiftIn();
			
			
			DMap.addCrowd(tempCrowd);
			

			
		}

		
		DMap.updateArray(finalList, notFinalList);
		return DMap;
	}
	
	
	


	/**
	 * 将DFA转换成分析表
	 * @param DMap
	 * @return
	 */
	public static String[][] stateOut(DFAMap DMap) {
		
		int maxCrowd = DMap.crowdList.size()+1;
		int maxFinal = DMap.finalSymble.size();
		int maxNotFinal = DMap.notFinalSymble.size();
		
		int maxRow = maxFinal + maxNotFinal;
		
		String [][] anlyzeTable = new String[maxCrowd][maxRow];
		
		//初始化第一行   
		for (int i = 0; i < maxRow; i++) {
			if(i < maxFinal) {
				anlyzeTable[0][i] = DMap.finalSymble.get(i);
			}
			else {
				anlyzeTable[0][i] = DMap.notFinalSymble.get(i-maxFinal);
			}
		}
		
		String [][] relationTable = new String[maxCrowd-1][maxCrowd-1]; 
		relationTable = DMap.updateRelation();
		
		int rIndex = 1;
		
		for (int i = 0; i<maxCrowd-1; i++) {
			
			//置 acc
			if(DMap.crowdList.get(i).isShiftIn == START) {
				int s_pos = getSybPos(anlyzeTable,'#');
				anlyzeTable[i+1][s_pos] = "acc";
			}
			
			//移进项目的处理
			if(DMap.crowdList.get(i).isShiftIn == SHIFT_IN) {
				for(int j = 0; j<maxCrowd-1; j++) {
					if(relationTable[i][j] != null) {
						int pos = getSybPos(anlyzeTable,relationTable[i][j].charAt(0));
						if(pos < maxFinal) {
							anlyzeTable[i+1][pos] = "s" +j;
						}
						else {
							anlyzeTable[i+1][pos] = String.valueOf(j);
						}	
					}
				}
			}
			
			//规约项目的处理
			else if(DMap.crowdList.get(i).isShiftIn == INDUCE) {
				for(int k =0; k < maxFinal; k++) {
					anlyzeTable[i+1][k] = "r" + rIndex;
				}
				++rIndex;
			}
			
			else {
				//...
			}
		}
		return anlyzeTable;
		
	}
	
	
	/**
	 *查找数组元素的位置 
	 * @param sString  源数组
	 * @param searchS  查找的元素
	 * @return
	 */
	public static int getSybPos(String[][] pString,char a) {
		for(int i = 0; i<pString[0].length; i++) {
			String symble = pString[0][i];
			if(symble !=null&&symble.charAt(0) == a) {
				return i;
			}
		}
		return -1;
	}
	

	/**
	 * 将分析表输出到制定文件
	 * @param state
	 * @param printPath
	 * @throws IOException 
	 */
	public static void statePrint(String[][] state,String printPath,int finalNum,int notFinalNum) throws IOException {
		FileWriter fw = new FileWriter(printPath);
		
		int stateNum = state.length-1;                            //簇的数目
		int bitNum = String.valueOf(stateNum).length();
		
		int stateLen = 12;
		
		//action块的宽度
		int actLen = (1 + bitNum) * finalNum + (1+finalNum) * 3;
		if(actLen < 8) {
			actLen = 8;
		}
		String spaFill = "   ";
		
		//goto块的宽度
		int gotoLen = (3 + 1 + bitNum) * notFinalNum;
		if(gotoLen < 4) {
			gotoLen = 4;
		}
		
		String stateStr = "    " + "状态" + "    ";
		stateStr = fillSpace(stateStr,stateLen-2);
		int aPreFillNum = (actLen - 6)/2;
		String actStr = bornSpace(aPreFillNum) + "ACTION" + bornSpace(actLen - aPreFillNum - 6);
		
		int gPreFillNum = (gotoLen - 4)/2;
		String gotoStr = bornSpace(gPreFillNum) + "GOTO" + bornSpace(gotoLen - gPreFillNum - 4);
		
		String title = stateStr + "|" + actStr + "|" + gotoStr + "\r\n";                                //输出的首行
		
		String line = "";
		for(int i = 0; i < stateNum + actLen + gotoLen +2; i++) {
			line = line + "-";
		}                                                                                      //输出的分割线
		line = line + "\r\n";
		fw.write(title);
		
		fw.write(line);
		
		for(int j = 0;j<state.length;j++) {
			String index = String.valueOf(j);
			String str = "    " + fillSpace(index,4) + "    " + "|";
			
			for ( int k = 0;k<finalNum;k++) {
				String tmp  = " ";
				if(state[j][k] != null) {
					tmp = state[j][k];
				}
				tmp = fillSpace(tmp,4);
				str = str + tmp + "   ";
			}
			str = str + "|";
			
			for (int m = 0; m<notFinalNum;m++) {
				String tmp2 = " ";
				if(state[j][finalNum + m] != null) {
					tmp2 = state[j][finalNum + m];
				}
				tmp2 = fillSpace(tmp2,4);
				str = str + tmp2 + "   ";
			}
			str = str + "\r\n";
			fw.write(str);
		}
		
		
		
		
//		for (int j = 0;j<state.length;j++) {
//			String str = "";
//			str = "    " + fillSpace(String.valueOf(j),8) + "|";
//			for(int k = 0;k<finalNum;k++) {
//				String tmp = "";
//				if(state[j][k] == null) {
//					tmp =" ";
//				}
//				else {
//					tmp = state[j][k];
//				}
//				str = str + spaFill + fillSpace(tmp,1+bitNum);
//			}
//			str = str + "|";
//			for(int m = 0;m<notFinalNum;m++) {
//				String tmp = "";
//				if(state[j][finalNum + m] == null) {
//					tmp =" ";
//				}
//				else {
//					tmp = state[j][finalNum + m];
//				}
//				str = str + spaFill + fillSpace(tmp,bitNum);
//			}
//			str = str + "\r\n";
//			fw.write(str);
//		}
//		

		
		fw.close();
		
		
		
		
	}
	
	/**
	 * 填充空格
	 * @param str
	 * @param maxBit
	 * @return
	 */
	public static String fillSpace(String pstr,int maxBit) {
		
		int strBit = pstr.length();
		for(int i = strBit;i < maxBit; i++) {
			pstr = pstr + " ";
		}
		return pstr;
	}
	
	
	
	/**
	 * 
	 * @param num
	 * @return
	 */
	public static String bornSpace(int num) {
		String str="";
		for (int i = 0;i < num; i++) {
			str = str + " ";
		}
		return str;
	}
	public static void main(String[] args) throws IOException {
		// TODO 自动生成的方法存根
		String filePath = "F:/Input.txt";
		DFAMap DMap = readDFA(filePath);
		
		int finalNum = DMap.finalSymble.size();
		int notFinalNum = DMap.notFinalSymble.size();

		String [][] analyzeTable = stateOut(DMap);
		
		String printPath = "F:/OutPut.txt";
		statePrint(analyzeTable,printPath,finalNum,notFinalNum);
	}

}
