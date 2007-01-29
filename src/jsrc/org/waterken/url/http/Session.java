// Copyright 2002-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.url.http;

import org.waterken.url.io.BoundedInputStream;
import org.waterken.url.io.BoundedOutputStream;
import org.waterken.url.io.PipelineInputStream;
import org.waterken.url.io.Semaphore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

/**
 * An HTTP connection session to a particular server.
 *
 * @author Tyler
 */
public final class Session {

    private final org.waterken.url.Locator locator;   // The site locator.
    private String http_version;                // The server HTTP version.
    private boolean do_not_pipeline;            // Never pipeline requests?

    private final Semaphore writing;          // The lock for writing a request.
    private Connection most_recent;     // The most recent connection.

    /**
     * Constructs a <code>Session</code>.
     *
     * @param locator         The site locator.
     * @param http_version    The HTTP version of the server.
     * @param do_not_pipeline Should requests never be pipelined?
     */
    public Session(final org.waterken.url.Locator locator,
                   final String http_version,
                   final boolean do_not_pipeline) throws IOException {
        this.locator = locator;
        this.http_version = http_version;
        this.do_not_pipeline = do_not_pipeline;

        writing = new Semaphore(1);
        most_recent = new Connection();
    }

    // org.waterken.url.http.Session interface.

    /**
     * Creates a new request.
     *
     * @param target The request target.
     */
    public Connection request(final URL target) {
        return new Connection(target);
    }

    /**
     * An HTTP connection.
     */
    public final class Connection extends java.net.HttpURLConnection {

        // The connection info.
        private Connection predecessor; // The preceeding request.
        private Socket socket;          // The connection socket.
        private boolean closed;         // Done reading from the socket?

        // The response.
        private int response_headers;
        private String[] response_header_key;
        private String[] response_header_value;
        private PipelineInputStream response_message;

        // The request.
        private int request_headers;
        private String[] request_header_key;
        private String[] request_header_value;
        private OutputStream request_message;

        Connection(final URL url) {
            super(url);

            response_header_key = new String[8];
            response_header_value = new String[8];

            request_header_key = new String[4];
            request_header_value = new String[4];

            // Set the default Host header.
            request_header_key[0] = "host";
            request_header_value[0] = url.getHost();
            if (-1 != url.getPort()) {
                request_header_value[0] += ":" + url.getPort();
            }
            ++request_headers;
        }

        /**
         * The sentinel constructor.
         */
        Connection() throws java.net.MalformedURLException {
            super(new URL("http://localhost/"));
            closed = true;
        }

        // java.url.URLConnection interface.

        public String getHeaderField(final String name) {
            try {
                connect();
            } catch (final IOException _) {
            }

            int i = response_headers;
            while (0 != i-- &&
              !name.equalsIgnoreCase(response_header_key[i])) {
            }
            return 0 > i ? null : response_header_value[i];
        }

        public String getHeaderFieldKey(final int n) {
            try {
                connect();
            } catch (final IOException _) {
            }

            String r;
            try {
                r = response_header_key[n];
            } catch (final ArrayIndexOutOfBoundsException _) {
                r = null;
            }
            return r;
        }

        public String getHeaderField(final int n) {
            try {
                connect();
            } catch (final IOException _) {
            }

            String r;
            try {
                r = response_header_value[n];
            } catch (final ArrayIndexOutOfBoundsException _) {
                r = null;
            }
            return r;
        }

        public java.util.Map getHeaderFields() {
            try {
                connect();
            } catch (final IOException _) {
            }

            // TODO
            return java.util.Collections.EMPTY_MAP;
        }

        public void setRequestProperty(final String key, final String value) {
            if (connected) {
                throw new IllegalStateException("Already connected");
            }
            if (null == key) {
                throw new NullPointerException("key is null");
            }
            if (null == value) {
                throw new NullPointerException("value is null");
            }

            int i = 0;
            while (i != request_headers &&
              !key.equalsIgnoreCase(request_header_key[i])) {
                ++i;
            }
            request_header_key[i] = key;
            request_header_value[i] = value;
            if (i == request_headers) {
                if (++request_headers == request_header_key.length) {
                    System.arraycopy(request_header_key,
                                     0,
                                     request_header_key =
                                       new String[2 * request_headers],
                                     0,
                                     request_headers);
                    System.arraycopy(request_header_value,
                                     0,
                                     request_header_value =
                                       new String[2 * request_headers],
                                     0,
                                     request_headers);
                }
            }
        }

