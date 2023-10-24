  public void test_noVerification() throws Exception {
    // Sign a JWT and then attempt to verify it using None.
    JWT jwt = new JWT().setSubject("art");
    String encodedJWT = JWT.getEncoder().encode(jwt, HMACSigner.newSHA256Signer("secret"));

    expectException(MissingVerifierException.class, ()
        -> JWT.getDecoder().decode(encodedJWT));
  }

  @Test
