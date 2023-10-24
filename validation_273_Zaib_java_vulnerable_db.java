    public void testHandleNoInitialResponseNull() throws Exception
    {
        final AuthenticationResult result = _negotiator.handleResponse(null);
        assertEquals("Unexpected authentication status", AuthenticationResult.AuthenticationStatus.CONTINUE, result.getStatus());
        assertArrayEquals("Unexpected authentication challenge", new byte[0], result.getChallenge());

        final AuthenticationResult firstResult = _negotiator.handleResponse(VALID_RESPONSE.getBytes());
        assertEquals("Unexpected first authentication result", _expectedResult, firstResult);
    }
