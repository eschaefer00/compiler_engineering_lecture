package com.thecout.lox;
import java.util.ArrayList;
import java.util.List;
import static com.thecout.lox.TokenType.EOF;

public class Scanner {
    // declare variables
    private final String src;
    private boolean comment = false;
    private final List<Token> tks = new ArrayList<>();

    public Scanner(String src) {
        this.src = src;
    }

    public List<Token> scanLine(String l, int lineNr) {
        List<Token> returnToken = new ArrayList<>();

        String tmp = "";
        Token token;
        Token newToken = null;
        char[] chars = l.toCharArray();

        for (int i = 0; i < chars.length; i++) {

            tmp += chars[i];
            token = newToken;
            newToken = getToken(tmp, lineNr);

            // not reading the line any further as it is a comment
            if (comment) {
                token = new Token(TokenType.COMMENT, tmp, tmp, lineNr);
                returnToken.add(token);
                break;
            }

            if (token != null && token.type == TokenType.NUMBER && newToken == null && chars[i] == '.') {
                tmp += chars[++i];
                newToken = getToken(tmp, lineNr);
            }

            // if new token is null, the last token must be the greatest possible match --> then add token
            if (newToken == null && token != null) {
                returnToken.add(token);
                tmp = "";

                if (chars[i] != ' ')
                    i--;
            }

            if (i == chars.length - 1 && newToken != null) {
                returnToken.add(newToken);
            }
        }

        comment = false;
        return returnToken;
    }

    private Token getToken(String tmp, int lineNr) {

        Token token;

        if (tmp.equals("//")) {
            comment = true;
            return null;
        }

        if (tmp.matches("\\d+([.]\\d+)?"))
            return new Token(TokenType.NUMBER, tmp, Double.parseDouble(tmp), lineNr);

        if (tmp.matches("\".*\""))
            return new Token(TokenType.STRING, tmp, tmp.substring(1, tmp.length() - 1), lineNr);

        token = getKeywordToken(tmp, lineNr);
        if (token != null)
            return token;

        if (tmp.matches("([a-z]|_)([a-z]|[A-Z]|_|\\d)*"))
            return new Token(TokenType.IDENTIFIER, tmp, tmp, lineNr);


        token = getSingleCharacterToken(tmp, lineNr);
        if (token != null)
            return token;

        token = getOneOrTwoCharacterToken(tmp, lineNr);
        return token;
    }

    private Token getSingleCharacterToken(String tmp, int lineNum) {

        return switch (tmp) {
            // Single-character tokens
            case "(" -> new Token(TokenType.LEFT_PAREN, tmp, tmp, lineNum);
            case ")" -> new Token(TokenType.RIGHT_PAREN, tmp, tmp, lineNum);
            case "{" -> new Token(TokenType.LEFT_BRACE, tmp, tmp, lineNum);
            case "}" -> new Token(TokenType.RIGHT_BRACE, tmp, tmp, lineNum);
            case "," -> new Token(TokenType.COMMA, tmp, tmp, lineNum);
            case "." -> new Token(TokenType.DOT, tmp, tmp, lineNum);
            case "-" -> new Token(TokenType.MINUS, tmp, tmp, lineNum);
            case "+" -> new Token(TokenType.PLUS, tmp, tmp, lineNum);
            case ";" -> new Token(TokenType.SEMICOLON, tmp, tmp, lineNum);
            case "/" -> new Token(TokenType.SLASH, tmp, tmp, lineNum);
            case "*" -> new Token(TokenType.STAR, tmp, tmp, lineNum);
            default -> null;
        };
    }

    private Token getOneOrTwoCharacterToken(String tmp, int lineNum) {

        return switch (tmp) {
            // tokens of one or two chars
            case "!=" -> new Token(TokenType.BANG_EQUAL, tmp, tmp, lineNum);
            case "!" -> new Token(TokenType.BANG, tmp, tmp, lineNum);
            case "==" -> new Token(TokenType.EQUAL_EQUAL, tmp, tmp, lineNum);
            case "=" -> new Token(TokenType.EQUAL, tmp, tmp, lineNum);
            case ">=" -> new Token(TokenType.GREATER_EQUAL, tmp, tmp, lineNum);
            case ">" -> new Token(TokenType.GREATER, tmp, tmp, lineNum);
            case "<=" -> new Token(TokenType.LESS_EQUAL, tmp, tmp, lineNum);
            case "<" -> new Token(TokenType.LESS, tmp, tmp, lineNum);
            default -> null;
        };
    }

    private Token getKeywordToken(String tmp, int lineNum) {

        return switch (tmp) {
            // tokens of one or two chars
            case "and" -> new Token(TokenType.AND, tmp, tmp, lineNum);
            case "else" -> new Token(TokenType.ELSE, tmp, tmp, lineNum);
            case "false" -> new Token(TokenType.FALSE, tmp, tmp, lineNum);
            case "fun" -> new Token(TokenType.FUN, tmp, tmp, lineNum);
            case "for" -> new Token(TokenType.FOR, tmp, tmp, lineNum);
            case "if" -> new Token(TokenType.IF, tmp, tmp, lineNum);
            case "nil" -> new Token(TokenType.NIL, tmp, tmp, lineNum);
            case "or" -> new Token(TokenType.OR, tmp, tmp, lineNum);
            case "print" -> new Token(TokenType.PRINT, tmp, tmp, lineNum);
            case "return" -> new Token(TokenType.RETURN, tmp, tmp, lineNum);
            case "true" -> new Token(TokenType.TRUE, tmp, tmp, lineNum);
            case "var" -> new Token(TokenType.VAR, tmp, tmp, lineNum);
            case "while" -> new Token(TokenType.WHILE, tmp, tmp, lineNum);
            default -> null;
        };
    }

    public List<Token> scan() {
        String[] lines = src.split("\n");
        for (int i = 0; i < lines.length; i++) {
            tks.addAll(scanLine(lines[i], i));
        }
        tks.add(new Token(EOF, "", "", lines.length));
        return tks;
    }
}