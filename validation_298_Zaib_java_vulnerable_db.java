    public boolean isTransferException() {
        return transferException;
    }

    /**
     * If enabled and an Exchange failed processing on the consumer side, and if the caused Exception was send back serialized
     * in the response as a application/x-java-serialized-object content type (for example using Jetty or Servlet Camel components).
     * On the producer side the exception will be deserialized and thrown as is, instead of the AhcOperationFailedException.
     * The caused exception is required to be serialized.
     */
