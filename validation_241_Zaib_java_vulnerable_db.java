    public Set<ControllerServiceNode> getControllerServices(final boolean recursive) {
        readLock.lock();
        try {
            final Set<ControllerServiceNode> services = new HashSet<>();
            services.addAll(controllerServices.values());

            if (recursive && parent.get() != null) {
                services.addAll(parent.get().getControllerServices(true));
            }

            return services;
        } finally {
            readLock.unlock();
        }
    }

    @Override
