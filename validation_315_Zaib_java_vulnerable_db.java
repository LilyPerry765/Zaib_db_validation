    public void repositoryVerificationTimeoutTest() throws Exception {
        Client client = client();

        Settings settings = ImmutableSettings.settingsBuilder()
                .put("location", newTempDir(LifecycleScope.SUITE))
                .put("random_control_io_exception_rate", 1.0).build();
        logger.info("-->  creating repository that cannot write any files - should fail");
        assertThrows(client.admin().cluster().preparePutRepository("test-repo-1")
                        .setType(MockRepositoryModule.class.getCanonicalName()).setSettings(settings),
                RepositoryVerificationException.class);

        logger.info("-->  creating repository that cannot write any files, but suppress verification - should be acked");
        assertAcked(client.admin().cluster().preparePutRepository("test-repo-1")
                .setType(MockRepositoryModule.class.getCanonicalName()).setSettings(settings).setVerify(false));

        logger.info("-->  verifying repository");
        assertThrows(client.admin().cluster().prepareVerifyRepository("test-repo-1"), RepositoryVerificationException.class);

        File location = newTempDir(LifecycleScope.SUITE);

        logger.info("-->  creating repository");
        try {
            client.admin().cluster().preparePutRepository("test-repo-1")
                    .setType(MockRepositoryModule.class.getCanonicalName())
                    .setSettings(ImmutableSettings.settingsBuilder()
                                    .put("location", location)
                                    .put("localize_location", true)
                    ).get();
            fail("RepositoryVerificationException wasn't generated");
        } catch (RepositoryVerificationException ex) {
            assertThat(ex.getMessage(), containsString("is not shared"));
        }
    }

