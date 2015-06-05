package ch.puzzle.modjprof;

import static org.objectweb.asm.Opcodes.ASM5;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.RETURN;

import org.objectweb.asm.MethodVisitor;

public class InstrumentMeasurementPonitsMethodVisitor extends MethodVisitor {

    static {
        System.err.println("*** InstrumentMeasurementPonitsMethodVisitor loaded by "
                + InstrumentMeasurementPonitsMethodVisitor.class.getClassLoader().getClass().getSimpleName());
    }

    private String methodName;
    private String className;
    private String methodDescriptor;

    public InstrumentMeasurementPonitsMethodVisitor(MethodVisitor methodVisitor, String className, String methodName,
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
        mv.visitMethodInsn(INVOKESTATIC, "ch/puzzle/modjprof/Profiler", "notifyEnterMethod", "(Ljava/lang/String;)V", false);
    }

    @Override
    public void visitInsn(int opcode) {
        if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
            mv.visitMethodInsn(INVOKESTATIC, "ch/puzzle/modjprof/Profiler", "notifyExitMethod", "()V", false);
        }
        super.visitInsn(opcode);
    }
}
