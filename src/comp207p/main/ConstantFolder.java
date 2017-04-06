package comp207p.main;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.HashMap;
import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ICONST;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.util.InstructionFinder;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.TargetLostException;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantString;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.generic.*;
import org.apache.bcel.classfile.*;



public class ConstantFolder
{
	ClassParser parser = null;
	ClassGen gen = null;

	JavaClass original = null;
	JavaClass optimized = null;

	public ConstantFolder(String classFilePath)
	{
		try{
			this.parser = new ClassParser(classFilePath);
			this.original = this.parser.parse();
			this.gen = new ClassGen(this.original);
		} catch(IOException e){
			e.printStackTrace();
		}
	}

	private boolean condition(InstructionHandle handle, Instruction instruction, ConstantPoolGen cpgen, InstructionList instList, Number temp, Number temp3, int type)
	{
			// long temp1 = temp.longValue();
			System.out.println("====================sfdljsfjlskfjslfklsklfjsd");
		BigDecimal temp2;
		BigDecimal temp1;
		if(type == 1) {
			temp2 = new BigDecimal(temp3.longValue());
      temp1 = new BigDecimal(temp.longValue());
		}
		else if(type == 2) {
			temp2 = new BigDecimal(temp3.doubleValue());
      temp1 = new BigDecimal(temp.doubleValue());
		}
		else if(type == 3) {
			temp2 = new BigDecimal(temp3.floatValue());
      temp1 = new BigDecimal(temp.floatValue());
		}
		else {
			temp2 = new BigDecimal(temp3.intValue());
      temp1 = new BigDecimal(temp.intValue());
		}

		System.out.println(temp2);
		System.out.println(temp1);
		System.out.println("====================sfdljsfjlskfjslfklsklfjsd");
		if((instruction instanceof IF_ICMPLE)||(instruction instanceof IFLE))
		{
			System.out.println("IFLE");
			System.out.println(temp2.compareTo(temp1));
   		if(temp2.compareTo(temp1) == -1)
			{
				System.out.println("TRUTH");
				return true;
			}
		}
		else if((instruction instanceof IF_ICMPEQ)||(instruction instanceof IFEQ))
		{
   		if(temp2.equals(temp1))
			{
				return true;
			}
		}
		else if((instruction instanceof IF_ICMPGE)||(instruction instanceof IFGE))
		{
			System.out.println("====================sfdljsfjlskfjslfklsklfjsd");
   		if(temp2.compareTo(temp1) == 1)
			{
				System.out.println("====================sfdljsfjlskfjslfklsklfjsd");
				return true;
			}
		}
		else if((instruction instanceof IF_ICMPGT)||(instruction instanceof IFGT))
		{
   		if(temp2.compareTo(temp1) == 1)
			{
				return true;
			}
		}
		else if((instruction instanceof IF_ICMPLT)||(instruction instanceof IFLT))
		{
   		if(temp2.compareTo(temp1) == -1)
			{
				return true;
			}
		}
		else if((instruction instanceof IF_ICMPNE)||(instruction instanceof IFNE))
		{
   		if(!temp2.equals(temp1))
			{
				return true;
			}
		}
		// else if(instruction instanceof IFNONNULL) {
		// 	if(temp2 != null) {
		// 		return true;
		// 	}
		// }
		// else if(instruction instanceof IFNULL) {
		// 	if(temp2 == null) {
		// 		return true;
		// 	}
		// }
		return false;
	}


