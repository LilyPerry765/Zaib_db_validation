    protected void execute() {

        Arrays.asList(controllersListStrings).forEach(
                cInfoString -> controllers.add(parseCInfoString(cInfoString)));
        DriverService service = get(DriverService.class);
        deviceId = DeviceId.deviceId(uri);
        DriverHandler h = service.createHandler(deviceId);
        ControllerConfig config = h.behaviour(ControllerConfig.class);
        print("before:");
        config.getControllers().forEach(c -> print(c.target()));
        try {
            if (removeAll) {
                if (!controllers.isEmpty()) {
                    print("Controllers list should be empty to remove all controllers");
                } else {
                    List<ControllerInfo> controllersToRemove = config.getControllers();
                    controllersToRemove.forEach(c -> print("Will remove " + c.target()));
                    config.removeControllers(controllersToRemove);
                }
            } else {
                if (controllers.isEmpty()) {
                    print("Controllers list is empty, cannot set/remove empty controllers");
                } else {
                    if (removeCont) {
                        print("Will remove specified controllers");
                        config.removeControllers(controllers);
                    } else {
                        print("Will add specified controllers");
                        config.setControllers(controllers);
                    }
                }
            }
        } catch (NullPointerException e) {
            print("No Device with requested parameters {} ", uri);
        }
        print("after:");
        config.getControllers().forEach(c -> print(c.target()));
        print("size %d", config.getControllers().size());
    }


