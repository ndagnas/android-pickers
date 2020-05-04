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
 *    Created by Nicolas Dagnas on 02-05-2020.
 *
 */

package com.github.ndagnas.pickers;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.Collection;

/** Defines a file picker dialog. */
@SuppressWarnings({"unused"})
public class ListPickerDialog extends ListPickerDialogBase {
    private static java.util.Locale DEF_LOCAL = java.util.Locale.getDefault();

    /** Defines a listener to be use picker dialog. */
    public interface PickerWrapperListener {
        /**
         * Obtains an item the list of items corresponding to the children of the root item.
         *
         * <p>Exemple: new Item ("File 1", "size: ...", R.drawable..., parent, true)
         *
         * <p>Exemple: new PickableItem ("File 1", "size: ...", R.drawable..., parent, true)
         *
         * @param item root item of the list to display.
         * @return a collection of Item objet to load in list.
         */
        Collection<Item> getChildrenFor(ItemBase item);

        /**
         * Obtains an item to display dialog header.
         *
         * @param item root item of the list to display.
         * @return a ItemBase objet to define title, sub-title and icon of dialog header.
         */
        ItemBase getHeaderItem(ItemBase item);

        /**
         * Obtains an item used to create first item in list for return to parent item.
         *
         * <p>Exemple: new ItemBase ("...", "Parent directory", R.drawable...)
         *
         * @return a ItemBase objet to used to create first item in list for return to parent item.
         */
        BackItem getBackItem(ItemBase item);

        /**
         * Obtains an item used to initialize the selector.
         *
         * @return a ItemBase objet to used to initialize the selector.
         */
        ItemBase getRootItem();

        /**
         * Indicates if the picker is in multiple selection mode.
         *
         * @return a boolean value who indicates if the picker is in multiple selection mode.
         */
        boolean isMultiSelectMode();

        /**
         * Called for validation of the selection.
         *
         * @param items collection of picked items.
         */
        void onValidateSelection(Collection<PickableItem> items);
    }

    private PickerWrapperListener mListener;

    /**
     * Object initialisation.
     *
     * @param context current context.
     * @param listener listener for use picker.
     */
    public ListPickerDialog(@NonNull Context context, @NonNull PickerWrapperListener listener) {
        super(context, R.style.ListPickerDialogBase);

        this.mListener = listener;
    }

    /**
     * Object initialisation.
     *
     * @param context current context.
     * @param listener listener for use picker.
     * @param themeResId style resource id.
     */
    public ListPickerDialog(
            @NonNull Context context, @NonNull PickerWrapperListener listener, int themeResId) {
        super(context, themeResId);

        this.mListener = listener;
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
        return this.mListener.getChildrenFor(item);
    }

    /**
     * Obtains an item to display dialog header.
     *
     * @param item Root element of the list to display.
     * @return a ItemBase objet to define title, sub-title and icon of dialog header.
     */
    @Override
    protected ItemBase getHeaderItem(ItemBase item) {
        return this.mListener.getHeaderItem(item);
    }

    /**
     * Obtains an item used to create first item in list for return to parent item.
     *
     * @return a ItemBase objet to used to create first item in list for return to parent item.
     */
    @Override
    protected BackItem getBackItem(ItemBase item) {
        return this.mListener.getBackItem(item);
    }

    /**
     * Obtains an item used to initialize the selector.
     *
     * @return a ItemBase objet to used to initialize the selector.
     */
    @Override
    protected ItemBase getRootItem() {
        return this.mListener.getRootItem();
    }

    /**
     * Indicates if the picker is in multiple selection mode.
     *
     * @return a boolean value who indicates if the picker is in multiple selection mode.
     */
    @Override
    protected boolean isMultiSelectMode() {
        return this.mListener.isMultiSelectMode();
    }

    /**
     * Called for validation of the selection.
     *
     * @param items collection of picked items.
     */
    @Override
    protected void onValidateSelection(Collection<PickableItem> items) {
        this.mListener.onValidateSelection(items);
    }
}
