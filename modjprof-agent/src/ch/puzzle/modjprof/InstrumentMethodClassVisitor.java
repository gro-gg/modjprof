package ch.puzzle.modjprof;

import static org.objectweb.asm.Opcodes.ASM5;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class InstrumentMethodClassVisitor extends ClassVisitor {

    static {
        System.err.println("*** InstrumentMethodClassVisitor loaded by "
                + InstrumentMethodClassVisitor.class.getClassLoader().getClass().getSimpleName());
    }

    private String className;

    public InstrumentMethodClassVisitor(ClassVisitor classVisitor, String className) {
        super(ASM5, classVisitor);
        this.className = className;
    }

    @Override
    public MethodVisitor visitMethod(int access, String methodName, String desc, String signature, String[] exceptions) {
        MethodVisitor parentMethodVisitor = super.visitMethod(access, methodName, desc, signature, exceptions);
        if (!methodName.equals("<init>")) {
            MethodVisitor instrumentMeasurementPonitsMethodVisitor = new InstrumentMeasurementPonitsMethodVisitor(
                    parentMethodVisitor, className, methodName, desc);
            return instrumentMeasurementPonitsMethodVisitor;
        }
        return parentMethodVisitor;
    }

}