	private Number arithmeticMethod(InstructionHandle handle, Instruction instruction, ConstantPoolGen cpgen, InstructionList instList, Number val1, Number val2,int index)
	{
		// 0 for int, 1 for double, 2 for float and 3 for long.
	  Number arithNumber = 0;
		int type = 0;
	  if (instruction instanceof IADD)
		{
	    arithNumber = val1.intValue() + val2.intValue();
	    cpgen.addInteger(arithNumber.intValue());
	    System.out.println("IADD " + val1 + " + " + val2);
	  }
		else if (instruction instanceof IMUL)
		{
	    arithNumber = val1.intValue() * val2.intValue();
	    cpgen.addInteger(arithNumber.intValue());
	    System.out.println("IMUL " + val1 + " * " + val2);
	  }
		else if (instruction instanceof ISUB)
		{
	    arithNumber = val1.intValue() - val2.intValue();
	    cpgen.addInteger(arithNumber.intValue());
	    System.out.println("ISUB " + val1 + " - " + val2);
	  }
		else if (instruction instanceof IDIV)
		{
	    arithNumber = val1.intValue() / val2.intValue();
	    cpgen.addInteger(arithNumber.intValue());
	    System.out.println("IDIV " + val1 + " / " + val2);
	  }
		else if (instruction instanceof DADD)
		{
	    arithNumber = val1.doubleValue() + val2.doubleValue();
	    cpgen.addDouble(arithNumber.doubleValue());
	    System.out.println("DADD " + val1 + " + " + val2);
			type = 1;
	  }
		else if (instruction instanceof DMUL)
		{
	    arithNumber = val1.doubleValue() * val2.doubleValue();
	    cpgen.addDouble(arithNumber.doubleValue());
	    System.out.println("DMUL " +val1 + " * " + val2);
			type = 1;
	  }
		else if (instruction instanceof DSUB)
		{
	    arithNumber = val1.doubleValue() - val2.doubleValue();
	    cpgen.addDouble(arithNumber.doubleValue());
	    System.out.println("DSUB " + val1 + " - " + val2);
			type = 1;
	  }
		else if (instruction instanceof DDIV)
		{
	    arithNumber = val1.doubleValue() / val2.doubleValue();
	    cpgen.addDouble(arithNumber.doubleValue());
	    System.out.println("DDIV " + val1 + " / " + val2);
			type = 1;
	  }
		else if (instruction instanceof FADD)
		{
	    arithNumber = val1.floatValue() + val2.floatValue();
	    cpgen.addFloat(arithNumber.floatValue());
	    System.out.println("FADD " + val1 + " + " + val2);
			type = 2;
	  }
		else if (instruction instanceof FMUL)
		{
	    arithNumber = val1.floatValue() * val2.floatValue();
	    cpgen.addFloat(arithNumber.floatValue());
	    System.out.println("FMUL " + val1 + " * " + val2);
			type = 2;
	  }
		else if (instruction instanceof FSUB)
		{
	    arithNumber = val1.floatValue() - val2.floatValue();
	    cpgen.addFloat(arithNumber.floatValue());
	    System.out.println("FSUB " + val1 + " - " + val2);
			type = 2;
	  }
		else if (instruction instanceof FDIV)
		{
	    arithNumber = val1.floatValue() / val2.floatValue();
	    cpgen.addFloat(arithNumber.floatValue());
	    System.out.println("FDIV " + val1 + " / " + val2);
			type = 2;
	  }
		else if (instruction instanceof LADD)
		{
	    arithNumber = val1.longValue() + val2.longValue();
	    cpgen.addLong(arithNumber.longValue());
	    System.out.println("LADD " + val1 + " + " + val2);
			type = 3;
	  }
		else if (instruction instanceof LMUL)
		{
	    arithNumber = val1.longValue() * val2.longValue();
	    cpgen.addLong(arithNumber.longValue());
	    System.out.println("LMUL " + val1 + " * " + val2);
			type = 3;
	  }
		else if (instruction instanceof LSUB)
		{
	    arithNumber = val1.longValue() - val2.longValue();
	    cpgen.addLong(arithNumber.longValue());
	    System.out.println("LSUB " + val1 + " - " + val2);
			type = 3;
	  }
		else if (instruction instanceof LDIV)
		{
	    arithNumber = val1.longValue() / val2.longValue();
	    cpgen.addLong(arithNumber.longValue());
	    System.out.println("LDIV " + val1 + " / " + val2);
			type = 3;
	  }
		// instList.insert(handle,new LDC(cpgen.getSize() - 1));
		// if((type == 0)||(type==2)) {
		// 	instList.getInstructionHandles()[0].setInstruction(new LDC(cpgen.getSize() - 1));
		// }
		// else {
		// 	instList.getInstructionHandles()[0].setInstruction(new LDC2_W(cpgen.getSize() - 1));
		// }
		if((type == 0)||(type==2)) {
			System.out.println("Type 0: "+index);
			System.out.println(instList.getInstructionHandles()[index]);
			instList.getInstructionHandles()[index].setInstruction(new LDC(cpgen.getSize() - 1));
			System.out.println(instList.getInstructionHandles()[index]);
		}
		else {
			System.out.println("Type Other: "+index);
			System.out.println(instList.getInstructionHandles()[index]);
			instList.getInstructionHandles()[index].setInstruction(new LDC2_W(cpgen.getSize() - 2));
			System.out.println(instList.getInstructionHandles()[index]);
		}
		return arithNumber;
	}

