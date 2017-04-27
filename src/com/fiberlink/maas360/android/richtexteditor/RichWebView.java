package com.fiberlink.maas360.android.richtexteditor;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Copyright (C) 2015 Wasabeef
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

public class RichWebView extends WebView
{

    public enum Type
    {
        BOLD,
        ITALIC,
        UNDERLINE,
        FORECOLOR,
        HILITECOLOR,
        UNORDEREDLIST,
        ORDEREDLIST,
        IMAGE
    }

    public enum StateType
    {
        ENABLE,
        ALLOW
    }

    public interface OnStateChangeListener
    {
        void onStateChanged(String text, List<Type> types, StateType stateType);
    }

    public interface AfterInitialLoadListener
    {
        void onAfterInitialLoad(boolean isReady);
    }

    public interface ScrollListener
    {
        void onScrollTo(int y);
    }

    private static final String SETUP_HTML = "file:///android_asset/editor.html";
    private static final String CALLBACK_SEPARATOR = "~!~!~!";
    private static final String JAVA_SCRIPT_INTERFACE_NAME = "JSInterface";
    private boolean isReady;
    private String mSaveStatePath = "";
    private String mContents;
    private OnStateChangeListener mStateChangeListener;
    private AfterInitialLoadListener mLoadListener;
    private ScrollListener mScrollListener;

    public RichWebView(Context context)
    {
        this(context, null);
    }

    public RichWebView(Context context, AttributeSet attrs)
    {
        this(context, attrs, android.R.attr.webViewStyle);
    }

    @SuppressLint ("SetJavaScriptEnabled")
    public RichWebView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        getSettings().setJavaScriptEnabled(true);
        getSettings().setAllowFileAccess(false);
        addJavascriptInterface(new EditorJavaScriptInterface(), JAVA_SCRIPT_INTERFACE_NAME);
        setWebChromeClient(new WebChromeClient());
        setWebViewClient(createWebviewClient());
        loadUrl(SETUP_HTML);

