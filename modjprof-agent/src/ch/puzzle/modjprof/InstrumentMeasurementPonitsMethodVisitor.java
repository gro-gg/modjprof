package ch.puzzle.modjprof;

import static org.objectweb.asm.Opcodes.ASM5;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class InstrumentMeasurementPonitsMethodVisitor extends MethodVisitor {

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
        if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) {
            mv.visitMethodInsn(INVOKESTATIC, "ch/puzzle/modjprof/Profiler", "notifyExitMethod", "()V", false);
            //            mv.visitMethodInsn(INVOKESTATIC, "ch/puzzle/modjprof/Profiler", "notifyExitMethod", "(Ljava/lang/String;)V", false);
        }
        super.visitInsn(opcode);
    }

    //    @Override
    //    public void visitEnd() {
    //        mv.visitLdcInsn("L" + className + "; " + methodName + " " + methodDescriptor);
    //        mv.visitMethodInsn(INVOKESTATIC, "ch/puzzle/modjprof/Profiler", "notifyExitMethod", "(Ljava/lang/String;)V", false);
    //        super.visitEnd();
    //    }

}
