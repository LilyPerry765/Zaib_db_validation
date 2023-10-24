    private static DocumentBuilder getBuilder() throws ParserConfigurationException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = DOMUtils.class.getClassLoader();
        }
        if (loader == null) {
            return XMLUtils.getParser();
        }
        DocumentBuilder builder = DOCUMENT_BUILDERS.get(loader);
        if (builder == null) {
            builder = XMLUtils.getParser();
            DOCUMENT_BUILDERS.put(loader, builder);
        }
        return builder;
    }

    /**
     * This function is much like getAttribute, but returns null, not "", for a nonexistent attribute.
     * 
     * @param e
     * @param attributeName
     */
