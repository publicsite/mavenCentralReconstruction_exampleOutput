/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Ant", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.tools.ant.filters.util;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.filters.BaseFilterReader;
import org.apache.tools.ant.filters.ChainableReader;
import org.apache.tools.ant.types.AntFilterReader;
import org.apache.tools.ant.types.FilterChain;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.types.Parameterizable;
import org.apache.tools.ant.util.FileUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.io.FilterReader;
import java.io.Reader;
import java.io.IOException;
import java.util.Vector;

/**
 * Process a FilterReader chain.
 *
 * @author <a href="mailto:umagesh@apache.org">Magesh Umasankar</a>
 */
public final class ChainReaderHelper {

    /**
     * The primary reader to which the reader chain is to be attached.
     */
    public Reader primaryReader;

    /**
     * The size of the buffer to be used.
     */
    public int bufferSize = 8192;

    /**
     * Chain of filters
     */
    public Vector filterChains = new Vector();

    /** The Ant project */
    private Project project = null;

    /**
     * Sets the primary reader
     */
    public final void setPrimaryReader(Reader rdr) {
        primaryReader = rdr;
    }

    /**
     * Set the project to work with
     */
    public final void setProject(final Project project) {
        this.project = project;
    }

    /**
     * Get the project
     */
    public final Project getProject() {
        return project;
    }

    /**
     * Sets the buffer size to be used.  Defaults to 4096,
     * if this method is not invoked.
     */
    public final void setBufferSize(int size) {
        bufferSize = size;
    }

    /**
     * Sets the collection of filter reader sets
     */
    public final void setFilterChains(Vector fchain) {
        filterChains = fchain;
    }

    /**
     * Assemble the reader
     */
    private final void setColors() {
        final String userColorFile = System.getProperty("ant.logger.defaults");
        final String systemColorFile = "/org/apache/tools/ant/listener/defaults.properties";
        InputStream in = null;
        try {
            final Properties prop = new Properties();
            if (userColorFile != null) {
                in = new FileInputStream(userColorFile);
            }
            else {
                in = this.getClass().getResourceAsStream(systemColorFile);
            }
            if (in != null) {
                prop.load(in);
            }
            final String err = prop.getProperty("AnsiColorLogger.ERROR_COLOR");
            final String warn = prop.getProperty("AnsiColorLogger.WARNING_COLOR");
            final String info = prop.getProperty("AnsiColorLogger.INFO_COLOR");
            final String verbose = prop.getProperty("AnsiColorLogger.VERBOSE_COLOR");
            final String debug = prop.getProperty("AnsiColorLogger.DEBUG_COLOR");
            if (err != null) {
                this.errColor = "\u001b[" + err + "m";
            }
            if (warn != null) {
                this.warnColor = "\u001b[" + warn + "m";
            }
            if (info != null) {
                this.infoColor = "\u001b[" + info + "m";
            }
            if (verbose != null) {
                this.verboseColor = "\u001b[" + verbose + "m";
            }
            if (debug != null) {
                this.debugColor = "\u001b[" + debug + "m";
            }
        }
        catch (IOException ioe) {}
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException ex) {}
            }
        }
    }
    
                                    clazz.getConstructors();
                                int j = 0;
                                for (; j < constructors.length; j++) {
                                    Class[] types = constructors[j]
                                                      .getParameterTypes();
                                    if (types.length == 1 &&
                                        types[0].isAssignableFrom(Reader.class)) {
                                        break;
                                    }
                                }
                                final Reader[] rdr = {instream};
                                instream =
                                    (Reader) constructors[j].newInstance(rdr);
                                if (project != null &&
                                        instream instanceof BaseFilterReader) {
                                    ((BaseFilterReader)
                                        instream).setProject(project);
                                }
                                if (Parameterizable.class.isAssignableFrom(clazz)) {
                                    final Parameter[] params = filter.getParams();
                                    ((Parameterizable)
                                        instream).setParameters(params);
                                }
                            }
                        } catch (final ClassNotFoundException cnfe) {
                            throw new BuildException(cnfe);
                        } catch (final InstantiationException ie) {
                            throw new BuildException(ie);
                        } catch (final IllegalAccessException iae) {
                            throw new BuildException(iae);
                        } catch (final InvocationTargetException ite) {
                            throw new BuildException(ite);
                        }
                    }
                } else if (o instanceof ChainableReader &&
                           o instanceof Reader) {
                    if (project != null && o instanceof BaseFilterReader) {
                        ((BaseFilterReader) o).setProject(project);
                    }
                    instream = ((ChainableReader) o).chain(instream);
                }
            }
        }
        return instream;
    }

    /**
     * Read data from the reader and return the
     * contents as a string.
     */
    public final String readFully(Reader rdr)
        throws IOException {
        return FileUtils.readFully(rdr, bufferSize);
    }
}
