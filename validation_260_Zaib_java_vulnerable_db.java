    public LockoutPolicy getLockoutPolicy() {
        LockoutPolicy res = IdentityZoneHolder.get().getConfig().getClientLockoutPolicy();
        return res.getLockoutAfterFailures() != -1 ? res : defaultLockoutPolicy;
    }

    @Override
