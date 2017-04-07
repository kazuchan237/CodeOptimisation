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
		System.out.println("Folding If Condition");
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

		System.out.println(handle.toString());
		System.out.println("\tValue 1: " + temp1);
		System.out.println("\tValue 2: " + temp2);

		if((instruction instanceof IF_ICMPLE)||(instruction instanceof IFLE))
		{
			System.out.println(temp2.compareTo(temp1));
   		if(temp2.compareTo(temp1) == -1)
			{
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
   		if(temp2.compareTo(temp1) == 1)
			{
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
		return false;
	}


	private Number arithmeticMethod(InstructionHandle handle, Instruction instruction, ConstantPoolGen cpgen, InstructionList instList, Number val1, Number val2,int index)
	{
		System.out.println("Arithmetic Calculation");
		System.out.println(handle.toString());
		// 0 for int, 1 for double, 2 for float and 3 for long.
	  Number arithNumber = 0;
		int type = 0;
	  if (instruction instanceof IADD)
		{
	    arithNumber = val1.intValue() + val2.intValue();
	    cpgen.addInteger(arithNumber.intValue());
	    System.out.println("\tIADD " + val1 + " + " + val2);
	  }
		else if (instruction instanceof IMUL)
		{
	    arithNumber = val1.intValue() * val2.intValue();
	    cpgen.addInteger(arithNumber.intValue());
	    System.out.println("\tIMUL " + val1 + " * " + val2);
	  }
		else if (instruction instanceof ISUB)
		{
	    arithNumber = val1.intValue() - val2.intValue();
	    cpgen.addInteger(arithNumber.intValue());
	    System.out.println("\tISUB " + val1 + " - " + val2);
	  }
		else if (instruction instanceof IDIV)
		{
	    arithNumber = val1.intValue() / val2.intValue();
	    cpgen.addInteger(arithNumber.intValue());
	    System.out.println("\tIDIV " + val1 + " / " + val2);
	  }
		else if (instruction instanceof DADD)
		{
	    arithNumber = val1.doubleValue() + val2.doubleValue();
	    cpgen.addDouble(arithNumber.doubleValue());
	    System.out.println("\tDADD " + val1 + " + " + val2);
			type = 1;
	  }
		else if (instruction instanceof DMUL)
		{
	    arithNumber = val1.doubleValue() * val2.doubleValue();
	    cpgen.addDouble(arithNumber.doubleValue());
	    System.out.println("\tDMUL " +val1 + " * " + val2);
			type = 1;
	  }
		else if (instruction instanceof DSUB)
		{
	    arithNumber = val1.doubleValue() - val2.doubleValue();
	    cpgen.addDouble(arithNumber.doubleValue());
	    System.out.println("\tDSUB " + val1 + " - " + val2);
			type = 1;
	  }
		else if (instruction instanceof DDIV)
		{
	    arithNumber = val1.doubleValue() / val2.doubleValue();
	    cpgen.addDouble(arithNumber.doubleValue());
	    System.out.println("\tDDIV " + val1 + " / " + val2);
			type = 1;
	  }
		else if (instruction instanceof FADD)
		{
	    arithNumber = val1.floatValue() + val2.floatValue();
	    cpgen.addFloat(arithNumber.floatValue());
	    System.out.println("\tFADD " + val1 + " + " + val2);
			type = 2;
	  }
		else if (instruction instanceof FMUL)
		{
	    arithNumber = val1.floatValue() * val2.floatValue();
	    cpgen.addFloat(arithNumber.floatValue());
	    System.out.println("\tFMUL " + val1 + " * " + val2);
			type = 2;
	  }
		else if (instruction instanceof FSUB)
		{
	    arithNumber = val1.floatValue() - val2.floatValue();
	    cpgen.addFloat(arithNumber.floatValue());
	    System.out.println("\tFSUB " + val1 + " - " + val2);
			type = 2;
	  }
		else if (instruction instanceof FDIV)
		{
	    arithNumber = val1.floatValue() / val2.floatValue();
	    cpgen.addFloat(arithNumber.floatValue());
	    System.out.println("\tFDIV " + val1 + " / " + val2);
			type = 2;
	  }
		else if (instruction instanceof LADD)
		{
	    arithNumber = val1.longValue() + val2.longValue();
	    cpgen.addLong(arithNumber.longValue());
	    System.out.println("\tLADD " + val1 + " + " + val2);
			type = 3;
	  }
		else if (instruction instanceof LMUL)
		{
	    arithNumber = val1.longValue() * val2.longValue();
	    cpgen.addLong(arithNumber.longValue());
	    System.out.println("\tLMUL " + val1 + " * " + val2);
			type = 3;
	  }
		else if (instruction instanceof LSUB)
		{
	    arithNumber = val1.longValue() - val2.longValue();
	    cpgen.addLong(arithNumber.longValue());
	    System.out.println("\tLSUB " + val1 + " - " + val2);
			type = 3;
	  }
		else if (instruction instanceof LDIV)
		{
	    arithNumber = val1.longValue() / val2.longValue();
	    cpgen.addLong(arithNumber.longValue());
	    System.out.println("\tLDIV " + val1 + " / " + val2);
			type = 3;
	  }
		if((type == 0) || (type == 2)) {
			System.out.println("\tType LDC: "+index);
			System.out.println(instList.getInstructionHandles()[index]);
			instList.getInstructionHandles()[index].setInstruction(new LDC(cpgen.getSize() - 1));
			System.out.println(instList.getInstructionHandles()[index]);
		}
		else {
			System.out.println("\tType LDC2_W: "+index);
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
					System.out.println("load ---- ********");
					LoadInstruction loadInst = (LoadInstruction) instruction;
					int loadIndex = loadInst.getIndex();
					System.out.println("load ------ "+loadIndex);
					return loadIndex;
				}
			}
			return -1;
		}

		private int getHandleIndex(InstructionList instList, int index)
		{
			InstructionHandle[] handles = instList.getInstructionHandles();
			for(int i = 0; i<handles.length; i++)
			{
				if(handles[i].getPosition() == index)
				{
					return i;
				}
			}
			return -1;
		}

		// private int getForLoopStart(InstructionList instList, int start)
		// {
		// 	InstructionHandle[] handles = instList.getInstructionHandles();
		// 	System.out.println("startindex for getForLoopStart"+start);
		// 	int temp= 0;
		// 	for(int i = 0; i<handles.length; i++)
		// 	{
		// 		if(handles[i].getPosition() == start)
		// 		{
		// 			temp = i;
		// 			System.out.println("found ");
		// 			System.out.println(i);
		// 			break;
		// 		}
		// 	}
		// 	int count = 1;
		// 	for(int j = temp - 1 ; j >= 0; j--)
		// 	{
		// 		InstructionHandle handle = handles[j];
		// 		System.out.println(handle);
		// 		Instruction a = handle.getInstruction();
		// 		if(a instanceof ConstantPushInstruction || a instanceof LDC || a instanceof LDC2_W || a instanceof LoadInstruction)
		// 		{
		// 			count = count -1;
		// 			if(count == 0)
		// 			{
		// 				System.out.println("hello");
		// 				System.out.println(handle.getPosition());
		// 				return handle.getPosition();
		// 			}
		// 		}
		//
		// 		if(a instanceof ArithmeticInstruction)
		// 		{
		// 			count = 2;
		// 		}
		//
		// 	}
		//
		// 	return 0;
		// }

		private ForLoops firstMethod(ClassGen cgen, ConstantPoolGen cpgen, Method method)
		{
			System.out.println("-------------------- First Phase --------------------");
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
					if(target < end)
					{
						int start = goTo.getTarget().getPrev().getPosition();
						int loadIndex = getLocalVariableIndex(instList, target);
 						System.out.println("what is localindesx"+loadIndex);
						System.out.println("FirstCheck---------FOR LOOOOOOOOOOOOOOOP___________--------*******************");
						System.out.println("Start = " + start);
						System.out.println("End = " + end);
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
			public ForLoopHash(HashMap<Integer, ArrayList<Integer>> ab)
			{
				this.forloophash = ab;
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

			public HashMap<Integer, ArrayList<Integer>> replaceHash(InstructionList instList)
			{
				HashMap<Integer, ArrayList<Integer>> newHash = new HashMap<Integer, ArrayList<Integer>>();
				for(Integer key: forloophash.keySet())
				{
					ArrayList<Integer> loadIndex = forloophash.get(key);
					Integer newKey = getHandleIndex(instList,key);
					newHash.put(newKey,loadIndex);
					// System.out.println("oldkey");
					System.out.println(key);
					// System.out.println("new key");
					System.out.println(newKey);

				}
				return newHash;
			}
		}

		public ForLoopHash hashForLps(ForLoops flops, InstructionList instList)
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

			System.out.println("yohoh");

			forhash.printForlpHash();
			ForLoopHash newHash = new ForLoopHash(forhash.replaceHash(instList));
			return newHash;
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

		public void setStart(int index, int value)
		{
			ArrayList<Integer> temp = new ArrayList<Integer>();
			temp.add(value);
			temp.add(getEnd(index));
			delete.set(index, temp);
		}

		public void setEnd(int index, int value)
		{
			ArrayList<Integer> temp = new ArrayList<Integer>();
			temp.add(getStart(index));
			temp.add(value);
			delete.set(index, temp);
		}

		public void deleteShift(int deleteIndex)
		{
			for (int x = 0; x < getSize(); x++)
			{
				if (getStart(x) > deleteIndex)
					setStart(x, getStart(x) - 1);
				if (getEnd(x) > deleteIndex)
					setEnd(x, getEnd(x) - 1);
			}
		}
	}

	private DeleteTable secondMethod( ConstantPoolGen cpgen, InstructionList instList,ForLoopHash forhash) {
		System.out.println("\n-------------------- Second Phase --------------------");
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
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		int startLoop = 0;
		int endLoop = 0;
		boolean beforeLoop = false;
		boolean afterI = false;
		boolean skip = false;
		boolean skip1 = false;
	  int pushBeforeLoop = 0;
		// InstructionHandle is a wrapper for actual Instructions
		for (InstructionHandle handle : instList.getInstructionHandles())
		{
			System.out.println("INST1: " + replaceInstructionIndex);
			System.out.println(handle);
			Instruction instruction = handle.getInstruction();
			if(forhash.getList(counter)!=null)
			{
				if((forhash.getList(counter).get(0)==-1)||(forhash.getList(counter).size() == indexes.size()-1)) {
					//End of loop
					endLoop = counter;
					beforeLoop = false;
					replaceInstructionIndex = counter + 1;
					afterI = false;
					skip = false;
					System.out.println("LOOOOOOOOOOOOOOOP___________ENDS");
				}
				else if(forhash.getList(counter).size() == indexes.size()+1)
				{
					//Start of loop
					startLoop = counter;
					endLoop = startLoop;
					beforeLoop = true;
					// replaceInstructionIndex = counter;
					System.out.println("LOOOOOOOOOOOOOOOP___________STARTS");
				}
				indexes = forhash.getList(counter);
				System.out.println("StartLoopIndex: " + startLoop);
				System.out.println("EndLoopIndex: " + endLoop);
				System.out.println("Indexes: " + indexes);
				System.out.println("INST: "+replaceInstructionIndex);

			}
			else if(instruction instanceof LDC)
			{
				LDC l = (LDC) instruction;
				System.out.println("LDC");
				System.out.println(l.getValue(cpgen));
				temp1 = temp2;
				temp2 = (int)l.getValue(cpgen);
				System.out.println("temp1="+temp1+" 2 = "+temp2);
				System.out.println(handle);
			}
			else if(((instruction instanceof INVOKEVIRTUAL)||(instruction instanceof GETSTATIC)||(instruction instanceof ReturnInstruction))&&(!afterI)) {
					deleteTable.add(replaceInstructionIndex+1,counter - 1);
					int size = deleteTable.getSize();
					System.out.println("SPECIAL");
					System.out.println(size);
					System.out.println(deleteTable.getStart(size-1));
					System.out.println(deleteTable.getEnd(size-1));
					replaceInstructionIndex = counter + 1;
					System.out.println("INST: "+replaceInstructionIndex);
			}
			if(instruction instanceof IINC){
				replaceInstructionIndex = counter + 1;
				System.out.println("INST: "+replaceInstructionIndex);
			}
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
				// if(beforeLoop) {
				// 	pushBeforeLoop++;
				// }
				System.out.println("push= "+(constPush.getValue()));
				temp1 = temp2;
				temp2 = constPush.getValue();
				System.out.println("temp1 = "+temp1+"temp2 = "+temp2);

			}else if(instruction instanceof StoreInstruction)
			{
				StoreInstruction a = (StoreInstruction) instruction;

				System.out.println("storedINstruectino ------"+a.getIndex());
				if(indexes.contains(a.getIndex())){
					System.out.println("DONT TOUCH");

					// counter++;
				}
				else {
					lvt.addVariable(a.getIndex(),temp2);
				}
			}
				else if(instruction instanceof LoadInstruction) //need to load value here
			{
				LoadInstruction loadInst = (LoadInstruction) instruction;

				System.out.println("load --------- "+loadInst.getIndex());
				if(indexes.contains(loadInst.getIndex())){
					System.out.println("DONT TOUCH 2");
					skip = true;
					temp1 = 0;
					temp2 = 0;

					if(!beforeLoop) {
						afterI = true;
					}
					System.out.println("LOL: " + handle.getPrev().getPosition());
					// LoadInstruction loadInst = (LoadInstruction)handle.getPrev().getInstruction()
					if(handle.getPrev().getInstruction() instanceof StoreInstruction){
						StoreInstruction a = (StoreInstruction) handle.getPrev().getInstruction();
						if(indexes.contains(a.getIndex())) {
							deleteTable.add(replaceInstructionIndex+1,counter - 2);
							int size = deleteTable.getSize();
							System.out.println("SPECIAL");
							System.out.println(size);
							System.out.println(deleteTable.getStart(size - 1));
							System.out.println(deleteTable.getEnd(size - 1));
					}
					}
					else {
						deleteTable.add(replaceInstructionIndex+1,counter - 1);
						int size = deleteTable.getSize();
						System.out.println("SPECIAL");
						System.out.println(size);
						System.out.println(deleteTable.getStart(size - 1));
						System.out.println(deleteTable.getEnd(size - 1));
					}
						replaceInstructionIndex+=2;

					// counter++;
				}
				else {
					temp1 = temp2;
					temp2 = lvt.getVariable(loadInst.getIndex());
					System.out.println("temp1 = " + temp1+" "+temp2);
				}
			}
			else if((instruction instanceof IfInstruction)&&(temp1!=null)&&(temp2!=null)) {
				System.out.println("if");
				if(skip) {
					skip = false;
				}
				if(pushBeforeLoop == 1) {
					temp2 = 0;
				}
				if(beforeLoop) {
					beforeLoop = false;
					replaceInstructionIndex = counter + 1;
					System.out.println("If in loop condition: " + replaceInstructionIndex);
				}
				else {
					boolean answer = condition(handle, instruction, cpgen, instList, temp1, temp2, type);
					System.out.println(answer);
					int a = 0;
					if(answer == true) {
						a = 1;
					}
					cpgen.addInteger(a);
					System.out.println("INST: "+replaceInstructionIndex);
					System.out.println(instList.getInstructionHandles()[replaceInstructionIndex]);
					instList.getInstructionHandles()[replaceInstructionIndex].setInstruction(new LDC(cpgen.getSize() - 1));
					System.out.println(instList.getInstructionHandles()[replaceInstructionIndex]);
				}

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
			else if((instruction instanceof ArithmeticInstruction)&&(!skip))
			{
				System.out.println("arith");
				System.out.println("WLQ: "+instruction.getName());
				if(((instruction.getName().contains("mult"))||(instruction.getName().contains("div")))&&(skip1)){
					System.out.println("Change to 1");
					temp1 = 1;
					skip1=false;
				}
				Number temp3 = temp2;
				System.out.println(replaceInstructionIndex);
				temp2 =	arithmeticMethod(handle, instruction, cpgen, instList, temp1, temp2, replaceInstructionIndex); //handle IADD, DADD, ISUB etc
				temp1 = temp3;
			}
			else if((instruction instanceof ArithmeticInstruction)&&(skip)) {
				skip = false;
				skip1 = true;
				replaceInstructionIndex++;
			}
			counter++;
			if(beforeLoop) {
				pushBeforeLoop++;
			}
		}
		return deleteTable;
	}

	private void thirdMethod(InstructionList instList, ConstantPoolGen cpgen, DeleteTable deleteTable)
	{
		System.out.println("\n-------------------- Third Phase --------------------");

		//First pass-through replacing gotos and ifs
		for (int x = deleteTable.getSize() - 1; x >= 0; x--)
	  {
	    int start = deleteTable.getStart(x);
	    int end = deleteTable.getEnd(x);
	    for (int y = end; y >= start; y--)
	    {
	  		InstructionHandle instructionHandle = instList.getInstructionHandles()[y];
				if (instructionHandle.getInstruction() instanceof IfInstruction || instructionHandle.getInstruction() instanceof GotoInstruction)
				try{
					System.out.println("REMOVED: " + instructionHandle);
					instList.delete(instructionHandle);
					deleteTable.deleteShift(y);
				}
				catch(TargetLostException e)
				{
					e.printStackTrace();
				}
	    }
	  }

		//Second pass-through delete
	  //For each entry in the deleteTable starting at the end
	  for (int x = deleteTable.getSize() - 1; x >= 0; x--)
	  {
	    int start = deleteTable.getStart(x);
	    int end = deleteTable.getEnd(x);
	    for (int y = end; y >= start; y--)
	    {
	  		InstructionHandle instruction = instList.getInstructionHandles()[y];
				try{
					System.out.println("REMOVED: " + instruction);
					instList.delete(instruction);
				}
				catch(TargetLostException e)
				{
					e.printStackTrace();
				}
	    }
	  }
	}

	// we rewrite integer constants with 5 :)
	private void optimizeMethod(ClassGen cgen, ConstantPoolGen cpgen, Method method)
	{
		ForLoops forloops = firstMethod(cgen,cpgen,method);
		forloops.printFor();
		Code methodCode = method.getCode();

		// Now get the actualy bytecode data in byte array,
		// and use it to initialise an InstructionList
		InstructionList instList = new InstructionList(methodCode.getCode());


		ForLoopHash forhash = hashForLps(forloops,instList);
		System.out.println("new has ===");
		forhash.printForlpHash();
		// DeleteTable deleteTable = secondMethod(cgen, cpgen, method, forloops);
		// Get the Code of the method, which is a collection of bytecode instructions

		// Initialise a method generator with the original method as the baseline
		MethodGen methodGen = new MethodGen(method.getAccessFlags(), method.getReturnType(), method.getArgumentTypes(), null, method.getName(), cgen.getClassName(), instList, cpgen);
		Number temp1 = 0;
		Number temp2 = 0;
		LocalVariables lvt = new LocalVariables();
		int type = 0;
		DeleteTable deleteTable = secondMethod(cpgen, instList, forhash);

		thirdMethod(instList, cpgen, deleteTable);

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
			if (!m.toString().contains("method"))
			{
				continue;
			}
			System.out.println("\n" + m.toString());
			optimizeMethod(cgen,cpgen,m);
		}

		// we generate a new class with modifications
		// and store it in a member variable
		cgen.setConstantPool(cpgen);
		cgen.setMajor(50);
		this.optimized = cgen.getJavaClass();
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
