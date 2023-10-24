    public void resetPassword_ReturnsUnprocessableEntity_NewPasswordSameAsOld() throws Exception {
        ScimUserProvisioning userProvisioning = getWebApplicationContext().getBean(ScimUserProvisioning.class);
        List<ScimUser> users = userProvisioning.query("username eq \"marissa\"");
        assertNotNull(users);
        assertEquals(1, users.size());
        ScimUser user = users.get(0);

        ExpiringCode code = codeStore.generateCode(user.getId(), new Timestamp(System.currentTimeMillis() + UaaResetPasswordService.PASSWORD_RESET_LIFETIME), null);
        getMockMvc().perform(createChangePasswordRequest(user, code, true, "d3faultPasswd", "d3faultPasswd"));

        code = codeStore.generateCode(user.getId(), new Timestamp(System.currentTimeMillis() + UaaResetPasswordService.PASSWORD_RESET_LIFETIME), null);
        getMockMvc().perform(createChangePasswordRequest(user, code, true, "d3faultPasswd", "d3faultPasswd"))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(view().name("forgot_password"))
            .andExpect(model().attribute("message", "Your new password cannot be the same as the old password."));
    }

