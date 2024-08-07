import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.*;
import java.util.HashMap;


public class JackTokenizer {
    private Scanner scan; 
    private static ArrayList<String> keyWords; 
    private static String symbols; 
    private static String operations; 
    private ArrayList<String> tokens; 
    private String Jack; 
    private String TokenType; 
    private String keyWordType; 
    private char SymbolType; 
    private String Identifier;  
    private String StringValue; 
    private int IntValue; 
    private int pointer; 
    private boolean First; 

    // list of all the keywords, symbols operations and OS classes.
    static {
        keyWords = new ArrayList<String>();
        keyWords.add("do");
        keyWords.add("if");
        keyWords.add("else");
        keyWords.add("while");
        keyWords.add("return");
        keyWords.add("let");
        keyWords.add("boolean");
        keyWords.add("void");
        keyWords.add("true");
        keyWords.add("false");
        keyWords.add("null");
        keyWords.add("this");
        operations = "+-*/&|<>=";
        symbols = "{}()[].,;+-*/&|<>=-~";
        keyWords.add("class");
        keyWords.add("constructor");
        keyWords.add("function");
        keyWords.add("method");
        keyWords.add("field");
        keyWords.add("static");
        keyWords.add("var");
        keyWords.add("int");
        keyWords.add("char");
    
    }

    //opens the jack file and prepares it 
    public JackTokenizer(File file) {
        try {
            scan = new Scanner(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // it will save the jackcode in a line.
        Jack = "";
        while (scan.hasNextLine()) {
            String Line = scan.nextLine();
            while (Line.equals("") || hasComments(Line)) {
                if (hasComments(Line)) {
                    Line = removeComments(Line);
                }
                if (Line.trim().equals("")) {
                    if (scan.hasNextLine()) {
                        Line = scan.nextLine();
                    } else {
                        break;
                    }
                }
            }
            Jack += Line.trim();
        }

        // we will add all the tokens that we found
        tokens = new ArrayList<String>();
        while (Jack.length() > 0) {
            while (Jack.charAt(0) == ' ') {
                Jack = Jack.substring(1);
            }
         
            // Stores the keywords
            for (int i = 0; i < keyWords.size(); i++) {
                if (Jack.startsWith(keyWords.get(i).toString())) {
                    String keyword = keyWords.get(i).toString();
                    tokens.add(keyword);
                    Jack = Jack.substring(keyword.length());
                }

            }
            
            // Stores the symbols
            if (symbols.contains(Jack.substring(0, 1))) {
                char symbol = Jack.charAt(0);
                tokens.add(Character.toString(symbol));
                Jack = Jack.substring(1);
            }

            // Stores the integers
            else if (Character.isDigit(Jack.charAt(0))) {
                String value = Jack.substring(0, 1);
                Jack = Jack.substring(1);
                while (Character.isDigit(Jack.charAt(0))) {
                    value += Jack.substring(0, 1);
                    Jack = Jack.substring(1);
                }
                tokens.add(value);

            }

            // Stores the Strings
            else if (Jack.substring(0, 1).equals("\"")) {
                Jack = Jack.substring(1);
                String str = "\"";
                while ((Jack.charAt(0) != '\"')) {
                    str += Jack.charAt(0);
                    Jack = Jack.substring(1);
                }
                str = str + "\"";
                tokens.add(str);
                Jack = Jack.substring(1);

            }

            // Stores the identifiers
            else if (Character.isLetter(Jack.charAt(0)) || (Jack.substring(0, 1).equals("_"))) {
                String strIdentifier = Jack.substring(0, 1);
                Jack = Jack.substring(1);
                while ((Character.isLetter(Jack.charAt(0))) || (Jack.substring(0, 1).equals("_"))) {
                    strIdentifier += Jack.substring(0, 1);
                    Jack = Jack.substring(1);
                }
                tokens.add(strIdentifier);
            }

            First = true;
            pointer = 0;
        }
    }

    //Checks if we still have more tokens to consider
    public boolean haMoreTokens(){
        boolean moreTokens = false;
        if(pointer < tokens.size()){
            moreTokens = true;
        }
        return moreTokens;
    }

   //only called if hasmoretokens is true, and advances to the next token, taking into assumption it exists
    public void advance(){
        if(haMoreTokens()){
            if(!First){
                pointer++;
            }
            else if (First){
                First = false;
            }
            String current = tokens.get(pointer);
            if(keyWords.contains(current)){
                TokenType = "KEYWORD";
                keyWordType = current;
            }
            else if (symbols.contains(current)){
                SymbolType = current.charAt(0);
                TokenType = "SYMBOL"; 
            }
            else if (Character.isDigit(current.charAt(0))){
                IntValue = Integer.parseInt(current);
                TokenType = "INT_CONST";
            }
            else if (current.substring(0, 1).equals("\"")){
                TokenType = "STRING_CONST";
                StringValue = current.substring(1, current.length() - 1);
            }
            else if ((Character.isLetter(current.charAt(0))) || (current.charAt(0) == '_')) {
                TokenType = "IDENTIFIER";
                Identifier = current;
            }
        }
        else {
            return;
        }
    }


    //Returns the current token
    public String tokenType(){
        return TokenType;
    }


    //Returns the keyword, that represents the current token, called only if token is keyword
    public String keyWord() {
        return keyWordType;
    }

    //Returns the symbol which is the current token, only called if current token is indeed a symbol
    public char symbol() {
        return SymbolType;
    }

    //Returns identifier as a string that represents the current token, called only if current token is identifier
    public String identifier() {
        return Identifier;
    }

   //Returns the integer that is the current token, called only if the current token is an integer
    public int intVal() {
        return IntValue;
    }

    //Returns the stringvalue as its the current token
    public String stringVal() {
        return StringValue;
    }

    //Go backwards in the arraylist of tokens
    public void decrementPointer() {
        if (pointer > 0) {
            pointer--;
        }
    }


    //Checks if there exists in the jack file a line that is a comment
    private boolean hasComments(String str) {
        boolean HasComments = false;
        if (str.contains("//") || str.contains("/*") || str.startsWith(" *")) {
            HasComments = true;
        }
        return HasComments;

    }


    //Removes all comments from line
    private String removeComments(String str) {
        String NoComments = str;
        if (hasComments(str)) {
            int offSet;
            if (str.startsWith(" *")) {
                offSet = str.indexOf("*");
            } else if (str.contains("/*")) {
                offSet = str.indexOf("/*");
            } else {
                offSet = str.indexOf("//");
            }
            NoComments = str.substring(0, offSet).trim();

        }
        return NoComments;
    }
    
    //Checks if the symbol is an operation symbol
    public boolean isOperation() {
        for (int i = 0; i < operations.length(); i++) {
            if (operations.charAt(i) == SymbolType) {
                return true;
            }
        }
        return false;
    }

}

