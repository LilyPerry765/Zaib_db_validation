    protected boolean parseChunkHeader()
        throws IOException {

        int result = 0;
        boolean eol = false;
        boolean readDigit = false;
        boolean trailer = false;

        while (!eol) {

            if (pos >= lastValid) {
                if (readBytes() <= 0)
                    return false;
            }

            if (buf[pos] == Constants.CR || buf[pos] == Constants.LF) {
                parseCRLF(false);
                eol = true;
            } else if (buf[pos] == Constants.SEMI_COLON) {
                trailer = true;
            } else if (!trailer) {
                //don't read data after the trailer
                int charValue = HexUtils.getDec(buf[pos]);
                if (charValue != -1) {
                    readDigit = true;
                    result *= 16;
                    result += charValue;
                } else {
                    //we shouldn't allow invalid, non hex characters
                    //in the chunked header
                    return false;
                }
            }

            // Parsing the CRLF increments pos
            if (!eol) {
                pos++;
            }

        }

        if (!readDigit)
            return false;

        if (result == 0)
            endChunk = true;

        remaining = result;
        if (remaining < 0)
            return false;

        return true;

    }


    /**
     * Parse CRLF at end of chunk.
     *
     * @param   tolerant    Should tolerant parsing (LF and CRLF) be used? This
     *                      is recommended (RFC2616, section 19.3) for message
     *                      headers.
     */
