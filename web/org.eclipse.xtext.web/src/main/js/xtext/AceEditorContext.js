/*******************************************************************************
 * Copyright (c) 2015 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

define([], function() {
	
	/**
	 * An editor context mediates between the Xtext services and the Ace editor framework.
	 */
	function AceEditorContext(editor) {
		this._editor = editor;
		this._serverState = {};
		this._serverStateListeners = [];
		this._clientServiceState = {};
		this._clean = true;
		this._dirtyStateListeners = [];
		this._annotations = [];
		this._occurrenceMarkers = [];
	};

	AceEditorContext.prototype = {
		
		getEditor: function() {
			return this._editor;
		},
		
		getServerState: function() {
			return this._serverState;
		},
		
		updateServerState: function(currentText, currentStateId) {
			this._serverState.text = currentText;
			this._serverState.stateId = currentStateId;
			return this._serverStateListeners;
		},
		
		addServerStateListener: function(listener) {
			this._serverStateListeners.push(listener);
		},
		
		getClientServiceState: function() {
			return this._clientServiceState;
		},
		
		clearClientServiceState: function() {
			this._clientServiceState = {};
		},
		
		getCaretOffset: function() {
			var pos = this._editor.getCursorPosition();
			return this._editor.getSession().getDocument().positionToIndex(pos);
		},
		
		getLineStart: function(lineNumber) {
			var pos = this._editor.getCursorPosition();
			return pos.row;
		},
		
		getSelection: function() {
			var range = this._editor.getSelectionRange();
			var document = this._editor.getSession().getDocument();
        	return {
        		start: document.positionToIndex(range.start),
        		end: document.positionToIndex(range.end)
        	};
		},
		
		getText: function(start, end) {
			var session = this._editor.getSession();
			if (start && end) {
				var document = session.getDocument();
				var startPos = document.indexToPosition(start);
				var endPos = document.indexToPosition(end);
				var mRange = require('ace/range');
				return session.getTextRange(new mRange.Range(startPos.row, startPos.column, endPos.row, endPos.column));
			} else {
				return session.getValue();
			}
		},
		
		isDirty: function() {
			return !this._clean;
		},
		
		markClean: function(clean) {
			if (clean != this._clean) {
				for (var i = 0; i < this._dirtyStateListeners.length; i++) {
					this._dirtyStateListeners[i](clean);
				}
			}
			this._clean = clean;
		},
		
		addDirtyStateListener: function(listener) {
			this._dirtyStateListeners.push(listener);
		},
		
		clearUndoStack: function() {
			this._editor.getSession().getUndoManager().reset();
		},
		
		setCaretOffset: function(offset) {
			var pos = this._editor.getSession().getDocument().indexToPosition(offset);
			this._editor.moveCursorTo(pos.row, pos.column);
		},
		
		setSelection: function(selection) {
			if (this._editor.selection) {
				var document = this._editor.getSession().getDocument();
				var startPos = document.indexToPosition(selection.start);
				var endPos = document.indexToPosition(selection.end);
				this._editor.selection.setSelectionRange(new mRange.Range(startPos.row, startPos.column, endPos.row, endPos.column));
			}
		},
		
		setText: function(text, start, end) {
			var session = this._editor.getSession();
			var document = session.getDocument();
			if (!start)
				start = 0;
			if (!end)
				end = document.getValue().length;
			var startPos = document.indexToPosition(start);
			var endPos = document.indexToPosition(end);
			var mRange = require('ace/range');
			return session.replace(new mRange.Range(startPos.row, startPos.column, endPos.row, endPos.column), text);
		}
		
	};
	
	return AceEditorContext;
});