    public void chaosSnapshotTest() throws Exception {
        final List<String> indices = new CopyOnWriteArrayList<>();
        Settings settings = settingsBuilder().put("action.write_consistency", "one").build();
        int initialNodes = between(1, 3);
        logger.info("--> start {} nodes", initialNodes);
        for (int i = 0; i < initialNodes; i++) {
            internalCluster().startNode(settings);
        }

        logger.info("-->  creating repository");
        assertAcked(client().admin().cluster().preparePutRepository("test-repo")
                .setType("fs").setSettings(ImmutableSettings.settingsBuilder()
                        .put("location", newTempDir(LifecycleScope.SUITE))
                        .put("compress", randomBoolean())
                        .put("chunk_size", randomIntBetween(100, 1000))));

        int initialIndices = between(1, 3);
        logger.info("--> create {} indices", initialIndices);
        for (int i = 0; i < initialIndices; i++) {
            createTestIndex("test-" + i);
            indices.add("test-" + i);
        }

        int asyncNodes = between(0, 5);
        logger.info("--> start {} additional nodes asynchronously", asyncNodes);
        ListenableFuture<List<String>> asyncNodesFuture = internalCluster().startNodesAsync(asyncNodes, settings);

        int asyncIndices = between(0, 10);
        logger.info("--> create {} additional indices asynchronously", asyncIndices);
        Thread[] asyncIndexThreads = new Thread[asyncIndices];
        for (int i = 0; i < asyncIndices; i++) {
            final int cur = i;
            asyncIndexThreads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    createTestIndex("test-async-" + cur);
                    indices.add("test-async-" + cur);

                }
            });
            asyncIndexThreads[i].start();
        }

        logger.info("--> snapshot");

        ListenableActionFuture<CreateSnapshotResponse> snapshotResponseFuture = client().admin().cluster().prepareCreateSnapshot("test-repo", "test-snap").setWaitForCompletion(true).setIndices("test-*").setPartial(true).execute();

        long start = System.currentTimeMillis();
        // Produce chaos for 30 sec or until snapshot is done whatever comes first
        int randomIndices = 0;
        while (System.currentTimeMillis() - start < 30000 && !snapshotIsDone("test-repo", "test-snap")) {
            Thread.sleep(100);
            int chaosType = randomInt(10);
            if (chaosType < 4) {
                // Randomly delete an index
                if (indices.size() > 0) {
                    String index = indices.remove(randomInt(indices.size() - 1));
                    logger.info("--> deleting random index [{}]", index);
                    internalCluster().wipeIndices(index);
                }
            } else if (chaosType < 6) {
                // Randomly shutdown a node
                if (cluster().size() > 1) {
                    logger.info("--> shutting down random node");
                    internalCluster().stopRandomDataNode();
                }
            } else if (chaosType < 8) {
                // Randomly create an index
                String index = "test-rand-" + randomIndices;
                logger.info("--> creating random index [{}]", index);
                createTestIndex(index);
                randomIndices++;
            } else {
                // Take a break
                logger.info("--> noop");
            }
        }

        logger.info("--> waiting for async indices creation to finish");
        for (int i = 0; i < asyncIndices; i++) {
            asyncIndexThreads[i].join();
        }

        logger.info("--> update index settings to back to normal");
        assertAcked(client().admin().indices().prepareUpdateSettings("test-*").setSettings(ImmutableSettings.builder()
                        .put(AbstractIndexStore.INDEX_STORE_THROTTLE_TYPE, "node")
        ));

        // Make sure that snapshot finished - doesn't matter if it failed or succeeded
        try {
            CreateSnapshotResponse snapshotResponse = snapshotResponseFuture.get();
            SnapshotInfo snapshotInfo = snapshotResponse.getSnapshotInfo();
            assertNotNull(snapshotInfo);
            logger.info("--> snapshot is done with state [{}], total shards [{}], successful shards [{}]", snapshotInfo.state(), snapshotInfo.totalShards(), snapshotInfo.successfulShards());
        } catch (Exception ex) {
            logger.info("--> snapshot didn't start properly", ex);
        }

        asyncNodesFuture.get();
        logger.info("--> done");
    }

