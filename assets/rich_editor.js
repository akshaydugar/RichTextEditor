/**
 * Copyright (C) 2015 Wasabeef
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

var RE = {};
var changeDir = true;
var saveStateDivId = "CFMaaS360ARState";

RE.currentSelection = {
    "startContainer": 0,
    "startOffset": 0,
    "endContainer": 0,
    "endOffset": 0};

RE.editor = document.getElementById('editor');

RE.setHtml = function(contents) {
    RE.editor.innerHTML = "";
    var div = document.createElement('div');
    div.dir = "ltr";
    div.innerHTML = decodeURIComponent(contents.replace(/\+/g, '%20'));
    RE.editor.appendChild(div);
    RE.setCaretAtStart(div);
}

RE.setHtmlAndPath = function(contents, path) {
    RE.editor.innerHTML = "";
    var div = document.createElement('div');
    div.dir = "ltr";
    div.innerHTML = decodeURIComponent(contents.replace(/\+/g, '%20'));
    RE.editor.appendChild(div);
    RE.setCaretAtStart(div);

    if (path != null && path != "") {
        var node = document.getElementById(saveStateDivId);
        if (node == null) {
            node = div;
            node.id = saveStateDivId;
        }
        var pathArray = path.split(',');
        var length = pathArray.length;
        for(var i = 0; i < length - 1; i++) {
            var index = pathArray[i];
            node = node.childNodes[index];
        }

        var currentIndex = pathArray[length - 1];
        RE.setCaretAtPosition(node, currentIndex);

        var range = document.createRange();
        range.setStart(node, currentIndex);
        range.setEnd(node, currentIndex);
        var y = RE.getY(range);
        RE.blockAllItems(y, path);
    }
}

RE.getHtml = function() {
    return RE.editor.innerHTML;
}

RE.getText = function() {
    return RE.editor.innerText;
}

RE.setBaseTextColor = function(color) {
    RE.editor.style.color  = color;
}

RE.setBaseFontSize = function(size) {
    RE.editor.style.fontSize = size;
}

RE.setPadding = function(left, top, right, bottom) {
  RE.editor.style.paddingLeft = left;
  RE.editor.style.paddingTop = top;
  RE.editor.style.paddingRight = right;
  RE.editor.style.paddingBottom = bottom;
}

RE.setBackgroundColor = function(color) {
    document.body.style.backgroundColor = color;
}

RE.insertImage = function(url, alt) {
    var html = '<img src="' + url + '" alt="' + alt + '" /><br><br>';
    RE.insertHTML(html);
}

RE.setBackgroundImage = function(image) {
    RE.editor.style.backgroundImage = image;
}

RE.setWidth = function(size) {
    RE.editor.style.minWidth = size;
}

RE.setHeight = function(size) {
    document.body.style.minHeight = size;
}

RE.setTextAlign = function(align) {
    RE.editor.style.textAlign = align;
}

RE.setVerticalAlign = function(align) {
    RE.editor.style.verticalAlign = align;
}

RE.setPlaceholder = function(placeholder) {
    RE.editor.setAttribute("placeholder", placeholder);
}

RE.undo = function() {
    document.execCommand('undo', false, null);
}

RE.redo = function() {
    document.execCommand('redo', false, null);
}

RE.setBold = function() {
    document.execCommand('bold', false, null);
}

RE.setItalic = function() {
    document.execCommand('italic', false, null);
}

RE.setSubscript = function() {
    document.execCommand('subscript', false, null);
}

RE.setSuperscript = function() {
    document.execCommand('superscript', false, null);
}

RE.setStrikeThrough = function() {
    document.execCommand('strikeThrough', false, null);
}

RE.setUnderline = function() {
    document.execCommand('underline', false, null);
}

RE.setBullets = function() {
    document.execCommand('InsertUnorderedList', false, null);
}

RE.setNumbers = function() {
    document.execCommand('InsertOrderedList', false, null);
}

RE.setTextColor = function(color) {
    RE.restorerange();
    document.execCommand("styleWithCSS", null, true);
    document.execCommand('foreColor', false, color);
    document.execCommand("styleWithCSS", null, false);
}

RE.setTextBackgroundColor = function(color) {
    RE.restorerange();
    document.execCommand("styleWithCSS", null, true);
    document.execCommand('hiliteColor', false, color);
    document.execCommand("styleWithCSS", null, false);
}

RE.setTextAndBackgroundColor = function(color1, color2) {
    RE.restorerange();
    document.execCommand("styleWithCSS", null, true);
    document.execCommand('foreColor', false, color1);
    document.execCommand('hiliteColor', false, color2);
    document.execCommand("styleWithCSS", null, false);
}

RE.setHeading = function(heading) {
    document.execCommand('formatBlock', false, '<h'+heading+'>');
}

RE.setIndent = function() {
    document.execCommand('indent', false, null);
}

RE.setOutdent = function() {
    document.execCommand('outdent', false, null);
}

RE.setJustifyLeft = function() {
    document.execCommand('justifyLeft', false, null);
}

RE.setJustifyCenter = function() {
    document.execCommand('justifyCenter', false, null);
}

RE.setJustifyRight = function() {
    document.execCommand('justifyRight', false, null);
}

RE.setBlockquote = function() {
    document.execCommand('formatBlock', false, '<blockquote>');
}

RE.insertHTML = function(html) {
    RE.restorerange();
    document.execCommand('insertHTML', false, html);
}

RE.prepareInsert = function() {
    RE.backuprange();
}

RE.backuprange = function(){
    var selection = window.getSelection();
    if (selection.rangeCount > 0) {
      var range = selection.getRangeAt(0);
      RE.currentSelection = {
          "startContainer": range.startContainer,
          "startOffset": range.startOffset,
          "endContainer": range.endContainer,
          "endOffset": range.endOffset};
    }
}

RE.restorerange = function(){
    var selection = window.getSelection();
    selection.removeAllRanges();
    var range = document.createRange();
    range.setStart(RE.currentSelection.startContainer, RE.currentSelection.startOffset);
    range.setEnd(RE.currentSelection.endContainer, RE.currentSelection.endOffset);
    selection.addRange(range);
}

RE.focus = function() {
    var range = document.createRange();
    range.selectNodeContents(RE.editor);
    range.collapse(false);
    var selection = window.getSelection();
    selection.removeAllRanges();
    selection.addRange(range);
    RE.editor.focus();
}

RE.blurFocus = function() {
    RE.editor.blur();
}

RE.removeFormat = function() {
    execCommand('removeFormat', false, null);
}

RE.blockAllItems = function(y, saveStatePath) {
    var enabledItems = [];
    if (document.queryCommandState('bold')) {
        enabledItems.push('BOLD');
    }
    if (document.queryCommandState('italic')) {
        enabledItems.push('ITALIC');
    }
    if (document.queryCommandState('underline')) {
        enabledItems.push('UNDERLINE');
    }

    var enabledEditableItems = encodeURI(enabledItems.join(','));

    JSInterface.callback("~!~!~!" + enabledEditableItems + "~!~!~!" + y + "~!~!~!" + saveStatePath + "~!~!~!" + encodeURI(RE.getHtml()));
}

RE.allowAllItems = function(y, saveStatePath) {
    var allowedItems = [];
    allowedItems.push('BOLD');
    allowedItems.push('ITALIC');
    allowedItems.push('UNDERLINE');
    allowedItems.push('FORECOLOR');
    allowedItems.push('HILITECOLOR');
    allowedItems.push('UNORDEREDLIST');
    allowedItems.push('ORDEREDLIST');
    allowedItems.push('IMAGE');

    var allowedEditableItems = encodeURI(allowedItems.join(','));

    var enabledItems = [];
    if (document.queryCommandState('bold')) {
        enabledItems.push('BOLD');
    }
    if (document.queryCommandState('italic')) {
        enabledItems.push('ITALIC');
    }
    if (document.queryCommandState('underline')) {
        enabledItems.push('UNDERLINE');
    }

    var enabledEditableItems = encodeURI(enabledItems.join(','));

    JSInterface.callback(allowedEditableItems + "~!~!~!" + enabledEditableItems + "~!~!~!" + y + "~!~!~!" + saveStatePath + "~!~!~!" + encodeURI(RE.getHtml()));
}

RE.getY = function(range) {
    var newRange = range.cloneRange();
    newRange.collapse(false);
    var span = document.createElement("span");
    span.appendChild( document.createTextNode("\u200b") );
    newRange.insertNode(span);
    var y = span.offsetTop + (span.offsetHeight / 2);
    var spanOffsetParent = span.offsetParent;
    while (spanOffsetParent != null) {
        y = y + spanOffsetParent.offsetTop;
        spanOffsetParent = spanOffsetParent.offsetParent;
    }
    var spanParent = span.parentNode;
    spanParent.removeChild(span);
    spanParent.normalize();
    return y;
}

RE.getSaveStatePath = function(index) {
    var saveStateDiv = document.getElementById(saveStateDivId);
    if (saveStateDiv != null) {
        saveStateDiv.id = "";
    }
    var node = window.getSelection().anchorNode;
    var path = "";
    if (node != null) {
        path = index + path;
        while (node.nodeName != "DIV") {
            var child = node;
            var i = 0;
            while( (child = child.previousSibling) != null ) {
                i++;
            }
            path = i + "," + path;
            node = node.parentNode;
        }
        var curDiv = node;
        if (curDiv.id != "editor") {
            curDiv.id = saveStateDivId;
        }
    }
    return path;
}

// Event Listeners
RE.editor.addEventListener("click", function(e) {
    var selection = window.getSelection();
    if (selection.rangeCount > 0) {
        var range = selection.getRangeAt(0);
        var text = range.startContainer.data;
        var index = range.endOffset;

        var y = RE.getY(range);
        var path = RE.getSaveStatePath(index);

        if (typeof text === 'undefined' && index == 0) {
            // Click on beginning of html
            RE.allowAllItems(y, path);
        }
        else if (index > 0 && (text[index - 1] == ' ' || text.charCodeAt(index - 1) == 160)) {
            // Click after a space
            RE.allowAllItems(y, path);
        }
        else {
            RE.blockAllItems(y, path);
        }
    }
});

RE.editor.addEventListener("input", function(e) {
    var selection = window.getSelection();
    if (selection.rangeCount > 0) {
        var range = selection.getRangeAt(0);
        var text = range.startContainer.data;
        var index = range.endOffset;

        var y = RE.getY(range);
        var path = RE.getSaveStatePath(index);

        if (typeof text != 'undefined' && index > 0) {
            var char = text.charAt(index - 1);
            var letter = RE.checkLetter(char);
            if (letter && changeDir) {
                var rtl = RE.checkRTL(char);
                if (rtl) {
                    RE.setDirection("rtl", char);
                }
                else {
                    RE.setDirection("ltr", char);
                }
                changeDir = false;
            }
        }

        if (typeof text == 'undefined') {
            // User just entered newline or something unknown
            RE.allowAllItems(y, path);
            changeDir = true;
        }
        else if (index > 0 && text.charCodeAt(index - 1) == 160) {
            // User just entered space
            RE.allowAllItems(y, path);
        }
        else {
            RE.blockAllItems(y, path);
        }
    }
});

document.addEventListener("selectionchange", function() {
    RE.backuprange();
    var selection = window.getSelection();
    if (selection.rangeCount > 0) {
        var range = selection.getRangeAt(0);
        var start = range.startOffset;
        var end = range.endOffset;

        var y = RE.getY(range);

        if (start >= 0 && end >= 0 && end - start > 0) {
            RE.allowAllItems(y, "");
        }
    }
});

RE.checkRTL = function(s) {
    var ltrChars        = 'A-Za-z\u00C0-\u00D6\u00D8-\u00F6\u00F8-\u02B8\u0300-\u0590\u0800-\u1FFF'+'\u2C00-\uFB1C\uFDFE-\uFE6F\uFEFD-\uFFFF',
        rtlChars        = '\u0591-\u07FF\uFB1D-\uFDFD\uFE70-\uFEFC',
        rtlDirCheck     = new RegExp('^[^'+ltrChars+']*['+rtlChars+']');

    return rtlDirCheck.test(s);
};

RE.checkLetter = function(s) {
    var allChars        = 'A-Za-z\u00C0-\u00D6\u00D8-\u00F6\u00F8-\u02B8\u0300-\u0590\u0800-\u1FFF'+'\u2C00-\uFB1C\uFDFE-\uFE6F\uFEFD-\uFFFF'+'\u0591-\u07FF\uFB1D-\uFDFD\uFE70-\uFEFC',
        allCharsCheck     = new RegExp('['+allChars+']');

    return allCharsCheck.test(s);
}

RE.setDirection = function(direction, newChar) {
    var currentNode = document.getSelection().anchorNode;
    var node = document.getSelection().anchorNode;
    if (node != null) {
        while (node.nodeName != "DIV") {
            node = node.parentNode;
        }
        var curDiv = node;
        if (curDiv.dir == direction) {
            // Direction is correct, do nothing
            return;
        }

        var curDivContentHtml = curDiv.innerHTML.trim();
        if ((curDiv.dir == '' || curDivContentHtml == '' || curDivContentHtml == newChar) && curDiv.id != "editor") {
            // Just update direction of current div (if not the root div)
            curDiv.dir = direction;
            return;
        }

        var curDivContentText = curDiv.innerText.trim();
        // Create a new div with appropriate direction
        var endIndex = curDivContentText.indexOf(newChar);
        var index = endIndex - 1;
        while (index >= 0) {
            if (curDivContentText.charCodeAt(index) == 10) {
                break;
            }
            var letter = RE.checkLetter(curDivContentText.charAt(index));
            if (!letter) {
                index--;
                continue;
            }
            var rtl = RE.checkRTL(curDivContentText.charAt(index));
            if (direction == "ltr" && rtl == true) {
                break;
            }
            else if (direction == "rtl" && rtl == false) {
                break;
            }
            index--;
        }

        var newChars = curDivContentText.substring(index + 1, endIndex + 1);
        curDivContentHtml = curDivContentHtml.replace(newChars, '');
        curDiv.innerHTML = curDivContentHtml;

        var newDivContent = newChars;
        var div = document.createElement('div');
        div.dir = direction;
        div.innerHTML = newDivContent;
        curDiv.appendChild(div);
        RE.setCaretAtEnd(div);
    }
}

RE.setCaretAtEnd = function(node) {
    var range = document.createRange();
    var sel = window.getSelection();
    range.selectNodeContents(node);
    range.collapse(false);
    sel.removeAllRanges();
    sel.addRange(range);
    node.focus();
}

RE.setCaretAtStart = function(node) {
    var range = document.createRange();
    var sel = window.getSelection();
    range.selectNodeContents(node);
    range.collapse(true);
    sel.removeAllRanges();
    sel.addRange(range);
    node.focus();
}

RE.setCaretAtPosition = function(node, pos) {
    var range = document.createRange();
    var sel = window.getSelection();
    range.setStart(node, pos);
    range.collapse(true);
    sel.removeAllRanges();
    sel.addRange(range);
}