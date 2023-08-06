package main.htmlparser;

public class Token {

    private TokenType tokenType;

    private String content = "";

    public Token(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    public Token(TokenType tokenType, String content) {
        this.tokenType = tokenType;
        this.content = content;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    @Override
    public String toString() {
        switch (tokenType){
            case TAG_START:
            case TAG_CLOSING:
            case TAG_END:
                return tokenType.getSymbol();
        }
        return tokenType + (content.isEmpty()? "": ": "+content);
    }
}
