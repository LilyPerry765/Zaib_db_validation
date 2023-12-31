    protected RequestEntity createRequestEntity(Exchange exchange) throws CamelExchangeException {
        Message in = exchange.getIn();
        if (in.getBody() == null) {
            return null;
        }

        RequestEntity answer = in.getBody(RequestEntity.class);
        if (answer == null) {
            try {
                Object data = in.getBody();
                if (data != null) {
                    String contentType = ExchangeHelper.getContentType(exchange);

                    if (contentType != null && HttpConstants.CONTENT_TYPE_JAVA_SERIALIZED_OBJECT.equals(contentType)) {
                        // serialized java object
                        Serializable obj = in.getMandatoryBody(Serializable.class);
                        // write object to output stream
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        HttpHelper.writeObjectToStream(bos, obj);
                        answer = new ByteArrayRequestEntity(bos.toByteArray(), HttpConstants.CONTENT_TYPE_JAVA_SERIALIZED_OBJECT);
                        IOHelper.close(bos);
                    } else if (data instanceof File || data instanceof GenericFile) {
                        // file based (could potentially also be a FTP file etc)
                        File file = in.getBody(File.class);
                        if (file != null) {
                            answer = new FileRequestEntity(file, contentType);
                        }
                    } else if (data instanceof String) {
                        // be a bit careful with String as any type can most likely be converted to String
                        // so we only do an instanceof check and accept String if the body is really a String
                        // do not fallback to use the default charset as it can influence the request
                        // (for example application/x-www-form-urlencoded forms being sent)
                        String charset = IOHelper.getCharsetName(exchange, false);
                        answer = new StringRequestEntity((String) data, contentType, charset);
                    }
                    // fallback as input stream
                    if (answer == null) {
                        // force the body as an input stream since this is the fallback
                        InputStream is = in.getMandatoryBody(InputStream.class);
                        answer = new InputStreamRequestEntity(is, contentType);
                    }
                }
            } catch (UnsupportedEncodingException e) {
                throw new CamelExchangeException("Error creating RequestEntity from message body", exchange, e);
            } catch (IOException e) {
                throw new CamelExchangeException("Error serializing message body", exchange, e);
            }
        }
        return answer;
    }

