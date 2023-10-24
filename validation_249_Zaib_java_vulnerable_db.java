    public Document getMetaData(Idp config) throws RuntimeException {
        //Return as text/xml
        try {
            Crypto crypto = CertsUtils.createCrypto(config.getCertificate());
            
            ByteArrayOutputStream bout = new ByteArrayOutputStream(4096);
            Writer streamWriter = new OutputStreamWriter(bout, "UTF-8");
            XMLStreamWriter writer = XML_OUTPUT_FACTORY.createXMLStreamWriter(streamWriter);

            writer.writeStartDocument("UTF-8", "1.0");

            String referenceID = IDGenerator.generateID("_");
            writer.writeStartElement("md", "EntityDescriptor", SAML2_METADATA_NS);
            writer.writeAttribute("ID", referenceID);
                      
            writer.writeAttribute("entityID", config.getIdpUrl().toString());

            writer.writeNamespace("md", SAML2_METADATA_NS);
            writer.writeNamespace("fed", WS_FEDERATION_NS);
            writer.writeNamespace("wsa", WS_ADDRESSING_NS);
            writer.writeNamespace("auth", WS_FEDERATION_NS);
            writer.writeNamespace("xsi", SCHEMA_INSTANCE_NS);
            
            writeFederationMetadata(writer, config, crypto);
            
            writer.writeEndElement(); // EntityDescriptor

            writer.writeEndDocument();
            streamWriter.flush();
            bout.flush();

            if (LOG.isDebugEnabled()) {
                String out = new String(bout.toByteArray());
                LOG.debug("***************** unsigned ****************");
                LOG.debug(out);
                LOG.debug("***************** unsigned ****************");
            }
            
            InputStream is = new ByteArrayInputStream(bout.toByteArray());
            
            Document result = SignatureUtils.signMetaInfo(crypto, null, config.getCertificatePassword(), is, referenceID);
            if (result != null) {
                return result;
            } else {
                throw new RuntimeException("Failed to sign the metadata document: result=null");
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Error creating service metadata information ", e);
            throw new RuntimeException("Error creating service metadata information: " + e.getMessage());
        }
        
    }
    
