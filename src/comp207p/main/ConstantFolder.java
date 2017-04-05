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
		// System.out.println("====================sfdljsfjlskfjslfklsklfjsd");
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
			// System.out.println("====================sfdljsfjlskfjslfklsklfjsd");
   		if(temp2.compareTo(temp1) == 1)
			{
				// System.out.println("====================sfdljsfjlskfjslfklsklfjsd");
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


	private Number arithmeticMethod(InstructionHandle handle, Instruction instruction, ConstantPoolGen cpgen, InstructionList instList, Number val1, Number val2)
	{
	  Number arithNumber = 0;
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
	  }
		else if (instruction instanceof DMUL)
		{
	    arithNumber = val1.doubleValue() * val2.doubleValue();
	    cpgen.addDouble(arithNumber.doubleValue());
	    System.out.println("DMUL " +val1 + " * " + val2);
	  }
		else if (instruction instanceof DSUB)
		{
	    arithNumber = val1.doubleValue() - val2.doubleValue();
	    cpgen.addDouble(arithNumber.doubleValue());
	    System.out.println("DSUB " + val1 + " - " + val2);
	  }
		else if (instruction instanceof DDIV)
		{
	    arithNumber = val1.doubleValue() / val2.doubleValue();
	    cpgen.addDouble(arithNumber.doubleValue());
	    System.out.println("DDIV " + val1 + " / " + val2);
	  }
		else if (instruction instanceof FADD)
		{
	    arithNumber = val1.floatValue() + val2.floatValue();
	    cpgen.addFloat(arithNumber.floatValue());
	    System.out.println("FADD " + val1 + " + " + val2);
	  }
		else if (instruction instanceof FMUL)
		{
	    arithNumber = val1.floatValue() * val2.floatValue();
	    cpgen.addFloat(arithNumber.floatValue());
	    System.out.println("FMUL " + val1 + " * " + val2);
	  }
		else if (instruction instanceof FSUB)
		{
	    arithNumber = val1.floatValue() - val2.floatValue();
	    cpgen.addFloat(arithNumber.floatValue());
	    System.out.println("FSUB " + val1 + " - " + val2);
	  }
		else if (instruction instanceof FDIV)
		{
	    arithNumber = val1.floatValue() / val2.floatValue();
	    cpgen.addFloat(arithNumber.floatValue());
	    System.out.println("FDIV " + val1 + " / " + val2);
	  }
		else if (instruction instanceof LADD)
		{
	    arithNumber = val1.longValue() + val2.longValue();
	    cpgen.addLong(arithNumber.longValue());
	    System.out.println("LADD " + val1 + " + " + val2);
	  }
		else if (instruction instanceof LMUL)
		{
	    arithNumber = val1.longValue() * val2.longValue();
	    cpgen.addLong(arithNumber.longValue());
	    System.out.println("LMUL " + val1 + " * " + val2);
	  }
		else if (instruction instanceof LSUB)
		{
	    arithNumber = val1.longValue() - val2.longValue();
	    cpgen.addLong(arithNumber.longValue());
	    System.out.println("LSUB " + val1 + " - " + val2);
	  }
		else if (instruction instanceof LDIV)
		{
	    arithNumber = val1.longValue() / val2.longValue();
	    cpgen.addLong(arithNumber.longValue());
	    System.out.println("LDIV " + val1 + " / " + val2);
	  }
		instList.insert(handle,new LDC(cpgen.getSize() - 1));
		return arithNumber;
	}

	private static class LocalVariables{
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


		private static class ForLoops{
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
				int b = flps.size();
				System.out.println(b);
				// int[] a = flps.get(0);
				int[] a;
				for(int j = 0; j<b; j++)
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
					Integer start = goTo.getTarget().getPosition();
					Integer end = handle.getPosition();

					if(start<end) // for loop
					{
						int loadIndex = getLocalVariableIndex(instList,start);
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

	// we rewrite integer constants with 5 :)
	private void optimizeMethod(ClassGen cgen, ConstantPoolGen cpgen, Method method)
	{
		ForLoops forloops = firstMethod(cgen,cpgen,method);
		forloops.printFor(); 
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
		// InstructionHandle is a wrapper for actual Instructions
		for (InstructionHandle handle : instList.getInstructionHandles())
		{
			System.out.println(handle);
			Instruction instruction = handle.getInstruction();
			System.out.println("check in for looop _________"+forloops.checkinForloop(handle.getPosition()));
			if((instruction instanceof LDC)&&(handle.getPosition() != 0))
			{
				LDC l = (LDC) instruction;
				System.out.println("LDC");
				System.out.println(l.getValue(cpgen));
				temp1 = temp2;
				temp2 = (int)l.getValue(cpgen);
				System.out.println("temp1="+temp1+" 2 = "+temp2);
				System.out.println(handle);
				try{
					instList.delete(handle);
				}catch(TargetLostException e)
				{
					e.printStackTrace();
				}
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
			// else if(instruction instanceof ConversionInstruction) {
			// 	 System.out.println("convert");
			// 	//  temp1 = temp2;
			// 	 temp2 = convert(handle, instruction, cpgen, instList, temp2);
			//
			// }
			else if(instruction instanceof ArithmeticInstruction)
			{
				System.out.println("arith");
				Number temp3 = temp2;
				temp2 =	arithmeticMethod(handle, instruction, cpgen, instList, temp1, temp2); //handle IADD, DADD, ISUB etc
				temp1 = temp3;
				try
				{
					instList.delete(handle);
				}catch(TargetLostException e)
				{
					e.printStackTrace();
				}
			}
		}
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
