/**
 * Copyright (c) 2015 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.xtend.idea.autobuild;

import com.google.common.io.CharStreams;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import junit.framework.TestCase;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtend.idea.LightXtendTest;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.idea.build.XtextAutoBuilderComponent;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.resource.impl.ChunkedResourceDescriptions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.junit.ComparisonFailure;

@SuppressWarnings("all")
public class IdeaIntegrationTest extends LightXtendTest {
  public void testManualDeletionOfGeneratedSourcesTriggersRebuild() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package otherPackage");
    _builder.newLine();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.myFixture.addFileToProject("otherPackage/Foo.xtend", _builder.toString());
    final VirtualFile file = this.myFixture.findFileInTempDir("xtend-gen/otherPackage/Foo.java");
    boolean _exists = file.exists();
    TestCase.assertTrue(_exists);
    Application _application = ApplicationManager.getApplication();
    final Runnable _function = new Runnable() {
      @Override
      public void run() {
        try {
          file.delete(null);
        } catch (Throwable _e) {
          throw Exceptions.sneakyThrow(_e);
        }
      }
    };
    _application.runWriteAction(_function);
    final VirtualFile regenerated = this.myFixture.findFileInTempDir("xtend-gen/otherPackage/Foo.java");
    boolean _exists_1 = regenerated.exists();
    TestCase.assertTrue(_exists_1);
  }
  
  public void testJavaDeletionTriggersError() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package otherPackage");
    _builder.newLine();
    _builder.newLine();
    _builder.append("import mypackage.Bar");
    _builder.newLine();
    _builder.newLine();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t");
    _builder.append("def void callToBar(Bar bar) {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("bar.doStuff()");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    final PsiFile xtendFile = this.myFixture.addFileToProject("otherPackage/Foo.xtend", _builder.toString());
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("package mypackage;");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("public class Bar {");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("public void doStuff() {");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    this.myFixture.addFileToProject("myPackage/Bar.java", _builder_1.toString());
    VirtualFile _virtualFile = xtendFile.getVirtualFile();
    this.myFixture.testHighlighting(true, true, true, _virtualFile);
    Application _application = ApplicationManager.getApplication();
    final Runnable _function = new Runnable() {
      @Override
      public void run() {
        try {
          final VirtualFile javaFile = IdeaIntegrationTest.this.myFixture.findFileInTempDir("myPackage/Bar.java");
          javaFile.delete(null);
        } catch (Throwable _e) {
          throw Exceptions.sneakyThrow(_e);
        }
      }
    };
    _application.runWriteAction(_function);
    try {
      VirtualFile _virtualFile_1 = xtendFile.getVirtualFile();
      this.myFixture.testHighlighting(true, true, true, _virtualFile_1);
      TestCase.fail("expecting errors");
    } catch (final Throwable _t) {
      if (_t instanceof ComparisonFailure) {
        final ComparisonFailure e = (ComparisonFailure)_t;
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
  }
  
  public void testJavaChangeTriggersError() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package otherPackage");
    _builder.newLine();
    _builder.newLine();
    _builder.append("import mypackage.Bar");
    _builder.newLine();
    _builder.newLine();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t");
    _builder.append("def void callToBar(Bar bar) {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("bar.doStuff()");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    final PsiFile xtendFile = this.myFixture.addFileToProject("otherPackage/Foo.xtend", _builder.toString());
    try {
      VirtualFile _virtualFile = xtendFile.getVirtualFile();
      this.myFixture.testHighlighting(true, true, true, _virtualFile);
      TestCase.fail("expecting errors");
    } catch (final Throwable _t) {
      if (_t instanceof ComparisonFailure) {
        final ComparisonFailure e = (ComparisonFailure)_t;
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("package mypackage;");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("public class Bar {");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("public void doStuff() {");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    this.myFixture.addFileToProject("myPackage/Bar.java", _builder_1.toString());
    VirtualFile _virtualFile_1 = xtendFile.getVirtualFile();
    this.myFixture.testHighlighting(true, true, true, _virtualFile_1);
  }
  
  public void testCyclicResolution() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package mypackage;");
    _builder.newLine();
    _builder.newLine();
    _builder.append("public class Bar {");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t");
    _builder.append("public void callToFoo(Foo foo) {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("foo.callToBar(this);");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.myFixture.addClass(_builder.toString());
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("package mypackage");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("class Foo {");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("def void callToBar(Bar bar) {");
    _builder_1.newLine();
    _builder_1.append("\t\t");
    _builder_1.append("bar.callToFoo(this)");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    final PsiFile xtendFile = this.myFixture.addFileToProject("mypackage/Foo.xtend", _builder_1.toString());
    VirtualFile _virtualFile = xtendFile.getVirtualFile();
    this.myFixture.testHighlighting(true, true, true, _virtualFile);
  }
  
  public void testCyclicResolution2() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package mypackage;");
    _builder.newLine();
    _builder.newLine();
    _builder.append("public class Bar extends Foo {");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t");
    _builder.append("public void someMethod() {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.myFixture.addClass(_builder.toString());
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("package mypackage");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("class Foo {");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("def void callToBar(Bar bar) {");
    _builder_1.newLine();
    _builder_1.append("\t\t");
    _builder_1.append("bar.someMethod");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    final PsiFile xtendFile = this.myFixture.addFileToProject("mypackage/Foo.xtend", _builder_1.toString());
    VirtualFile _virtualFile = xtendFile.getVirtualFile();
    this.myFixture.testHighlighting(true, true, true, _virtualFile);
  }
  
  public void testCyclicResolution3() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package mypackage;");
    _builder.newLine();
    _builder.newLine();
    _builder.append("public class Bar extends Foo<? extends Bar> {");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t");
    _builder.append("public void someMethod() {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.myFixture.addClass(_builder.toString());
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("package mypackage");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("class Foo<T extends Bar> {");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("def void callToBar(T bar) {");
    _builder_1.newLine();
    _builder_1.append("\t\t");
    _builder_1.append("bar.someMethod");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    final PsiFile xtendFile = this.myFixture.addFileToProject("mypackage/Foo.xtend", _builder_1.toString());
    VirtualFile _virtualFile = xtendFile.getVirtualFile();
    this.myFixture.testHighlighting(true, true, true, _virtualFile);
  }
  
  public void testCyclicResolution4() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package mypackage;");
    _builder.newLine();
    _builder.newLine();
    _builder.append("public class Bar extends Foo<Bar> {");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t");
    _builder.append("public void someMethod(Bar b) {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.myFixture.addClass(_builder.toString());
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("package mypackage");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("class Foo<T extends Bar> {");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("def void callToBar(T bar) {");
    _builder_1.newLine();
    _builder_1.append("\t\t");
    _builder_1.append("bar.someMethod(bar)");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    final PsiFile xtendFile = this.myFixture.addFileToProject("mypackage/Foo.xtend", _builder_1.toString());
    VirtualFile _virtualFile = xtendFile.getVirtualFile();
    this.myFixture.testHighlighting(true, true, true, _virtualFile);
  }
  
  public void testAffectedUpdated() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package otherPackage");
    _builder.newLine();
    _builder.newLine();
    _builder.append("import java.util.List");
    _builder.newLine();
    _builder.newLine();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("val list = OtherClass.getIt(\"foo\")");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.myFixture.addFileToProject("otherPackage/Foo.xtend", _builder.toString());
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("package otherPackage;");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("@SuppressWarnings(\"all\")");
    _builder_1.newLine();
    _builder_1.append("public class Foo {");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("private final Object list /* Skipped initializer because of errors */;");
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    this.assertFileContents("xtend-gen/otherPackage/Foo.java", _builder_1);
    StringConcatenation _builder_2 = new StringConcatenation();
    _builder_2.append("package otherPackage;");
    _builder_2.newLine();
    _builder_2.newLine();
    _builder_2.append("class OtherClass {");
    _builder_2.newLine();
    _builder_2.append("\t");
    _builder_2.append("public static java.util.List<String> getIt(CharSequence value) {");
    _builder_2.newLine();
    _builder_2.append("\t\t");
    _builder_2.append("return null");
    _builder_2.newLine();
    _builder_2.append("\t");
    _builder_2.append("}");
    _builder_2.newLine();
    _builder_2.append("}");
    _builder_2.newLine();
    this.myFixture.addFileToProject("otherPackage/OtherClass.java", _builder_2.toString());
    StringConcatenation _builder_3 = new StringConcatenation();
    _builder_3.append("package otherPackage;");
    _builder_3.newLine();
    _builder_3.newLine();
    _builder_3.append("import java.util.List;");
    _builder_3.newLine();
    _builder_3.append("import otherPackage.OtherClass;");
    _builder_3.newLine();
    _builder_3.newLine();
    _builder_3.append("@SuppressWarnings(\"all\")");
    _builder_3.newLine();
    _builder_3.append("public class Foo {");
    _builder_3.newLine();
    _builder_3.append("  ");
    _builder_3.append("private final List<String> list = OtherClass.getIt(\"foo\");");
    _builder_3.newLine();
    _builder_3.append("}");
    _builder_3.newLine();
    this.assertFileContents("xtend-gen/otherPackage/Foo.java", _builder_3);
    VirtualFile _findFileInTempDir = this.myFixture.findFileInTempDir("otherPackage/OtherClass.java");
    StringConcatenation _builder_4 = new StringConcatenation();
    _builder_4.append("package otherPackage;");
    _builder_4.newLine();
    _builder_4.newLine();
    _builder_4.append("class OtherClass {");
    _builder_4.newLine();
    _builder_4.append("\t");
    _builder_4.append("public static java.util.List<String> getIt(CharSequence value) {");
    _builder_4.newLine();
    _builder_4.append("\t\t");
    _builder_4.append("return null");
    _builder_4.newLine();
    _builder_4.append("\t");
    _builder_4.append("}");
    _builder_4.newLine();
    _builder_4.append("\t");
    _builder_4.append("public static String[] getIt(String value) {");
    _builder_4.newLine();
    _builder_4.append("\t\t");
    _builder_4.append("return null");
    _builder_4.newLine();
    _builder_4.append("\t");
    _builder_4.append("}");
    _builder_4.newLine();
    _builder_4.append("}");
    _builder_4.newLine();
    this.myFixture.saveText(_findFileInTempDir, _builder_4.toString());
    StringConcatenation _builder_5 = new StringConcatenation();
    _builder_5.append("package otherPackage;");
    _builder_5.newLine();
    _builder_5.newLine();
    _builder_5.append("import otherPackage.OtherClass;");
    _builder_5.newLine();
    _builder_5.newLine();
    _builder_5.append("@SuppressWarnings(\"all\")");
    _builder_5.newLine();
    _builder_5.append("public class Foo {");
    _builder_5.newLine();
    _builder_5.append("  ");
    _builder_5.append("private final String[] list = OtherClass.getIt(\"foo\");");
    _builder_5.newLine();
    _builder_5.append("}");
    _builder_5.newLine();
    this.assertFileContents("xtend-gen/otherPackage/Foo.java", _builder_5);
  }
  
  public void testTraceFilesGeneratedAndDeleted() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package otherPackage");
    _builder.newLine();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.myFixture.addFileToProject("otherPackage/Foo.xtend", _builder.toString());
    VirtualFile _findFileInTempDir = this.myFixture.findFileInTempDir("xtend-gen/otherPackage/Foo.java");
    boolean _exists = _findFileInTempDir.exists();
    TestCase.assertTrue(_exists);
    VirtualFile _findFileInTempDir_1 = this.myFixture.findFileInTempDir("xtend-gen/otherPackage/.Foo.java._trace");
    boolean _exists_1 = _findFileInTempDir_1.exists();
    TestCase.assertTrue(_exists_1);
    VirtualFile _findFileInTempDir_2 = this.myFixture.findFileInTempDir("otherPackage/Foo.xtend");
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("package otherPackage;");
    _builder_1.newLine();
    _builder_1.append("class OtherClass {");
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    this.myFixture.saveText(_findFileInTempDir_2, _builder_1.toString());
    VirtualFile _findFileInTempDir_3 = this.myFixture.findFileInTempDir("xtend-gen/otherPackage/Foo.java");
    TestCase.assertNull(_findFileInTempDir_3);
    VirtualFile _findFileInTempDir_4 = this.myFixture.findFileInTempDir("xtend-gen/otherPackage/.Foo.java._trace");
    TestCase.assertNull(_findFileInTempDir_4);
    VirtualFile _findFileInTempDir_5 = this.myFixture.findFileInTempDir("xtend-gen/otherPackage/OtherClass.java");
    boolean _exists_2 = _findFileInTempDir_5.exists();
    TestCase.assertTrue(_exists_2);
    VirtualFile _findFileInTempDir_6 = this.myFixture.findFileInTempDir("xtend-gen/otherPackage/.OtherClass.java._trace");
    boolean _exists_3 = _findFileInTempDir_6.exists();
    TestCase.assertTrue(_exists_3);
  }
  
  public void testActiveAnnotation() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package otherPackage");
    _builder.newLine();
    _builder.newLine();
    _builder.append("import mypackage.Bar");
    _builder.newLine();
    _builder.append("import org.eclipse.xtend.lib.macro.Data");
    _builder.newLine();
    _builder.newLine();
    _builder.append("@Data class Foo {");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t");
    _builder.append("String myField");
    _builder.newLine();
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.myFixture.addFileToProject("otherPackage/Foo.xtend", _builder.toString());
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("package otherPackage;");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("import org.eclipse.xtend.lib.Data;");
    _builder_1.newLine();
    _builder_1.append("import org.eclipse.xtext.xbase.lib.Pure;");
    _builder_1.newLine();
    _builder_1.append("import org.eclipse.xtext.xbase.lib.util.ToStringHelper;");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("@Data");
    _builder_1.newLine();
    _builder_1.append("@SuppressWarnings(\"all\")");
    _builder_1.newLine();
    _builder_1.append("public class Foo {");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("private final String _myField;");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("public Foo(final String myField) {");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("super();");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("this._myField = myField;");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("@Override");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("@Pure");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("public int hashCode() {");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("final int prime = 31;");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("int result = 1;");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("result = prime * result + ((this._myField== null) ? 0 : this._myField.hashCode());");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("return result;");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("@Override");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("@Pure");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("public boolean equals(final Object obj) {");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("if (this == obj)");
    _builder_1.newLine();
    _builder_1.append("      ");
    _builder_1.append("return true;");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("if (obj == null)");
    _builder_1.newLine();
    _builder_1.append("      ");
    _builder_1.append("return false;");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("if (getClass() != obj.getClass())");
    _builder_1.newLine();
    _builder_1.append("      ");
    _builder_1.append("return false;");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("Foo other = (Foo) obj;");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("if (this._myField == null) {");
    _builder_1.newLine();
    _builder_1.append("      ");
    _builder_1.append("if (other._myField != null)");
    _builder_1.newLine();
    _builder_1.append("        ");
    _builder_1.append("return false;");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("} else if (!this._myField.equals(other._myField))");
    _builder_1.newLine();
    _builder_1.append("      ");
    _builder_1.append("return false;");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("return true;");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("@Override");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("@Pure");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("public String toString() {");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("String result = new ToStringHelper().toString(this);");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("return result;");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("@Pure");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("public String getMyField() {");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("return this._myField;");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    this.assertFileContents("xtend-gen/otherPackage/Foo.java", _builder_1);
  }
  
  public void testMoveFile() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package otherPackage");
    _builder.newLine();
    _builder.newLine();
    _builder.append("import mypackage.Bar");
    _builder.newLine();
    _builder.newLine();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t");
    _builder.append("def void callToBar(Bar bar) {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("bar.doStuff()");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    final PsiFile xtendFile = this.myFixture.addFileToProject("otherPackage/Foo.xtend", _builder.toString());
    final VirtualFile vf = xtendFile.getVirtualFile();
    final URI before = URI.createURI("temp:///src/otherPackage/Foo.xtend");
    final URI after = URI.createURI("temp:///src/Foo.xtend");
    ChunkedResourceDescriptions _index = this.getIndex();
    IResourceDescription _resourceDescription = _index.getResourceDescription(after);
    TestCase.assertNull(_resourceDescription);
    ChunkedResourceDescriptions _index_1 = this.getIndex();
    IResourceDescription _resourceDescription_1 = _index_1.getResourceDescription(before);
    TestCase.assertNotNull(_resourceDescription_1);
    Application _application = ApplicationManager.getApplication();
    final Runnable _function = new Runnable() {
      @Override
      public void run() {
        try {
          VirtualFile _parent = vf.getParent();
          VirtualFile _parent_1 = _parent.getParent();
          vf.move(null, _parent_1);
        } catch (Throwable _e) {
          throw Exceptions.sneakyThrow(_e);
        }
      }
    };
    _application.runWriteAction(_function);
    ChunkedResourceDescriptions _index_2 = this.getIndex();
    IResourceDescription _resourceDescription_2 = _index_2.getResourceDescription(after);
    TestCase.assertNotNull(_resourceDescription_2);
    ChunkedResourceDescriptions _index_3 = this.getIndex();
    IResourceDescription _resourceDescription_3 = _index_3.getResourceDescription(before);
    TestCase.assertNull(_resourceDescription_3);
  }
  
  public ChunkedResourceDescriptions getIndex() {
    Project _project = this.getProject();
    final XtextAutoBuilderComponent builder = _project.<XtextAutoBuilderComponent>getComponent(XtextAutoBuilderComponent.class);
    final XtextResourceSet rs = new XtextResourceSet();
    builder.installCopyOfResourceDescriptions(rs);
    final ChunkedResourceDescriptions index = ChunkedResourceDescriptions.findInEmfObject(rs);
    return index;
  }
  
  public void assertFileContents(final String path, final CharSequence sequence) {
    try {
      final VirtualFile file = this.myFixture.findFileInTempDir(path);
      String _string = sequence.toString();
      InputStream _inputStream = file.getInputStream();
      Charset _charset = file.getCharset();
      InputStreamReader _inputStreamReader = new InputStreamReader(_inputStream, _charset);
      String _string_1 = CharStreams.toString(_inputStreamReader);
      TestCase.assertEquals(_string, _string_1);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