	private static class LocalVariables
	{
		private HashMap<Integer, Number> lvt;
		public LocalVariables()
		{
			this.lvt = new HashMap<Integer,Number>();
		}
		public void addVariable(Integer index, Number value)
		{
			lvt.put(index,value);
		}
		public Number getVariable(Integer index)
		{
			return lvt.get(index);
		}
	}


	private static class ForLoops
	{
		ArrayList<int[]> flps = new ArrayList<int[]>();
		// private static ArrayList<Integer[]> flps = new ArrayList<Integer[]>();
		public ForLoops()
		{
		}
		public void addForLoop(int start, int end, int loadIndex)
		{
			int[] a = new int[3];
			a[0] = start;
			a[1] = end;
			a[2] = loadIndex;
			flps.add(a);
		}
		public boolean checkEmpty()
		{
			return flps.isEmpty();
		}
		public int getSize()
		{
			return flps.size();
		}
		public void printFor()
		{
			int[] a;
			for(int j = 0; j<getSize(); j++)
			{
					a = flps.get(0);
					System.out.println(a[0]);
					System.out.println(a[1]);
					System.out.println(a[2]);
			}
		}
		public boolean checkinForloop(int value) // checks if instruction index is inside the for loop
		{
					int[] a;
					int start;
					int end;
					for(int j = 0; j<this.getSize(); j++)
					{
						a = flps.get(j);
					start = a[0];
					end = a[1];
					if((value >= start) && (value <= end))
					{
						return true;
					}
				}
				return false;
			}
	}

		private int getLocalVariableIndex(InstructionList instList, int start)
		{
			for(InstructionHandle handle : instList.getInstructionHandles())
			{
				Instruction instruction = handle.getInstruction();
				if(handle.getPosition() == start)
				{
					System.out.println("load ----- ********");
					LoadInstruction loadInst = (LoadInstruction) instruction;
					int loadIndex = loadInst.getIndex();
					System.out.println("load --------- "+loadIndex);
					return loadIndex;
				}
			}
			return -1;
		}

		private int getForLoopStart(InstructionList instList, int start)
		{
			InstructionHandle[] handles = instList.getInstructionHandles();
			System.out.println("startindex for getForLoopStart"+start);
			int temp= 0;
			for(int i = 0; i<handles.length; i++)
			{
				if(handles[i].getPosition() == start)
				{
					temp = i;
					System.out.println("found ");
					System.out.println(i);
					break;
				}
			}
			for(int j = temp; j>=0; j--)
			{
				InstructionHandle handle = handles[j];
				System.out.println(handle);
				if(handle.getInstruction() instanceof ConstantPushInstruction)
				{
					System.out.println("hello");
					System.out.println(handle.getPosition());
					return handle.getPosition();
				}

			}

			return 0;
		}

		private ForLoops firstMethod(ClassGen cgen, ConstantPoolGen cpgen, Method method)
		{
			System.out.println("first go");
			System.out.println(method.toString());
			Code methodCode = method.getCode();
			InstructionList instList = new InstructionList(methodCode.getCode());
			MethodGen methodGen = new MethodGen(method.getAccessFlags(), method.getReturnType(), method.getArgumentTypes(), null, method.getName(), cgen.getClassName(), instList, cpgen);
			ForLoops forloops = new ForLoops();

			for(InstructionHandle handle : instList.getInstructionHandles())
			{
				Instruction instruction = handle.getInstruction();
				if(instruction instanceof GotoInstruction)
				{
					System.out.println("GOTO_)_____");
					GotoInstruction goTo = (GotoInstruction) instruction;
					Integer target = goTo.getTarget().getPosition();
					Integer end = handle.getPosition();
					if(target<end)
					{
						int start = getForLoopStart(instList,target);
						int loadIndex = getLocalVariableIndex(instList,target);
 						System.out.println("what is localindesx"+loadIndex);
						System.out.println("FirstCheck------------------FOR LOOOOOOOOOOOOOOOP___________-------*******************");
						System.out.println("start = "+start);
						System.out.println("end = "+end);
						forloops.addForLoop(start,end,loadIndex);
 						forloops.printFor();
					}
				}
			}
			return forloops;
		}