        public void addRequestProperty(final String key, final String value) {
            if (connected) {
                throw new IllegalStateException("Already connected");
            }
            if (null == key) {
                throw new NullPointerException("key is null");
            }
            if (null == value) {
                throw new NullPointerException("value is null");
            }

            int i = 0;
            while (i != request_headers &&
              !key.equalsIgnoreCase(request_header_key[i])) {
                ++i;
            }
            if (i == request_headers) {
                request_header_key[i] = key;
                request_header_value[i] = value;
                if (++request_headers == request_header_key.length) {
                    System.arraycopy(request_header_key,
                                     0,
                                     request_header_key =
                                       new String[2 * request_headers],
                                     0,
                                     request_headers);
                    System.arraycopy(request_header_value,
                                     0,
                                     request_header_value =
                                       new String[2 * request_headers],
                                     0,
                                     request_headers);
                }
            } else {
                request_header_value[i] += ", " + value;
            }
        }

        public String getRequestProperty(final String key) {

            if (connected) {
                throw new IllegalStateException("Already connected");
            }

            int i = 0;
            while (i != request_headers &&
              !key.equalsIgnoreCase(request_header_key[i])) {
                ++i;
            }
            return request_header_value[i];
        }

        public java.util.Map getRequestProperties() {

            if (connected) {
                throw new IllegalStateException("Already connected");
            }

            // TODO
            return java.util.Collections.EMPTY_MAP;
        }

        public void setDoOutput(final boolean do_output) {

            if (connected) {
                throw new IllegalStateException("Already connected");
            }

            doOutput = do_output;
            method = "POST";
        }

