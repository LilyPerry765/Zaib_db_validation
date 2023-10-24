    public void batchingShardUpdateTaskTest() throws Exception {

        final Client client = client();

        logger.info("-->  creating repository");
        assertAcked(client.admin().cluster().preparePutRepository("test-repo")
                .setType("fs").setSettings(ImmutableSettings.settingsBuilder()
                        .put("location", newTempDir())
                        .put("compress", randomBoolean())
                        .put("chunk_size", randomIntBetween(100, 1000))));

        assertAcked(prepareCreate("test-idx", 0, settingsBuilder().put("number_of_shards", between(1, 20))
                .put("number_of_replicas", 0)));
        ensureGreen();

        logger.info("--> indexing some data");
        final int numdocs = randomIntBetween(10, 100);
        IndexRequestBuilder[] builders = new IndexRequestBuilder[numdocs];
        for (int i = 0; i < builders.length; i++) {
            builders[i] = client().prepareIndex("test-idx", "type1", Integer.toString(i)).setSource("field1", "bar " + i);
        }
        indexRandom(true, builders);
        flushAndRefresh();

        final int numberOfShards = getNumShards("test-idx").numPrimaries;
        logger.info("number of shards: {}", numberOfShards);

        final ClusterService clusterService = internalCluster().clusterService(internalCluster().getMasterName());
        BlockingClusterStateListener snapshotListener = new BlockingClusterStateListener(clusterService, "update_snapshot [", "update snapshot state", Priority.HIGH);
        try {
            clusterService.addFirst(snapshotListener);
            logger.info("--> snapshot");
            ListenableActionFuture<CreateSnapshotResponse> snapshotFuture = client.admin().cluster().prepareCreateSnapshot("test-repo", "test-snap").setWaitForCompletion(true).setIndices("test-idx").execute();

            // Await until shard updates are in pending state.
            assertBusyPendingTasks("update snapshot state", numberOfShards);
            snapshotListener.unblock();

            // Check that the snapshot was successful
            CreateSnapshotResponse createSnapshotResponse = snapshotFuture.actionGet();
            assertEquals(SnapshotState.SUCCESS, createSnapshotResponse.getSnapshotInfo().state());
            assertEquals(numberOfShards, createSnapshotResponse.getSnapshotInfo().totalShards());
            assertEquals(numberOfShards, createSnapshotResponse.getSnapshotInfo().successfulShards());

        } finally {
            clusterService.remove(snapshotListener);
        }

        // Check that we didn't timeout
        assertFalse(snapshotListener.timedOut());
        // Check that cluster state update task was called only once
        assertEquals(1, snapshotListener.count());

        logger.info("--> close indices");
        client.admin().indices().prepareClose("test-idx").get();

        BlockingClusterStateListener restoreListener = new BlockingClusterStateListener(clusterService, "restore_snapshot[", "update snapshot state", Priority.HIGH);

        try {
            clusterService.addFirst(restoreListener);
            logger.info("--> restore snapshot");
            ListenableActionFuture<RestoreSnapshotResponse> futureRestore = client.admin().cluster().prepareRestoreSnapshot("test-repo", "test-snap").setWaitForCompletion(true).execute();

            // Await until shard updates are in pending state.
            assertBusyPendingTasks("update snapshot state", numberOfShards);
            restoreListener.unblock();

            RestoreSnapshotResponse restoreSnapshotResponse = futureRestore.actionGet();
            assertThat(restoreSnapshotResponse.getRestoreInfo().totalShards(), equalTo(numberOfShards));

        } finally {
            clusterService.remove(restoreListener);
        }

        // Check that we didn't timeout
        assertFalse(restoreListener.timedOut());
        // Check that cluster state update task was called only once
        assertEquals(1, restoreListener.count());
    }

