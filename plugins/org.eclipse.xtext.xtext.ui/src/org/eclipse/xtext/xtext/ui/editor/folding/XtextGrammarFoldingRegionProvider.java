/*******************************************************************************
 * Copyright (c) 2010 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.xtext.ui.editor.folding;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.AbstractRule;
import org.eclipse.xtext.ui.editor.folding.DefaultFoldingRegionProvider;

/**
 * @author Sven Efftinge - Initial contribution and API
 */
public class XtextGrammarFoldingRegionProvider extends DefaultFoldingRegionProvider {

	@Override
	protected boolean shouldProcessContent(EObject object) {
		return !(object instanceof AbstractRule);
	}
}
