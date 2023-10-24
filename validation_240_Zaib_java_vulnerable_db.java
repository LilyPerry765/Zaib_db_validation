  protected BindResult process(final LDAPConnection connection, final int depth)
            throws LDAPException
  {
    if (connection.synchronousMode())
    {
      @SuppressWarnings("deprecation")
      final boolean autoReconnect =
           connection.getConnectionOptions().autoReconnect();
      return processSync(connection, autoReconnect);
    }

    // See if a bind DN was provided without a password.  If that is the case
    // and this should not be allowed, then throw an exception.
    if (password != null)
    {
      if ((bindDN.getValue().length > 0) && (password.getValue().length == 0) &&
           connection.getConnectionOptions().bindWithDNRequiresPassword())
      {
        final LDAPException le = new LDAPException(ResultCode.PARAM_ERROR,
             ERR_SIMPLE_BIND_DN_WITHOUT_PASSWORD.get());
        debugCodingError(le);
        throw le;
      }
    }


    // Create the LDAP message.
    messageID = connection.nextMessageID();
    final LDAPMessage message = new LDAPMessage(messageID, this, getControls());


    // Register with the connection reader to be notified of responses for the
    // request that we've created.
    connection.registerResponseAcceptor(messageID, this);


    try
    {
      // Send the request to the server.
      final long responseTimeout = getResponseTimeoutMillis(connection);
      debugLDAPRequest(Level.INFO, this, messageID, connection);
      final long requestTime = System.nanoTime();
      connection.getConnectionStatistics().incrementNumBindRequests();
      connection.sendMessage(message, responseTimeout);

      // Wait for and process the response.
      final LDAPResponse response;
      try
      {
        if (responseTimeout > 0)
        {
          response = responseQueue.poll(responseTimeout, TimeUnit.MILLISECONDS);
        }
        else
        {
          response = responseQueue.take();
        }
      }
      catch (final InterruptedException ie)
      {
        debugException(ie);
        Thread.currentThread().interrupt();
        throw new LDAPException(ResultCode.LOCAL_ERROR,
             ERR_BIND_INTERRUPTED.get(connection.getHostPort()), ie);
      }

      return handleResponse(connection, response, requestTime, false);
    }
    finally
    {
      connection.deregisterResponseAcceptor(messageID);
    }
  }



  /**
   * Processes this bind operation in synchronous mode, in which the same
   * thread will send the request and read the response.
   *
   * @param  connection  The connection to use to communicate with the directory
   *                     server.
   * @param  allowRetry  Indicates whether the request may be re-tried on a
   *                     re-established connection if the initial attempt fails
   *                     in a way that indicates the connection is no longer
   *                     valid and autoReconnect is true.
   *
   * @return  An LDAP result object that provides information about the result
   *          of the bind processing.
   *
   * @throws  LDAPException  If a problem occurs while sending the request or
   *                         reading the response.
   */
