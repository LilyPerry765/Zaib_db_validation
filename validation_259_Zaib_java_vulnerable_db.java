  public void testRejectBindWithDNButNoPassword()
         throws Exception
  {
    if (! isDirectoryInstanceAvailable())
    {
      return;
    }

    LDAPConnection conn = getUnauthenticatedConnection();
    SimpleBindRequest bindRequest = new SimpleBindRequest(getTestBindDN(), "");

    try
    {
      bindRequest.process(conn, 1);
      fail("Expected an exception when binding with a DN but no password");
    }
    catch (LDAPException le)
    {
      assertEquals(le.getResultCode(), ResultCode.PARAM_ERROR);
    }


    // Reconfigure the connection so that it will allow binds with a DN but no
    // password.
    conn.getConnectionOptions().setBindWithDNRequiresPassword(false);
    try
    {
      bindRequest.process(conn, 1);
    }
    catch (LDAPException le)
    {
      // The server will still likely reject the operation, but we should at
      // least verify that it wasn't a parameter error.
      assertFalse(le.getResultCode() == ResultCode.PARAM_ERROR);
    }

    conn.getConnectionOptions().setBindWithDNRequiresPassword(true);
    conn.close();
  }
