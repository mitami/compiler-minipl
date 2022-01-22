import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CompilerMain {
    static boolean hadError = false;
    public static void main(String[] args) {
        System.out.println("This will become a compiler.");

        if(args.length > 1) {
            System.out.println("Only one argument should be given.");
        } else {//if(args.length == 1) {
                try {
                    byte[] fileAsBytes = Files.readAllBytes(Paths.get("test.txt")); //args[0]));

                    Lexer lexer = new Lexer(new String(fileAsBytes, Charset.defaultCharset()));
                    List<Token> tokens = lexer.scanFileForTokens();

                    for (Token token : tokens) {
                        System.out.println(token);
                    }
                } catch (Exception e) {
                    System.out.println("Unable to read file: " + args[0]);
                }
        } // else {
            //System.out.println("Give a filepath as an argument.");
        // }
    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error " + where + ": " + message);
        hadError = true;
    }
}