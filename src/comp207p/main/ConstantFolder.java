package comp207p.main;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

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
import org.apache.bcel.generic.LDC;
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

	private boolean condition(InstructionHandle handle, Instruction instruction, ConstantPoolGen cpgen, InstructionList instList, Number temp, Number temp3)
	{
			long temp1 = temp.longValue();
			long temp2 = temp3.longValue();
			System.out.println(temp2);
			System.out.println(temp1);
			if(instruction instanceof IF_ICMPLE)  {
	     		if(temp2 <= temp1) {
						return true;
					}
			}
			else if(instruction instanceof IF_ICMPEQ)  {
	     		if(temp2 == temp1) {
						return true;
					}
			}
			else if(instruction instanceof IF_ICMPGE)  {
	     		if(temp2 >= temp1) {
						return true;
					}
			}
			else if(instruction instanceof IF_ICMPGT)  {
	     		if(temp2 > temp1) {
						return true;
					}
			}
			else if(instruction instanceof IF_ICMPLT)  {
	     		if(temp2 < temp1) {
						return true;
					}
			}
			else if(instruction instanceof IF_ICMPNE)  {
	     		if(temp2 != temp1) {
						return true;
					}
			}


		return false;
	}


	private Number arithmeticMethod(InstructionHandle handle, Instruction instruction, ConstantPoolGen cpgen, InstructionList instList, Number temp1, Number temp2)
	{
		Number arithNumber = 0;
		if(instruction instanceof IADD)
		{
			arithNumber = temp1.intValue()+temp2.intValue();
			cpgen.addInteger(arithNumber.intValue());
			System.out.println("iadd "+temp1+" +  "+temp2);
		}
		else if(instruction instanceof FADD)
		{
			arithNumber = temp1.floatValue()+temp2.floatValue();
			cpgen.addFloat(arithNumber.floatValue());
			System.out.println("fadd "+temp1+" +  "+temp2);
		}
		else if(instruction instanceof DADD)
		{
			arithNumber = temp1.doubleValue()+temp2.doubleValue();
			cpgen.addDouble(arithNumber.doubleValue());
			System.out.println("dadd "+temp1+" +  "+temp2);
		}
		else if(instruction instanceof ISUB)
		{
			arithNumber = temp1.intValue()-temp2.intValue();
			cpgen.addInteger(arithNumber.intValue());
			System.out.println("isub= "+temp1+" - "+temp2);
		}
		else if(instruction instanceof FSUB)
		{
			arithNumber = temp1.floatValue() - temp2.floatValue();
			cpgen.addFloat(arithNumber.floatValue());
			System.out.println("fsub "+temp1+" - "+temp2);
		}

		instList.insert(handle,new LDC(cpgen.getSize()-1));
		return arithNumber;
	}

	// we rewrite integer constants with 5 :)
	private void optimizeMethod(ClassGen cgen, ConstantPoolGen cpgen, Method method)
	{
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
		// InstructionHandle is a wrapper for actual Instructions
		for (InstructionHandle handle : instList.getInstructionHandles())
		{
			System.out.println(handle);
			Instruction instruction = handle.getInstruction();
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
			}else if(instruction instanceof LoadInstruction) //need to load value here
			{
				ConstantPool cp = cpgen.getConstantPool();
				Constant[] constants = cp.getConstantPool();
				LoadInstruction loadInst = (LoadInstruction) instruction;
				System.out.println("load --------- ");
				// int i = loadInst.produceStack(cpgen);
				if(instruction instanceof IndexedInstruction)
				{
					System.out.println("indexed______");
					IndexedInstruction a = (IndexedInstruction) instruction;
					System.out.println(a.getIndex());
				}
				if(instruction instanceof LocalVariableInstruction)
				{
					System.out.println("localvI ------******* ");
					System.out.println("index="+loadInst.getIndex());
					System.out.println(instruction.toString());

					// for(int i = 0; i<constants.length; i++)
					// {
					// 	if(constants[i] instanceof ConstantInteger)
					// 	{
					// 		ConstantInteger a = (ConstantInteger) constants[i];
					// 		System.out.println("CONSTANT INTEGER + "+i+"Value = "+a.getBytes());
					// 	}
					// 	if(constants[i] instanceof ConstantDouble)
					// 	{
					// 		ConstantDouble a = (ConstantDouble) constants[i];
					// 		System.out.println("CONSTANT Double + "+i+"Value = "+a.getBytes());
					// 	}
					// }

					// Constant a = cpgen.getConstant(loadInst.getIndex());
					// if(a instanceof ConstantInteger)
					// {
					// 	ConstantInteger b = (ConstantInteger) a;
					// 	System.out.println("integer constant");
					//   System.out.println("a = "+b.getBytes());
					// }
					// if(a instanceof ConstantDouble)
					// {
					// 	System.out.println("DOUBLE CONSTANT +++++++");
					// }
					// if(a instanceof ConstantLong)
					// {
					// 	System.out.println("LONDG CONSTANT +++++++++++++++++");
					// }
				}
			}
			else if(instruction instanceof IfInstruction) {
				System.out.println("if");
				boolean answer = condition(handle, instruction, cpgen, instList, temp1, temp2);
				System.out.println(answer);
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