		private class ForLoopHash
		{
			private HashMap<Integer, ArrayList<Integer>> forloophash;
			public ForLoopHash(ForLoops flops)
			{
				forloophash = new HashMap<Integer, ArrayList<Integer>>();
				ArrayList<int[]> flps = flops.flps;
				for(int[] a : flps)
				{
					ArrayList<Integer> temp1 = new ArrayList<Integer>();
					ArrayList<Integer> temp2 = new ArrayList<Integer>();
					temp1.add(-1);
					temp2.add(-1);
					forloophash.put(a[0],temp1);
					forloophash.put(a[1],temp2);
				}
			}
			public void printForlpHash()
			{
				// System.out.println("****************key = ");
				for(Integer key: forloophash.keySet())
				{
					System.out.println(key);
					for(Integer temp : forloophash.get(key))
					{
						System.out.println(temp);
					}
				}
			}
			public boolean keyExists(Integer key)
			{
				return forloophash.containsKey(key);
			}
			public void addHash(Integer key, ArrayList<Integer> list)
			{
				forloophash.put(key,list);
			}
			public ArrayList<Integer> getList(Integer key)
			{
				return forloophash.get(key);
			}

		}

		public ForLoopHash hashForLps(ForLoops flops)
		{
			ForLoops forloops = flops;
			ForLoopHash forhash = new ForLoopHash(flops);
			for(int[] a : forloops.flps)
			{
				Integer temp1 = a[0];
				Integer temp2 = a[1];
				for(int i = temp1; i< temp2; i++)
				{
					// System.out.println(i);
					if(forhash.keyExists(i))
					{
						System.out.println("keyexists");
						System.out.println(i);
						ArrayList<Integer> loadIndexes = forhash.getList(i);
						if(loadIndexes.get(0) == -1)
						{
							System.out.println("minus");
							loadIndexes.add(a[2]);
							loadIndexes.remove(0);
							System.out.println(loadIndexes.get(0));
						}else
						{
							loadIndexes.add(a[2]);
						}
						forhash.addHash(i,loadIndexes);
					}
				}
			}

			forhash.printForlpHash();
			return forhash;
		}




	private static class DeleteTable {
		private ArrayList<ArrayList<Integer>> delete = new ArrayList<ArrayList<Integer>>();

		public void add(int start, int end) {
			ArrayList<Integer> temp = new ArrayList<Integer>();
			temp.add(start);
			temp.add(end);
			delete.add(temp);
		}

		public int getSize() {
			return delete.size();
		}

		public int getStart(int index) {
			return delete.get(index).get(0);
		}

		public int getEnd(int index) {
			return delete.get(index).get(1);
		}
	}

