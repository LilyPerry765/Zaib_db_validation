  public void testInvalidate() throws IOException {
    ProjectWorkspace workspace =
        TestDataHelper.createProjectWorkspaceForScenario(this, "parser_with_cell", tmp);
    workspace.setUp();

    // Warm the parser cache.
    TestContext context = new TestContext();
    ProcessResult runBuckResult =
        workspace.runBuckdCommand(context, "query", "deps(//Apps:TestAppsLibrary)");
    runBuckResult.assertSuccess();
    assertThat(
        runBuckResult.getStdout(),
        Matchers.containsString(
            "//Apps:TestAppsLibrary\n"
                + "//Libraries/Dep1:Dep1_1\n"
                + "//Libraries/Dep1:Dep1_2\n"
                + "bar//Dep2:Dep2"));

    // Save the parser cache to a file.
    NamedTemporaryFile tempFile = new NamedTemporaryFile("parser_data", null);
    runBuckResult =
        workspace.runBuckdCommand(context, "parser-cache", "--save", tempFile.get().toString());
    runBuckResult.assertSuccess();

    // Write an empty content to Apps/BUCK.
    Path path = tmp.getRoot().resolve("Apps/BUCK");
    byte[] data = {};
    Files.write(path, data);

    // Write an empty content to Apps/BUCK.
    Path invalidationJsonPath = tmp.getRoot().resolve("invalidation-data.json");
    String jsonData = "[{\"path\":\"Apps/BUCK\",\"status\":\"M\"}]";
    Files.write(invalidationJsonPath, jsonData.getBytes(StandardCharsets.UTF_8));

    context = new TestContext();
    // Load the parser cache to a new buckd context.
    runBuckResult =
        workspace.runBuckdCommand(
            context,
            "parser-cache",
            "--load",
            tempFile.get().toString(),
            "--changes",
            invalidationJsonPath.toString());
    runBuckResult.assertSuccess();

    // Perform the query again.
    try {
      workspace.runBuckdCommand(context, "query", "deps(//Apps:TestAppsLibrary)");
    } catch (HumanReadableException e) {
      assertThat(
          e.getMessage(), Matchers.containsString("//Apps:TestAppsLibrary could not be found"));
    }
  }
