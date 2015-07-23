/**
 * Copyright 2015 The modjprof Project Developers. See the COPYRIGHT file at the top-level directory of this distribution and at
 * https://github.com/gro-gg/modjprof/blob/master/COPYRIGHT.
 *
 * This file is part of modjprof Project. It is subject to the license terms in the LICENSE file found in the top-level directory
 * of this distribution and at https://github.com/gro-gg/modjprof/blob/master/LICENSE. No part of modjprof Project, including this
 * file, may be copied, modified, propagated, or distributed except according to the terms contained in the LICENSE file.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See LICENSE file for more details.
 */
package ch.puzzle.modjprof.instrumentation;

import static org.objectweb.asm.Opcodes.ASM5;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.RETURN;

import org.objectweb.asm.MethodVisitor;

public class InstrumentationMethodVisitor extends MethodVisitor {

    //    static {
    //        System.err.println("*** " + InstrumentationMethodVisitor.class.getSimpleName() + " loaded by "
    //                + InstrumentationMethodVisitor.class.getClassLoader().getClass().getSimpleName());
    //    }

    private String methodName;
    private String className;
    private String methodDescriptor;

    public InstrumentationMethodVisitor(MethodVisitor methodVisitor, String className, String methodName,
            String methodDescriptor) {
        super(ASM5, methodVisitor);
        this.methodName = methodName;
        this.className = className;
        this.methodDescriptor = methodDescriptor;
    }

    @Override
    public void visitCode() {
        super.visitCode();
        mv.visitLdcInsn("L" + className + "; " + methodName + " " + methodDescriptor);
        mv.visitMethodInsn(INVOKESTATIC, "ch/puzzle/modjprof/Agent", "notifyEnterMethod", "(Ljava/lang/String;)V", false);
    }

    @Override
    public void visitInsn(int opcode) {
        if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
            mv.visitMethodInsn(INVOKESTATIC, "ch/puzzle/modjprof/Agent", "notifyExitMethod", "()V", false);
        }
        super.visitInsn(opcode);
    }
}
