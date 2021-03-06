/**
 * Copyright (c) 2015 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.xtext.xtext.generator.model;

import com.google.common.base.Objects;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.google.inject.Injector;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.generator.IFileSystemAccess;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.parser.IEncodingProvider;
import org.eclipse.xtext.util.RuntimeIOException;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xtext.generator.IGuiceAwareGeneratorComponent;
import org.eclipse.xtext.xtext.generator.model.TypeReference;

@SuppressWarnings("all")
public class FileSystemAccess implements IFileSystemAccess2, IGuiceAwareGeneratorComponent {
  public static class Extensions {
    public boolean containsJavaFile(final IFileSystemAccess2 fsa, final TypeReference typeRef) {
      String _path = typeRef.getPath();
      String _plus = (_path + ".java");
      return fsa.isFile(_plus);
    }
    
    public boolean containsXtendFile(final IFileSystemAccess2 fsa, final TypeReference typeRef) {
      String _path = typeRef.getPath();
      String _plus = (_path + ".xtend");
      return fsa.isFile(_plus);
    }
  }
  
  @Inject
  private IEncodingProvider encodingProvider;
  
  private final URI baseUri;
  
  public FileSystemAccess(final String path) {
    URI _createFileURI = URI.createFileURI(path);
    this.baseUri = _createFileURI;
  }
  
  @Override
  public void initialize(final Injector injector) {
    injector.injectMembers(this);
  }
  
  protected Charset getCharset(final URI uri) {
    String _encoding = this.encodingProvider.getEncoding(uri);
    return Charset.forName(_encoding);
  }
  
  @Override
  public URI getURI(final String path) {
    return this.getURI(path, IFileSystemAccess.DEFAULT_OUTPUT);
  }
  
  @Override
  public URI getURI(final String path, final String outputConfiguration) {
    boolean _equals = Objects.equal(outputConfiguration, IFileSystemAccess.DEFAULT_OUTPUT);
    if (_equals) {
      String[] _split = path.split("/");
      return this.baseUri.appendSegments(_split);
    } else {
      throw new IllegalArgumentException(("Unsupported configuration: " + outputConfiguration));
    }
  }
  
  @Override
  public boolean isFile(final String path) throws RuntimeIOException {
    return this.isFile(path, IFileSystemAccess.DEFAULT_OUTPUT);
  }
  
  @Override
  public boolean isFile(final String path, final String outputConfiguration) throws RuntimeIOException {
    boolean _equals = Objects.equal(outputConfiguration, IFileSystemAccess.DEFAULT_OUTPUT);
    if (_equals) {
      URI _uRI = this.getURI(path);
      String _fileString = _uRI.toFileString();
      final File file = new File(_fileString);
      boolean _and = false;
      boolean _exists = file.exists();
      if (!_exists) {
        _and = false;
      } else {
        boolean _isDirectory = file.isDirectory();
        boolean _not = (!_isDirectory);
        _and = _not;
      }
      return _and;
    } else {
      throw new IllegalArgumentException(("Unsupported configuration: " + outputConfiguration));
    }
  }
  
  @Override
  public void deleteFile(final String fileName) {
    this.deleteFile(fileName, IFileSystemAccess.DEFAULT_OUTPUT);
  }
  
  @Override
  public void deleteFile(final String fileName, final String outputConfiguration) {
    boolean _equals = Objects.equal(outputConfiguration, IFileSystemAccess.DEFAULT_OUTPUT);
    if (_equals) {
      URI _uRI = this.getURI(fileName);
      String _fileString = _uRI.toFileString();
      final File file = new File(_fileString);
      file.delete();
    } else {
      throw new IllegalArgumentException(("Unsupported configuration: " + outputConfiguration));
    }
  }
  
  @Override
  public void generateFile(final String fileName, final CharSequence contents) {
    this.generateFile(fileName, IFileSystemAccess.DEFAULT_OUTPUT, contents);
  }
  
  @Override
  public void generateFile(final String fileName, final String outputConfiguration, final CharSequence contents) {
    try {
      boolean _equals = Objects.equal(outputConfiguration, IFileSystemAccess.DEFAULT_OUTPUT);
      if (_equals) {
        final URI uri = this.getURI(fileName);
        String _fileString = uri.toFileString();
        final File file = new File(_fileString);
        final File parent = file.getParentFile();
        if ((parent != null)) {
          parent.mkdirs();
        }
        Charset _charset = this.getCharset(uri);
        Files.write(contents, file, _charset);
      } else {
        throw new IllegalArgumentException(("Unsupported configuration: " + outputConfiguration));
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Override
  public void generateFile(final String fileName, final InputStream content) throws RuntimeIOException {
    this.generateFile(fileName, IFileSystemAccess.DEFAULT_OUTPUT, content);
  }
  
  @Override
  public void generateFile(final String fileName, final String outputConfiguration, final InputStream content) throws RuntimeIOException {
    try {
      boolean _equals = Objects.equal(outputConfiguration, IFileSystemAccess.DEFAULT_OUTPUT);
      if (_equals) {
        final URI uri = this.getURI(fileName);
        String _fileString = uri.toFileString();
        final File file = new File(_fileString);
        final File parent = file.getParentFile();
        if ((parent != null)) {
          parent.mkdirs();
        }
        Charset _charset = this.getCharset(uri);
        final BufferedWriter fileWriter = Files.newWriter(file, _charset);
        try {
          int c = content.read();
          while ((c >= 0)) {
            {
              fileWriter.write(c);
              int _read = content.read();
              c = _read;
            }
          }
        } finally {
          fileWriter.close();
        }
      } else {
        throw new IllegalArgumentException(("Unsupported configuration: " + outputConfiguration));
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Override
  public InputStream readBinaryFile(final String fileName) throws RuntimeIOException {
    return this.readBinaryFile(fileName, IFileSystemAccess.DEFAULT_OUTPUT);
  }
  
  @Override
  public InputStream readBinaryFile(final String fileName, final String outputConfiguration) throws RuntimeIOException {
    try {
      boolean _equals = Objects.equal(outputConfiguration, IFileSystemAccess.DEFAULT_OUTPUT);
      if (_equals) {
        URI _uRI = this.getURI(fileName);
        String _fileString = _uRI.toFileString();
        return new FileInputStream(_fileString);
      } else {
        throw new IllegalArgumentException(("Unsupported configuration: " + outputConfiguration));
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Override
  public CharSequence readTextFile(final String fileName) throws RuntimeIOException {
    return this.readTextFile(fileName, IFileSystemAccess.DEFAULT_OUTPUT);
  }
  
  @Override
  public CharSequence readTextFile(final String fileName, final String outputConfiguration) throws RuntimeIOException {
    try {
      boolean _equals = Objects.equal(outputConfiguration, IFileSystemAccess.DEFAULT_OUTPUT);
      if (_equals) {
        final URI uri = this.getURI(fileName);
        String _fileString = uri.toFileString();
        File _file = new File(_fileString);
        Charset _charset = this.getCharset(uri);
        return Files.toString(_file, _charset);
      } else {
        throw new IllegalArgumentException(("Unsupported configuration: " + outputConfiguration));
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