	// private DeleteTable secondMethod(ClassGen cgen, ConstantPoolGen cpgen, Method method, ForLoops forloops) {
	// 	System.out.println("second go");
	// 	Code methodCode = method.getCode();
	//
	// 	// Now get the actualy bytecode data in byte array,
	// 	// and use it to initialise an InstructionList
	// 	InstructionList instList = new InstructionList(methodCode.getCode());
	//
	// 	// Initialise a method generator with the original method as the baseline
	// 	MethodGen methodGen = new MethodGen(method.getAccessFlags(), method.getReturnType(), method.getArgumentTypes(), null, method.getName(), cgen.getClassName(), instList, cpgen);
	// 	Number temp1 = 0;
	// 	Number temp2 = 0;
	// 	LocalVariables lvt = new LocalVariables();
	// 	int type = 0;
	// 	DeleteTable deleteTable = new DeleteTable();
	// 	int replaceInstructionIndex = 0;
	// 	// InstructionHandle is a wrapper for actual Instructions
	// 	for (InstructionHandle handle : instList.getInstructionHandles())
	// 	{
	// 		System.out.println(handle);
	// 		Instruction instruction = handle.getInstruction();
	// 		if(instruction instanceof LDC)
	// 		{
	// 			LDC l = (LDC) instruction;
	// 			System.out.println("LDC");
	// 			System.out.println(l.getValue(cpgen));
	// 			temp1 = temp2;
	// 			temp2 = (int)l.getValue(cpgen);
	// 			System.out.println("temp1="+temp1+" 2 = "+temp2);
	// 			System.out.println(handle);
	// 		}
	// 		else if((instruction instanceof INVOKEVIRTUAL)&&(instruction instanceof GETSTATIC) &&(instruction instanceof ReturnInstruction)) {
	// 			replaceInstructionIndex++;
	// 		}
	// 		// else if() {
	// 		//
	// 		// }
	// 		else if(handle.getInstruction() instanceof LDC2_W)
	// 		{
	// 			LDC2_W l = (LDC2_W) handle.getInstruction();
	// 			System.out.println("LDC2_W");
	// 			System.out.println(l.getValue(cpgen));
	// 			temp1 = temp2;
	// 			temp2 = (Number)l.getValue(cpgen);
	// 		}
	// 		else if(instruction instanceof ConstantPushInstruction) //gets value for SIPUSH, BIPUSH etc
	// 		{
	// 			ConstantPushInstruction constPush = (ConstantPushInstruction) instruction;
	// 			System.out.println("push= "+(constPush.getValue()));
	// 			temp1 = temp2;
	// 			temp2 = constPush.getValue();
	// 			System.out.println("temp1 = "+temp1+"temp2 = "+temp2);
	//
	// 		}else if(instruction instanceof StoreInstruction)
	// 		{
	// 			StoreInstruction a = (StoreInstruction) instruction;
	// 			System.out.println("storedINstruectino ------"+a.getIndex());
	// 			lvt.addVariable(a.getIndex(),temp2);
	// 		}
	// 			else if(instruction instanceof LoadInstruction) //need to load value here
	// 		{
	// 			LoadInstruction loadInst = (LoadInstruction) instruction;
	// 			System.out.println("load --------- "+loadInst.getIndex());
	// 			temp1 = temp2;
	// 			temp2 = lvt.getVariable(loadInst.getIndex());
	// 			System.out.println("temp1 = "+temp1+" "+temp2);
	// 		}
	// 		else if((instruction instanceof IfInstruction)&&(temp1!=null)&&(temp2!=null)) {
	// 			System.out.println("if");
	// 			boolean answer = condition(handle, instruction, cpgen, instList, temp1, temp2, type);
	// 			System.out.println(answer);
	// 		}
	// 		else if(instruction instanceof LCMP) {
	// 			System.out.println("LCMP");
	// 			type = 1;
	// 		}
	// 		else if((instruction instanceof DCMPG)||(instruction instanceof DCMPL)) {
	// 			type = 2;
	// 		}
	// 		else if((instruction instanceof FCMPG)||(instruction instanceof FCMPL))
	// 		{
	// 			type = 3;
	// 		}
	// 		else if(instruction instanceof ArithmeticInstruction)
	// 		{
	// 			System.out.println("arith");
	// 			Number temp3 = temp2;
	// 			temp2 =	arithmeticMethod(handle, instruction, cpgen, instList, temp1, temp2); //handle IADD, DADD, ISUB etc
	// 			temp1 = temp3;
	// 		}
	// 	}
	// 	return deleteTable;
	// }

