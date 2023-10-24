                    public void execute(String key, ActionMapping mapping) {
                        String location = key.substring(REDIRECT_ACTION_PREFIX
                                .length());
                        ServletRedirectResult redirect = new ServletRedirectResult();
                        container.inject(redirect);
                        String extension = getDefaultExtension();
                        if (extension != null && extension.length() > 0) {
                            location += "." + extension;
                        }
                        redirect.setLocation(location);
                        mapping.setResult(redirect);
                    }
                });
            }
        };
    }

    /**
     * Adds a parameter action.  Should only be called during initialization
     *
     * @param prefix          The string prefix to trigger the action
     * @param parameterAction The parameter action to execute
     * @since 2.1.0
     */
