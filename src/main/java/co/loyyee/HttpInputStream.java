package co.loyyee;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map;

public class HttpInputStream extends InputStream {
	private Reader source;
	private int byteRemaining;

	/**
	 * Chunking -"...the sender is responsible for breaking the data into chunks of known size and prepending each chunk with its length. The size of the pending chunk is provided as CRLF-delimited ASCII-formatted hexadecimal."
	 * Joshua Davies did a detail explain on what, why and how to chunk.
	 * @link Reference: at <a href="https://commandlinefanatic.com/cgi-bin/showarticle.cgi?article=art077"> Chunked transfer encoding</a> section.
	 *
	 * */
	private boolean chunked = false;

	public HttpInputStream(Reader source, Map<String, String> headers) throws IOException {
		this.source = source;
		String contentLength = headers.get("Content-Length").trim();
		if(contentLength != null) {
			try {
				byteRemaining = Integer.parseInt(contentLength);
			} catch (NumberFormatException e ) {
				throw new IOException("Malformed or missing 'Content-Length'");
			}
		} else if ("chunked".equals(headers.get("Transfer-Encoding"))) {
			chunked = true;
			byteRemaining = parseChunkSize();
		}
	}

	private int parseChunkSize() throws IOException {
		int b;
		int size = 0;
		while( (b = source.read()) != '\r') {
				size = (size << 4)	|
							(( b > '9') ?
									(( b > 'F')) ?
											(b - 'a'  + 10 ) :
											(b - 'A'  + 10 ) :
									(b - '0'));
		}
		/** consume trailing \n */
		if (source.read() != '\n') {
				throw  new IOException("Malformed or missing chunk encoding.");
		}
	return size;
	}


	/**
	 * Reads the next byte of data from the input stream. The value byte is
	 * returned as an {@code int} in the range {@code 0} to
	 * {@code 255}. If no byte is available because the end of the stream
	 * has been reached, the value {@code -1} is returned. This method
	 * blocks until input data is available, the end of the stream is detected,
	 * or an exception is thrown.
	 *
	 * @return the next byte of data, or {@code -1} if the end of the
	 * stream is reached.
	 * @throws IOException if an I/O error occurs.
	 */
	@Override
	public int read() throws IOException {
		if(byteRemaining == 0) {
			if(!chunked) {
				 return -1;
			}else {
				/**Read all until EOS (end of stream) */
				if ( source.read() != '\r' || source.read() != '\n') {
					throw  new IOException("Malformed or missing chunk encoding.");
				}
				byteRemaining = parseChunkSize();
				if(byteRemaining == 0) {
					return -1;
				}
			}
		}
			byteRemaining--;
			/** source is referring to Socket InputStream */
			return source.read();
	}
}
