            public void send(final String format, final Object... args) {
                descriptor.debug(logger, format, args);
            }
        }
        final Debug debug = new Debug();

        Set<User> users = null;

        final Run<?, ?> currentRun = context.getRun();
        if (currentRun == null) {
            debug.send("currentRun was null");
        } else {
            if (!Objects.equals(currentRun.getResult(), Result.FAILURE)) {
                debug.send("currentBuild did not fail");
            } else {
                users = new HashSet<>();
                debug.send("Collecting builds with suspects...");
                final HashSet<Run<?, ?>> buildsWithSuspects = new HashSet<>();
                Run<?, ?> firstFailedBuild = currentRun;
                Run<?, ?> candidate = currentRun;
                while (candidate != null) {
                    final Result candidateResult = candidate.getResult();
                    if ( candidateResult == null || !candidateResult.isWorseOrEqualTo(Result.FAILURE) ) {
                        break;
                    }
                    firstFailedBuild = candidate;
                    candidate = candidate.getPreviousCompletedBuild();
                }
                if (firstFailedBuild instanceof AbstractBuild) {
                    buildsWithSuspects.add(firstFailedBuild);
                } else {
                    debug.send("  firstFailedBuild was not an instance of AbstractBuild");
                }
                debug.send("Collecting suspects...");
                users.addAll(RecipientProviderUtilities.getChangeSetAuthors(buildsWithSuspects, debug));
                users.addAll(RecipientProviderUtilities.getUsersTriggeringTheBuilds(buildsWithSuspects, debug));
            }
        }
        if (users != null) {
            RecipientProviderUtilities.addUsers(users, context.getListener(), env, to, cc, bcc, debug);
        }
    }

    @Extension
    public static final class DescriptorImpl extends RecipientProviderDescriptor {
        @Override
        public String getDisplayName() {
            return "Suspects Causing the Build to Begin Failing";
        }
    }

}
