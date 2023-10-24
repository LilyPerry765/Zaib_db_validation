    private XPathEvaluator createEvaluator(String xpath2) {
        try {
            return (XPathEvaluator)EVALUATOR_CONSTRUCTOR.newInstance(new Object[] {xpath});
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            }
            throw new RuntimeException("Invalid XPath Expression: " + xpath + " reason: " + e.getMessage(), e);
        } catch (Throwable e) {
            throw new RuntimeException("Invalid XPath Expression: " + xpath + " reason: " + e.getMessage(), e);
        }
    }

