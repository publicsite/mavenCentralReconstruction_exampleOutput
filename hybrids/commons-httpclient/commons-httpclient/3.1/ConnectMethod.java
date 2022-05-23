/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//httpclient/src/java/org/apache/commons/httpclient/ConnectMethod.java,v 1.29 2004/06/24 21:39:52 mbecke Exp $
 * $Revision: 483949 $
 * $Date: 2006-12-08 12:34:50 +0100 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.commons.httpclient;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Establishes a tunneled HTTP connection via the CONNECT method.
 *
 * @author Ortwin Gl???ck
 * @author dIon Gillard
 * @author <a href="mailto:mbowler@GargoyleSoftware.com">Mike Bowler</a>
 * @author <a href="mailto:oleg@ural.ru">Oleg Kalnichevski</a>
 * @since 2.0
 * @version $Revision: 483949 $ $Date: 2006-12-08 12:34:50 +0100 (Fri, 08 Dec 2006) $
 */
public class ConnectMethod extends HttpMethodBase {
    
    /** the name of this method */
    public static final String NAME = "CONNECT";

    private final HostConfiguration targethost;

    /**
     * @deprecated use #ConnectMethod(HttpHost);
     * 
     * Create a connect method.
     * 
     * @since 3.0
     */
    {
        private HttpConnection httpConnection;
        private HttpParams connectionParams;
        
        public void closeIdleConnections(final long idleTimeout) {
        }
        
        public HttpConnection getConnection() {
            return this.httpConnection;
        }
        
        public void setConnectionParams(final HttpParams httpParams) {
            this.connectionParams = httpParams;
        }
        
        public HttpConnection getConnectionWithTimeout(final HostConfiguration hostConfiguration, final long timeout) {
            (this.httpConnection = new HttpConnection(hostConfiguration)).setHttpConnectionManager(this);
            this.httpConnection.getParams().setDefaults(this.connectionParams);
            return this.httpConnection;
        }
        
        public HttpConnection getConnection(final HostConfiguration hostConfiguration, final long timeout) throws HttpException {
            return this.getConnectionWithTimeout(hostConfiguration, timeout);
        }
        
        public HttpConnection getConnection(final HostConfiguration hostConfiguration) {
            return this.getConnectionWithTimeout(hostConfiguration, -1L);
        }
        
        public void releaseConnection(final HttpConnection conn) {
        }
        
        public HttpConnectionManagerParams getParams() {
            return null;
        }
        
        public void setParams(final HttpConnectionManagerParams params) {
        }
    }
}
    public String getPath() {
        if (this.targethost != null) {
            StringBuffer buffer = new StringBuffer();
            buffer.append(this.targethost.getHost()); 
            int port = this.targethost.getPort();
            if (port == -1) {
                port = this.targethost.getProtocol().getDefaultPort();  
            }
            buffer.append(':'); 
            buffer.append(port);
            return buffer.toString();
        } else {
            return "/";
        }
    }

    public URI getURI() throws URIException {
        String charset = getParams().getUriCharset();
        return new URI(getPath(), true, charset);
    }

    /**
     * This method does nothing. <tt>CONNECT</tt> request is not supposed 
     * to contain <tt>Cookie</tt> request header.
     *
     * @param state current state of http requests
     * @param conn the connection to use for I/O
     *
     * @throws IOException when errors occur reading or writing to/from the
     *         connection
     * @throws HttpException when a recoverable error occurs
     *  
     * @see HttpMethodBase#addCookieRequestHeader(HttpState, HttpConnection)
     */
    protected void addCookieRequestHeader(HttpState state, HttpConnection conn)
        throws IOException, HttpException {
        // Do nothing. Not applicable to CONNECT method
    }


    /**
     * Populates the request headers map to with additional {@link Header
     * headers} to be submitted to the given {@link HttpConnection}.
     *
     * <p>
     * This implementation adds <tt>User-Agent</tt>, <tt>Host</tt>,
     * and <tt>Proxy-Authorization</tt> headers, when appropriate.
     * </p>
     *
     * @param state the client state
     * @param conn the {@link HttpConnection} the headers will eventually be
     *        written to
     * @throws IOException when an error occurs writing the request
     * @throws HttpException when a HTTP protocol error occurs
     *
     * @see #writeRequestHeaders
     */
    protected void addRequestHeaders(HttpState state, HttpConnection conn)
        throws IOException, HttpException {
        LOG.trace("enter ConnectMethod.addRequestHeaders(HttpState, "
            + "HttpConnection)");
        addUserAgentRequestHeader(state, conn);
        addHostRequestHeader(state, conn);
        addProxyConnectionHeader(state, conn);
    }

    /**
     * Execute this method and create a tunneled HttpConnection.  If the method
     * is successful (i.e. the status is a 2xx) tunnelCreated() will be called
     * on the connection.
     *
     * @param state the current http state
     * @param conn the connection to write to
     * @return the http status code from execution
     * @throws HttpException when an error occurs writing the headers
     * @throws IOException when an error occurs writing the headers
     * 
     * @see HttpConnection#tunnelCreated()
     */
    public int execute(HttpState state, HttpConnection conn) 
    throws IOException, HttpException {

        LOG.trace("enter ConnectMethod.execute(HttpState, HttpConnection)");
        int code = super.execute(state, conn);
        if (LOG.isDebugEnabled()) {
            LOG.debug("CONNECT status code " + code);
        }
        return code;
    }

    /**
     * Special Connect request.
     *
     * @param state the current http state
     * @param conn the connection to write to
     * @throws IOException when an error occurs writing the request
     * @throws HttpException when an error occurs writing the request
     */
    protected void writeRequestLine(HttpState state, HttpConnection conn)
    throws IOException, HttpException {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getName()); 
        buffer.append(' '); 
        if (this.targethost != null) {
            buffer.append(getPath()); 
        } else {
            int port = conn.getPort();
            if (port == -1) {
                port = conn.getProtocol().getDefaultPort();  
            }
            buffer.append(conn.getHost()); 
            buffer.append(':'); 
            buffer.append(port); 
        }
        buffer.append(" "); 
        buffer.append(getEffectiveVersion()); 
        String line = buffer.toString();
        conn.printLine(line, getParams().getHttpElementCharset());
        if (Wire.HEADER_WIRE.enabled()) {
            Wire.HEADER_WIRE.output(line);
        }
    }

    /**
     * Returns <code>true</code> if the status code is anything other than
     * SC_OK, <code>false</code> otherwise.
     * 
     * @see HttpMethodBase#shouldCloseConnection(HttpConnection)
     * @see HttpStatus#SC_OK
     * 
     * @return <code>true</code> if the connection should be closed
     */
    protected boolean shouldCloseConnection(HttpConnection conn) {
        if (getStatusCode() == HttpStatus.SC_OK) {
            Header connectionHeader = null;
            if (!conn.isTransparent()) {
                connectionHeader = getResponseHeader("proxy-connection");
            }
            if (connectionHeader == null) {
                connectionHeader = getResponseHeader("connection");
            }
            if (connectionHeader != null) {
                if (connectionHeader.getValue().equalsIgnoreCase("close")) {
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("Invalid header encountered '" + connectionHeader.toExternalForm() 
                        + "' in response " + getStatusLine().toString());
                    }
                }
            }
            return false;
        } else {
            return super.shouldCloseConnection(conn);
        }
    }
    
    /** Log object for this class. */
    private static final Log LOG = LogFactory.getLog(ConnectMethod.class);

}
