    private String buildErrorMessage(Throwable e, Object[] args) {
        String errorKey = "struts.message.upload.error." + e.getClass().getSimpleName();
        if (LOG.isDebugEnabled())
            LOG.debug("Preparing error message for key: [#0]", errorKey);
        return LocalizedTextUtil.findText(this.getClass(), errorKey, defaultLocale, e.getMessage(), args);
    }

    /**
     * Build action message.
     *
     * @param e
     * @param args
     * @return
     */
