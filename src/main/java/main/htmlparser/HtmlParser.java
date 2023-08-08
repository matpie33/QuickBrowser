package main.htmlparser;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class HtmlParser {

    private StringReader stringReader;
    private long currentPosition;
    private boolean eofEncountered;

    private int lineNumber;

    private List<Token> tokens = new ArrayList<>();

    private TokenType previousToken = TokenType.START;

    public List<Token> parse (String html ) throws IOException {
        lineNumber = 1;
        stringReader = new StringReader(html);
        char nextCharacter = readNext();
        while (!eofEncountered){
            if (nextCharacter == '<'){

                nextCharacter = readNext();
                if (nextCharacter == '!'){
                    nextCharacter = readNext();
                    previousToken = TokenType.PREAMBLE;
                    tokens.add(new Token(previousToken));
                }
                else if (nextCharacter == '/'){
                    previousToken = TokenType.TAG_END;
                    tokens.add(new Token(previousToken));
                    nextCharacter = readNext();
                }
                else{
                    previousToken = TokenType.TAG_START;
                    tokens.add(new Token(previousToken));
                }
                nextCharacter = skipSpaces(nextCharacter);
                if (eofEncountered){
                    throwError("Encountered EOF after tag opening '<'");
                }
                if (Character.isDigit(nextCharacter)){
                    throwError("Encountered digit after tag opening, expected tag name");
                }

            }
            if (Character.isLetter(nextCharacter)){
                if (previousToken.equals(TokenType.TAG_START)|| previousToken.equals(TokenType.TAG_END)){
                    nextCharacter = parseTagName(nextCharacter);
                }
                else if (previousToken.equals(TokenType.PREAMBLE)){
                    nextCharacter = parsePreamble(nextCharacter);
                }
                else if (previousToken.equals(TokenType.TAG_NAME) || previousToken.equals(TokenType.TAG_ATTRIBUTE_VALUE)){
                    nextCharacter = parseAttributeName(nextCharacter);
                }
                else if (previousToken.equals(TokenType.TAG_ATTRIBUTE_NAME)){
                    nextCharacter = parseAttributeValue (nextCharacter);
                }
                else if (previousToken.equals(TokenType.TAG_CLOSING)){
                    nextCharacter = parseTagContent (nextCharacter);
                }

            }
            if (Character.isDigit(nextCharacter)){
                if (previousToken.equals(TokenType.TAG_CLOSING)){
                    nextCharacter = parseTagContent(nextCharacter);
                }
                else if (previousToken.equals(TokenType.TAG_ATTRIBUTE_NAME)){
                    nextCharacter = parseAttributeValue(nextCharacter);
                }
                else {
                    throwError("Digit encountered not after tag closing and not after attribute name");
                }
            }
            if (nextCharacter == '>'){
                previousToken = TokenType.TAG_CLOSING;
                tokens.add(new Token(previousToken));
                nextCharacter = readNext();
                nextCharacter = skipSpaces(nextCharacter);
            }
            if (nextCharacter == '='){
                if (!previousToken.equals(TokenType.TAG_ATTRIBUTE_NAME)){
                    throwError("Equals sign encountered, should be after tag attribute name");
                }
                nextCharacter = readNext();
                nextCharacter = skipSpaces(nextCharacter);
                if (nextCharacter == '"'){
                    nextCharacter = readNext();
                    nextCharacter = skipSpaces(nextCharacter);
                }
            }

        }
        return tokens;
    }

    private char parsePreamble(char nextCharacter) throws IOException {
        StringBuilder preamble = new StringBuilder();
        while (nextCharacter != '>'){
            preamble.append(nextCharacter);
            nextCharacter = readNext();
        }
        if (!preamble.toString().equals("DOCTYPE html")){
            throwError("Invalid preamble: "+ preamble);
        }
        nextCharacter = readNext();
        nextCharacter = skipSpaces(nextCharacter);
        return nextCharacter;
    }

    private char parseAttributeValue(char nextCharacter) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        while (nextCharacter!= '"'){
            stringBuilder.append(nextCharacter);
            nextCharacter = readNext();
        }
        previousToken = TokenType.TAG_ATTRIBUTE_VALUE;
        tokens.add(new Token(previousToken, stringBuilder.toString()));
        nextCharacter = readNext();
        nextCharacter = skipSpaces(nextCharacter);
        return nextCharacter;

    }

    private char parseTagContent(char nextCharacter) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        while (nextCharacter!= '<'){
            stringBuilder.append(nextCharacter);
            nextCharacter = readNext();
        }
        previousToken = TokenType.TAG_CONTENT;
        tokens.add(new Token(previousToken, stringBuilder.toString()));
        return nextCharacter;
    }

    private void throwError (String message){
        throw new RuntimeException(String.format("%s. Line number: %d, position: %d", message, lineNumber, currentPosition));
    }

    private char parseAttributeName(char nextCharacter) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        while (Character.isLetter(nextCharacter) || nextCharacter =='-'){
            stringBuilder.append(nextCharacter);
            nextCharacter = readNext();
        }
        previousToken = TokenType.TAG_ATTRIBUTE_NAME;
        tokens.add(new Token(previousToken, stringBuilder.toString()));
        nextCharacter = skipSpaces(nextCharacter);
        return nextCharacter;

    }

    private char skipSpaces(char nextChar) throws IOException {
        while  (nextChar == ' ' || nextChar == '\n'){
            nextChar = readNext();
        }
        return nextChar;
    }

    private char parseTagName(char nextCharacter) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        while (Character.isLetterOrDigit(nextCharacter)){
            stringBuilder.append(nextCharacter);
            nextCharacter = readNext();
        }
        previousToken = TokenType.TAG_NAME;
        tokens.add(new Token(TokenType.TAG_NAME, stringBuilder.toString()));
        nextCharacter = skipSpaces(nextCharacter);
        return nextCharacter;
    }

    private char readNext() throws IOException {
        currentPosition++;
        int nextChar = stringReader.read();
        if (nextChar == '\n'){
            lineNumber ++;
            currentPosition = 0;
        }
        if (nextChar == -1){
            eofEncountered = true;
        }
        return (char) nextChar;
    }

}
