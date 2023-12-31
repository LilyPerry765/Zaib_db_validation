    private void processInternal(Exchange exchange, AsyncCallback callback) throws Exception {
        // creating the url to use takes 2-steps
        String url = HttpHelper.createURL(exchange, getEndpoint());
        URI uri = HttpHelper.createURI(exchange, url, getEndpoint());
        // get the url from the uri
        url = uri.toASCIIString();

        // execute any custom url rewrite
        String rewriteUrl = HttpHelper.urlRewrite(exchange, url, getEndpoint(), this);
        if (rewriteUrl != null) {
            // update url and query string from the rewritten url
            url = rewriteUrl;
        }

        String methodName = HttpHelper.createMethod(exchange, getEndpoint(), exchange.getIn().getBody() != null).name();

        JettyContentExchange httpExchange = getEndpoint().createContentExchange();
        httpExchange.init(exchange, getBinding(), client, callback);
        httpExchange.setURL(url); // Url has to be set first
        httpExchange.setMethod(methodName);
        
        if (getEndpoint().getHttpClientParameters() != null) {
            // For jetty 9 these parameters can not be set on the client
            // so we need to set them on the httpExchange
            String timeout = (String)getEndpoint().getHttpClientParameters().get("timeout");
            if (timeout != null) {
                httpExchange.setTimeout(new Long(timeout));
            }
            String supportRedirect = (String)getEndpoint().getHttpClientParameters().get("supportRedirect");
            if (supportRedirect != null) {
                httpExchange.setSupportRedirect(Boolean.valueOf(supportRedirect));
            }
        }

        LOG.trace("Using URL: {} with method: {}", url, methodName);

        // if there is a body to send as data
        if (exchange.getIn().getBody() != null) {
            String contentType = ExchangeHelper.getContentType(exchange);
            if (contentType != null) {
                httpExchange.setRequestContentType(contentType);
            }

            if (contentType != null && HttpConstants.CONTENT_TYPE_JAVA_SERIALIZED_OBJECT.equals(contentType)) {
                // serialized java object
                Serializable obj = exchange.getIn().getMandatoryBody(Serializable.class);
                // write object to output stream
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                try {
                    HttpHelper.writeObjectToStream(bos, obj);
                    httpExchange.setRequestContent(bos.toByteArray());
                } finally {
                    IOHelper.close(bos, "body", LOG);
                }
            } else {
                Object body = exchange.getIn().getBody();
                if (body instanceof String) {
                    String data = (String) body;
                    // be a bit careful with String as any type can most likely be converted to String
                    // so we only do an instanceof check and accept String if the body is really a String
                    // do not fallback to use the default charset as it can influence the request
                    // (for example application/x-www-form-urlencoded forms being sent)
                    String charset = IOHelper.getCharsetName(exchange, false);
                    httpExchange.setRequestContent(data, charset);
                } else {
                    // then fallback to input stream
                    InputStream is = exchange.getContext().getTypeConverter().mandatoryConvertTo(InputStream.class, exchange, exchange.getIn().getBody());
                    httpExchange.setRequestContent(is);
                    // setup the content length if it is possible
                    String length = exchange.getIn().getHeader(Exchange.CONTENT_LENGTH, String.class);
                    if (ObjectHelper.isNotEmpty(length)) {
                        httpExchange.addRequestHeader(Exchange.CONTENT_LENGTH, length);
                    }
                }
            }
        }

        // if we bridge endpoint then we need to skip matching headers with the HTTP_QUERY to avoid sending
        // duplicated headers to the receiver, so use this skipRequestHeaders as the list of headers to skip
        Map<String, Object> skipRequestHeaders = null;
        if (getEndpoint().isBridgeEndpoint()) {
            exchange.setProperty(Exchange.SKIP_GZIP_ENCODING, Boolean.TRUE);
            String queryString = exchange.getIn().getHeader(Exchange.HTTP_QUERY, String.class);
            if (queryString != null) {
                skipRequestHeaders = URISupport.parseQuery(queryString, false, true);
            }
            // Need to remove the Host key as it should be not used 
            exchange.getIn().getHeaders().remove("host");
        }

        // propagate headers as HTTP headers
        Message in = exchange.getIn();
        HeaderFilterStrategy strategy = getEndpoint().getHeaderFilterStrategy();
        for (Map.Entry<String, Object> entry : in.getHeaders().entrySet()) {
            String key = entry.getKey();
            Object headerValue = in.getHeader(key);

            if (headerValue != null) {
                // use an iterator as there can be multiple values. (must not use a delimiter, and allow empty values)
                final Iterator<?> it = ObjectHelper.createIterator(headerValue, null, true);

                // the values to add as a request header
                final List<String> values = new ArrayList<String>();

                // if its a multi value then check each value if we can add it and for multi values they
                // should be combined into a single value
                while (it.hasNext()) {
                    String value = exchange.getContext().getTypeConverter().convertTo(String.class, it.next());

                    // we should not add headers for the parameters in the uri if we bridge the endpoint
                    // as then we would duplicate headers on both the endpoint uri, and in HTTP headers as well
                    if (skipRequestHeaders != null && skipRequestHeaders.containsKey(key)) {
                        continue;
                    }
                    if (value != null && strategy != null && !strategy.applyFilterToCamelHeaders(key, value, exchange)) {
                        values.add(value);
                    }
                }

                // add the value(s) as a http request header
                if (values.size() > 0) {
                    // use the default toString of a ArrayList to create in the form [xxx, yyy]
                    // if multi valued, for a single value, then just output the value as is
                    String s = values.size() > 1 ? values.toString() : values.get(0);
                    httpExchange.addRequestHeader(key, s);
                }
            }
        }

        // set the callback, which will handle all the response logic
        if (LOG.isDebugEnabled()) {
            LOG.debug("Sending HTTP request to: {}", httpExchange.getUrl());
        }
        httpExchange.send(client);
    }

