    public void fireOnComplete() {
        List<AsyncListenerWrapper> listenersCopy = new ArrayList<>();
        listenersCopy.addAll(listeners);

        ClassLoader oldCL = context.bind(Globals.IS_SECURITY_ENABLED, null);
        try {
            for (AsyncListenerWrapper listener : listenersCopy) {
                try {
                    listener.fireOnComplete(event);
                } catch (Throwable t) {
                    ExceptionUtils.handleThrowable(t);
                    log.warn("onComplete() failed for listener of type [" +
                            listener.getClass().getName() + "]", t);
                }
            }
        } finally {
            context.fireRequestDestroyEvent(request);
            clearServletRequestResponse();
            context.unbind(Globals.IS_SECURITY_ENABLED, oldCL);
        }
    }


