package main.htmlparser;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class HtmlParser {

    private List<Token> tokens = new ArrayList<>();
    private StringReader stringReader;

    private int currentPosition;
    private boolean eofEncountered;

    public void parse(String html) throws IOException {
        eofEncountered = false;
        stringReader = new StringReader(html);

        char nextChar = readNext();
        while ( !eofEncountered){
            if (nextChar == '<'){
                nextChar = parseTag();
            }
            else{
                nextChar = parseTagContent(nextChar);
            }
            nextChar = skipSpaces(nextChar);
        }

        System.out.println(tokens.toString().replace(",", ""));
    }

    private char parseTag() throws IOException {
        char nextCharacter = readNext();
        if (nextCharacter == '/'){
            tokens.add(new Token(TokenType.TAG_END));
            nextCharacter = readNext();
        }
        else if (Character.isWhitespace(nextCharacter)){
            StringBuilder contentBuilder = new StringBuilder();
            contentBuilder.append("<");
            contentBuilder.append(nextCharacter);
            nextCharacter = readNext();
            contentBuilder.append(nextCharacter);
            while (nextCharacter != '>'){
                nextCharacter = readNext();
                contentBuilder.append(nextCharacter);
            }
            tokens.add(new Token(TokenType.TAG_CONTENT, contentBuilder.toString()));
            nextCharacter = readNext();
            return nextCharacter;
        }
        else{
            tokens.add(new Token(TokenType.TAG_START));
        }

        nextCharacter = parseTagName(nextCharacter);
        nextCharacter = skipSpaces(nextCharacter);
        if (nextCharacter == '>'){
            tokens.add(new Token(TokenType.TAG_CLOSING));
            nextCharacter=readNext();
        }
        else{
            parseTagAttributesOrClosing(nextCharacter);
        }
        nextCharacter = parseTagContent(nextCharacter);
        if (nextCharacter == '<'){
            nextCharacter = parseTag();
        }
        return nextCharacter;
    }

    private char readNext() throws IOException {
        currentPosition++;
        int nextChar = stringReader.read();
        if (nextChar == -1){
            eofEncountered = true;
        }
        return (char) nextChar;
    }

    private void parseTagAttributesOrClosing(char nextChar) throws IOException {
        while (nextChar != '>'){
            StringBuilder attributeName = new StringBuilder();
            while (Character.isLetter(nextChar)){
                attributeName.append(nextChar);
                nextChar = readNext();
            }
            if (attributeName.length()>0){
                tokens.add(new Token(TokenType.TAG_ATTRIBUTE_NAME, attributeName.toString()));
                skipSpaces(nextChar);
                if (nextChar == '='){
                    nextChar = readNext();
                    skipSpaces(nextChar);
                    if (nextChar != '"'){
                        throw new RuntimeException("Expecting \" after = in attribute name: "+attributeName + " position: "+currentPosition);
                    }
                    nextChar = readNext();
                    skipSpaces(nextChar);
                    String attributeValue = parseAttributeValue(nextChar);
                    nextChar = readNext();
                    tokens.add(new Token(TokenType.TAG_ATTRIBUTE_VALUE, attributeValue));

                }
            }

            nextChar = skipSpaces(nextChar);
        }

    }

    private String parseAttributeValue(char nextChar) throws IOException {
        StringBuilder word  = new StringBuilder();
        while (nextChar != '"'){
            word.append(nextChar);
            nextChar = readNext();
        }
        return word.toString();
    }

    private char skipSpaces(char nextChar) throws IOException {
        while (nextChar == ' ' || nextChar == '\n'){
            nextChar = readNext();
        }
        return nextChar;
    }

    private char parseTagContent(char nextCharacter) throws IOException {
        StringBuilder content = new StringBuilder();
        while (nextCharacter != '<'){
            if (nextCharacter!= '\n'){
                content.append(nextCharacter);
            }
            nextCharacter = readNext();
            if (eofEncountered){
                break;
            }
        }
        if (content.length()>0){
            tokens.add(new Token(TokenType.TAG_CONTENT, content.toString()));
        }
        return nextCharacter;
    }

    private char parseTagName(char nextCharacter) throws IOException {
        StringBuilder tagName = new StringBuilder();
        while (Character.isLetterOrDigit(nextCharacter)){
            tagName.append(nextCharacter);
            nextCharacter = readNext();
        }
        if (tagName.length()==0 && !Character.isWhitespace(nextCharacter)){
            throw new RuntimeException("Tag name missing after tag opening < Position: "+currentPosition);
        }
        else{
            tokens.add(new Token(TokenType.TAG_NAME, tagName.toString()));
        }
        return nextCharacter;
    }

}
