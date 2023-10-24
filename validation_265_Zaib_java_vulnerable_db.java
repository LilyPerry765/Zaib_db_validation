    private File getLocationUnderBuild(AbstractBuild build) {
        return new File(build.getRootDir(), "fileParameters/" + location);
    }

    /**
     * Default implementation from {@link File}.
     */
