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

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.logging.Logger;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

public class InstrumentationClassFileTransformer implements ClassFileTransformer {

    private static Logger LOGGER = Logger.getLogger(InstrumentationClassFileTransformer.class.getName());

    private static InstrumentationConfiguration config;

    public InstrumentationClassFileTransformer(File propertiesFile) {
        config = InstrumentationConfiguration.getInstance();
        config.initialize(propertiesFile);
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        // do not instrument yourself
        if (!className.startsWith("ch/puzzle/modjprof")) {
            for (String packageToInstrument : config.getPackagesToInstrument()) {
                if (className.startsWith(packageToInstrument.replaceAll("\\.", "/"))) {
                    try {
                        ClassReader classReader = new ClassReader(classfileBuffer);
                        // do not instrument interfaces, enums and annotations
                        if ((classReader.getAccess() & (ACC_INTERFACE + ACC_ENUM + ACC_ANNOTATION)) == 0) {
                            LOGGER.fine("instrumenting class " + className);
                            ClassWriter classWriter = new AgentClassWriter(classReader, COMPUTE_FRAMES, loader);
                            ClassVisitor classVisitor = new MethodSelectorClassVisitor(classWriter, className);
                            classReader.accept(classVisitor, 0);
                            return classWriter.toByteArray();
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    return classfileBuffer;
                }
            }
        }
        return classfileBuffer;
    }

}
