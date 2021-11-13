package schoolFile;

import java.util.ArrayList;
import java.util.Stack;

public class Propositional_Calculus {
	// remember this use v and ! as Propositional Calculus
	static Stack<PPCObject> storage = new Stack<PPCObject>();
	static Stack<PPCObject> v = new Stack<PPCObject>();
	static Stack<PPCObject> THOA = new Stack<PPCObject>();
	static Stack<PPCObject> memory = new Stack<PPCObject>();
	static Stack<PPCObject> THOARemove = new Stack<PPCObject>();
	static Stack<Integer> WayToGoal = new Stack<Integer>();
	static Stack<String> ListPartsTarget = new Stack<>();
	static int currentNumber;
	static int sizeStore;
	static boolean CheckIfGoal = false;

	public static PPCObject dequeue(Stack<PPCObject> res) {
		memory = (Stack<PPCObject>) res.clone();
		memory.remove(0);
		if (THOARemove.isEmpty()) {
			while (!res.isEmpty()) {
				THOARemove.push(res.pop());
			}
		}
		res.addAll(memory);
		PPCObject datapopup = THOARemove.pop();
		THOARemove.clear();
		memory.clear();
		return datapopup;
	}


	public static void StepProcess(ArrayList<String> DataInput, String needProve) {
		ListPartsTarget.clear();
		PPCObject resultTHOA = null, currentV = null, current = null;
		String result = "";
		NormalizationData.Normalization(DataInput);
		for (int i = 0; i < DataInput.size(); i++) {
			storage.add(new PPCObject(DataInput.get(i), i + 1));
		}
		currentNumber = storage.size() + 1;
		THOA.add(new PPCObject(needProve, currentNumber));
		// first result line
		System.out.printf("%-22s%-22s%-22s%-22s\n", "", "", "",
				"|" + THOA.get(0).groupData + "(" + THOA.get(0).NumberRule + ")");
		while (THOA.size() != 0) {
			if (currentV != null && current != null) {
				WayToGoal.add(currentV.NumberRule);
				WayToGoal.add(current.NumberRule);
			}
			System.out.println("-----------------------------------------------------------------------------");
			current = dequeue(THOA);
			findVMatch(current);
			// use this for make THOA smaller by using that parts already find when search v to Multiplication 
			for (String parts:ListPartsTarget) {
				currentV = dequeue(v);
				result = resultUV(parts, currentV.groupData, current.groupData);
				if (result.length() == 0) {
					resultTHOA = new PPCObject(result, currentNumber + 1);
					DisplayDataObject(current, currentV, resultTHOA);
					WayToGoal.add(currentV.NumberRule);
					WayToGoal.add(current.NumberRule);
					CheckIfGoal = true;
					break;
				}
				resultTHOA = new PPCObject(result, currentNumber + 1);
				THOA.add(0, resultTHOA);
				currentNumber++;
				DisplayDataObject(current, currentV, resultTHOA);
			}
			if (result.length() == 0) {
				break;
			}
		}
		DisplayWayToGoal();
	}

	// find what in storage match to push in v can use to reduce current
	public static void findVMatch(PPCObject current) {
		sizeStore = storage.size();
		String[] parts = current.groupData.split("v");
		for (String s : parts) {
			int i = 0;
			while (i < sizeStore) {
				int decisition = s.indexOf("!");
				if (decisition == -1) {
					if (storage.get(i).groupData.indexOf("!" + s) != -1) {
						ListPartsTarget.add(s);
						v.add(storage.get(i));
					}
				} else {
					if (decisition != -1) {
						String checkData = String.valueOf(s.charAt(1));
						if (storage.get(i).groupData.indexOf(checkData) != -1) {
							if (storage.get(i).groupData.indexOf("!" + checkData) == -1) {
								ListPartsTarget.add(s);
								v.add(storage.get(i));
							}
						}
					}
				}
				i++;
			}
		}
	}

