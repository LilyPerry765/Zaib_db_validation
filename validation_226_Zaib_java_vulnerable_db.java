    public  final void recycle() {
        try {
            // Must clear super's buffer.
            while (ready()) {
                // InputStreamReader#skip(long) will allocate buffer to skip.
                read();
            }
        } catch(IOException ioe){
        }
    }
}


/** Special output stream where close() is overriden, so super.close()
    is never called.
    
    This allows recycling. It can also be disabled, so callbacks will
    not be called if recycling the converter and if data was not flushed.
*/
final class IntermediateInputStream extends InputStream {
    ByteChunk bc = null;
    boolean initialized = false;
    
    public IntermediateInputStream() {
    }
    
    public  final void close() throws IOException {
        // shouldn't be called - we filter it out in writer
        throw new IOException("close() called - shouldn't happen ");
    }
    
    public  final  int read(byte cbuf[], int off, int len) throws IOException {
        if (!initialized) return -1;
        int nread = bc.substract(cbuf, off, len);
        return nread;
    }
    
    public  final int read() throws IOException {
        if (!initialized) return -1;
        return bc.substract();
    }
    
    public int available() throws IOException {
        if (!initialized) return 0;
        return bc.getLength();
    }


    // -------------------- Internal methods --------------------


    void setByteChunk( ByteChunk mb ) {
        initialized = (mb!=null);
        bc = mb;
    }

