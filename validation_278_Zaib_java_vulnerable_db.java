    public Object compile(String expression) throws OgnlException {
        if (enableExpressionCache) {
            Object o = expressions.get(expression);
            if (o == null) {
                o = Ognl.parseExpression(expression);
                expressions.putIfAbsent(expression, o);
            }
            return o;
        } else
            return Ognl.parseExpression(expression);
    }

    /**
     * Copies the properties in the object "from" and sets them in the object "to"
     * using specified type converter, or {@link com.opensymphony.xwork2.conversion.impl.XWorkConverter} if none
     * is specified.
     *
     * @param from       the source object
     * @param to         the target object
     * @param context    the action context we're running under
     * @param exclusions collection of method names to excluded from copying ( can be null)
     * @param inclusions collection of method names to included copying  (can be null)
     *                   note if exclusions AND inclusions are supplied and not null nothing will get copied.
     */