        public synchronized void connect() throws IOException {

            if (0 == response_headers) {
                // Check for a failed prior connect attempt.
                if (closed) {
                    throw new IOException();
                }

                // Make the connection.
                try {
                    // Send the request, and check for errors in writing the
                    // message body.
                    getOutputStream().close();

                    // Pump out the preceeding request.
                    if (!predecessor.closed) {
                        predecessor.connect();
                        predecessor.response_message.pump();
                    }

                    // Read in the response.
                    final InputStream in = socket.getInputStream();
                    final LineInput response = new LineInput(in, 128);

                    // Read the Status-Line.
                    final String line = response.readln();
                    if (!line.startsWith("HTTP/1.")) {
                        throw new java.net.SocketTimeoutException(
                          "Invalid Status-Line.");
                    }

                    // Parse the Status-Line.
                    final int begin_http_version = 0;
                    int end_http_version = "HTTP/1.".length();
                    while (-1 == " \t".indexOf(line.charAt(end_http_version))) {
                        ++end_http_version;
                    }
                    http_version =
                      line.substring(begin_http_version, end_http_version);
                    int begin_status = end_http_version + 1;
                    while (-1 != " \t".indexOf(line.charAt(begin_status))) {
                        ++begin_status;
                    }
                    int end_status = begin_status + 1;
                    while (end_status != line.length() &&
                      -1 == " \t".indexOf(line.charAt(end_status))) {
                        ++end_status;
                    }
                    responseCode =
                      Integer.parseInt(line.substring(begin_status,
                                                      end_status));
                    responseMessage = line.substring(end_status).trim();
                    response_header_value[0] = line;
                    response_headers = 1;

                    // Initialize the connection management.
                    boolean close = "HTTP/1.0".equals(http_version);

                    // Parse the response headers.
                    _readHeaders(response);

                    // Check for informational response.
                    if (100 <= responseCode && 200 > responseCode) {
                        switch (responseCode) {
                        case 101: {
                            // Switching protocols.
                            if (!do_not_pipeline) {
                                throw new IOException(
                                  "No Upgrade on pipelined connection!");
                            }
                            final Socket socket = this.socket;
                            this.socket = null;
                            closed = true;
                            throw new UpgradeProtocol(socket,
                                                      TokenList.decode(
                                                        getHeaderField(
                                                          "Upgrade")));
                        }
                        default:
                            // RFC 2616, section 10.1:
                            // Unexpected 1xx status responses MAY be ignored
                            // by a user agent.
                        }

                        // Wipe the response.
                        while (0 != response_headers) {
                            --response_headers;
                            response_header_key[response_headers] = null;
                            response_header_value[response_headers] = null;
                        }
                        responseCode = -1;
                        responseMessage = null;

                        // Wait for another response.
                        connect();
                    } else {
                        // Filter out the Connection headers.
                        for (int i = response_headers; 0 != i--;) {
                            if ("Connection".equalsIgnoreCase(
                              response_header_key[i])) {
                                final String[] token =
                                  TokenList.decode(response_header_value[i]);

                                // Remove the Connection header.
                                System.arraycopy(response_header_key,
                                                 i + 1,
                                                 response_header_key,
                                                 i,
                                                 response_headers - (i + 1));
                                System.arraycopy(response_header_value,
                                                 i + 1,
                                                 response_header_value,
                                                 i,
                                                 response_headers - (i + 1));
                                --response_headers;

                                // Remove the identified headers.
                                for (int j = token.length; 0 != j--;) {
                                    if ("close".equalsIgnoreCase(token[j])) {
                                        close = true;
                                    } else
                                    if ("keep-alive".equalsIgnoreCase(token[j])) {
                                        close = false;
                                    }

                                    // Remove the corresponding header.
                                    for (int k = response_headers; 0 != k--;) {
                                        if (token[j].equalsIgnoreCase(
                                          response_header_key[k])) {
                                            System.arraycopy(
                                              response_header_key,
                                              k + 1,
                                              response_header_key,
                                              k,
                                              response_headers - (k + 1));
                                            System.arraycopy(
                                              response_header_value,
                                              k + 1,
                                              response_header_value,
                                              k,
                                              response_headers - (k + 1));
                                            --response_headers;

                                            // Adjust the current index.
                                            if (k < i) {
                                                --i;
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Setup the response message.
                        class Closer extends java.io.FilterInputStream {

                            private boolean close;

                            Closer(final InputStream in, final boolean close) {
                                super(in);
                                this.close = close;
                            }

                            // java.io.InputStream interface.

                            public void close() throws IOException {
                                closed = true;
                                if (close) {
                                    socket.close();
                                }
                            }
                        }
                        InputStream message = new Closer(in, close);
                        if (close) {
                            do_not_pipeline = true;
                        }

                        // Reverse the transfer encodings.
                        for (int i = response_headers; 0 != i--;) {
                            if ("Transfer-Encoding".equalsIgnoreCase(
                              response_header_key[i])) {
                                final String[] token =
                                  TokenList.decode(response_header_value[i]);
                                for (int j = token.length; 0 != j--;) {
                                    if ("chunked".equalsIgnoreCase(token[j])) {
                                        if (!(message instanceof Closer)) {
                                            throw new IOException(
                                              "First token must be: chunked");
                                        }
                                        message =
                                          ChunkedInputStream.make(new FilterInputStream(
                                            message) {

                                              public void close()
                                                throws IOException {
                                                  // Read in the entity-header
                                                  // trailers.
                                                  _readHeaders(new LineInput(
                                                    this.in,
                                                    128));
                                                  super.close();
                                              }
                                          });
                                    } else
                                    if ("gzip".equalsIgnoreCase(token[j])) {
                                        message = new GZIPInputStream(message);
                                    } else
                                    if ("deflate".equalsIgnoreCase(token[j])) {
                                        message =
                                          new InflaterInputStream(message);
                                    } else
                                    if ("identity".equalsIgnoreCase(token[j])) {
                                        message = message;
                                    } else {
                                        throw new IOException(
                                          "Unrecognized transfer-coding: " +
                                            token[j]);
                                    }
                                }

                                // Remove the header.
                                System.arraycopy(response_header_key,
                                                 i + 1,
                                                 response_header_key,
                                                 i,
                                                 response_headers - (i + 1));
                                System.arraycopy(response_header_value,
                                                 i + 1,
                                                 response_header_value,
                                                 i,
                                                 response_headers - (i + 1));
                                --response_headers;
                            }
                        }

                        // Check for Content-Length.
                        if (message instanceof Closer) {
                            // Determine the length of the response entity.
                            if (204 == responseCode || 304 == responseCode ||
                              "HEAD".equals(method)) {
                                message = BoundedInputStream.make(message, 0);
                            } else {
                                final int content_length = getContentLength();
                                if (-1 != content_length) {
                                    message = BoundedInputStream.make(message,
                                                                      content_length);
                                } else {
                                    // Response terminated by connection close.
                                    do_not_pipeline = true;
                                }
                            }
                        }

                        // Make the response message pumpable.
                        response_message = new PipelineInputStream(message);
                    }

                    // Enable GC.
                    predecessor = null;
                } catch (final java.net.SocketTimeoutException e) {
                    // Connection is messed up.
                    if (!do_not_pipeline && !doOutput) {
                        // Probably a buggy server. Try again without
                        // pipelining.
                        do_not_pipeline = true;
                        socket.close();

                        // Wipe the response.
                        while (0 != response_headers) {
                            --response_headers;
                            response_header_key[response_headers] = null;
                            response_header_value[response_headers] = null;
                        }
                        responseCode = -1;
                        responseMessage = null;

                        // Redo the request.
                        connected = false;
                        closed = false;
                        request_message = null;
                        most_recent = predecessor;

                        // Wait for another response.
                        connect();
                    } else {
                        throw e;
                    }
                } finally {
                    if (null == response_message) {
                        do_not_pipeline = true;
                        closed = true;
                        if (null != socket) {
                            socket.close();
                        }
                    }
                }
            }
        }

        private void _readHeaders(final LineInput response)
          throws IOException {
            String line = response.readln();
            while (!"".equals(line)) {
                int i = 0;
                final int len = line.length();

                // Get the header name.
                i = line.indexOf(':', i);
                final String name = line.substring(0, i);

                // Skip whitespace.
                while (++i != len && -1 != " \t".indexOf(line.charAt(i))) {
                }

                // Get the header value.
                String value = line.substring(i);

                // Check for continuations.
                line = response.readln();
                while (line.startsWith(" ") || line.startsWith("\t")) {
                    value += line.substring(1);
                    line = response.readln();
                }

                // Add the header.
                response_header_key[response_headers] = name;
                response_header_value[response_headers] = value;
                if (++response_headers == response_header_key.length) {
                    System.arraycopy(response_header_key,
                                     0,
                                     response_header_key =
                                       new String[2 * response_headers],
                                     0,
                                     response_headers);
                    System.arraycopy(response_header_value,
                                     0,
                                     response_header_value =
                                       new String[2 * response_headers],
                                     0,
                                     response_headers);
                }
            }
        }

        public InputStream getInputStream() throws IOException {
            connect();

            // Check Status-Code.
            if (HTTP_OK != responseCode) {
                switch (responseCode) {
                case HTTP_NOT_FOUND:
                    throw new FileNotFoundException(getResponseMessage());
                default:
                    throw new IOException(getResponseMessage());
                }
            }

            return response_message;
        }

        public final OutputStream getOutputStream() throws IOException {
            if (!connected) {
                // Acquire write-access to the shared socket.
                final Runnable done_write = writing.acquire();
                try {
                    predecessor = most_recent;
                    most_recent = this;

                    // If pipelining is not supported, pump out the preceeding
                    // response.
                    if (do_not_pipeline) {
                        if (!predecessor.closed) {
                            predecessor.response_message.pump();
                        }
                    }

                    // Get an open socket to the target site.
                    if (predecessor.closed) {
                        // Try to reuse previous socket.
                        socket = predecessor.socket;
                        if (null == socket) {
                            socket = locator.locate(url.getAuthority(), null);
                        } else if (socket.isClosed()) {
                            socket = locator.locate(url.getAuthority(),
                                                    socket.getRemoteSocketAddress());
                        } else {
                            // Check that input is still live.
                            int so_timeout = socket.getSoTimeout();
                            try {
                                // Search for EOF.
                                socket.setSoTimeout(30);
                                while (-1 != socket.getInputStream().read()) {
                                }

                                // EOF found, open a new socket.
                                socket.close();
                                socket = locator.locate(url.getAuthority(),
                                                        socket.getRemoteSocketAddress());
                            } catch (final java.net.SocketTimeoutException _) {
                                // Assume socket is still live.
                                socket.setSoTimeout(so_timeout);
                            }
                        }
                    } else {
                        // The predecessor is still reading, so assume socket
                        // is still live.
                        socket = predecessor.socket;
                    }

                    // If message, ensure a Content-Type is set.
                    if (doOutput &&
                      null == getRequestProperty("Content-Type")) {
                        setRequestProperty("Content-Type",
                                           "application/x-www-form-urlencoded");
                    }

                    // Write the request.
                    final OutputStream out = socket.getOutputStream();

                    // Write the Request-Line.
                    out.write(method.getBytes("US-ASCII"));
                    out.write(" ".getBytes("US-ASCII"));

                    if (locator instanceof org.waterken.url.proxy.Locator ||
                      0 <= http_version.compareTo("HTTP/1.1")) {
                        String request = url.toExternalForm();
                        int end_request = request.indexOf('#');
                        if (-1 != end_request) {
                            request = request.substring(0, end_request);
                        }
                        out.write(request.getBytes("US-ASCII"));
                    } else {
                        String path = url.getPath();
                        if (null == path || "".equals(path)) {
                            path = "/";
                        }
                        out.write(path.getBytes("US-ASCII"));
                        final String query = url.getQuery();
                        if (null != query) {
                            out.write("?".getBytes("US-ASCII"));
                            out.write(query.getBytes("US-ASCII"));
                        }
                    }

                    out.write(" HTTP/1.1\r\n".getBytes("US-ASCII"));

                    // Write out the client specified headers.
                    for (int i = 0; i != request_headers; ++i) {
                        out.write(request_header_key[i].getBytes("US-ASCII"));
                        out.write(": ".getBytes("US-ASCII"));
                        out.write(request_header_value[i].getBytes("US-ASCII"));
                        out.write("\r\n".getBytes("US-ASCII"));
                    }

                    // Write out the connection headers.
                    if ("HTTP/1.0".equals(http_version)) {
                        out.write("Connection: keep-alive\r\n".getBytes(
                          "US-ASCII"));
                    }

                    // Setup the request message.
                    request_message = new OutputStream() {

                        public void write(final int b) throws IOException {
                            out.write(b);
                        }

                        public void write(final byte[] b) throws IOException {
                            out.write(b);
                        }

                        public void write(final byte[] b,
                                          final int off,
                                          final int len) throws IOException {
                            out.write(b, off, len);
                        }

                        public void flush() throws IOException {
                            out.flush();
                        }

                        public void close() {
                            // Start writing the next request.
                            done_write.run();
                        }
                    };
                    if (doOutput) {
                        final String content_length =
                          getRequestProperty("Content-Length");
                        if (null != content_length) {
                            // End the headers.
                            out.write("\r\n".getBytes("US-ASCII"));

                            request_message = BoundedOutputStream.make(
                              request_message,
                              Integer.parseInt(content_length));
                        } else {
                            if (0 >= http_version.compareTo("HTTP/1.0") ||
                              "application/x-www-form-urlencoded".equalsIgnoreCase(
                                getRequestProperty("Content-Type"))) {
                                // Server might not support chunked encoding,
                                // so buffer the message to determine the
                                // length.
                                request_message =
                                  new ByteArrayOutputStream(1024) {

                                      private OutputStream base =
                                        request_message;

                                      public void close() throws IOException {
                                          if (null != base) {
                                              // End the headers.
                                              out.write(("Content-Length: " +
                                                count + "\r\n").getBytes(
                                                "US-ASCII"));
                                              out.write("\r\n".getBytes(
                                                "US-ASCII"));

                                              base.write(buf, 0, count);
                                              base.flush();
                                              base.close();
                                              base = null;
                                          }
                                      }
                                  };
                            } else {
                                // Server supports chunked encoding.
                                out.write("Transfer-Encoding: chunked\r\n".getBytes(
                                  "US-ASCII"));
                                out.write("\r\n".getBytes("US-ASCII"));

                                request_message =
                                  ChunkedOutputStream.make(1024,
                                                           request_message);
                            }
                        }
                    } else {
                        // End the headers.
                        out.write("\r\n".getBytes("US-ASCII"));
                        out.flush();

                        request_message = new OutputStream() {
                            private final OutputStream base = request_message;

                            public void write(final int b) throws IOException {
                                throw new IOException(
                                  "Must call setDoOutput(true)");
                            }

                            public void close() throws IOException {
                                base.close();
                            }
                        };
                    }

                    // Mark as connected.
                    connected = true;
                } finally {
                    if (!connected) {
                        // Socket is messed up.
                        connected = true;
                        socket = null;
                        closed = true;
                        done_write.run();
                    }
                }
            }

            return request_message;
        }

        // java.net.HttpURLConnection interface.

        public InputStream getErrorStream() {
            return HTTP_OK != responseCode ? response_message : null;
        }

        public void disconnect() {
            try {
                if (null != socket) {
                    socket.close();
                }
            } catch (final IOException _) {
            }
        }

        public boolean usingProxy() {
            return false;
        }

        public void setRequestMethod(final String method) {
            this.method = method;
        }

        // Connection interface.

        /**
         * Resumes an upgraded connection.
         */
        public void resume(final Socket upgraded) throws IOException {
            if (!closed || !connected || null != socket) {
                throw new IllegalStateException();
            }
            closed = false;
            socket = upgraded;

            // Wipe the 100 response.
            while (0 != response_headers) {
                --response_headers;
                response_header_key[response_headers] = null;
                response_header_value[response_headers] = null;
            }
            responseCode = -1;
            responseMessage = null;

            // Wait for another response.
            connect();
        }
    }
}
