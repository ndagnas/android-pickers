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
 *    Created by Nicolas Dagnas on 02-05-2020, updated on 08-05-2020.
 *
 */

package com.github.ndagnas.pickers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;

import java.util.Collection;

/** Defines a file picker dialog. */
@SuppressWarnings({"unused"})
public class ListPickerDialog extends ListPickerDialogBase implements PickerInterface {

    // Constants

    private static java.util.Locale DEF_LOCAL = java.util.Locale.getDefault();

    // Attributes

    private final ListPickerDialogListener mListener;

    /**
     * Create a list picker dialog.
     *
     * @param builder a builder object contains dialog parameters.
     */
    private ListPickerDialog(@NonNull Builder builder) {
        super(builder.P);

        this.mListener = builder.mListener;
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
        return this.mListener.getBackItem(item);
    }

    /**
     * Obtains an item list of items corresponding to the children of the root item.
     *
     * @param item item of the list to display.
     * @return a collection of PickerItem objects to load in list.
     */
    @Override
    protected Collection<PickerItem> getChildrenFor(ItemBase item) {
        return this.mListener.getChildrenFor(item);
    }

    /**
     * Obtains an item used to initialize the selector.
     *
     * @return a ItemBase object to used to initialize the selector.
     */
    @Override
    protected ItemBase getRootItem() {
        return this.mListener.getRootItem();
    }

    /**
     * Obtains an item to display dialog header.
     *
     * @param item item of the list to display.
     * @return a ItemBase object to define title, sub-title and icon of dialog header.
     */
    @Override
    protected ItemBase getTitleItem(ItemBase item) {
        return this.mListener.getTitleItem(item);
    }

    /**
     * Obtains a toolbar view which will be placed between the title bar and the list.
     *
     * @return a view object.
     */
    @Override
    protected View getToolBarView() {
        return this.mListener.getToolBarView();
    }

    /**
     * Indicates if the picker is in multiple selection mode.
     *
     * @return a boolean value who indicates if the picker is in multiple selection mode.
     */
    @Override
    protected boolean isMultiSelectionMode() {
        return this.mListener.isMultiSelectionMode();
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

    /** Provide a builder for a list picker dialog. */
    @SuppressWarnings("UnusedReturnValue")
    public static class Builder {

        // Attributes

        private final PickerParams P;
        private ListPickerDialogListener mListener;

        /**
         * Creates a builder for a file picker dialog that uses the default dialog dialog theme.
         *
         * @param context the parent context
         */
        public Builder(@NonNull Context context) {
            this(context, R.style.ListPickerDialogBase);
        }

        /**
         * Creates a builder for a file picker dialog that uses an explicit theme resource.
         *
         * @param context the parent context
         * @param themeResId the resource ID of the theme against which to inflate this dialog, or
         *     {@code 0} to use the parent {@code context}'s default dialog dialog theme
         */
        @SuppressWarnings("WeakerAccess")
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
            this.P.iconId = iconId;
            return this;
        }

        /**
         * Set the title using the given resource id.
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setTitle(@StringRes int titleId) {
            this.P.title = this.P.context.getText(titleId);
            return this;
        }

        /**
         * Set the title displayed in the.
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setTitle(@Nullable CharSequence title) {
            this.P.title = title;
            return this;
        }

        /**
         * Set a listener to be invoked when the positive button of the dialog is pressed.
         *
         * @param textId The resource id of the text to display in the positive button
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setPositiveButton(@StringRes int textId) {
            this.P.positiveButtonText = this.P.context.getText(textId);
            return this;
        }

        /**
         * Set a listener to be invoked when the positive button of the dialog is pressed.
         *
         * @param text The text to display in the positive button
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setPositiveButton(CharSequence text) {
            this.P.positiveButtonText = text;
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
         * Set a listener to be invoked when the negative button of the dialog is pressed.
         *
         * @param textId The resource id of the text to display in the negative button
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setNegativeButton(@StringRes int textId) {
            this.P.negativeButtonText = this.P.context.getText(textId);
            return this;
        }

        /**
         * Set a listener to be invoked when the negative button of the dialog is pressed.
         *
         * @param text The text to display in the negative button
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setNegativeButton(CharSequence text) {
            this.P.negativeButtonText = text;
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

        /* List Picker Properties */

        /**
         * Sets listener that populate dialog.
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setListPickerDialogListener(ListPickerDialogListener listener) {
            this.mListener = listener;
            return this;
        }

        /**
         * Creates an {@link ListPickerDialog} with the arguments supplied to this builder and
         * immediately displays the dialog.
         *
         * <p>Calling this method is functionally identical to:
         *
         * @return a FilePickerDialog dialog.
         */
        public ListPickerDialog show() {
            final ListPickerDialog dialog = new ListPickerDialog(this);

            dialog.show();

            return dialog;
        }
    }
}
