/*
 * Copyright (C) 2020 Nicolas Dagnas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *    Created by Nicolas Dagnas on 01-05-2020, updated on 03-06-20201.
 *
 */

package com.github.ndagnas.pickers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/** Defines a json picker dialog. */
@SuppressWarnings({"unused", "WeakerAccess"})
public class JSonPickerDialog extends ListPickerDialogBase implements PickerInterface {
    /* SELECTION_TYPES */

    /**
     * NODE_WITHOUT_CHILDREN_SELECT specifies that from list of json nodes without child has to be
     * selected. It is the default Selection Type.
     */
    public static final int NODE_WITHOUT_CHILD_SELECT = 0;

    /** ALL_NODE_SELECT specifies that all json nodes has to be selected. */
    public static final int ALL_NODE_SELECT = 1;

    /** SORT_BY_NAME specifies that list of json nodes is sorted by title. */
    public static final int SORT_BY_TITLE = 0;

    /** SORT_BY_LAST_MODIFIED specifies that list of json nodes is sorted by children existence. */
    public static final int SORT_BY_HAVE_CHILDREN = 1;

    /** SORT_ORDER_NORMAL specifies that list of json nodes is sorted by normal order. */
    public static final int SORT_ORDER_DISABLE = 0;

    /** SORT_ORDER_NORMAL specifies that list of json nodes is sorted by normal order. */
    public static final int SORT_ORDER_NORMAL = 1;

    /** SORT_ORDER_NORMAL specifies that list of json nodes is sorted by reverse order. */
    public static final int SORT_ORDER_REVERSE = 2;

    /** Defines an item of the list. */
    static class JSONItem {
        /**
         * Object initialisation.
         *
         * @param object json node object.
         * @param parent json node parent.
         */
        public JSONItem(JSONObject object, JSONItem parent) {
            this.object = object;
            this.parent = parent;
            this.listViewState = null;
        }

        /** Json node object. */
        public JSONObject object;

        /** List view state. */
        public Parcelable listViewState;

        /** Json node parent. */
        public JSONItem parent;
    }

    // Constants

    private static java.util.Locale DEF_LOCAL = java.util.Locale.getDefault();

    // Attributes

    private final Context mContext;
    private final int mSelectionType;
    private final String mTitleNodeName;
    private final String mSubTitleNodeName;
    private final String mChildrenNodeName;
    private final String mTitleMask;
    private final String mSubTitleMask;
    private final CharSequence mBackItemTitle;
    private final JSONObject mRootNode;
    private final int mSortBy;
    private final int mSortOrder;
    private final OnSingleChoiceValidationListener<JSONObject> mOnSingleChoiceValidationListener;
    private final OnMultiChoiceValidationListener<JSONObject> mOnMultiChoiceValidationListener;
    private final boolean mOneClickMode;
    private final boolean mTitleNodeNameDefined;
    private final boolean mSubTitleNodeNameDefined;
    private final boolean mChildrenNodeNameDefined;
    private final Comparator<JSONItem> mSorter;

    /**
     * Create a list picker dialog.
     *
     * @param builder a builder object contains dialog parameters.
     */
    private JSonPickerDialog(@NonNull Builder builder) {
        super(builder.P);

        this.mContext = builder.P.context;
        this.mSelectionType = builder.mSelectionType;
        this.mTitleNodeName =
                (builder.mTitleNodeName != null) ? builder.mTitleNodeName.toString() : "";
        this.mSubTitleNodeName =
                (builder.mSubTitleNodeName != null) ? builder.mSubTitleNodeName.toString() : "";
        this.mChildrenNodeName =
                (builder.mChildrenNodeName != null) ? builder.mChildrenNodeName.toString() : "";
        this.mTitleMask = (builder.mTitleMask != null) ? builder.mTitleMask.toString() : "";
        this.mSubTitleMask =
                (builder.mSubTitleMask != null) ? builder.mSubTitleMask.toString() : "";
        this.mBackItemTitle = builder.mBackItemTitle;
        this.mRootNode = builder.mRootNode;
        this.mSortBy = builder.mSortBy;
        this.mSortOrder = builder.mSortOrder;
        this.mOnSingleChoiceValidationListener = builder.mOnSingleChoiceValidationListener;
        this.mOnMultiChoiceValidationListener = builder.mOnMultiChoiceValidationListener;

        this.mOneClickMode = (builder.P.positiveButtonVisibility != View.VISIBLE);

        this.mTitleNodeNameDefined = (!TextUtils.isEmpty(this.mTitleNodeName));
        this.mSubTitleNodeNameDefined = (!TextUtils.isEmpty(this.mSubTitleNodeName));
        this.mChildrenNodeNameDefined = (!TextUtils.isEmpty(this.mChildrenNodeName));

        this.mSorter = createComparator(this);
    }

