    public void testWelcomeFileStrict() throws Exception {

        Tomcat tomcat = getTomcatInstance();

        File appDir = new File("test/webapp");

        StandardContext ctxt = (StandardContext) tomcat.addWebapp(null, "/test",
                appDir.getAbsolutePath());
        ctxt.setReplaceWelcomeFiles(true);
        ctxt.addWelcomeFile("index.jsp");
        // Mapping for *.do is defined in web.xml
        ctxt.addWelcomeFile("index.do");

        // Simulate STRICT_SERVLET_COMPLIANCE
        ctxt.setResourceOnlyServlets("");

        tomcat.start();
        ByteChunk bc = new ByteChunk();
        int rc = getUrl("http://localhost:" + getPort() +
                "/test/welcome-files", bc, new HashMap<String,List<String>>());
        Assert.assertEquals(HttpServletResponse.SC_OK, rc);
        Assert.assertTrue(bc.toString().contains("JSP"));

        rc = getUrl("http://localhost:" + getPort() +
                "/test/welcome-files/sub", bc,
                new HashMap<String,List<String>>());
        Assert.assertEquals(HttpServletResponse.SC_NOT_FOUND, rc);
    }

    /**
     * Prepare a string to search in messages that contain a timestamp, when it
     * is known that the timestamp was printed between {@code timeA} and
     * {@code timeB}.
     */
