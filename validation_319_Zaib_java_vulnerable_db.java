    private boolean evaluate(String text) {
        try {
            InputSource inputSource = new InputSource(new StringReader(text));
            return ((Boolean)expression.evaluate(inputSource, XPathConstants.BOOLEAN)).booleanValue();
        } catch (XPathExpressionException e) {
            return false;
        }
    }

    @Override
