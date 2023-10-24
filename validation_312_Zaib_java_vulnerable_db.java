  private Cache verifyCacheExists() {
    int timeToWait = 0;
    Cache cache = null;
    while (timeToWait < TIME_TO_WAIT_FOR_CACHE) {
      try {
        cache = CacheFactory.getAnyInstance();
        break;
      } catch (Exception ignore) {
        // keep trying and hope for the best
      }
      try {
        Thread.sleep(250);
      } catch (InterruptedException ie) {
        Thread.currentThread().interrupt();
        break;
      }
      timeToWait += 250;
    }

    if (cache == null) {
      cache = new CacheFactory().create();
    }

    return cache;
  }

