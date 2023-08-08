package main.dom;

import main.htmlparser.Token;
import main.htmlparser.TokenType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DOMBuilder {

    public DomElement transformTokensToDOM (List<Token> tokens){
        DomElement root = new DomElement(DOMElementType.DOCUMENT, null);
        DomElement localParentElement = root;
        String localAttributeName = "";
        boolean isClosingTag = false;
        for (Token token : tokens) {
            if (token.getTokenType().equals(TokenType.TAG_START)){
                isClosingTag = false;
            }
            if (!isClosingTag && token.getTokenType().equals(TokenType.TAG_NAME)){
                DOMElementType domElementType = DOMElementType.fromString(token.getContent());
                DomElement domElement = new DomElement(domElementType, localParentElement);
                localParentElement.addElement(domElement);
                if (domElementType.isContainerTag()){
                    localParentElement = domElement;
                }
            }
            if (token.getTokenType().equals(TokenType.TAG_END)){
                localParentElement = localParentElement.getParent();
                isClosingTag = true;
            }
            if (token.getTokenType().equals(TokenType.TAG_CONTENT)){
                localParentElement.setText(new DomElementContent(token.getContent()));
            }
            if (token.getTokenType().equals(TokenType.TAG_ATTRIBUTE_NAME)){
                localAttributeName = token.getContent();
                localParentElement.putAttributeName(localAttributeName);
            }
            if (token.getTokenType().equals(TokenType.TAG_ATTRIBUTE_VALUE)){
                localParentElement.putAttributeValue(localAttributeName, token.getContent());
                localAttributeName = "";
            }
        }
        return root;
    }

}
