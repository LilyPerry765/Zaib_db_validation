  public Source getAssociatedStylesheet(
          Source source, String media, String title, String charset)
            throws TransformerConfigurationException
  {

    String baseID;
    InputSource isource = null;
    Node node = null;
    XMLReader reader = null;

    if (source instanceof DOMSource)
    {
      DOMSource dsource = (DOMSource) source;

      node = dsource.getNode();
      baseID = dsource.getSystemId();
    }
    else
    {
      isource = SAXSource.sourceToInputSource(source);
      baseID = isource.getSystemId();
    }

    // What I try to do here is parse until the first startElement
    // is found, then throw a special exception in order to terminate 
    // the parse.
    StylesheetPIHandler handler = new StylesheetPIHandler(baseID, media,
                                    title, charset);
    
    // Use URIResolver. Patch from Dmitri Ilyin 
    if (m_uriResolver != null) 
    {
      handler.setURIResolver(m_uriResolver); 
    }

    try
    {
      if (null != node)
      {
        TreeWalker walker = new TreeWalker(handler, new org.apache.xml.utils.DOM2Helper(), baseID);

        walker.traverse(node);
      }
      else
      {

        // Use JAXP1.1 ( if possible )
        try
        {
          javax.xml.parsers.SAXParserFactory factory =
            javax.xml.parsers.SAXParserFactory.newInstance();

          factory.setNamespaceAware(true);

          if (m_isSecureProcessing)
          {
            try
            {
              factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            }
            catch (org.xml.sax.SAXException e) {}
          }

          javax.xml.parsers.SAXParser jaxpParser = factory.newSAXParser();

          reader = jaxpParser.getXMLReader();
        }
        catch (javax.xml.parsers.ParserConfigurationException ex)
        {
          throw new org.xml.sax.SAXException(ex);
        }
        catch (javax.xml.parsers.FactoryConfigurationError ex1)
        {
          throw new org.xml.sax.SAXException(ex1.toString());
        }
        catch (NoSuchMethodError ex2){}
        catch (AbstractMethodError ame){}

        if (null == reader)
        {
          reader = XMLReaderFactory.createXMLReader();
        }

        // Need to set options!
        reader.setContentHandler(handler);
        reader.parse(isource);
      }
    }
    catch (StopParseException spe)
    {

      // OK, good.
    }
    catch (org.xml.sax.SAXException se)
    {
      throw new TransformerConfigurationException(
        "getAssociatedStylesheets failed", se);
    }
    catch (IOException ioe)
    {
      throw new TransformerConfigurationException(
        "getAssociatedStylesheets failed", ioe);
    }

    return handler.getAssociatedStylesheet();
  }

  /**
   * Create a new Transformer object that performs a copy
   * of the source to the result.
   *
   * @return A Transformer object that may be used to perform a transformation
   * in a single thread, never null.
   *
   * @throws TransformerConfigurationException May throw this during
   *            the parse when it is constructing the
   *            Templates object and fails.
   */
