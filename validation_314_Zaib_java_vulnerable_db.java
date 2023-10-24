    public String toStringInternal() {
        String strValue=null;
        try {
            if( enc==null ) enc=DEFAULT_CHARACTER_ENCODING;
            strValue = new String( buff, start, end-start, enc );
            /*
             Does not improve the speed too much on most systems,
             it's safer to use the "clasical" new String().
             
             Most overhead is in creating char[] and copying,
             the internal implementation of new String() is very close to
             what we do. The decoder is nice for large buffers and if
             we don't go to String ( so we can take advantage of reduced GC)
             
             // Method is commented out, in:
              return B2CConverter.decodeString( enc );
              */
        } catch (java.io.UnsupportedEncodingException e) {
            // Use the platform encoding in that case; the usage of a bad
            // encoding will have been logged elsewhere already
            strValue = new String(buff, start, end-start);
        }
        return strValue;
    }

