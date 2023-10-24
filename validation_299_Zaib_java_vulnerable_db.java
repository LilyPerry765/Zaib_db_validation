    public void setMethods(Set<String> methods) {
        this.methods = new HashSet<String>();
        for (String method : methods) {
            this.methods.add(method.toUpperCase());
        }
    }

    /**
     * @param authenticationEntryPoint the authenticationEntryPoint to set
     */
