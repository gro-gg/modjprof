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

public class InstrumentationClassFileTransformer implements ClassFileTransformer {

    //    static {
    //        System.err.println("*** " + InstrumentationClassFileTransformer.class.getSimpleName() + " loaded by "
    //                + InstrumentationClassFileTransformer.class.getClassLoader().getClass().getSimpleName());
    //    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        if (className.startsWith("ch/puzzle") || className.startsWith("najs")
                || className.startsWith("org/jboss/as/quickstarts/greeter/web")) {
            try {
                ClassReader classReader = new ClassReader(classfileBuffer);
                ClassWriter classWriter = new AgentClassWriter(classReader, COMPUTE_FRAMES, loader);
                ClassVisitor classVisitor = new MethodSelectorClassVisitor(classWriter, className);
                if ((classReader.getAccess() & (ACC_INTERFACE + ACC_ENUM + ACC_ANNOTATION)) == 0) {
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