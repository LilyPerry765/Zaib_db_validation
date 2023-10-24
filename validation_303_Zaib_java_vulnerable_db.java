    public static boolean isExpression(Object value) {
        String expr = value.toString();
        return expr.startsWith("%{") && expr.endsWith("}");
    }

