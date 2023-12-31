    public void setDynamicAttribute(String uri, String localName, Object value) throws JspException {
        if (ComponentUtils.altSyntax(getStack()) && ComponentUtils.isExpression(value)) {
            dynamicAttributes.put(localName, String.valueOf(ObjectUtils.defaultIfNull(findValue(value.toString()), value)));
        } else {
            dynamicAttributes.put(localName, value);
        }
    }

