package ch.puzzle.modjprof.instrumentation;

import static org.objectweb.asm.Opcodes.ASM5;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class MethodSelectorClassVisitor extends ClassVisitor {

    static {
        System.err.println("*** " + MethodSelectorClassVisitor.class.getSimpleName() + " loaded by "
                + MethodSelectorClassVisitor.class.getClassLoader().getClass().getSimpleName());
    }

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
