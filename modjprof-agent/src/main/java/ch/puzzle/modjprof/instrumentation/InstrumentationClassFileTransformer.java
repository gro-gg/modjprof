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

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.Opcodes.ACC_ANNOTATION;
import static org.objectweb.asm.Opcodes.ACC_ENUM;
import static org.objectweb.asm.Opcodes.ACC_INTERFACE;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstrumentationClassFileTransformer implements ClassFileTransformer {

    static Logger LOGGER = LoggerFactory.getLogger(InstrumentationClassFileTransformer.class);

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        if (className.startsWith("ch/puzzle") || className.startsWith("najs")
                || className.startsWith("org/jboss/as/quickstarts/greeter/web")) {
            try {
                ClassReader classReader = new ClassReader(classfileBuffer);
                if ((classReader.getAccess() & (ACC_INTERFACE + ACC_ENUM + ACC_ANNOTATION)) == 0) {
                    LOGGER.debug("instrumenting class " + className);
                    ClassWriter classWriter = new AgentClassWriter(classReader, COMPUTE_FRAMES, loader);
                    ClassVisitor classVisitor = new MethodSelectorClassVisitor(classWriter, className);
                    classReader.accept(classVisitor, 0);
                    return classWriter.toByteArray();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return classfileBuffer;
    }

}