	private DeleteTable secondMethod( ConstantPoolGen cpgen, InstructionList instList,ForLoops forloops) {
		System.out.println("second go");
		// Code methodCode = method.getCode();
		//
		// // Now get the actualy bytecode data in byte array,
		// // and use it to initialise an InstructionList
		// InstructionList instList = new InstructionList(methodCode.getCode());
		//
		// // Initialise a method generator with the original method as the baseline
		// MethodGen methodGen = new MethodGen(method.getAccessFlags(), method.getReturnType(), method.getArgumentTypes(), null, method.getName(), cgen.getClassName(), instList, cpgen);
		Number temp1 = 0;
		Number temp2 = 0;
		LocalVariables lvt = new LocalVariables();
		int type = 0;
		DeleteTable deleteTable = new DeleteTable();
		int replaceInstructionIndex = 0;
		int counter = 0;
		// InstructionHandle is a wrapper for actual Instructions
		for (InstructionHandle handle : instList.getInstructionHandles())
		{
			System.out.println(handle);
			Instruction instruction = handle.getInstruction();
			if(instruction instanceof LDC)
			{
				// deleteTable.add(2,3);
				// System.out.println("WilliamLam");
				// System.out.println(deleteTable.getSize());
				// System.out.println(deleteTable.getStart(0));
				// System.out.println(deleteTable.getEnd(0));
				LDC l = (LDC) instruction;
				System.out.println("LDC");
				System.out.println(l.getValue(cpgen));
				temp1 = temp2;
				temp2 = (int)l.getValue(cpgen);
				System.out.println("temp1="+temp1+" 2 = "+temp2);
				System.out.println(handle);
			}
			else if((instruction instanceof INVOKEVIRTUAL)||(instruction instanceof GETSTATIC)||(instruction instanceof ReturnInstruction)) {
				// if(handle.getNext() != null){
					replaceInstructionIndex = counter + 1;
					System.out.println("INST: "+replaceInstructionIndex);
				// }
				// replaceInstructionIndex++;
			}
			// else if(instruction instanceof ) {
			//
			// }
			else if(handle.getInstruction() instanceof LDC2_W)
			{
				LDC2_W l = (LDC2_W) handle.getInstruction();
				System.out.println("LDC2_W");
				System.out.println(l.getValue(cpgen));
				temp1 = temp2;
				temp2 = (Number)l.getValue(cpgen);
			}
			else if(instruction instanceof ConstantPushInstruction) //gets value for SIPUSH, BIPUSH etc
			{
				ConstantPushInstruction constPush = (ConstantPushInstruction) instruction;
				System.out.println("push= "+(constPush.getValue()));
				temp1 = temp2;
				temp2 = constPush.getValue();
				System.out.println("temp1 = "+temp1+"temp2 = "+temp2);

			}else if(instruction instanceof StoreInstruction)
			{
				StoreInstruction a = (StoreInstruction) instruction;
				System.out.println("storedINstruectino ------"+a.getIndex());
				lvt.addVariable(a.getIndex(),temp2);
			}
				else if(instruction instanceof LoadInstruction) //need to load value here
			{
				LoadInstruction loadInst = (LoadInstruction) instruction;
				System.out.println("load --------- "+loadInst.getIndex());
				temp1 = temp2;
				temp2 = lvt.getVariable(loadInst.getIndex());
				System.out.println("temp1 = "+temp1+" "+temp2);
			}
			else if((instruction instanceof IfInstruction)&&(temp1!=null)&&(temp2!=null)) {
				System.out.println("if");
				boolean answer = condition(handle, instruction, cpgen, instList, temp1, temp2, type);
				System.out.println(answer);
				cpgen.addUtf8(String.valueOf(answer));
				System.out.println("INST: "+replaceInstructionIndex);
				System.out.println(instList.getInstructionHandles()[replaceInstructionIndex]);
				instList.getInstructionHandles()[replaceInstructionIndex].setInstruction(new LDC(cpgen.getSize() - 1));
				System.out.println(instList.getInstructionHandles()[replaceInstructionIndex]);

			}
			else if(instruction instanceof LCMP) {
				System.out.println("LCMP");
				type = 1;
			}
			else if((instruction instanceof DCMPG)||(instruction instanceof DCMPL)) {
				type = 2;
			}
			else if((instruction instanceof FCMPG)||(instruction instanceof FCMPL))
			{
				type = 3;
			}
			else if(instruction instanceof ArithmeticInstruction)
			{
				System.out.println("arith");
				Number temp3 = temp2;
				System.out.println(replaceInstructionIndex);
				temp2 =	arithmeticMethod(handle, instruction, cpgen, instList, temp1, temp2, replaceInstructionIndex); //handle IADD, DADD, ISUB etc
				temp1 = temp3;
			}
			counter++;
		}


		// instList.setPositions(true);
		//
		// // set max stack/local
		// methodGen.setMaxStack();
		// methodGen.setMaxLocals();
		//
		// // generate the new method with replaced iconst
		// Method nMethod = methodGen.getMethod();
		// // replace the method in the original class
		// cgen.replaceMethod(method, nMethod);
		return deleteTable;
	}

