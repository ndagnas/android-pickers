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
 *    Created by Nicolas Dagnas on 01-05-2020, updated on 02-05-2020.
 *
 */

package com.github.ndagnas.pickers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/** Defines a json picker dialog. */
@SuppressWarnings({"unused", "WeakerAccess"})
public class JsonPickerDialog extends ListPickerDialogBase {
    private static java.util.Locale DEF_LOCAL = java.util.Locale.getDefault();

    /** Defines a listener to be informed of the validation of the selection. */
    public interface ValidateSelectionListener {
        /**
         * Called on dialog validation.
         *
         * @param object list of selected json nodes.
         */
        void onValidateSelection(JSONObject[] object);
    }

    // SELECTION_MODES

    /**
     * SINGLE_MODE specifies that a single json node has to be selected from the list of json nodes.
     * It is the default Selection Mode.
     */
    public static final int SINGLE_MODE = 0;

    /**
     * MULTI_MODE specifies that multiple json node has to be selected from the list of json nodes.
     */
    public static final int MULTI_MODE = 1;

    // SELECTION_TYPES

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

    /**
     * Descriptor class to define properties of the Dialog. Actions are performed upon these
     * Properties.
     */
    @SuppressWarnings("WeakerAccess")
    public static class Properties {
        /** Object initialisation. */
        public Properties() {
            this.selectionMode = JsonPickerDialog.SINGLE_MODE;
            this.selectionType = JsonPickerDialog.ALL_NODE_SELECT;

            this.jsonTitleNodeName = "";
            this.jsonSubTitleNodeName = "";
            this.jsonChildrenNodeName = "";

            this.titleMask = null;
            this.subTitleMask = null;

            this.root = null;

            this.sortBy = JsonPickerDialog.SORT_BY_TITLE;
            this.sortOrder = JsonPickerDialog.SORT_ORDER_DISABLE;
        }

        /**
         * Clone current object.
         *
         * @return a Properties object.
         */
        Properties cloneProperties() {
            Properties result = new Properties();

            result.selectionMode = this.selectionMode;
            result.selectionType = this.selectionType;
            result.jsonTitleNodeName = this.jsonTitleNodeName;
            result.jsonSubTitleNodeName = this.jsonSubTitleNodeName;
            result.jsonChildrenNodeName = this.jsonChildrenNodeName;
            result.titleMask = this.titleMask;
            result.subTitleMask = this.subTitleMask;
            result.root = this.root;
            result.sortBy = this.sortBy;
            result.sortOrder = this.sortOrder;

            return result;
        }

        /**
         * Selection Mode defines whether a single of multiple json nodes have to be selected.
         *
         * <p>SINGLE_MODE and MULTI_MODE are the two selection modes.
         *
         * <p>Set to SINGLE_MODE as default value by constructor.
         */
        public int selectionMode;

        /**
         * Selection Type defines that whether a File/Directory or both of these has to be selected.
         *
         * <p>ALL_NODE_SELECT, NODE_WITHOUT_CHILD_SELECT are the three selection types.
         *
         * <p>Set to ALL_NODE_SELECT as default value by constructor.
         */
        public int selectionType;

        /** The name of json node contains title item. */
        public @NonNull String jsonTitleNodeName;

        /** The name of json node contains sub-title item. */
        public @NonNull String jsonSubTitleNodeName;

        /** The name of json node contains child nodes. */
        public @NonNull String jsonChildrenNodeName;

        /**
         * Mask of title item. Must contain one and only one '%s'.
         *
         * <p>Ex: "My item title: %s"
         */
        public String titleMask;

        /**
         * Mask of sub-title item. Must contain one and only one '%s'.
         *
         * <p>Ex: "My item sub-title: %s"
         */
        public String subTitleMask;

        /** The root json node. List of json nodes are populated from here. */
        public JSONObject root;

        /**
         * Sort by defines the sort order of the items.
         *
         * <p>SORT_BY_TITLE, SORT_BY_HAVE_CHILDREN are the two sort by.
         *
         * <p>SSet to SORT_BY_TITLE as default value by constructor.
         */
        public int sortBy;

