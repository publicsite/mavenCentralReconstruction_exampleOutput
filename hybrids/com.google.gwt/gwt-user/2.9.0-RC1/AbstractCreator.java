/*
 * Copyright 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.validation.rebind;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.user.rebind.AbstractSourceCreator;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import java.io.PrintWriter;

/**
 * Abstract Class for Creating source files.
 * <p>
 * This class is not thread safe.
 */
public abstract class AbstractCreator extends AbstractSourceCreator {

  final GeneratorContext context;

  final TreeLogger logger;

  final JClassType validatorType;

  final BeanHelperCache cache;

  AbstractCreator(GeneratorContext context, TreeLogger logger,
      JClassType validatorType, BeanHelperCache cache) {
    this.context = context;
    this.logger = branch(logger, "Creating " + validatorType);
    this.validatorType = validatorType;
    this.cache = cache;
  }

  public final String create() throws UnableToCompleteException {
    SourceWriter sourceWriter = getSourceWriter(logger, context);
    if (sourceWriter != null) {
      writeClassBody(sourceWriter);
      sourceWriter.commit(logger);
    }
    return getQualifiedName();
  }

  protected void addImports(ClassSourceFileComposerFactory composerFactory,
      Class<?>... imports) {
    for (Class<?> imp : imports) {
      composerFactory.addImport(imp.getCanonicalName());
    }
  }

  protected abstract void compose(ClassSourceFileComposerFactory composerFactory);

  protected BeanHelper createBeanHelper(Class<?> clazz)
      throws UnableToCompleteException {
    return cache.createHelper(clazz, logger, context);
  }

  protected BeanHelper createBeanHelper(JClassType jType)
      throws UnableToCompleteException {
    return cache.createHelper(jType, logger, context);
  }

    public String toString() {
        return this.getTypeCanonicalName();
    }
    
  protected String getSimpleName() {
      final int length = getPackage().length();
      final String rawName = validatorType.getQualifiedSourceName().substring(
          length == 0 ? 0 : length + 1);
      return rawName.replace('.', '_') + "Impl";
    }

  protected abstract void writeClassBody(SourceWriter sourceWriter)
      throws UnableToCompleteException;

  private String getQualifiedName() {
    String packageName = getPackage();
    return (packageName == "" ? "" : packageName + ".") + getSimpleName();
  }

  private SourceWriter getSourceWriter(TreeLogger logger, GeneratorContext ctx) {
    String packageName = getPackage();
    String simpleName = getSimpleName();
    PrintWriter printWriter = ctx.tryCreate(logger, packageName, simpleName);
    if (printWriter == null) {
      return null;
    }

    ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(
        packageName, simpleName);
    compose(composerFactory);
    return composerFactory.createSourceWriter(ctx, printWriter);
  }
}