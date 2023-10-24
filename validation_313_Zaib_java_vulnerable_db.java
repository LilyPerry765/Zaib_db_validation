    public void clientCredentials_byDefault_WillNotLockoutDuringFailedBasicAuthAndFormData() throws Exception {
        String clientId = "testclient" + generator.generate();
        String scopes = "space.*.developer,space.*.admin,org.*.reader,org.123*.admin,*.*,*";
        setUpClients(clientId, scopes, scopes, GRANT_TYPES, true);

        String body = null;
        for(int i = 0; i < 3; i++){
            body = getMockMvc().perform(post("/oauth/token")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Basic " + new String(Base64.encode((clientId + ":" + BADSECRET).getBytes())))
                .param("grant_type", "client_credentials")
                )
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

            body = getMockMvc().perform(post("/oauth/token")
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .param("grant_type", "client_credentials")
                    .param("client_id", clientId)
                    .param("client_secret", BADSECRET)
                    )
                    .andExpect(status().isUnauthorized())
                    .andReturn().getResponse().getContentAsString();

        }

        body = getMockMvc().perform(post("/oauth/token")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Basic " + new String(Base64.encode((clientId + ":" + SECRET).getBytes())))
                .param("grant_type", "client_credentials")
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
