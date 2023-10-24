    public void changePassword_Resets_Session() throws Exception {
        ScimUser user = createUser();

        MockHttpSession session = new MockHttpSession();
        MockHttpSession afterLoginSession = (MockHttpSession) getMockMvc().perform(post("/login.do")
            .session(session)
            .accept(TEXT_HTML_VALUE)
            .param("username", user.getUserName())
            .param("password", "secr3T"))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/"))
            .andReturn().getRequest().getSession(false);

        assertTrue(session.isInvalid());
        assertNotNull(afterLoginSession);
        assertNotNull(afterLoginSession.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY));

        MockHttpSession afterPasswordChange = (MockHttpSession) getMockMvc().perform(post("/change_password.do")
            .session(afterLoginSession)
            .with(csrf())
            .accept(TEXT_HTML_VALUE)
            .param("current_password", "secr3T")
            .param("new_password", "secr3T1")
            .param("confirm_password", "secr3T1"))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("profile"))
            .andReturn().getRequest().getSession(false);

        assertTrue(afterLoginSession.isInvalid());
        assertNotNull(afterPasswordChange);
        assertNotNull(afterPasswordChange.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY));
        assertNotSame(afterLoginSession, afterPasswordChange);

    }

