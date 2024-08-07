import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CompilationEngine {
    
    private FileWriter out; // the output file that we will return.
    private JackTokenizer jacktoken; // the input that contains all the tokens of the jack file.
    private boolean FirstRoutine; // check if we in the same routine or we move to new routine.
    
    public CompilationEngine(File in, File outFile) {
        try {
            jacktoken = new JackTokenizer(in);
            out = new FileWriter(outFile);
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        FirstRoutine = true;
    }

    /*
     * Compiles a complete class.
     */
    public void compileClass() {
        try {
            jacktoken.advance();
            out.write("<class>\n");
            out.write("<keyword> class </keyword>\n");
            jacktoken.advance();
            out.write("<identifier> " + jacktoken.identifier() + " </identifier>\n");
            jacktoken.advance();
            out.write("<symbol> { </symbol>\n");
            compileClassVarDec();
            compileSubRoutine();
            out.write("<symbol> } </symbol>\n");
            out.write("</class>\n");
            out.close();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void compileClassVarDec() {
        jacktoken.advance();
        try {
            while (jacktoken.keyWord().equals("static") || jacktoken.keyWord().equals("field")) {
                out.write("<classVarDec>\n");
                out.write("<keyword> " + jacktoken.keyWord() + " </keyword>\n");
                jacktoken.advance();
                if (jacktoken.tokenType().equals("IDENTIFIER")) {
                    out.write("<identifier> " + jacktoken.identifier() + " </identifier>\n");
                }
                else {
                    out.write("<keyword> " + jacktoken.keyWord() + " </keyword>\n");
                }
                jacktoken.advance();
                out.write("<identifier> " + jacktoken.identifier() + " </identifier>\n");
                jacktoken.advance();
                if (jacktoken.symbol() == ',') {
                    out.write("<symbol> , </symbol>\n");
                    jacktoken.advance();
                    out.write(("<identifier> " + jacktoken.identifier() + " </identifier>\n"));
                    jacktoken.advance();
                }
                out.write("<symbol> ; </symbol>\n");
                jacktoken.advance();
                out.write("</classVarDec>\n");
            }
            if (jacktoken.keyWord().equals("function") || jacktoken.keyWord().equals("method") || jacktoken.keyWord().equals("constructor")) {
                jacktoken.decrementPointer();
                return;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void compileSubRoutine() {
        boolean thereSubRoutines = false;
        jacktoken.advance();
        try {

            if (jacktoken.symbol() == '}' && jacktoken.tokenType().equals("SYMBOL")) {
                return;
            }
    
            if ((FirstRoutine) && (jacktoken.keyWord().equals("function") || jacktoken.keyWord().equals("method") || jacktoken.keyWord().equals("constructor"))) {
                FirstRoutine = false;
                out.write("<subroutineDec>\n");
                thereSubRoutines = true;
            }

            if (jacktoken.keyWord().equals("function") || jacktoken.keyWord().equals("method") || jacktoken.keyWord().equals("constructor")) {
                thereSubRoutines = true;
                out.write("<keyword> " + jacktoken.keyWord() + " </keyword>\n");
                jacktoken.advance();
            }
        
            if (jacktoken.tokenType().equals("IDENTIFIER")) {
                out.write("<identifier> " + jacktoken.identifier() + " </identifier>\n");
                jacktoken.advance();
            }
           
            else if (jacktoken.tokenType().equals("KEYWORD")) {
                out.write("<keyword> " + jacktoken.keyWord() + " </keyword>\n");
                jacktoken.advance();
            }
            
            if (jacktoken.tokenType().equals("IDENTIFIER")) {
                out.write("<identifier> " + jacktoken.identifier() + " </identifier>\n");
                jacktoken.advance();
            }
    
            if (jacktoken.symbol() == '(') {
                out.write("<symbol> ( </symbol>\n");
                out.write("<parameterList>\n");
                compileParameterList();
                out.write("</parameterList>\n");
                out.write("<symbol> ) </symbol>\n");

            }
            compileSubroutineBody();
            if (thereSubRoutines) {
                out.write("</subroutineBody>\n");
                out.write("</subroutineDec>\n");
                FirstRoutine = true;
            }
            compileSubRoutine();

        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
     //compiles parameters
    public void compileParameterList() {
        jacktoken.advance();
        try {
            while (!(jacktoken.tokenType().equals("SYMBOL") && jacktoken.symbol() == ')')) {
                if (jacktoken.tokenType().equals("IDENTIFIER")) {
                    out.write("<identifier> " + jacktoken.identifier() + " </identifier>\n");
                    jacktoken.advance();
                } else if (jacktoken.tokenType().equals("KEYWORD")) {
                    out.write("<keyword> " + jacktoken.keyWord() + " </keyword>\n");
                    jacktoken.advance();
                }
                else if ((jacktoken.tokenType().equals("SYMBOL")) && (jacktoken.symbol() == ',')) {
                    out.write("<symbol> , </symbol>\n");
                    jacktoken.advance();

                }
            }
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void compileSubroutineBody() {
        jacktoken.advance();
        try {
        if (jacktoken.symbol() == '{') {
            out.write("<subroutineBody>\n");
            out.write("<symbol> { </symbol>\n");
            jacktoken.advance();
        }
        while (jacktoken.keyWord().equals("var") && (jacktoken.tokenType().equals("KEYWORD"))) {
            out.write("<varDec>\n");
            jacktoken.decrementPointer();
            compileVarDec();
            out.write("</varDec>\n");
        }
        out.write("<statements>\n");
        compileStatements();
        out.write("</statements>\n");
        out.write("<symbol> " + jacktoken.symbol() + " </symbol>\n");
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Compiles Variables or Declarations
    public void compileVarDec() {
        jacktoken.advance();
        try {
            if (jacktoken.keyWord().equals("var") && (jacktoken.tokenType().equals("KEYWORD"))) {
                out.write("<keyword> var </keyword>\n");
                jacktoken.advance();
            }
            if (jacktoken.tokenType().equals("IDENTIFIER")) {
                out.write("<identifier> " + jacktoken.identifier() + " </identifier>\n");
                jacktoken.advance();
            }
            else if (jacktoken.tokenType().equals("KEYWORD")) {
                out.write("<keyword> " + jacktoken.keyWord() + " </keyword>\n");
                jacktoken.advance();
            }
            if (jacktoken.tokenType().equals("IDENTIFIER")) {
                out.write("<identifier> " + jacktoken.identifier() + " </identifier>\n");
                jacktoken.advance();
            }
            if ((jacktoken.tokenType().equals("SYMBOL")) && (jacktoken.symbol() == ',')) {
                out.write("<symbol> , </symbol>\n");
                jacktoken.advance();
                out.write(("<identifier> " + jacktoken.identifier() + " </identifier>\n"));
                jacktoken.advance();
            }
            if ((jacktoken.tokenType().equals("SYMBOL")) && (jacktoken.symbol() == ';')) {
                out.write("<symbol> ; </symbol>\n");
                jacktoken.advance();

            }
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Compiles Statements
    public void compileStatements() {
        try {
            if (jacktoken.symbol() == '}' && (jacktoken.tokenType().equals("SYMBOL"))) {
                return;
            } 
            else if (jacktoken.keyWord().equals("do") && (jacktoken.tokenType().equals("KEYWORD"))) {
                out.write("<doStatement>\n");
                compileDo();
                out.write(("</doStatement>\n"));
            } 
            else if (jacktoken.keyWord().equals("let") && (jacktoken.tokenType().equals("KEYWORD"))) {
                out.write("<letStatement>\n");
                compileLet();
                out.write(("</letStatement>\n"));
            } 
            else if (jacktoken.keyWord().equals("if") && (jacktoken.tokenType().equals("KEYWORD"))) {
                out.write("<ifStatement>\n");
                compileIf();
                out.write(("</ifStatement>\n"));
            } 
            else if (jacktoken.keyWord().equals("while") && (jacktoken.tokenType().equals("KEYWORD"))) {
                out.write("<whileStatement>\n");
                compileWhile();
                out.write(("</whileStatement>\n"));
            } 
            else if (jacktoken.keyWord().equals("return") && (jacktoken.tokenType().equals("KEYWORD"))) {
                out.write("<returnStatement>\n");
                compileReturn();
                out.write(("</returnStatement>\n"));
            }
            jacktoken.advance();
            compileStatements();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void compileDo() {
        try {
            if (jacktoken.keyWord().equals("do")) {
                out.write("<keyword> do </keyword>\n");
            }
            compileCall();
            jacktoken.advance();
            out.write("<symbol> " + jacktoken.symbol() + " </symbol>\n");
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Treats the case of call
    private void compileCall() {
        jacktoken.advance();
        try {
            out.write("<identifier> " + jacktoken.identifier() + " </identifier>\n");
            jacktoken.advance();
            if ((jacktoken.tokenType().equals("SYMBOL")) && (jacktoken.symbol() == '.')) {
                out.write("<symbol> " + jacktoken.symbol() + " </symbol>\n");
                jacktoken.advance();
                out.write("<identifier> " + jacktoken.identifier() + " </identifier>\n");
                jacktoken.advance();
                out.write("<symbol> " + jacktoken.symbol() + " </symbol>\n");
                out.write("<expressionList>\n");
                compileExpressionList();
                out.write("</expressionList>\n");
                jacktoken.advance();
                out.write("<symbol> " + jacktoken.symbol() + " </symbol>\n");
            }
            else if ((jacktoken.tokenType().equals("SYMBOL")) && (jacktoken.symbol() == '(')) {
                out.write("<symbol> " + jacktoken.symbol() + " </symbol>\n");
                out.write("<expressionList>\n");
                compileExpressionList();
                out.write("</expressionList>\n");
                jacktoken.advance();
                out.write("<symbol> " + jacktoken.symbol() + " </symbol>\n");
            }
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Treats the case of Let indicator
    public void compileLet() {
        try {
            out.write("<keyword> " + jacktoken.keyWord() + " </keyword>\n");
            jacktoken.advance();
            out.write("<identifier> " + jacktoken.identifier() + " </identifier>\n");
            jacktoken.advance();
            if ((jacktoken.tokenType().equals("SYMBOL")) && (jacktoken.symbol() == '[')) {
                out.write("<symbol> " + jacktoken.symbol() + " </symbol>\n");
                compileExpression();
                jacktoken.advance();
                if ((jacktoken.tokenType().equals("SYMBOL")) && ((jacktoken.symbol() == ']'))) {
                    out.write("<symbol> " + jacktoken.symbol() + " </symbol>\n");
                }
                jacktoken.advance();
            }
            out.write("<symbol> " + jacktoken.symbol() + " </symbol>\n");
            compileExpression();
            out.write("<symbol> " + jacktoken.symbol() + " </symbol>\n");
            jacktoken.advance();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Compiles a while Statement
    public void compileWhile() {
        try {
            out.write("<keyword> " + jacktoken.keyWord() + " </keyword>\n");
            jacktoken.advance();
            out.write("<symbol> " + jacktoken.symbol() + " </symbol>\n");
            compileExpression();
            jacktoken.advance();
            out.write("<symbol> " + jacktoken.symbol() + " </symbol>\n");
            jacktoken.advance();
            out.write("<symbol> " + jacktoken.symbol() + " </symbol>\n");
            out.write("<statements>\n");
            compileStatements();
            out.write("</statements>\n");
            out.write("<symbol> " + jacktoken.symbol() + " </symbol>\n");
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Compiles a return statement
    public void compileReturn() {
        try {
            out.write("<keyword> return </keyword>\n");
            jacktoken.advance();
            if (!((jacktoken.tokenType().equals("SYMBOL") && jacktoken.symbol() == ';'))) {
                jacktoken.decrementPointer();
                compileExpression();
            }
            if (jacktoken.tokenType().equals("SYMBOL") && jacktoken.symbol() == ';') {
                out.write("<symbol> ; </symbol>\n");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //Compiles if condition, continues if theres an else
    public void compileIf() {
        try {
            out.write("<keyword> if </keyword>\n");
            jacktoken.advance();
            out.write("<symbol> ( </symbol>\n");
            compileExpression();
            out.write("<symbol> ) </symbol>\n");
            jacktoken.advance();
            out.write("<symbol> { </symbol>\n");
            jacktoken.advance();
            out.write("<statements>\n");
            compileStatements();
            out.write("</statements>\n");
            out.write("<symbol> } </symbol>\n");
            jacktoken.advance();
            if (jacktoken.tokenType().equals("KEYWORD") && jacktoken.keyWord().equals("else")) {
                out.write("<keyword> else </keyword>\n");
                jacktoken.advance();
                out.write("<symbol> { </symbol>\n");
                jacktoken.advance();
                out.write("<statements>\n");
                compileStatements();
                out.write("</statements>\n");
                out.write("<symbol> } </symbol>\n");
            } 
            else {
                jacktoken.decrementPointer();
            }
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
   //Compiles an Expression
    public void compileExpression() {
        try {
            out.write("<expression>\n");
            compileTerm();
            while (true) {
                jacktoken.advance();
                if (jacktoken.tokenType().equals("SYMBOL") && jacktoken.isOperation()) {
                    if (jacktoken.symbol() == '<') {
                        out.write("<symbol> &lt; </symbol>\n");
                    } 
                    else if (jacktoken.symbol() == '>') {
                        out.write("<symbol> &gt; </symbol>\n");
                    } 
                    else if (jacktoken.symbol() == '&') {
                        out.write("<symbol> &amp; </symbol>\n");
                    } 
                    else {
                        out.write("<symbol> " + jacktoken.symbol() + " </symbol>\n");
                    }
                    compileTerm();
                } 
                else {
                    jacktoken.decrementPointer();
                    break;
                }
            }
            out.write("</expression>\n");
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void compileTerm() {
        try {
            out.write("<term>\n");
            jacktoken.advance();
            if (jacktoken.tokenType().equals("IDENTIFIER")) {
                String prev = jacktoken.identifier();
                jacktoken.advance();
                if (jacktoken.tokenType().equals("SYMBOL") && jacktoken.symbol() == '[') {
                    out.write("<identifier> " + prev + " </identifier>\n");
                    out.write("<symbol> [ </symbol>\n");
                    compileExpression();
                    jacktoken.advance();
                    out.write("<symbol> ] </symbol>\n");
                }
                else if (jacktoken.tokenType().equals("SYMBOL") && (jacktoken.symbol() == '(' || jacktoken.symbol() == '.')) {
                    jacktoken.decrementPointer();
                    jacktoken.decrementPointer();
                    compileCall();
                } 
                else {
                    out.write("<identifier> " + prev + " </identifier>\n");
                    jacktoken.decrementPointer();
                }
            } 
            else {
                if (jacktoken.tokenType().equals("INT_CONST")) {
                    out.write("<integerConstant> " + jacktoken.intVal() + " </integerConstant>\n");
                }
                else if (jacktoken.tokenType().equals("STRING_CONST")) {
                    out.write("<stringConstant> " + jacktoken.stringVal() + " </stringConstant>\n");
                }
                else if (jacktoken.tokenType().equals("KEYWORD") && (jacktoken.keyWord().equals("this") || jacktoken.keyWord().equals("null")
                        || jacktoken.keyWord().equals("false") || jacktoken.keyWord().equals("true"))) {
                    out.write("<keyword> " + jacktoken.keyWord() + " </keyword>\n");
                }
                else if (jacktoken.tokenType().equals("SYMBOL") && jacktoken.symbol() == '(') {
                    out.write("<symbol> " + jacktoken.symbol() + " </symbol>\n");
                    compileExpression();
                    jacktoken.advance();
                    out.write("<symbol> " + jacktoken.symbol() + " </symbol>\n");
                }
                else if (jacktoken.tokenType().equals("SYMBOL") && (jacktoken.symbol() == '-' || jacktoken.symbol() == '~')) {
                    out.write("<symbol> " + jacktoken.symbol() + " </symbol>\n");
                    compileTerm();
                }
            }
            out.write("</term>\n");
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Compiles a list of expressions
    public void compileExpressionList() {
        jacktoken.advance();
        if (jacktoken.symbol() == ')' && jacktoken.tokenType().equals("SYMBOL")) {
            jacktoken.decrementPointer();
        } 
        else {
            jacktoken.decrementPointer();
            compileExpression();
        }
        while (true) {
            jacktoken.advance();
            if (jacktoken.tokenType().equals("SYMBOL") && jacktoken.symbol() == ',') {
                try {
                    out.write("<symbol> , </symbol>\n");
                } 
                catch (IOException e) {
                    e.printStackTrace();
                }
                compileExpression();
            } 
            else {
                jacktoken.decrementPointer();
                break;
            }
        }
    }

}
