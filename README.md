## RichTextEditor
Replacement for Android's EditText, which allows user to input text with formatting (WYSIWYG)

## Motivation
We wanted to add rich text editing capabilities to our email app.
There are many open source projects available that provide rich text editing capabilities for Android. Unfortunately, we couldn't find any that was readily usable.
**Wasabeef's richeditor-android** is an excellent WYSIWYG Editor for Android and that acted as a starting point for this project.
Rich Text Editor decouples the input view from the formatting toolbar so that they can be placed in a layout according to the app's design.
It uses JavaScript interface callbacks to inform the formatting toolbar about which formatting actions are allowed/enabled at the current cursor position and the formatting toolbar enables/disables and highlights buttons accordingly.

##Usage
1. Download the source code and add it as a module dependency in your Android app
2. Replace Android's EditText in the layout file with RichEditText
3. Add RichTextActions view (which is the formatting toolbar) to the layout file. For most scenarios the ideal place for this bar would be just above the soft keyboard. To achieve this, add RichTextActions to the root RelativeLayout of your layout with android:layout_alignParentBottom parameter set to true
4. In your activity's onCreate method get references to both RichEditText and RichTextActions from your layout, and pass the RichTextActions reference to the RichEditText's *setRichTextActionsView* method
5. RichEditText's input hint can be set by calling *setHint* method
6. To pre-populate the RichEditText with some HTML, pass the source HTML to *setHtml* method
7. To get the input HTML from RichEditText call the *getHtml* method
8. Add a content change listener to RichEditText by calling *addChangeListener* method

##Screenshots
![alt tag](https://github.com/akshaydugar/RichTextEditor/blob/master/screenshots/Screenshot1.png)

![alt tag](https://github.com/akshaydugar/RichTextEditor/blob/master/screenshots/Screenshot2.png)
