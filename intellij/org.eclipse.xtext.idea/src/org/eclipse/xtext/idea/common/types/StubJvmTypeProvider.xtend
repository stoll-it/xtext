/*******************************************************************************
 * Copyright (c) 2015 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.idea.common.types

import com.intellij.openapi.module.Module
import com.intellij.openapi.progress.ProgressIndicatorProvider
import com.intellij.psi.PsiClass
import com.intellij.psi.impl.compiled.SignatureParsing
import com.intellij.psi.search.GlobalSearchScope
import java.text.StringCharacterIterator
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.xtend.lib.annotations.Accessors
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.common.types.JvmType
import org.eclipse.xtext.common.types.access.impl.AbstractRuntimeJvmTypeProvider
import org.eclipse.xtext.common.types.access.impl.ITypeFactory
import org.eclipse.xtext.common.types.access.impl.IndexedJvmTypeAccess
import org.eclipse.xtext.common.types.access.impl.TypeResourceServices
import org.eclipse.xtext.psi.IPsiModelAssociator
import org.eclipse.xtext.resource.ISynchronizable
import org.eclipse.xtext.service.OperationCanceledError
import org.eclipse.xtext.util.Strings

import static extension org.eclipse.xtext.idea.extensions.IdeaProjectExtensions.*

class StubJvmTypeProvider extends AbstractRuntimeJvmTypeProvider {

	val ITypeFactory<PsiClass, JvmDeclaredType> psiClassFactory
	@Accessors val Module module
	@Accessors val extension StubURIHelper uriHelper
	@Accessors val GlobalSearchScope searchScope

	new(Module module, ResourceSet resourceSet, IndexedJvmTypeAccess indexedJvmTypeAccess,
		TypeResourceServices services, IPsiModelAssociator psiModelAssociator, GlobalSearchScope searchScope) {
		super(resourceSet, indexedJvmTypeAccess, services)
		this.module = module
		this.searchScope = searchScope
		this.uriHelper = createStubURIHelper
		this.psiClassFactory = createPsiClassFactory(psiModelAssociator)
	}

	def createPsiClassFactory(IPsiModelAssociator psiModelAssociator) {
		new PsiBasedTypeFactory(uriHelper, psiModelAssociator)
	}

	protected def createStubURIHelper() {
		new StubURIHelper
	}

	override findTypeByName(String name) {
		doFindTypeByName(name, false)
	}

	override findTypeByName(String name, boolean binaryNestedTypeDelimiter) {
		var result = doFindTypeByName(name, false)
		if (result != null || isBinaryNestedTypeDelimiter(name, binaryNestedTypeDelimiter)) {
			return result
		}
		val nameVariants = new ClassNameVariants(name)
		while (result == null && nameVariants.hasNext) {
			val nextVariant = nameVariants.next
			result = doFindTypeByName(nextVariant, true)
		}
		result
	}

	def doFindTypeByName(String name, boolean traverseNestedTypes) {
		ProgressIndicatorProvider.checkCanceled
		val normalizedName = name.normalize
		val resourceURI = createResourceURI(normalizedName)
		val fragment = getFragment(normalizedName)
		switch resourceSet : resourceSet {
			ISynchronizable<ResourceSet>:
				resourceSet.execute [
					findType(resourceURI, fragment, traverseNestedTypes)
				]
			default:
				findType(resourceURI, fragment, traverseNestedTypes)
		}
	}

	protected def normalize(String name) {
		if (name.startsWith('[')) {
			SignatureParsing.parseTypeString(new StringCharacterIterator(name))
		} else {
			name
		}
	}

	def findType(URI resourceURI, String fragment, boolean traverseNestedTypes) {
		val indexedJvmTypeAccess = getIndexedJvmTypeAccess
		if (indexedJvmTypeAccess !== null) {
			val proxyURI = resourceURI.appendFragment(fragment)
			val candidate = indexedJvmTypeAccess.getIndexedJvmType(proxyURI, resourceSet)
			if (candidate instanceof JvmType) {
				return candidate
			}
		}
		ProgressIndicatorProvider.checkCanceled
		try {
			val resource = resourceSet.getResource(resourceURI, true)
			resource.findType(fragment, traverseNestedTypes)
		} catch (OperationCanceledError e) {
			throw e.wrapped
		}
	}

	protected def findType(Resource resource, String fragment, boolean traverseNestedTypes) {
		val result = resource.getEObject(fragment) as JvmType
		if (result != null || !traverseNestedTypes) {
			return result
		}
		val rootType = resource.contents.head
		if (rootType instanceof JvmDeclaredType) {
			val rootTypeName = resource.getURI.segment(1)
			val nestedTypeName = fragment.substring(rootTypeName.length + 1)
			val segments = Strings.split(nestedTypeName, '$')
			return findNestedType(rootType, segments, 0)
		}
		return null
	}

	protected override createMirrorForFQN(String name) {
		val psiClass = module.project.javaPsiFacade.findClassWithAlternativeResolvedEnabled(name, searchScope)
		if (psiClass == null || psiClass.containingClass != null) {
			return null
		}
		new PsiClassMirror(psiClass, psiClassFactory)
	}

	protected def getSearchScope() {
		searchScope
	}

}
