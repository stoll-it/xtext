/**
 * Copyright (c) 2015 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.xtend.core.idea.highlighting;

import com.intellij.ide.highlighter.JavaHighlightingColors;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.EditorColors;
import org.eclipse.xtend.ide.common.highlighting.XtendHighlightingStyles;
import org.eclipse.xtext.idea.highlighting.IHighlightingConfiguration;
import org.eclipse.xtext.xbase.idea.highlighting.XbaseHighlightingConfiguration;

/**
 * @author Jan Koehnlein - Initial contribution and API
 */
@SuppressWarnings("all")
public class XtendHighlightingConfiguration extends XbaseHighlightingConfiguration {
  @Override
  public void configure(final IHighlightingConfiguration.IHighlightingStyleAcceptor it) {
    super.configure(it);
    it.addStyle(XtendHighlightingStyles.RICH_TEXT_ID, "Template text", EditorColors.INJECTED_LANGUAGE_FRAGMENT);
    it.addStyle(XtendHighlightingStyles.RICH_TEXT_DELIMITER_ID, "Template guillemets", DefaultLanguageHighlighterColors.PARENTHESES);
    it.addStyle(XtendHighlightingStyles.INSIGNIFICANT_TEMPLATE_TEXT, "Insignificant template text", DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR);
    it.addStyle(XtendHighlightingStyles.POTENTIAL_LINE_BREAK, "Potential Line Break (if line is not empty)", DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR);
    it.addStyle(XtendHighlightingStyles.TEMPLATE_LINE_BREAK, "Template Line Break", DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR);
    it.addStyle(XtendHighlightingStyles.ACTIVE_ANNOTATION, "Active Annotation", JavaHighlightingColors.VALID_STRING_ESCAPE);
  }
}
