package main.dom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DomElement {

    private DomElement parent;
    private DOMElementType domElementType;

    private List<DomElement> children = new ArrayList<>();
    private Map<String, String> attributes = new HashMap<>();

    private DomElementContent domElementContent;

    public DomElement(DOMElementType domElementType, DomElement parent) {
        this.domElementType = domElementType;
        this.parent = parent;
    }

    public DomElement getParent (){
        return parent;
    }

    public void setText(DomElementContent domElementContent) {
        this.domElementContent = domElementContent;
    }

    public void addElement(DomElement domElement){
        children.add(domElement);
    }

    public void putAttributeName (String attributeName){
        attributes.put(attributeName, "");
    }

    public void putAttributeValue (String attributeName, String attributeValue){
        attributes.put(attributeName, attributeValue);
    }

    public List<DomElement> getChildren() {
        return children;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public DomElementContent getText() {
        return domElementContent;
    }
}
