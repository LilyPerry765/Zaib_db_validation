    public void getAllPropertiesRequiresAdmin() {
        j.jenkins.setAuthorizationStrategy(new MockAuthorizationStrategy()
                .grant(Jenkins.ADMINISTER).everywhere().to("admin")
                .grant(Jenkins.READ).everywhere().toEveryone());
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());

        User admin = User.get("admin");
        User alice = User.get("alice");
        User bob = User.get("bob");

        // Admin can access user properties for all users
        try (ACLContext as = ACL.as(admin)) {
            assertThat(alice.getAllProperties(), not(empty()));
            assertThat(bob.getAllProperties(), not(empty()));
            assertThat(admin.getAllProperties(), not(empty()));
        }

        // Non admins can only view their own
        try (ACLContext as = ACL.as(alice)) {
            assertThat(alice.getAllProperties(), not(empty()));
            assertThat(bob.getAllProperties(), empty());
            assertThat(admin.getAllProperties(), empty());
        }
    }

