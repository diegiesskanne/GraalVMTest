package de.eso.bytebuddy;

import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;
import net.bytebuddy.utility.OpenedClassReader;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class MethodReplaceMethodVisitor extends MethodVisitor {
  MethodReplaceMethodVisitor(MethodVisitor methodVisitor) {
    super(OpenedClassReader.ASM_API, methodVisitor);
  }

  @Override
  public void visitMethodInsn(int opCode, String owner, String name, String desc, boolean itf) {
    System.out.println(opCode + " - " + owner + " - " + name + " - " + desc + " - " + itf);

    if (opCode == Opcodes.INVOKEINTERFACE
        && owner.equals("de/eso/api/ILogger")
        && name.equals("debug")
        && desc.equals("(Ljava/lang/String;)V")) {

      System.out.println("VISIT");

      List<Type> argTypes = Arrays.asList(Type.getArgumentTypes(desc));
      Collections.reverse(argTypes);
      for (Type t : argTypes) {
        visitInsn(t.getSize() == 2 ? Opcodes.POP2 : Opcodes.POP);
      }
      visitInsn(Opcodes.POP);

      // super.visitMethodInsn(opCode, owner, name, desc, itf);

      // super.visitMethodInsn(opCode, owner, name, "(Ljava/lang/String;)V", itf);
    } else // relaying to super will reproduce the same instruction
    super.visitMethodInsn(opCode, owner, name, desc, itf);
  }
}
