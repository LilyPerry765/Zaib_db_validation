  private static void doZipDir(File dir, ZipOutputStream out) throws IOException {
    File[] children = dir.listFiles();
    if (children == null) {
      throw new IllegalStateException("Fail to list files of directory " + dir.getAbsolutePath());
    }
    for (File child : children) {
      doZip(child.getName(), child, out);
    }
  }

  /**
   * @see #unzip(File, File, Predicate)
   * @deprecated replaced by {@link Predicate<ZipEntry>} in 6.2.
   */
  @Deprecated
  @FunctionalInterface
