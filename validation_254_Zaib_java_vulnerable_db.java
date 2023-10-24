    private void resetContext() throws Exception {
        // Restore the original state ( pre reading web.xml in start )
        // If you extend this - override this method and make sure to clean up
        
        // Don't reset anything that is read from a <Context.../> element since
        // <Context .../> elements are read at initialisation will not be read
        // again for this object
        children = new HashMap<String, Container>();
        startupTime = 0;
        startTime = 0;
        tldScanTime = 0;

        // Bugzilla 32867
        distributable = false;

        applicationListeners = new String[0];
        applicationEventListenersObjects = new Object[0];
        applicationLifecycleListenersObjects = new Object[0];
        jspConfigDescriptor = new ApplicationJspConfigDescriptor();
        
        initializers.clear();
        
        if(log.isDebugEnabled())
            log.debug("resetContext " + getObjectName());
    }

    /**
     * Return a String representation of this component.
     */
    @Override
