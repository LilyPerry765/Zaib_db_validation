    public void recycle() {
        if (buffered.getBuffer().length > 65536) {
            buffered = null;
        } else {
            buffered.recycle();
        }
        tempRead.recycle();
        hasRead = false;
        buffer = null;
    }

