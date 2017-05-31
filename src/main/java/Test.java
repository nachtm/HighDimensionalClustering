/**
 * Simple program for testing changes to our wrapper around gradle that runs a class with arguments.
 * It just prints its arguments and exits.
 */
public class Test {
    public static void main(String[] args) {
        System.out.println("ARGS:");
        for (String arg : args) {
            System.out.println(arg);
        }
    }
}
