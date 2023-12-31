    protected void stripScopesFromAuthentication(String identityZoneId, HttpServletRequest servletRequest) {
        OAuth2Authentication oa = (OAuth2Authentication)SecurityContextHolder.getContext().getAuthentication();

        Object oaDetails = oa.getDetails();

        //strip client scopes
        OAuth2Request request = oa.getOAuth2Request();
        Collection<String> requestAuthorities = UaaStringUtils.getStringsFromAuthorities(request.getAuthorities());
        Set<String> clientScopes = new HashSet<>();
        Set<String> clientAuthorities = new HashSet<>();
        for (String s : getZoneSwitchingScopes(identityZoneId)) {
            String scope = stripPrefix(s, identityZoneId);
            if (request.getScope().contains(s)) {
                clientScopes.add(scope);
            }
            if (requestAuthorities.contains(s)) {
                clientAuthorities.add(scope);
            }
        }
        request = new OAuth2Request(
            request.getRequestParameters(),
            request.getClientId(),
            UaaStringUtils.getAuthoritiesFromStrings(clientAuthorities),
            request.isApproved(),
            clientScopes,
            request.getResourceIds(),
            request.getRedirectUri(),
            request.getResponseTypes(),
            request.getExtensions()
            );


        UaaAuthentication userAuthentication = (UaaAuthentication)oa.getUserAuthentication();
        if (userAuthentication!=null) {
            userAuthentication = new UaaAuthentication(
                userAuthentication.getPrincipal(),
                null,
                UaaStringUtils.getAuthoritiesFromStrings(clientScopes),
                new UaaAuthenticationDetails(servletRequest),
                true);
        }
        oa = new OAuth2Authentication(request, userAuthentication);
        oa.setDetails(oaDetails);
        SecurityContextHolder.getContext().setAuthentication(oa);
    }

