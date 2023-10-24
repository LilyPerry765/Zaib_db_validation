	private static ZipOutputStream getZipOutputStream(OutputStream out, Charset charset) {
		charset = (null == charset) ? DEFAULT_CHARSET : charset;
		return new ZipOutputStream(out, charset);
	}

