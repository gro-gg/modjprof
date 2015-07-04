package ch.puzzle.sample;

import org.objectweb.asm.ClassReader;

public class ClassUnderTest {

    public static void main(String[] args) {
        System.out.println("ClassUnderTest.main() called");

        @SuppressWarnings("unused")
        Class<?> clz = ClassReader.class;

        ClassA classa = new ClassA();
        classa.run();
        classa.run();
    }

}