	// we rewrite integer constants with 5 :)
	private void optimizeMethod(ClassGen cgen, ConstantPoolGen cpgen, Method method)
	{
		ForLoops forloops = firstMethod(cgen,cpgen,method);
		forloops.printFor();
		System.out.println("hello11111111");
		ForLoopHash forhash = hashForLps(forloops);
		forhash.printForlpHash();
		// DeleteTable deleteTable = secondMethod(cgen, cpgen, method, forloops);
		// Get the Code of the method, which is a collection of bytecode instructions
		System.out.println(method.toString());
		Code methodCode = method.getCode();

		// Now get the actualy bytecode data in byte array,
		// and use it to initialise an InstructionList
		InstructionList instList = new InstructionList(methodCode.getCode());

		// Initialise a method generator with the original method as the baseline
		MethodGen methodGen = new MethodGen(method.getAccessFlags(), method.getReturnType(), method.getArgumentTypes(), null, method.getName(), cgen.getClassName(), instList, cpgen);
		Number temp1 = 0;
		Number temp2 = 0;
		LocalVariables lvt = new LocalVariables();
		int type = 0;
		DeleteTable deleteTable = secondMethod(cpgen, instList, forloops);

		// InstructionHandle is a wrapper for actual Instructions
		// for (InstructionHandle handle : instList.getInstructionHandles())
		// {
		// 	System.out.println(handle);
		// 	System.out.println("check in for looop _________"+forloops.checkinForloop(handle.getPosition()));
		// 	Instruction instruction = handle.getInstruction();
		// 	if((instruction instanceof LDC)&&(handle.getPosition() != 0))
		// 	{
		// 		LDC l = (LDC) instruction;
		// 		System.out.println("LDC");
		// 		System.out.println(l.getValue(cpgen));
		// 		temp1 = temp2;
		// 		temp2 = (int)l.getValue(cpgen);
		// 		System.out.println("temp1="+temp1+" 2 = "+temp2);
		// 		System.out.println(handle);
		// 		// try{
		// 		// 	instList.delete(handle);
		// 		// }catch(TargetLostException e)
		// 		// {
		// 		// 	e.printStackTrace();
		// 		// }
		// 	}
		// 	else if(handle.getInstruction() instanceof LDC2_W)
		// 	{
		// 		LDC2_W l = (LDC2_W) handle.getInstruction();
		// 		System.out.println("LDC2_W");
		// 		System.out.println(l.getValue(cpgen));
		// 		temp1 = temp2;
		// 		temp2 = (Number)l.getValue(cpgen);
		// 	}
		// 	else if(instruction instanceof ConstantPushInstruction) //gets value for SIPUSH, BIPUSH etc
		// 	{
		// 		ConstantPushInstruction constPush = (ConstantPushInstruction) instruction;
		// 		System.out.println("push= "+(constPush.getValue()));
		// 		temp1 = temp2;
		// 		temp2 = constPush.getValue();
		// 		System.out.println("temp1 = "+temp1+"temp2 = "+temp2);
		// 		// if(handle.getPosition() != 0) {
		// 		// 	try{
		// 		// 		instList.delete(handle);
		// 		// 	}catch(TargetLostException e)
		// 		// 	{
		// 		// 		e.printStackTrace();
		// 		// 	}
		// 		// }
		// 	}else if(instruction instanceof StoreInstruction)
		// 	{
		// 		StoreInstruction a = (StoreInstruction) instruction;
		// 		System.out.println("storedINstruectino ------"+a.getIndex());
		// 		lvt.addVariable(a.getIndex(),temp2);
		// 		// if(handle.getPosition() != 0) {
		// 		// 	try{
		// 		// 		instList.delete(handle);
		// 		// 	}catch(TargetLostException e)
		// 		// 	{
		// 		// 		e.printStackTrace();
		// 		// 	}
		// 		// }
		// 		// try{
		// 		// 	instList.delete(handle);
		// 		// }catch(TargetLostException e)
		// 		// {
		// 		// 	e.printStackTrace();
		// 		// }
		// 	}
		// 		else if(instruction instanceof LoadInstruction) //need to load value here
		// 	{
		// 		LoadInstruction loadInst = (LoadInstruction) instruction;
		// 		System.out.println("load --------- "+loadInst.getIndex());
		// 		temp1 = temp2;
		// 		temp2 = lvt.getVariable(loadInst.getIndex());
		// 		System.out.println("temp1 = "+temp1+" "+temp2);
		// 		// try{
		// 		// 	instList.delete(handle);
		// 		// }catch(TargetLostException e)
		// 		// {
		// 		// 	e.printStackTrace();
		// 		// }
		// 		// if(handle.getPosition() != 0) {
		// 			// try{
		// 			// 	instList.delete(handle);
		// 			// }catch(TargetLostException e)
		// 			// {
		// 			// 	e.printStackTrace();
		// 			// }
		// 		// 	}
		// 		// }
		// 	}
		// 	else if((instruction instanceof IfInstruction)&&(temp1!=null)&&(temp2!=null)) {
		// 		System.out.println("if");
		// 		boolean answer = condition(handle, instruction, cpgen, instList, temp1, temp2, type);
		// 		System.out.println(answer);
		// 		// if(handle.getPosition() != 0) {
		// 		// 	try{
		// 		// 		instList.delete(handle);
		// 		// 	}catch(TargetLostException e)
		// 		// 	{
		// 		// 		e.printStackTrace();
		// 		// 	}
		// 		// }
		// 	}
		// 	else if(instruction instanceof LCMP) {
		// 		System.out.println("LCMP");
		// 		type = 1;
		// 	}
		// 	else if((instruction instanceof DCMPG)||(instruction instanceof DCMPL)) {
		// 		type = 2;
		// 	}
		// 	else if((instruction instanceof FCMPG)||(instruction instanceof FCMPL))
		// 	{
		// 		type = 3;
		// 	}
		// 	// else if(instruction instanceof ConversionInstruction) {
		// 	// 	 System.out.println("convert");
		// 	// 	//  temp1 = temp2;
		// 	// 	 temp2 = convert(handle, instruction, cpgen, instList, temp2);
		// 	//
		// 	// }
		// 	else if(instruction instanceof ArithmeticInstruction)
		// 	{
		// 		System.out.println("arith");
		// 		Number temp3 = temp2;
		// 		temp2 =	arithmeticMethod(handle, instruction, cpgen, instList, temp1, temp2); //handle IADD, DADD, ISUB etc
		// 		temp1 = temp3;
		// 		try
		// 		{
		// 			instList.delete(handle);
		// 		}catch(TargetLostException e)
		// 		{
		// 			e.printStackTrace();
		// 		}
		// 	}
		// 	else if(instruction instanceof ConversionInstruction) {
		// 		try
		// 		{
		// 			instList.delete(handle);
		// 		}catch(TargetLostException e)
		// 		{
		// 			e.printStackTrace();
		// 		}
		// 	}
		// }
		instList.setPositions(true);

		// set max stack/local
		methodGen.setMaxStack();
		methodGen.setMaxLocals();

		// generate the new method with replaced iconst
		Method nMethod = methodGen.getMethod();
		// replace the method in the original class
		cgen.replaceMethod(method, nMethod);

	}

	public void optimize()
	{
		ClassGen cgen = new ClassGen(original);
		ConstantPoolGen cpgen = cgen.getConstantPool();



		// Do your optimization here
		Method[] methods = cgen.getMethods();
		for (Method m : methods)
		{
			optimizeMethod(cgen,cpgen,m);
			System.out.println("");

		}

		// we generate a new class with modifications
		// and store it in a member variable

		this.optimized = cgen.getJavaClass();

		// Method[] ms = cgen.getMethods();
		// for(Method m: ms){
		// 	LocalVariableTable lvt = m.getLocalVariableTable().getLocalVariableTable();
		// 	System.out.println("yolo");
		// 	System.out.println(lvt==null);
		// 	System.out.println(lvt.getTableLength());
		// 	LocalVariable lv1 = lvt.getLocalVariable(0,0);
		// 	System.out.println("lv1 = "+lv1+" "+lv1.getName());
		// }



	}


	public void write(String optimisedFilePath)
	{
		this.optimize();

		try {
			FileOutputStream out = new FileOutputStream(new File(optimisedFilePath));
			this.optimized.dump(out);
		} catch (FileNotFoundException e) {
			// Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
	}
}
