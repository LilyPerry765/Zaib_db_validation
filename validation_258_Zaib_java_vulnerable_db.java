            public void send(final String format, final Object... args) {
                descriptor.debug(logger, format, args);
            }
        }
        final Debug debug = new Debug();
        Set<User> users = RecipientProviderUtilities.getChangeSetAuthors(Collections.<Run<?, ?>>singleton(context.getRun()), debug);
        RecipientProviderUtilities.addUsers(users, context.getListener(), env, to, cc, bcc, debug);
    }

    @Extension
    public static final class DescriptorImpl extends RecipientProviderDescriptor {
        @Override
        public String getDisplayName() {
            return "Developers";
        }
    }
}
