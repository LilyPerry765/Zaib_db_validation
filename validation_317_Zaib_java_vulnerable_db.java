    public void noUserSearchCausesUsernameNotFound() throws Exception {
        DirContext ctx = mock(DirContext.class);
        when(ctx.getNameInNamespace()).thenReturn("");
        when(ctx.search(any(Name.class), any(String.class), any(Object[].class), any(SearchControls.class)))
                .thenReturn(new EmptyEnumeration<SearchResult>());

        provider.contextFactory = createContextFactoryReturning(ctx);

        provider.authenticate(joe);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = IncorrectResultSizeDataAccessException.class)