        /**
         * Sort order defines the sort direction of the items.
         *
         * <p>SORT_ORDER_DISABLE, SORT_ORDER_NORMAL, SORT_ORDER_REVERSE are the three sort orders.
         *
         * <p>Set to SORT_ORDER_DISABLE as default value by constructor.
         */
        public int sortOrder;
    }

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
        }

        /** Json node object. */
        public JSONObject object;

        /** Json node parent. */
        public JSONItem parent;
    }

    private final Properties mProperties;
    private final Comparator<JSONItem> mSorter;
    private ValidateSelectionListener mListener;

    /**
     * Object initialisation.
     *
     * @param context current context.
     * @param properties picker properties.
     */
    public JsonPickerDialog(@NonNull Context context, @NonNull Properties properties) {
        super(context, R.style.ListPickerDialogBase);

        this.mProperties = properties.cloneProperties();
        this.mSorter = createComparator(this.mProperties);
    }

    /**
     * Object initialisation.
     *
     * @param context current context.
     * @param properties picker properties.
     * @param themeResId style resource id.
     */
    public JsonPickerDialog(
            @NonNull Context context, @NonNull Properties properties, int themeResId) {
        super(context, themeResId);

        this.mProperties = properties.cloneProperties();
        this.mSorter = createComparator(this.mProperties);
    }

    /**
     * Create comparator for sort objects in list.
     *
     * @return A comparator object for sort list.
     */
    private static Comparator<JSONItem> createComparator(final Properties properties) {
        if (properties.sortOrder == JsonPickerDialog.SORT_ORDER_DISABLE) return null;

        final Comparator<JSONItem> comparator;

        final int reversed =
                ((properties.sortOrder == JsonPickerDialog.SORT_ORDER_REVERSE) ? -1 : 1);

        if (properties.sortBy == JsonPickerDialog.SORT_BY_HAVE_CHILDREN) {
            comparator =
                    new Comparator<JSONItem>() {
                        @Override
                        public int compare(JSONItem lht, JSONItem rht) {
                            try {
                                boolean lhtHaveChildren =
                                        (lht.object.has(properties.jsonChildrenNodeName)
                                                && lht.object
                                                                .getJSONArray(
                                                                        properties
                                                                                .jsonChildrenNodeName)
                                                                .length()
                                                        > 0);

                                boolean rhtHaveChildren =
                                        (rht.object.has(properties.jsonChildrenNodeName)
                                                && rht.object
                                                                .getJSONArray(
                                                                        properties
                                                                                .jsonChildrenNodeName)
                                                                .length()
                                                        > 0);

                                if (lhtHaveChildren && !rhtHaveChildren) return -1 * reversed;
                                if (!lhtHaveChildren && rhtHaveChildren) return reversed;

                                String lhtTitle =
                                        lht.object
                                                .getString(properties.jsonTitleNodeName)
                                                .toLowerCase();
                                String rhtTitle =
                                        rht.object
                                                .getString(properties.jsonTitleNodeName)
                                                .toLowerCase();

                                if (!lhtTitle.equals(rhtTitle))
                                    return lhtTitle.compareTo(rhtTitle) * reversed;

                                String lhtSubTitle =
                                        lht.object
                                                .getString(properties.jsonSubTitleNodeName)
                                                .toLowerCase();
                                String rhtSubTitle =
                                        rht.object
                                                .getString(properties.jsonSubTitleNodeName)
                                                .toLowerCase();

                                return lhtSubTitle.compareTo(rhtSubTitle) * reversed;
                            } catch (Exception Err) {
                                Log.e(
                                        "JsonPickerDialog.createComparator",
                                        "Exception: " + Err.toString());
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
                                String lhtTitle =
                                        lht.object
                                                .getString(properties.jsonTitleNodeName)
                                                .toLowerCase();
                                String rhtTitle =
                                        rht.object
                                                .getString(properties.jsonTitleNodeName)
                                                .toLowerCase();

                                if (!lhtTitle.equals(rhtTitle))
                                    return lhtTitle.compareTo(rhtTitle) * reversed;

                                String lhtSubTitle =
                                        lht.object
                                                .getString(properties.jsonSubTitleNodeName)
                                                .toLowerCase();
                                String rhtSubTitle =
                                        rht.object
                                                .getString(properties.jsonSubTitleNodeName)
                                                .toLowerCase();

                                return lhtSubTitle.compareTo(rhtSubTitle) * reversed;
                            } catch (Exception Err) {
                                Log.e(
                                        "JsonPickerDialog.createComparator",
                                        "Exception: " + Err.toString());
                            }

                            return -1 * reversed;
                        }
                    };
        }

        return comparator;
    }

    /* ---- Derived Methods ---- */

    /**
     * Obtains an item the list of items corresponding to the children of the root item.
     *
     * @param item Root element of the list to display.
     * @return a collection of Item objet to load in list.
     */
    @Override
    protected Collection<Item> getChildrenFor(ItemBase item) {
        ArrayList<Item> itemList = new ArrayList<>();

        if (item != null) {
            Object itemTag = item.getTag();

            if (itemTag instanceof JSONItem) {
                JSONItem jsonTag = (JSONItem) itemTag;

                if (jsonTag.object.has(this.mProperties.jsonChildrenNodeName)) {
                    try {
                        ArrayList<JSONItem> sortedObjects = new ArrayList<>();

                        JSONArray children =
                                jsonTag.object.getJSONArray(this.mProperties.jsonChildrenNodeName);

                        for (int index = 0; index < children.length(); index++) {
                            try {
                                JSONObject jsonChild = children.getJSONObject(index);

                                if (jsonChild.has(this.mProperties.jsonTitleNodeName)
                                        && jsonChild.has(this.mProperties.jsonSubTitleNodeName)) {
                                    sortedObjects.add(new JSONItem(jsonChild, jsonTag));
                                }
                            } catch (Exception Err) {
                                Log.e(
                                        "JsonPickerDialog.getChildrenFor",
                                        "Exception: " + Err.toString());
                            }
                        }

                        if (this.mSorter != null) Collections.sort(sortedObjects, this.mSorter);

                        for (JSONItem newObject : sortedObjects) {
                            Item newItem = this.createItem(newObject);

                            if (newItem != null) itemList.add(newItem);
                        }
                    } catch (Exception Err) {
                        Log.e("JsonPickerDialog.getChildrenFor", "Exception: " + Err.toString());
                    }
                } else {
                    Toast.makeText(
                                    this.getContext(),
                                    R.string.json_picker_dialog_error_dir_access,
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }

        return itemList;
    }

    /**
     * Obtains an item to display dialog header.
     *
     * @param item Root element of the list to display.
     * @return a ItemBase objet to define title, sub-title and icon of dialog header.
     */
    @Override
    protected ItemBase getHeaderItem(ItemBase item) {
        if (item != null) {
            Object itemTag = item.getTag();

            if (itemTag instanceof JSONItem) {
                try {
                    String tTitle =
                            ((JSONItem) itemTag)
                                    .object.getString(this.mProperties.jsonTitleNodeName);

                    StringBuilder subTitle = new StringBuilder();

                    JSONItem parent = ((JSONItem) itemTag);

                    while (parent != null) {
                        if (parent.parent != null)
                            subTitle.insert(
                                    0,
                                    "/"
                                            + parent.object.getString(
                                                    this.mProperties.jsonSubTitleNodeName));

                        parent = parent.parent;
                    }

                    if (subTitle.length() == 0) subTitle.append("/");

                    return new ItemBase(
                            tTitle, subTitle.toString(), R.drawable.ic_json_picker_header);
                } catch (Exception Err) {
                    Log.e("JsonPickerDialog.getHeaderItem", "Exception: " + Err.toString());
                }
            }
        }

        return null;
    }

    /**
     * Obtains an item used to create first item in list for return to parent item.
     *
     * @return a ItemBase objet to used to create first item in list for return to parent item.
     */
    @Override
    protected BackItem getBackItem(ItemBase item) {
        if (item != null) {
            Object itemTag = item.getTag();

            if (itemTag instanceof JSONItem) {
                JSONItem jsonTag = (JSONItem) itemTag;

                if (jsonTag.parent != null)
                    return new BackItem(
                            super.getContext()
                                    .getString(R.string.json_picker_dialog_parent_directory),
                            super.getContext()
                                    .getString(R.string.json_picker_dialog_parent_directory_text),
                            R.drawable.ic_json_picker_back,
                            jsonTag.parent);
            }
        }

        return null;
    }

    /**
     * Obtains an item used to initialize the selector.
     *
     * @return a ItemBase objet to used to initialize the selector.
     */
    @Override
    protected ItemBase getRootItem() {
        if (this.mProperties.root != null) {
            if (this.mProperties.root.has(this.mProperties.jsonTitleNodeName)
                    && this.mProperties.root.has(this.mProperties.jsonSubTitleNodeName)) {
                return this.createItem(new JSONItem(this.mProperties.root, null));
            }
        }

        Toast.makeText(
                        this.getContext(),
                        R.string.json_picker_dialog_error_dir_access,
                        Toast.LENGTH_SHORT)
                .show();

        return null;
    }

    /**
     * Indicates if the picker is in multiple selection mode.
     *
     * @return a boolean value who indicates if the picker is in multiple selection mode.
     */
    @Override
    protected boolean isMultiSelectMode() {
        return (this.mProperties.selectionMode == JsonPickerDialog.MULTI_MODE);
    }

    /**
     * Called for validation of the selection.
     *
     * @param items collection of picked items.
     */
    @Override
    protected void onValidateSelection(Collection<PickableItem> items) {
        if (this.mListener != null) {
            ArrayList<JSONObject> result = new ArrayList<>();

            for (PickableItem item : items) {
                Object itemTag = item.getTag();

                if (itemTag instanceof JSONItem) {
                    result.add(((JSONItem) itemTag).object);
                }
            }

            this.mListener.onValidateSelection(result.toArray(new JSONObject[0]));
        }
    }

    /* ---- Privates Methods ---- */

    /**
     * Create picker item corresponding to the specified file.
     *
     * @param jsonItem a json object.
     * @return a picker item corresponding to the specified file.
     */
    private Item createItem(JSONItem jsonItem) {
        try {
            String title = jsonItem.object.getString(this.mProperties.jsonTitleNodeName);
            String subTitle = jsonItem.object.getString(this.mProperties.jsonSubTitleNodeName);

            if (this.mProperties.titleMask != null && this.mProperties.titleMask.contains("%s"))
                title = String.format(DEF_LOCAL, this.mProperties.titleMask, title);

            if (this.mProperties.subTitleMask != null
                    && this.mProperties.subTitleMask.contains("%s"))
                subTitle = String.format(DEF_LOCAL, this.mProperties.subTitleMask, subTitle);

            boolean hasChildren = false;

            if (jsonItem.object.has(this.mProperties.jsonChildrenNodeName)) {
                try {
                    JSONArray jsonArray =
                            jsonItem.object.getJSONArray(this.mProperties.jsonChildrenNodeName);

                    hasChildren = (jsonArray.length() > 0);
                } catch (Exception Err) {
                    Log.e("JsonPickerDialog.createItem", "Exception: " + Err.toString());
                }
            }

            if (hasChildren) {
                if (this.mProperties.selectionType == JsonPickerDialog.ALL_NODE_SELECT)
                    return new PickableItem(
                            title,
                            subTitle,
                            R.drawable.ic_json_picker_node_has_children,
                            jsonItem,
                            true);
                else
                    return new Item(
                            title,
                            subTitle,
                            R.drawable.ic_json_picker_node_has_children,
                            jsonItem,
                            true);
            } else
                return new PickableItem(
                        title, subTitle, R.drawable.ic_json_picker_single_node, jsonItem, false);
        } catch (Exception Err) {
            Log.e("JsonPickerDialog.createItem", "Exception: " + Err.toString());
        }

        return null;
    }

    /* ---- Public Methods ---- */

    /**
     * Allows you to be informed when validating the json nodes selection.
     *
     * @param listener listener.
     */
    public void setValidateSelectionListener(ValidateSelectionListener listener) {
        this.mListener = listener;
    }
}
