package main.dom;

public enum DOMElementType {

    DOCUMENT, HTML, HEAD, BODY, TITLE, STYLE, P, H1, BR(false), EM, B;

    private boolean hasEndTag;

    DOMElementType(boolean hasEndTag) {
        this.hasEndTag = hasEndTag;
    }

    DOMElementType() {
        hasEndTag = true;
    }

    public boolean isContainerTag() {
        return hasEndTag;
    }

    public static DOMElementType fromString (String tagName){
        for (DOMElementType value : values()) {
            if (value.toString().equalsIgnoreCase(tagName)){
                return value;
            }
        }
        throw new RuntimeException("No element found in enumeration for tag name: "+tagName);
    }

}
