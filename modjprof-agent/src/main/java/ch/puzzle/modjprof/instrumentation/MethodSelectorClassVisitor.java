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

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class MethodSelectorClassVisitor extends ClassVisitor {

    //    static {
    //        System.err.println("*** " + MethodSelectorClassVisitor.class.getSimpleName() + " loaded by "
    //                + MethodSelectorClassVisitor.class.getClassLoader().getClass().getSimpleName());
    //    }

    private String className;

    public MethodSelectorClassVisitor(ClassVisitor classVisitor, String className) {
        super(ASM5, classVisitor);
        this.className = className;
    }

    @Override
    public MethodVisitor visitMethod(int access, String methodName, String desc, String signature, String[] exceptions) {
        MethodVisitor parentMethodVisitor = super.visitMethod(access, methodName, desc, signature, exceptions);
        if (!methodName.equals("<init>") && !methodName.equals("<clinit>")) {
            MethodVisitor methodVisitor = new InstrumentationMethodVisitor(parentMethodVisitor, className, methodName, desc);
            return methodVisitor;
        }
        return parentMethodVisitor;
    }

}