        applyAttributes(context, attrs);
    }

    protected EditorWebViewClient createWebviewClient()
    {
        return new EditorWebViewClient();
    }

    public void setStateChangeListener(OnStateChangeListener listener)
    {
        mStateChangeListener = listener;
    }

    public void setOnInitialLoadListener(AfterInitialLoadListener listener)
    {
        mLoadListener = listener;
    }

    public void setScrollListener(ScrollListener listener)
    {
        mScrollListener = listener;
    }

    private void handleCallback(String text)
    {
        String[] stringParts = text.split(CALLBACK_SEPARATOR);
        String allowedString = "";
        String enabledString = "";
        String y = "";
        if (stringParts.length > 0) {
            allowedString = stringParts[0];
            if (stringParts.length > 1) {
                enabledString = stringParts[1];
                if (stringParts.length > 2) {
                    y = stringParts[2];
                    if (stringParts.length > 3) {
                        mSaveStatePath = stringParts[3];
                        if (stringParts.length > 4) {
                            mContents = stringParts[4];
                        }
                    }
                }
            }
        }

        if (!TextUtils.isEmpty(y) && mScrollListener != null) {
            mScrollListener.onScrollTo((int) Float.parseFloat(y));
        }

        List<Type> types = new ArrayList<>();
        for (Type type : Type.values()) {
            if (TextUtils.indexOf(allowedString, type.name()) != -1) {
                types.add(type);
            }
        }

        if (mStateChangeListener != null) {
            mStateChangeListener.onStateChanged(allowedString, types, StateType.ALLOW);
        }

        types = new ArrayList<>();
        for (Type type : Type.values()) {
            if (TextUtils.indexOf(enabledString, type.name()) != -1) {
                types.add(type);
            }
        }

        if (mStateChangeListener != null) {
            mStateChangeListener.onStateChanged(enabledString, types, StateType.ENABLE);
        }

    }

    private void applyAttributes(Context context, AttributeSet attrs)
    {
        final int[] attrsArray = new int[] { android.R.attr.gravity };
        TypedArray ta = context.obtainStyledAttributes(attrs, attrsArray);

        int gravity = ta.getInt(0, NO_ID);
        switch (gravity)
        {
        case Gravity.LEFT:
            exec("javascript:RE.setTextAlign(\"left\")");
            break;
        case Gravity.RIGHT:
            exec("javascript:RE.setTextAlign(\"right\")");
            break;
        case Gravity.TOP:
            exec("javascript:RE.setVerticalAlign(\"top\")");
            break;
        case Gravity.BOTTOM:
            exec("javascript:RE.setVerticalAlign(\"bottom\")");
            break;
        case Gravity.CENTER_VERTICAL:
            exec("javascript:RE.setVerticalAlign(\"middle\")");
            break;
        case Gravity.CENTER_HORIZONTAL:
            exec("javascript:RE.setTextAlign(\"center\")");
            break;
        case Gravity.CENTER:
            exec("javascript:RE.setVerticalAlign(\"middle\")");
            exec("javascript:RE.setTextAlign(\"center\")");
            break;
        }

        ta.recycle();
    }

    public void setHtml(String contents)
    {
        if (contents == null) {
            contents = "";
        }
        mContents = contents;
        execSetHtml();
    }

    public void setHtmlAndPath(String contents, String path)
    {
        if (contents == null) {
            contents = "";
        }
        if (path == null) {
            path = "";
        }
        mContents = contents;
        mSaveStatePath = path;
        execSetHtml();
    }

    private void execSetHtml()
    {
        try {
            if (isReady) {
                load("javascript:RE.setHtmlAndPath('" + URLEncoder.encode(mContents, "UTF-8") + "','" + mSaveStatePath
                    + "');");
            }
            else {
                postDelayed(new Runnable() {
                    @Override
                    public void run()
                    {
                        execSetHtml();
                    }
                }, 100);
            }
        }
        catch (UnsupportedEncodingException e) {
            // No handling
        }
    }

    public String getHtml()
    {
        if (mContents == null) {
            return "";
        }
        return mContents;
    }

    public String getSaveStatePath()
    {
        if (mSaveStatePath == null) {
            return "";
        }
        return mSaveStatePath;
    }

    public void setEditorHeight(int px)
    {
        exec("javascript:RE.setHeight('" + px + "px');");
    }

    public void setEditorFontColor(int color)
    {
        String hex = convertHexColorString(color);
        exec("javascript:RE.setBaseTextColor('" + hex + "');");
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom)
    {
        super.setPadding(left, top, right, bottom);
        exec("javascript:RE.setPadding('" + left + "px', '" + top + "px', '" + right + "px', '" + bottom + "px');");
    }

    public void setPlaceholder(String placeholder)
    {
        exec("javascript:RE.setPlaceholder('" + placeholder + "');");
    }

    public void setBold()
    {
        exec("javascript:RE.setBold();");
    }

    public void setItalic()
    {
        exec("javascript:RE.setItalic();");
    }

    public void setUnderline()
    {
        exec("javascript:RE.setUnderline();");
    }

    public void setBullets()
    {
        exec("javascript:RE.setBullets();");
    }

    public void setNumbers()
    {
        exec("javascript:RE.setNumbers();");
    }

    public void setTextColor(int color)
    {
        exec("javascript:RE.prepareInsert();");

        String hex = convertHexColorString(color);
        exec("javascript:RE.setTextColor('" + hex + "');");
    }

    public void setTextBackgroundColor(int color)
    {
        exec("javascript:RE.prepareInsert();");

        String hex = convertHexColorString(color);
        exec("javascript:RE.setTextBackgroundColor('" + hex + "');");
    }

    public void setTextAndBackgroundColor(int textColor, int textBackgroundColor)
    {
        exec("javascript:RE.prepareInsert();");

        String hex1 = convertHexColorString(textColor);
        String hex2 = convertHexColorString(textBackgroundColor);
        exec("javascript:RE.setTextAndBackgroundColor('" + hex1 + "','" + hex2 + "');");
    }

    public void insertImage(String url, String alt)
    {
        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.insertImage('" + url + "', '" + alt + "');");
    }

    private String convertHexColorString(int color)
    {
        return String.format("#%06X", (0xFFFFFF & color));
    }

    protected void exec(final String trigger)
    {
        if (isReady) {
            load(trigger);
        }
        else {
            postDelayed(new Runnable() {
                @Override
                public void run()
                {
                    exec(trigger);
                }
            }, 100);
        }
    }

    private void load(String trigger)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript(trigger, null);
        }
        else {
            loadUrl(trigger);
        }
    }

    public class EditorWebViewClient extends WebViewClient
    {
        @Override
        public void onPageFinished(WebView view, String url)
        {
            isReady = url.equalsIgnoreCase(SETUP_HTML);
            if (mLoadListener != null) {
                mLoadListener.onAfterInitialLoad(isReady);
            }
        }
    }

    public class EditorJavaScriptInterface
    {
        @JavascriptInterface
        public void callback(String callbackString)
        {
            if (TextUtils.isEmpty(callbackString)) {
                return;
            }

            try {
                String decodedString = URLDecoder.decode(callbackString, "UTF-8");
                handleCallback(decodedString);
            }
            catch (UnsupportedEncodingException e) {
                // No handling
            }
        }
    }
}