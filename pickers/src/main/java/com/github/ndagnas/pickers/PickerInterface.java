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
 *    Created by Nicolas Dagnas on 08-05-2020.
 *
 */

package com.github.ndagnas.pickers;

import android.view.View;

import java.util.Collection;

/** Defines a file picker validation listeners. */
@SuppressWarnings({"unused"})
public interface PickerInterface {
    /** Defines a listener to be use picker dialog. */
    interface ListPickerDialogListener {
        /**
         * Obtains an item used to create first item in list for return to parent item.
         *
         * @param item item of the list to display.
         * @return a BackItem object to used to create first item in list for return to parent item.
         */
        ListPickerDialogBase.BackItem getBackItem(ListPickerDialogBase.ItemBase item);

        /**
         * Obtains an item list of items corresponding to the children of the root item.
         *
         * @param item item of the list to display.
         * @return a collection of PickerItem objects to load in list.
         */
        Collection<ListPickerDialogBase.PickerItem> getChildrenFor(
                ListPickerDialogBase.ItemBase item);

        /**
         * Obtains an item used to initialize the selector.
         *
         * @return a ItemBase object to used to initialize the selector.
         */
        ListPickerDialogBase.ItemBase getRootItem();

        /**
         * Obtains an item to display dialog header.
         *
         * @param item item of the list to display.
         * @return a ItemBase object to define title, sub-title and icon of dialog header.
         */
        ListPickerDialogBase.ItemBase getTitleItem(ListPickerDialogBase.ItemBase item);

        /**
         * Obtains a toolbar view which will be placed between the title bar and the list.
         *
         * @return a view object.
         */
        View getToolBarView();

        /**
         * Indicates if the picker is in multiple selection mode.
         *
         * @return a boolean value who indicates if the picker is in multiple selection mode.
         */
        boolean isMultiSelectionMode();

        /**
         * Called for validation of the selection.
         *
         * @param items collection of picked items.
         */
        void onValidateSelection(Collection<ListPickerDialogBase.PickableItem> items);
    }

    /**
     * Defines a listener that will be called if the dialog is validated on single selection mode.
     */
    interface OnSingleChoiceValidationListener<T> {
        /**
         * Called on dialog validation on single selection mode.
         *
         * @param sender picker interface.
         * @param result result.
         */
        void onClick(PickerInterface sender, T result);
    }

    /**
     * Defines a listener that will be called if the dialog is validated on multiple selection mode.
     */
    interface OnMultiChoiceValidationListener<T> {
        /**
         * Called on dialog validation on multiple selection mode.
         *
         * @param sender picker interface.
         * @param result result.
         */
        void onClick(PickerInterface sender, T[] result);
    }
}
