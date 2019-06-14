import exceptions.TestingException;

/** Class that performs testing and prints summary to the stdin */
public class Main {
    /** Runs testing of class with given name as command-line argument */
    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("Wrong number of arguments");
            return;
        }
        try {
            Testing.testClass(args[0]).printSummary(System.out);
        } catch (TestingException e) {
            System.out.println(e.getMessage());
        }
    }
}