	// send back result u*v
	public static String resultUV(String current, String CompareData, String originData) {
		String result = "";
		int decisition = current.indexOf("!");
		if (decisition == -1) {// this remove !a and normal a and clean other left behind
			String currentNeedReplace = current;
			result = CompareData + "v" + originData;
			result = result.replace("!" + currentNeedReplace, "");
			result = result.replace(currentNeedReplace, "");
			result = result.replace("vv", "v");
			if (result.length() == 1 && result.equals("v")) {
				result = "";
			} else {
				StringBuffer str = new StringBuffer(result);
				int check = result.lastIndexOf("v");
				if (check == result.length() - 1) {
					str.deleteCharAt(check);
				}
				check = result.indexOf("v");
				if (check == 0) {
					str.deleteCharAt(0);
				}
				result = str.toString();
			}
		} else {
			if (decisition != -1) {
				String currentNeedReplace = String.valueOf(current.charAt(1));
				result = CompareData + "v" + originData;
				result = result.replace("!" + currentNeedReplace, "");
				result = result.replace(currentNeedReplace, "");
				result = result.replace("vvv", "v");
				result = result.replace("vv", "v");
				if (result.length() == 1 && result.equals("v")) {
					result = "";
				} else {
					StringBuffer str = new StringBuffer(result);
					int check = result.lastIndexOf("v");
					if (check == result.length() - 1) {
						str.deleteCharAt(check);
					}
					check = result.indexOf("v");
					if (check == 0) {
						str.deleteCharAt(0);
					}
					result = str.toString();
				}
			}
		}
		return result;
	}

	// print process data after one cycle complete
	public static void DisplayDataObject(PPCObject u, PPCObject v, PPCObject res) {
		int sizeTHOA = THOA.size();
		String printThoa = "";
		for (int i = 0; i < sizeTHOA; i++) {
			printThoa += THOA.get(i).groupData + "(" + THOA.get(i).NumberRule + "), ";
		}
		if (printThoa.length() != 0) {
			StringBuffer str = new StringBuffer(printThoa);
			str.deleteCharAt(printThoa.length()-1);
			str.deleteCharAt(printThoa.length()-2);
			printThoa = str.toString();
		}
		if (res.groupData == "") {
			System.out.printf("%-22s%-22s%-22s%-22s\n", u.groupData + " (" + u.NumberRule + ")",
					"|" + v.groupData + " (" + v.NumberRule + ")", "|" + res.groupData, "|" + printThoa);
		} else {
			System.out.printf("%-22s%-22s%-22s%-22s\n", u.groupData + " (" + u.NumberRule + ")",
					"|" + v.groupData + " (" + v.NumberRule + ")", "|" + res.groupData + " (" + res.NumberRule + ")",
					"|" + printThoa);
		}
	}

	// print last line way to get result
	public static void DisplayWayToGoal() {
		if (THOA.size() == 0 && CheckIfGoal == false) {
			System.out.println("There is no way to find the goal");
		} else {
			System.out.println();
			System.out.print("Contradiction <-");
			while (WayToGoal.size() > 2) {
				System.out.print("(" + WayToGoal.pop() + "," + WayToGoal.pop() + ")" + " <-");
			}
			System.out.print("(" + WayToGoal.pop() + "," + WayToGoal.pop() + ")");
		}
	}
	
	// add input G1,G2...Gv into DataInput and Goal are second variable of function StepProcess
	public static void main(String[] arg) {
		ArrayList<String> DataInput = new ArrayList<>();
		DataInput.add("E=>F ");
		DataInput.add("A^B=> C ");
		DataInput.add("B=>D ");
		DataInput.add("C^D=>E ");
		DataInput.add("A");
		DataInput.add("B ");
		System.out.printf("%-22s%-22s%-22s%-22s\n", "u", "v", "result(u,v)", "THOA");
		StepProcess(DataInput, "!F");
	}
}
