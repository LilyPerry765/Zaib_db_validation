  public void run() {
    try {
      ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
      while (!closed) {
        Message msg = (Message) in.readObject();
        handle(msg);
      }
    } catch (EOFException eof) {
      // Remote side has closed the connection, just cleanup.
      try {
        close();
      } catch (Exception unused) {
        // no-op.
      }
    } catch (Exception e) {
      if (!closed) {
        LOG.log(Level.WARNING, "Error in inbound message handling.", e);
        try {
          close();
        } catch (Exception unused) {
          // no-op.
        }
      }
    }
  }

