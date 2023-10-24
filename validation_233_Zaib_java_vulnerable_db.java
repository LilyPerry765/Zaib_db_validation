  public void init(Map<String,Object> pluginConfig) {
    try {
      String delegationTokenEnabled = (String)pluginConfig.getOrDefault(DELEGATION_TOKEN_ENABLED_PROPERTY, "false");
      authFilter = (Boolean.parseBoolean(delegationTokenEnabled)) ? new HadoopAuthFilter() : new AuthenticationFilter();

      // Initialize kerberos before initializing curator instance.
      boolean initKerberosZk = Boolean.parseBoolean((String)pluginConfig.getOrDefault(INIT_KERBEROS_ZK, "false"));
      if (initKerberosZk) {
        (new Krb5HttpClientBuilder()).getBuilder();
      }

      FilterConfig conf = getInitFilterConfig(pluginConfig);
      authFilter.init(conf);

    } catch (ServletException e) {
      throw new SolrException(ErrorCode.SERVER_ERROR, "Error initializing " + getClass().getName() + ": "+e);
    }
  }

  @SuppressWarnings("unchecked")
