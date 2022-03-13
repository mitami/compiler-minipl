import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CompilerMain {
    private static final Interpreter interpreter = new Interpreter();

    static boolean hadError = false;
    static boolean hadRuntimeError = false;
    public static void main(String[] args) {
        System.out.println("Interpreter started.");

        if(args.length > 1) {
            System.out.println("Only one argument should be given.");
        } else {//if(args.length == 1) {
                try {
                    byte[] fileAsBytes = Files.readAllBytes(Paths.get("test.txt")); //args[0]));

                    Lexer lexer = new Lexer(new String(fileAsBytes, Charset.defaultCharset()));
                    List<Token> tokens = lexer.scanFileForTokens();

                    Parser parser = new Parser(tokens);
                    List<Statement> statements = parser.parseTokens();

                    if(hadError) return;
                    for (Token token : tokens) {
                        System.out.println(token);
                    }
                    // System.out.println(new AstPrinter().print(expression));

                    interpreter.interpretExpression(statements);
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

    static void error(Token token, String message) {
        if(token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }

    static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() + "\n[line " + error.token.line + "]");
        hadRuntimeError = true;
    }
}