    /**
     * Create comparator for sort objects in list.
     *
     * @return A comparator object for sort list.
     */
    private static Comparator<JSONItem> createComparator(final JSonPickerDialog dialog) {
        if (dialog.mSortOrder == JSonPickerDialog.SORT_ORDER_DISABLE) return null;

        final Comparator<JSONItem> comparator;

        final int reversed = ((dialog.mSortOrder == JSonPickerDialog.SORT_ORDER_REVERSE) ? -1 : 1);

        if (dialog.mSortBy == JSonPickerDialog.SORT_BY_HAVE_CHILDREN) {

            comparator =
                    new Comparator<JSONItem>() {
                        @Override
                        public int compare(JSONItem lht, JSONItem rht) {
                            try {
                                if (dialog.mChildrenNodeNameDefined) {
                                    boolean lhtHaveChildren =
                                            (lht.object.has(dialog.mChildrenNodeName)
                                                    && lht.object
                                                                    .getJSONArray(
                                                                            dialog.mChildrenNodeName)
                                                                    .length()
                                                            > 0);

                                    boolean rhtHaveChildren =
                                            (rht.object.has(dialog.mChildrenNodeName)
                                                    && rht.object
                                                                    .getJSONArray(
                                                                            dialog.mChildrenNodeName)
                                                                    .length()
                                                            > 0);

                                    if (lhtHaveChildren && !rhtHaveChildren) return -1 * reversed;
                                    if (!lhtHaveChildren && rhtHaveChildren) return reversed;
                                }

                                if (dialog.mTitleNodeNameDefined) {
                                    String lhtTitle =
                                            (lht.object.has(dialog.mTitleNodeName)
                                                    ? lht.object.getString(dialog.mTitleNodeName)
                                                    : "");
                                    String rhtTitle =
                                            (rht.object.has(dialog.mTitleNodeName)
                                                    ? rht.object.getString(dialog.mTitleNodeName)
                                                    : "");

                                    if (!lhtTitle.equals(rhtTitle))
                                        return lhtTitle.compareToIgnoreCase(rhtTitle) * reversed;
                                }

                                if (dialog.mSubTitleNodeNameDefined) {
                                    String lhtSubTitle =
                                            (lht.object.has(dialog.mSubTitleNodeName)
                                                    ? lht.object.getString(dialog.mSubTitleNodeName)
                                                    : "");
                                    String rhtSubTitle =
                                            (rht.object.has(dialog.mSubTitleNodeName)
                                                    ? rht.object.getString(dialog.mSubTitleNodeName)
                                                    : "");

                                    if (!lhtSubTitle.equals(rhtSubTitle))
                                        return lhtSubTitle.compareToIgnoreCase(rhtSubTitle)
                                                * reversed;
                                }
                            } catch (Exception Err) {
                                Log.e("JSonPicker.comparator", "Exception: " + Err.toString());
                            }

                            // Same as above but order of occurrence is different.
                            return -1 * reversed;
                        }
                    };
        } else {
            comparator =
                    new Comparator<JSONItem>() {
                        @Override
                        public int compare(JSONItem lht, JSONItem rht) {
                            try {
                                if (dialog.mTitleNodeNameDefined) {
                                    String lhtTitle =
                                            (lht.object.has(dialog.mTitleNodeName)
                                                    ? lht.object.getString(dialog.mTitleNodeName)
                                                    : "");
                                    String rhtTitle =
                                            (rht.object.has(dialog.mTitleNodeName)
                                                    ? rht.object.getString(dialog.mTitleNodeName)
                                                    : "");

                                    if (!lhtTitle.equals(rhtTitle))
                                        return lhtTitle.compareToIgnoreCase(rhtTitle) * reversed;
                                }

                                if (dialog.mSubTitleNodeNameDefined) {
                                    String lhtSubTitle =
                                            (lht.object.has(dialog.mSubTitleNodeName)
                                                    ? lht.object.getString(dialog.mSubTitleNodeName)
                                                    : "");
                                    String rhtSubTitle =
                                            (rht.object.has(dialog.mSubTitleNodeName)
                                                    ? rht.object.getString(dialog.mSubTitleNodeName)
                                                    : "");

                                    if (!lhtSubTitle.equals(rhtSubTitle))
                                        return lhtSubTitle.compareToIgnoreCase(rhtSubTitle)
                                                * reversed;
                                }
                            } catch (Exception Err) {
                                Log.e("JSonPicker.comparator", "Exception: " + Err.toString());
                            }

                            return -1 * reversed;
                        }
                    };
        }

        return comparator;
    }

