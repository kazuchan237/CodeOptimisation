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
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.util.InstructionFinder;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.TargetLostException;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantString;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.generic.IADD;



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

	// we rewrite integer constants with 5 :)
	private void optimizeMethod(ClassGen cgen, ConstantPoolGen cpgen, Method method)
	{
		// Get the Code of the method, which is a collection of bytecode instructions
		Code methodCode = method.getCode();

		// Now get the actualy bytecode data in byte array,
		// and use it to initialise an InstructionList
		InstructionList instList = new InstructionList(methodCode.getCode());

		// Initialise a method generator with the original method as the baseline
		MethodGen methodGen = new MethodGen(method.getAccessFlags(), method.getReturnType(), method.getArgumentTypes(), null, method.getName(), cgen.getClassName(), instList, cpgen);

		// InstructionHandle is a wrapper for actual Instructions
		for (InstructionHandle handle : instList.getInstructionHandles())
		{
			System.out.println(handle);
			System.out.println("class name"+ handle.getInstruction().getClass());
			// if(handle.getInstruction() instanceof LDC)
			// {
			// 	System.out.println("LDC");
			// 	// instList.insert(handle, new LDC(5));
			// 	try{
			// 		// System.out.println("delte "+handle);
			// 		instList.delete(handle);
			// 	}catch(TargetLostException e)
			// 	{
			// 		e.printStackTrace();
			// 	}
			//
			// }
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



	private int getIntValue(InstructionHandle a, ConstantPoolGen cpgen)
	{
		Instruction instruction = a.getInstruction();
		if(instruction instanceof LDC)
		{
			LDC l = (LDC) instruction;
			int val = (int) l.getValue(cpgen);
			System.out.println("true");
			return val;
		}
		return 1;
	}


	private void testMethod(ClassGen cgen, ConstantPoolGen cpgen, Method method)
	{
		Code methodCode = method.getCode();
		InstructionList instList = new InstructionList(methodCode.getCode());
		MethodGen methodGen = new MethodGen(method.getAccessFlags(), method.getReturnType(), method.getArgumentTypes(), null, method.getName(), cgen.getClassName(), instList, cpgen);
		InstructionHandle[] handles =instList.getInstructionHandles();
		for(int i = 0; i<handles.length; i++)
		{
			InstructionHandle handle = handles[i];
			System.out.println(handle);
			System.out.println(handle.getInstruction()); 
			if(handle.getInstruction() instanceof IADD)
			{
				System.out.println("IADD");
				InstructionHandle a = handles[i-1];
				int aval = getIntValue(a, cpgen);
				try{
					instList.delete(a);
				}catch(TargetLostException e){
					e.printStackTrace();
				}

				InstructionHandle b = handles[i-2];
				int bval = getIntValue(b, cpgen);
				// System.out.println(a+"and"+b);
				try{
					instList.delete(b);
				}catch(TargetLostException e){
					e.printStackTrace();
				}

				cpgen.addInteger(aval+bval);
				instList.insert(handles[i],new LDC(cpgen.getSize()-1));
				System.out.println("a + b"+(aval+bval));
				try{
					instList.delete(handle );
				}catch(TargetLostException e){
					e.printStackTrace();
				}
			}
		}

		// IntstructionHandle[] insthandles = instList.getInstructionHandles();
		// for(int i = 0; i < insthandles.getLength(); i++){
		// 	System.out.println(insthandles[i]);
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
			// optimizeMethod(cgen,cpgen,m);
			testMethod(cgen,cpgen,m);

		}

		// we generate a new class with modifications
		// and store it in a member variable








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
