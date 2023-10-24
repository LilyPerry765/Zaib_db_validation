	String hash(String plaintext, String salt, int iterations) throws EncryptionException;
	
	/**
	 * Encrypts the provided plaintext and returns a ciphertext string using the
	 * master secret key and default cipher transformation.
	 * </p><p>
     * <b>Compatibility with earlier ESAPI versions:</b> The symmetric encryption
     * in ESAPI 2.0 and later is not compatible with the encryption in ESAPI 1.4
     * or earlier. Not only are the interfaces slightly different, but they format
     * of the serialized encrypted data is incompatible. Therefore, if you have
     * encrypted data with ESAPI 1.4 or earlier, you must first encrypt it and
     * then re-encrypt it with ESAPI 2.0. Backward compatibility with ESAPI 1.4
     * was proposed to both the ESAPI Developers and ESAPI Users mailing lists
     * and voted down. More details are available in the ESAPI document
     * <a href="http://owasp-esapi-java.googlecode.com/svn/trunk/documentation/esapi4java-core-2.0-readme-crypto-changes.html">
     * Why Is OWASP Changing ESAPI Encryption?</a>
	 * </p><p>
	 * <b>Why this method is deprecated:</b> Most cryptographers strongly suggest
	 * that if you are creating crypto functionality for general-purpose use,
	 * at a minimum you should ensure that it provides authenticity, integrity,
	 * and confidentiality. This method only provides confidentiality, but not
	 * authenticity or integrity. Therefore, you are encouraged to use
	 * one of the other encryption methods referenced below. Because this
	 * method provides neither authenticity nor integrity, it may be
	 * removed in some future ESAPI Java release. Note: there are some cases
	 * where authenticity / integrity are not that important. For instance, consider
	 * a case where the encrypted data is never out of your application's control. For
	 * example, if you receive data that your application is encrypting itself and then
	 * storing the encrypted data in its own database for later use (and no other
	 * applications can query or update that column of the database), providing
	 * confidentiality alone might be sufficient. However, if there are cases
	 * where your application will be sending or receiving already encrypted data
	 * over an insecure, unauthenticated channel, in such cases authenticity and
	 * integrity of the encrypted data likely is important and this method should
	 * be avoided in favor of one of the other two.
	 * 
	 * @param plaintext
	 *      the plaintext {@code String} to encrypt. Note that if you are encrypting
	 *      general bytes, you should encypt that byte array to a String using
	 *      "UTF-8" encoding.
	 * 
	 * @return 
	 * 		the encrypted, base64-encoded String representation of 'plaintext' plus
	 * 		the random IV used.
	 * 
	 * @throws EncryptionException
	 *      if the specified encryption algorithm could not be found or another problem exists with 
	 *      the encryption of 'plaintext'
	 * 
	 * @see #encrypt(PlainText)
	 * @see #encrypt(SecretKey, PlainText)
	 * 
	 * @deprecated As of 1.4.2; use {@link #encrypt(PlainText)} instead, which
	 *			   also ensures message authenticity. This method will be
	 *             completely removed as of the next major release or point
	 *             release (3.0 or 2.1, whichever comes first) as per OWASP
	 *             deprecation policy.
	 */