    /* ---- Derived Methods ---- */

    /**
     * Obtains an item used to create first item in list for return to parent item.
     *
     * @param item item of the list to display.
     * @return a BackItem object to used to create first item in list for return to parent item.
     */
    @Override
    protected BackItem getBackItem(ItemBase item) {
        if (item != null) {
            Object itemTag = item.getTag();

            if (itemTag instanceof JSONItem) {
                JSONItem jsonTag = (JSONItem) itemTag;

                if (jsonTag.parent != null) {
                    if (!(item instanceof BackItem))
                        jsonTag.parent.listViewState = super.getListViewState();

                    String backItemTitle =
                            (TextUtils.isEmpty(this.mBackItemTitle))
                                    ? this.mContext.getString(
                                            R.string.json_picker_dialog_parent_directory_text)
                                    : this.mBackItemTitle.toString();

                    return new BackItem(
                            this.mContext.getString(R.string.json_picker_dialog_parent_directory),
                            backItemTitle,
                            R.drawable.ic_json_picker_back,
                            jsonTag.parent.listViewState,
                            jsonTag.parent);
                }
            }
        }

        return null;
    }

    /**
     * Obtains an item the list of items corresponding to the children of the root item.
     *
     * @param item item of the list to display.
     * @return a collection of PickerItem objects to load in list.
     */
    @Override
    protected Collection<PickerItem> getChildrenFor(ItemBase item) {
        ArrayList<PickerItem> itemList = new ArrayList<>();

        if (item != null && this.mTitleNodeNameDefined && this.mChildrenNodeNameDefined) {
            Object itemTag = item.getTag();

            if (itemTag instanceof JSONItem) {
                JSONItem jsonTag = (JSONItem) itemTag;

                if (jsonTag.object.has(this.mChildrenNodeName)) {
                    try {
                        ArrayList<JSONItem> sortedObjects = new ArrayList<>();

                        JSONArray children = jsonTag.object.getJSONArray(this.mChildrenNodeName);

                        for (int index = 0; index < children.length(); index++) {
                            try {
                                JSONObject jsonChild = children.getJSONObject(index);

                                if (jsonChild.has(this.mTitleNodeName))
                                    sortedObjects.add(new JSONItem(jsonChild, jsonTag));
                            } catch (Exception Err) {
                                Log.e("JSonPicker.childrenFor", "Exception: " + Err.toString());
                            }
                        }

                        if (this.mSorter != null) Collections.sort(sortedObjects, this.mSorter);

                        for (JSONItem newObject : sortedObjects) {
                            PickerItem newItem = this.createItem(newObject);

                            if (newItem != null) itemList.add(newItem);
                        }
                    } catch (Exception Err) {
                        Log.e("JSonPicker.childrenFor", "Exception: " + Err.toString());
                    }
                } else {
                    Toast.makeText(
                                    this.mContext,
                                    R.string.json_picker_dialog_error_dir_access,
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }

        return itemList;
    }

    /**
     * Obtains an item used to initialize the selector.
     *
     * @return a ItemBase object to used to initialize the selector.
     */
    @Override
    protected ItemBase getRootItem() {
        if (this.mRootNode != null && this.mTitleNodeNameDefined && this.mChildrenNodeNameDefined) {
            if (this.mRootNode.has(this.mTitleNodeName)
                    && this.mRootNode.has(this.mChildrenNodeName)) {
                return this.createItem(new JSONItem(this.mRootNode, null));
            }
        }

        Toast.makeText(
                        this.mContext,
                        R.string.json_picker_dialog_error_dir_access,
                        Toast.LENGTH_SHORT)
                .show();

        return null;
    }

    /**
     * Obtains an item to display dialog header.
     *
     * @param item item of the list to display.
     * @return a ItemBase object to define title, sub-title and icon of dialog header.
     */
    @Override
    protected ItemBase getTitleItem(ItemBase item) {
        if (item != null && this.mTitleNodeNameDefined) {
            Object itemTag = item.getTag();

            if (itemTag instanceof JSONItem) {
                JSONItem jsonTag = (JSONItem) itemTag;

                try {
                    String tTitle = jsonTag.object.getString(this.mTitleNodeName);

                    StringBuilder subTitle = new StringBuilder();

                    JSONItem parent = ((JSONItem) itemTag);

                    while (parent != null) {
                        if (parent.parent != null) {
                            if (this.mSubTitleNodeNameDefined) {
                                if (parent.object.has(this.mSubTitleNodeName)) {
                                    subTitle.insert(
                                            0,
                                            "/" + parent.object.getString(this.mSubTitleNodeName));
                                } else {
                                    subTitle.insert(
                                            0, "/" + parent.object.getString(this.mTitleNodeName));
                                }
                            } else {
                                subTitle.insert(
                                        0, "/" + parent.object.getString(this.mTitleNodeName));
                            }
                        }

                        parent = parent.parent;
                    }

                    if (subTitle.length() == 0) subTitle.append("/");

                    return new ItemBase(
                            tTitle, subTitle.toString(), R.drawable.ic_json_picker_header);
                } catch (Exception Err) {
                    Log.e("JSonPicker.getTitleItem", "Exception: " + Err.toString());
                }
            }
        }

        return null;
    }

    /**
     * Indicates if the picker is in multiple selection mode.
     *
     * @return a boolean value who indicates if the picker is in multiple selection mode.
     */
    @Override
    protected boolean isMultiSelectionMode() {
        return (this.mOnMultiChoiceValidationListener != null);
    }

    /**
     * Called for validation of the selection.
     *
     * @param items collection of picked items.
     */
    @Override
    protected void onValidateSelection(Collection<PickerItem> items) {
        if (items.size() > 0) {
            ArrayList<JSONObject> result = new ArrayList<>();

            for (PickerItem item : items) {
                Object itemTag = item.getTag();

                if (itemTag instanceof JSONItem) {
                    result.add(((JSONItem) itemTag).object);
                }
            }

            if (this.mOnSingleChoiceValidationListener != null)
                this.mOnSingleChoiceValidationListener.onClick(this, result.get(0));

            if (this.mOnMultiChoiceValidationListener != null)
                this.mOnMultiChoiceValidationListener.onClick(
                        this, result.toArray(new JSONObject[0]));
        }
    }

    /* ---- Privates Methods ---- */

    /**
     * Create picker item corresponding to the specified file.
     *
     * @param jsonItem a json object.
     * @return a picker item corresponding to the specified file.
     */
    private PickerItem createItem(@NonNull JSONItem jsonItem) {
        if (this.mTitleNodeNameDefined) {
            // Mode Sélectionnable ?

            boolean isPickable = !this.mOneClickMode;

            try {
                String title = jsonItem.object.getString(this.mTitleNodeName);

                if (!TextUtils.isEmpty(this.mTitleMask) && this.mTitleMask.contains("%s"))
                    title = String.format(DEF_LOCAL, this.mTitleMask, title);

                String subTitle = "";

                if (this.mSubTitleNodeNameDefined) {
                    if (jsonItem.object.has(this.mSubTitleNodeName)) {
                        subTitle = jsonItem.object.getString(this.mSubTitleNodeName);

                        if (!TextUtils.isEmpty(this.mSubTitleMask)
                                && this.mSubTitleMask.contains("%s"))
                            subTitle = String.format(DEF_LOCAL, this.mSubTitleMask, subTitle);
                    }
                }

                boolean hasChildren = false;

                if (this.mChildrenNodeNameDefined) {
                    if (jsonItem.object.has(this.mChildrenNodeName)) {
                        try {
                            JSONArray jsonArray =
                                    jsonItem.object.getJSONArray(this.mChildrenNodeName);

                            hasChildren = (jsonArray.length() > 0);
                        } catch (Exception Err) {
                            Log.e("JSonPicker.createItem", "Exception: " + Err.toString());
                        }
                    }

                    if (hasChildren) {
                        if (this.mSelectionType == JSonPickerDialog.ALL_NODE_SELECT)
                            return new PickerItem(
                                    title,
                                    subTitle,
                                    R.drawable.ic_json_picker_node_has_children,
                                    jsonItem,
                                    true,
                                    isPickable);
                        else
                            return new PickerItem(
                                    title,
                                    subTitle,
                                    R.drawable.ic_json_picker_node_has_children,
                                    jsonItem,
                                    true,
                                    false);
                    } else {
                        return new PickerItem(
                                title,
                                subTitle,
                                R.drawable.ic_json_picker_single_node,
                                jsonItem,
                                false,
                                isPickable);
                    }
                } else {
                    return new PickerItem(
                            title,
                            subTitle,
                            R.drawable.ic_json_picker_single_node,
                            jsonItem,
                            false,
                            isPickable);
                }
            } catch (Exception Err) {
                Log.e("JSonPicker.createItem", "Exception: " + Err.toString());
            }
        }

        return null;
    }

    /** Provide a builder for a json picker dialog. */
    @SuppressWarnings("UnusedReturnValue")
    public static class Builder {
        // Attributes

        private final PickerParams P;
        private int mSelectionType = JSonPickerDialog.ALL_NODE_SELECT;
        private CharSequence mTitleNodeName;
        private CharSequence mSubTitleNodeName;
        private CharSequence mChildrenNodeName;
        private CharSequence mTitleMask;
        private CharSequence mSubTitleMask;
        private CharSequence mBackItemTitle;
        private JSONObject mRootNode;
        private int mSortBy = JSonPickerDialog.SORT_BY_TITLE;
        private int mSortOrder = JSonPickerDialog.SORT_ORDER_DISABLE;
        private OnSingleChoiceValidationListener<JSONObject> mOnSingleChoiceValidationListener;
        private OnMultiChoiceValidationListener<JSONObject> mOnMultiChoiceValidationListener;

        /**
         * Creates a builder for a json picker dialog that uses the default dialog dialog theme.
         *
         * @param context the parent context
         */
        public Builder(@NonNull Context context) {
            this(context, R.style.ListPickerDialogBase);
        }

        /**
         * Creates a builder for a json picker dialog that uses an explicit theme resource.
         *
         * @param context the parent context
         * @param themeResId the resource ID of the theme against which to inflate this dialog, or
         *     {@code 0} to use the parent {@code context}'s default dialog dialog theme
         */
        public Builder(@NonNull Context context, @StyleRes int themeResId) {
            this.P = new PickerParams(context, themeResId);

            this.P.iconId = R.drawable.ic_file_picker_header;
        }

        /* Base List Picker Properties */

        /**
         * Set the resource id of the {@link Drawable} to be used in the title.
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setIcon(@DrawableRes int iconId) {
            if (iconId != 0) this.P.iconId = iconId;
            return this;
        }

        /**
         * Set the title using the given resource id.
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setTitle(@StringRes int titleId) {
            if (titleId != 0) this.P.title = this.P.context.getText(titleId);
            return this;
        }

        /**
         * Set the title displayed in the.
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setTitle(@Nullable CharSequence title) {
            if (!TextUtils.isEmpty(title)) this.P.title = title;
            return this;
        }

        /**
         * Set a listener to be invoked when the positive button of the dialog is pressed.
         *
         * @param textId The resource id of the text to display in the positive button
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setPositiveButton(@StringRes int textId) {
            if (textId != 0) this.P.positiveButtonText = this.P.context.getText(textId);

            return this;
        }

        /**
         * Set a listener to be invoked when the positive button of the dialog is pressed.
         *
         * @param text The text to display in the positive button
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setPositiveButton(CharSequence text) {
            if (!TextUtils.isEmpty(text)) this.P.positiveButtonText = text;

            return this;
        }

        /**
         * Set a listener to be invoked when the positive button of the dialog is pressed.
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setPositiveButtonListener(OnClickListener listener) {
            this.P.positiveButtonListener = listener;
            return this;
        }

        /**
         * Set visibility of the positive button of the dialog.
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setPositiveButtonVisibility(int visibility) {
            this.P.positiveButtonVisibility = visibility;
            return this;
        }

        /**
         * Set a listener to be invoked when the negative button of the dialog is pressed.
         *
         * @param textId The resource id of the text to display in the negative button
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setNegativeButton(@StringRes int textId) {
            if (textId != 0) this.P.negativeButtonText = this.P.context.getText(textId);

            return this;
        }

        /**
         * Set a listener to be invoked when the negative button of the dialog is pressed.
         *
         * @param text The text to display in the negative button
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setNegativeButton(CharSequence text) {
            if (!TextUtils.isEmpty(text)) this.P.negativeButtonText = text;

            return this;
        }

        /**
         * Set a listener to be invoked when the negative button of the dialog is pressed.
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setNegativeButtonListener(OnClickListener listener) {
            this.P.negativeButtonListener = listener;
            return this;
        }

        /**
         * Set visibility of the negative button of the dialog.
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setNegativeButtonVisibility(int visibility) {
            this.P.negativeButtonVisibility = visibility;
            return this;
        }

        /**
         * Sets whether the dialog is cancelable or not. Default is true.
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setCancelable(boolean cancelable) {
            this.P.cancelable = cancelable;
            return this;
        }

        /**
         * Sets the callback that will be called if the dialog is canceled.
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setOnCancelListener(OnCancelListener onCancelListener) {
            this.P.onCancelListener = onCancelListener;
            return this;
        }

        /* Json Picker Properties */

        /**
         * Sets selection Type defines that whether a json nodes or both of these has to be
         * selected. Default value is ALL_NODE_SELECT.
         *
         * <p>ALL_NODE_SELECT, NODE_WITHOUT_CHILD_SELECT are the two selection types.
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setSelectionType(int selectionType) {
            this.mSelectionType = selectionType;
            return this;
        }

        /**
         * Sets name of json node contains title item.
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setTitleNodeName(@NonNull CharSequence titleNodeName) {
            this.mTitleNodeName = titleNodeName;
            return this;
        }

        /**
         * Sets name of json node contains sub-title item.
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setSubTitleNodeName(@Nullable CharSequence subTitleNodeName) {
            this.mSubTitleNodeName = subTitleNodeName;
            return this;
        }

        /**
         * Sets name of json node contains child nodes.
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setChildrenNodeName(@NonNull CharSequence childrenNodeName) {
            this.mChildrenNodeName = childrenNodeName;
            return this;
        }

        /**
         * Sets title item mask. Must contain one and only one '%s'.
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setTitleMask(CharSequence titleMask) {
            this.mTitleMask = titleMask;
            return this;
        }

        /**
         * Sets sub-title item mask. Must contain one and only one '%s'.
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setSubTitleMask(CharSequence subTitleMask) {
            this.mSubTitleMask = subTitleMask;
            return this;
        }

        /**
         * Sets sub-title back item.
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setBackItemTitle(CharSequence backItemTitle) {
            this.mBackItemTitle = backItemTitle;
            return this;
        }

        /**
         * Sets root json node. List of json nodes are populated from here.
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setRootNode(@NonNull JSONObject rootNode) {
            this.mRootNode = rootNode;
            return this;
        }

        /**
         * Sort by defines the sort order of the items. Default value is SORT_BY_TITLE.
         *
         * <p>SORT_BY_TITLE, SORT_BY_HAVE_CHILDREN are the two sort by.
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setSortBy(int sortBy) {
            this.mSortBy = sortBy;
            return this;
        }

        /**
         * Sort order defines the sort direction of the items. Default value is SORT_ORDER_DISABLE.
         *
         * <p>SORT_ORDER_DISABLE, SORT_ORDER_NORMAL, SORT_ORDER_REVERSE are the three sort orders.
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setSortOrder(int sortOrder) {
            this.mSortOrder = sortOrder;
            return this;
        }

        /**
         * Sets the callback that will be called if the dialog is validated (single selection mode).
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setOnSingleChoiceValidationListener(
                OnSingleChoiceValidationListener<JSONObject> listener) {
            this.mOnSingleChoiceValidationListener = listener;

            return this;
        }

        /**
         * Sets the callback that will be called if the dialog is validated (multiple selection
         * mode).
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setOnMultiChoiceValidationListener(
                OnMultiChoiceValidationListener<JSONObject> listener) {
            this.mOnMultiChoiceValidationListener = listener;

            return this;
        }

        /**
         * Creates an {@link JSonPickerDialog} with the arguments supplied to this builder and
         * immediately displays the dialog.
         *
         * <p>Calling this method is functionally identical to:
         *
         * @return a FilePickerDialog dialog.
         */
        public JSonPickerDialog show() {
            final JSonPickerDialog dialog = new JSonPickerDialog(this);

            dialog.show();

            return dialog;
        }
    }
}
