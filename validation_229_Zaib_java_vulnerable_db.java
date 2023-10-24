  public XMLLoader init(SolrParams args) {
    inputFactory = XMLInputFactory.newInstance();
    try {
      // The java 1.6 bundled stax parser (sjsxp) does not currently have a thread-safe
      // XMLInputFactory, as that implementation tries to cache and reuse the
      // XMLStreamReader.  Setting the parser-specific "reuse-instance" property to false
      // prevents this.
      // All other known open-source stax parsers (and the bea ref impl)
      // have thread-safe factories.
      inputFactory.setProperty("reuse-instance", Boolean.FALSE);
    }
    catch (IllegalArgumentException ex) {
      // Other implementations will likely throw this exception since "reuse-instance"
      // isimplementation specific.
      log.debug("Unable to set the 'reuse-instance' property for the input chain: " + inputFactory);
    }
    inputFactory.setXMLReporter(xmllog);
    
    xsltCacheLifetimeSeconds = XSLT_CACHE_DEFAULT;
    if(args != null) {
      xsltCacheLifetimeSeconds = args.getInt(XSLT_CACHE_PARAM,XSLT_CACHE_DEFAULT);
      log.info("xsltCacheLifetimeSeconds=" + xsltCacheLifetimeSeconds);
    }
    return this;
  }

