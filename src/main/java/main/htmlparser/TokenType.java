package main.htmlparser;

public enum TokenType {
    PREAMBLE, START, WORD, TAG_START("<"), TAG_NAME(""), TAG_CLOSING(">"), TAG_CONTENT, TAG_ATTRIBUTE_NAME, TAG_ATTRIBUTE_VALUE, TAG_END("</");

    private String symbol;

    TokenType(String symbol) {
        this.symbol = symbol;
    }

    TokenType (){
        this.symbol = "";
    }

    public String getSymbol() {
        return symbol;
    }
}
