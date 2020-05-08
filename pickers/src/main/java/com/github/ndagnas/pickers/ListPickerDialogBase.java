/*
 * Copyright (C) 2020 Nicolas Dagnas
 *
 * Object inspired by Angad Singh, in project: https://github.com/Angads25/android-filepicker
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
 *    Created by Nicolas Dagnas on 01-05-2020, updated on 08-05-2020.
 *
 */

package com.github.ndagnas.pickers;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

import java.util.ArrayList;
import java.util.Collection;

/** Defines a base picker dialog. *** */
@SuppressWarnings({"unused", "WeakerAccess"})
public class ListPickerDialogBase extends Dialog {
    /** Defines a base item for picker. */
    public class ItemBase {
        // Attributes

        private final int mIconId;
        private final CharSequence mSubTitle;
        private final Object mTag;
        private final CharSequence mTitle;

        /**
         * Object initialisation.
         *
         * @param title text displayed in the title of the item.
         * @param subTitle text displayed in the sub-title of the item.
         * @param mIconId resource id of the icon displayed on the left of the item.
         */
        public ItemBase(
                @Nullable CharSequence title,
                @Nullable CharSequence subTitle,
                @DrawableRes int mIconId) {
            this.mIconId = mIconId;
            this.mSubTitle = subTitle;
            this.mTag = null;
            this.mTitle = title;
        }

        /**
         * Object initialisation.
         *
         * @param title text displayed in the title of the item.
         * @param subTitle text displayed in the sub-title of the item.
         * @param mIconId resource id of the icon displayed on the left of the item.
         * @param tag object associate with the item.
         */
        public ItemBase(
                @Nullable CharSequence title,
                @Nullable CharSequence subTitle,
                @DrawableRes int mIconId,
                Object tag) {
            this.mIconId = mIconId;
            this.mSubTitle = subTitle;
            this.mTag = tag;
            this.mTitle = title;
        }

        /**
         * Returns a string representation of the object.
         *
         * @return a string representation of the object.
         */
        @Override
        public String toString() {
            return (TextUtils.isEmpty(this.mTitle)) ? "" : this.mTitle.toString();
        }

        /**
         * Get resource id of the icon of item.
         *
         * @return a integer contains the resource id of the icon of item.
         */
        public @DrawableRes int getIconId() {
            return this.mIconId;
        }

        /**
         * Get sub-title of the item.
         *
         * @return a string contains sub-title of the item.
         */
        public @Nullable CharSequence getSubTitle() {
            return this.mSubTitle;
        }

        /**
         * Get title of the item.
         *
         * @return a string contains title of the item.
         */
        public @Nullable CharSequence getTitle() {
            return this.mTitle;
        }

        /**
         * Get item tag.
         *
         * @return a object contains item tag.
         */
        public Object getTag() {
            return this.mTag;
        }
    }

    /** Defines a item for back action. */
    public class BackItem extends ItemBase {
        /**
         * Object initialisation.
         *
         * @param title text displayed in the title of the item.
         * @param subTitle text displayed in the sub-title of the item.
         * @param mIconId resource id of the icon displayed on the left of the item.
         */
        public BackItem(
                @NonNull CharSequence title,
                @Nullable CharSequence subTitle,
                @DrawableRes int mIconId) {
            super(title, subTitle, mIconId);
        }

        /**
         * Object initialisation.
         *
         * @param title text displayed in the title of the item.
         * @param subTitle text displayed in the sub-title of the item.
         * @param mIconId resource id of the icon displayed on the left of the item.
         * @param tag object associate with the item.
         */
        public BackItem(
                @NonNull CharSequence title,
                @Nullable CharSequence subTitle,
                @DrawableRes int mIconId,
                Object tag) {
            super(title, subTitle, mIconId, tag);
        }
    }

    /** Defines a item for navigation (not pickable). */
    public class PickerItem extends ItemBase {
        // Attributes

        private final boolean mHasChildren;

        /**
         * Object initialisation.
         *
         * @param title text displayed in the title of the item.
         * @param subTitle text displayed in the sub-title of the item.
         * @param mIconId resource id of the icon displayed on the left of the item.
         * @param hasChildren indicates if the item has children.
         */
        public PickerItem(
                @NonNull CharSequence title,
                @Nullable CharSequence subTitle,
                @DrawableRes int mIconId,
                boolean hasChildren) {
            super(title, subTitle, mIconId);

            this.mHasChildren = hasChildren;
        }

        /**
         * Object initialisation.
         *
         * @param title text displayed in the title of the item.
         * @param subTitle text displayed in the sub-title of the item.
         * @param mIconId resource id of the icon displayed on the left of the item.
         * @param tag object associate with the item.
         * @param hasChildren indicates if the item has children.
         */
        public PickerItem(
                @NonNull CharSequence title,
                @Nullable CharSequence subTitle,
                @DrawableRes int mIconId,
                Object tag,
                boolean hasChildren) {
            super(title, subTitle, mIconId, tag);

            this.mHasChildren = hasChildren;
        }

        /**
         * Indicates if the item has children.
         *
         * @return a boolean value who indicates if the item has children.
         */
        public boolean hasChildren() {
            return this.mHasChildren;
        }
    }

    /** Defines a item for navigation. */
    public class PickableItem extends PickerItem {
        // Attributes

        private boolean mIsPicked = false;

        /**
         * Object initialisation.
         *
         * @param title text displayed in the title of the item.
         * @param subTitle text displayed in the sub-title of the item.
         * @param mIconId resource id of the icon displayed on the left of the item.
         */
        public PickableItem(
                @NonNull CharSequence title,
                @Nullable CharSequence subTitle,
                @DrawableRes int mIconId) {
            super(title, subTitle, mIconId, false);
        }

        /**
         * Object initialisation.
         *
         * @param title text displayed in the title of the item.
         * @param subTitle text displayed in the sub-title of the item.
         * @param mIconId resource id of the icon displayed on the left of the item.
         * @param hasChildren indicates if the item has children.
         */
        public PickableItem(
                @NonNull CharSequence title,
                @Nullable CharSequence subTitle,
                @DrawableRes int mIconId,
                boolean hasChildren) {
            super(title, subTitle, mIconId, hasChildren);
        }

        /**
         * Object initialisation.
         *
         * @param title text displayed in the title of the item.
         * @param subTitle text displayed in the sub-title of the item.
         * @param mIconId resource id of the icon displayed on the left of the item.
         * @param tag object associate with the item.
         */
        public PickableItem(
                @NonNull CharSequence title,
                @Nullable CharSequence subTitle,
                @DrawableRes int mIconId,
                Object tag) {
            super(title, subTitle, mIconId, tag, false);
        }

        /**
         * Object initialisation.
         *
         * @param title text displayed in the title of the item.
         * @param subTitle text displayed in the sub-title of the item.
         * @param mIconId resource id of the icon displayed on the left of the item.
         * @param tag object associate with the item.
         * @param hasChildren indicates if the item has children.
         */
        public PickableItem(
                @NonNull CharSequence title,
                @Nullable CharSequence subTitle,
                @DrawableRes int mIconId,
                Object tag,
                boolean hasChildren) {
            super(title, subTitle, mIconId, tag, hasChildren);
        }

        /**
         * Indicates if the item is picked.
         *
         * @return a boolean value who indicates if the item is picked.
         */
        public boolean isPicked() {
            return this.mIsPicked;
        }

        /**
         * Defines item as picked or not.
         *
         * @param value new value for picked status.
         */
        private void setPicked(boolean value) {
            this.mIsPicked = value;
        }
    }

    /** Defines a ViewItem for Adapter. */
    static class ItemViewHolder {
        // Attributes

        private final View SelfView;
        private final ImageView mIcon;
        private final TextView mTitle;
        private final TextView mSubTitle;
        private final CheckBox mCheckbox;

        /**
         * Object initialisation.
         *
         * @param View associate view.
         */
        private ItemViewHolder(@NonNull View View) {
            this.SelfView = View;
            this.mIcon = View.findViewById(R.id.list_picker_dialog_base_item_icon);
            this.mTitle = View.findViewById(R.id.list_picker_dialog_base_item_title);
            this.mSubTitle = View.findViewById(R.id.list_picker_dialog_base_item_sub_title);
            this.mCheckbox = View.findViewById(R.id.list_picker_dialog_base_item_checkbox);
        }

        /**
         * Apply data of item on current ViewItem.
         *
         * @param item associate item.
         */
        void applyData(@NonNull ItemBase item) {
            this.mIcon.setImageResource(item.getIconId());
            this.mTitle.setText(item.getTitle());

            CharSequence subTitle = item.getSubTitle();

            if (!TextUtils.isEmpty(subTitle)) {
                this.mSubTitle.setText(subTitle);

                this.mSubTitle.setVisibility(View.VISIBLE);
            } else {
                this.mSubTitle.setText("");

                this.mSubTitle.setVisibility(View.GONE);
            }

            if (item instanceof PickableItem) {
                this.mCheckbox.setChecked(((PickableItem) item).isPicked());

                this.mCheckbox.setVisibility(View.VISIBLE);
            } else {
                this.mCheckbox.setChecked(false);

                this.mCheckbox.setVisibility(View.GONE);
            }
        }
    }

    /** Defines a adapter object use in ListView. */
    static class PickerAdapter extends BaseAdapter {
        // Attributes

        private final ListPickerDialogBase mOwner;
        private final ArrayList<ItemBase> mItems = new ArrayList<>();

        /**
         * Object initialisation.
         *
         * @param owner owner of this object.
         */
        private PickerAdapter(@NonNull ListPickerDialogBase owner) {
            this.mOwner = owner;
        }

        /**
         * Replace all items of the current adapter.
         *
         * @param backItem item in first position used for back action.
         * @param items new item list.
         */
        public void replaceAll(BackItem backItem, @NonNull Collection<PickerItem> items) {
            this.mItems.clear();

            this.notifyDataSetChanged();

            if (backItem != null) this.mItems.add(backItem);

            for (PickerItem item : items) {
                if (item != null) this.mItems.add(item);
            }

            this.notifyDataSetChanged();
        }

        /**
         * Obtains a view object associate with item.
         *
         * @param Index item of the item.
         * @param ItemView object view.
         * @param ViewGroup view group.
         */
        @Override
        public View getView(final int Index, View ItemView, ViewGroup ViewGroup) {
            final ItemViewHolder Holder;

            if (ItemView == null) {
                ItemView =
                        LayoutInflater.from(this.mOwner.getContext())
                                .inflate(R.layout.list_picker_dialog_base_item, ViewGroup, false);

                Holder = new ItemViewHolder(ItemView);

                ItemView.setTag(Holder);
            } else {
                Holder = (ItemViewHolder) ItemView.getTag();
            }

            final ItemBase item = this.mItems.get(Index);

            if (item != null) {
                Holder.applyData(item);

                Holder.SelfView.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (item instanceof PickableItem
                                        && !((PickerItem) item).hasChildren()) {
                                    PickableItem pickableItem = (PickableItem) item;

                                    boolean picked = !pickableItem.isPicked();

                                    pickableItem.setPicked(picked);

                                    // If picked, it's necessary to check others items

                                    if (picked)
                                        PickerAdapter.this.checkOtherPickedItems(pickableItem);

                                    PickerAdapter.this.notifyDataSetChanged();

                                    PickerAdapter.this.mOwner.actualizePositiveButtonText();
                                } else {
                                    PickerAdapter.this.mOwner.onItemClick(item);
                                }
                            }
                        });

                if (item instanceof PickableItem) {
                    Holder.mCheckbox.setOnCheckedChangeListener(
                            new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(
                                        CompoundButton checkbox, boolean isChecked) {
                                    PickableItem pickableItem = (PickableItem) item;

                                    boolean picked = checkbox.isChecked();

                                    pickableItem.setPicked(picked);

                                    // If picked, it's necessary to check others items

                                    if (picked
                                            && PickerAdapter.this.checkOtherPickedItems(
                                                    pickableItem))
                                        PickerAdapter.this.notifyDataSetChanged();

                                    PickerAdapter.this.mOwner.actualizePositiveButtonText();
                                }
                            });
                }
            }

            return ItemView;
        }

        /**
         * Check other items if it's single selection mode.
         *
         * @param pickableItem item of the item.
         * @return a boolean value indicate who one or more other items are picked status changed.
         */
        private boolean checkOtherPickedItems(PickableItem pickableItem) {
            boolean result = false;

            if (!this.mOwner.isMultiSelectionMode()) {
                for (ItemBase item : this.mItems) {
                    if (!item.equals(pickableItem)
                            && item instanceof PickableItem
                            && ((PickableItem) item).isPicked()) {
                        ((PickableItem) item).setPicked(false);

                        result = true;
                    }
                }
            }

            return result;
        }

        /**
         * Get items count.
         *
         * @return a integer contains items count.
         */
        @Override
        public int getCount() {
            return this.mItems.size();
        }

        /**
         * Get item at index position.
         *
         * @param index index of item.
         * @return a ItemBase object contains item at index position.
         */
        @Override
        public ItemBase getItem(int index) {
            return this.mItems.get(index);
        }

        /**
         * Get item id at index position.
         *
         * @param index index of item.
         * @return a integer contains item id at index position.
         */
        @Override
        public long getItemId(int index) {
            return index;
        }

        /**
         * Get actual pickeds items.
         *
         * @return a collection contains actual pickeds items.
         */
        protected Collection<PickableItem> getPickedItem() {
            ArrayList<PickableItem> result = new ArrayList<>();

            for (ItemBase item : this.mItems) {
                if (item instanceof PickableItem && ((PickableItem) item).isPicked())
                    result.add((PickableItem) item);
            }

            return result;
        }

        /**
         * Get actual pickeds items count.
         *
         * @return a integer contains actual pickeds items count.
         */
        protected int getPickedItemCount() {
            int result = 0;

            for (ItemBase item : this.mItems) {
                if (item instanceof PickableItem && ((PickableItem) item).isPicked()) result++;
            }

            return result;
        }
    }

    private static java.util.Locale DEF_LOCAL = java.util.Locale.getDefault();

    private final ListPickerDialogBase mSelf;
    private final int mIconId;
    private final CharSequence mTitle;
    private final CharSequence mNegativeButtonText;
    private final DialogInterface.OnClickListener mNegativeButtonListener;
    private final CharSequence mPositiveButtonText;
    private final DialogInterface.OnClickListener mPositiveButtonListener;
    private PickerAdapter mAdapter = null;
    private ItemBase mRootItem = null;
    private BackItem mBackItem = null;
    private ImageView mIconView = null;
    private TextView mTitleView = null;
    private TextView mSubTitleView = null;
    private ListView mListView = null;
    private Button mPositiveButton = null;

    /**
     * Create a list picker dialog.
     *
     * @param controller dialog controller.
     */
    protected ListPickerDialogBase(@NonNull PickerParams controller) {
        super(controller.context, controller.theme);

        this.mSelf = this;

        super.setTitle(controller.title);
        super.setCancelable(controller.cancelable);
        super.setOnCancelListener(controller.onCancelListener);
        super.setOnDismissListener(controller.onDismissListener);
        super.setOnKeyListener(controller.onKeyListener);

        this.mIconId = controller.iconId;
        this.mTitle = controller.title;
        this.mNegativeButtonText = controller.negativeButtonText;
        this.mNegativeButtonListener = controller.negativeButtonListener;
        this.mPositiveButtonText = controller.positiveButtonText;
        this.mPositiveButtonListener = controller.positiveButtonListener;
    }

    /* ---- Derived Methods ---- */

    /**
     * Called on dialog create.
     *
     * @param savedInstanceState instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.setContentView(R.layout.list_picker_dialog_base);

        // Adapter

        this.mListView = this.findViewById(R.id.list_picker_dialog_base_list);

        this.mAdapter = new PickerAdapter(this);

        this.mListView.setAdapter(this.mAdapter);

        // Icon

        this.mIconView = this.findViewById(R.id.list_picker_dialog_base_icon);

        if (this.mIconId != 0) this.mIconView.setImageResource(this.mIconId);

        // Title

        this.mTitleView = this.findViewById(R.id.list_picker_dialog_base_title);
        this.mSubTitleView = this.findViewById(R.id.list_picker_dialog_base_sub_title);

        if (!TextUtils.isEmpty(this.mTitle)) this.mTitleView.setText(this.mTitle);

        // Negative button

        Button negativeButton = this.findViewById(R.id.list_picker_dialog_base_negative_button);

        negativeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View View) {
                        ListPickerDialogBase.this.dismiss();

                        if (mSelf.mNegativeButtonListener != null)
                            mSelf.mNegativeButtonListener.onClick(
                                    mSelf, DialogInterface.BUTTON_NEGATIVE);
                    }
                });

        if (TextUtils.isEmpty(this.mNegativeButtonText)) negativeButton.setVisibility(View.GONE);
        else negativeButton.setText(this.mNegativeButtonText);

        // Positive button

        this.mPositiveButton = this.findViewById(R.id.list_picker_dialog_base_positive_button);

        this.mPositiveButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View View) {
                        ListPickerDialogBase.this.onValidateSelection(
                                ListPickerDialogBase.this.mAdapter.getPickedItem());

                        ListPickerDialogBase.this.dismiss();

                        if (mSelf.mPositiveButtonListener != null)
                            mSelf.mPositiveButtonListener.onClick(
                                    mSelf, DialogInterface.BUTTON_POSITIVE);
                    }
                });

        if (TextUtils.isEmpty(this.mPositiveButtonText))
            this.mPositiveButton.setVisibility(View.GONE);
        else this.actualizePositiveButtonText();

        // Buttons view

        if (TextUtils.isEmpty(this.mNegativeButtonText)
                && TextUtils.isEmpty(this.mPositiveButtonText))
            this.findViewById(R.id.list_picker_dialog_base_buttons).setVisibility(View.GONE);

        // Toolbar View

        View toolbarView = this.getToolBarView();

        if (toolbarView != null) {
            LinearLayout headerLayout = this.findViewById(R.id.list_picker_dialog_base_toolbar);

            if (headerLayout != null) headerLayout.addView(toolbarView);
        }
    }

    /** Called on dialog show. */
    @Override
    public void show() {
        super.show();

        this.navigateToItem(this.getRootItem());
    }

    /** Called on back-key is pressed. */
    @Override
    public void onBackPressed() {
        if (this.mBackItem != null) {
            this.navigateToItem(this.mBackItem);
        } else {
            super.onBackPressed();
        }
    }

    /* ---- Privates Methods ---- */

    /** Actualize positive button label. */
    private void actualizePositiveButtonText() {
        if (this.mPositiveButton != null
                && this.mAdapter != null
                && !TextUtils.isEmpty(this.mPositiveButtonText)) {
            int pickedItemCount = this.mAdapter.getPickedItemCount();

            if (pickedItemCount > 0) {
                this.mPositiveButton.setEnabled(true);

                if (pickedItemCount > 1) {
                    this.mPositiveButton.setText(
                            String.format(
                                    DEF_LOCAL,
                                    "%s (%d)",
                                    this.mPositiveButtonText,
                                    this.mAdapter.getPickedItemCount()));
                } else {
                    this.mPositiveButton.setText(this.mPositiveButtonText);
                }
            } else {
                this.mPositiveButton.setEnabled(false);

                this.mPositiveButton.setText(this.mPositiveButtonText);
            }
        }
    }

    /** Actualize title dialog. */
    private void actualizeTitle() {
        if (this.mTitleView != null && this.mSubTitleView != null) {
            ItemBase titleItem = this.getTitleItem(this.mRootItem);

            if (titleItem != null) {
                this.mIconView.setImageResource(
                        (titleItem.mIconId != 0) ? titleItem.mIconId : this.mIconId);

                this.mTitleView.setText(
                        (!TextUtils.isEmpty(this.mTitle)) ? this.mTitle : titleItem.mTitle);
                this.mSubTitleView.setText(
                        (!TextUtils.isEmpty(titleItem.mSubTitle)) ? titleItem.mSubTitle : "");

                this.mSubTitleView.setVisibility(
                        (!TextUtils.isEmpty(this.mSubTitleView.getText()))
                                ? View.VISIBLE
                                : View.INVISIBLE);
            }
        }
    }

    /**
     * Navigate to specified item.
     *
     * @param rootItem root item.
     */
    private void navigateToItem(ItemBase rootItem) {
        if (rootItem != null) {
            this.mRootItem = rootItem;
            this.mBackItem = this.getBackItem(rootItem);

            this.actualizeTitle();

            this.mListView.setAdapter(null);

            this.mAdapter.replaceAll(this.mBackItem, this.getChildrenFor(rootItem));

            this.mListView.setAdapter(this.mAdapter);

            this.actualizePositiveButtonText();
        }
    }

    /**
     * Called on item click.
     *
     * @param item item clicked.
     */
    private void onItemClick(ItemBase item) {
        if (item instanceof BackItem) {
            this.navigateToItem(item);
        } else if (item instanceof PickerItem && ((PickerItem) item).hasChildren()) {
            this.navigateToItem(item);
        }
    }

    /* ---- Protected Methods, to be overridden in subclasses ---- */

    /**
     * Obtains an item used to create first item in list for return to parent item.
     *
     * <p>Exemple: new ItemBase ("...", "Parent directory", R.drawable...)
     *
     * @param item item of the list to display.
     * @return a BackItem object to used to create first item in list for return to parent item.
     */
    protected BackItem getBackItem(ItemBase item) {
        return null;
    }

    /**
     * Obtains an item list of items corresponding to the children of the root item.
     *
     * <p>Exemple: new Item ("File 1", "size: ...", R.drawable..., parent, true)
     *
     * <p>Exemple: new PickableItem ("File 1", "size: ...", R.drawable..., parent, true)
     *
     * @param item item of the list to display.
     * @return a collection of PickerItem objects to load in list.
     */
    protected Collection<PickerItem> getChildrenFor(ItemBase item) {
        return new ArrayList<>();
    }

    /**
     * Obtains an item used to initialize the selector.
     *
     * @return a ItemBase object to used to initialize the selector.
     */
    protected ItemBase getRootItem() {
        return null;
    }

    /**
     * Obtains an item to display dialog title.
     *
     * @param item item of the list to display.
     * @return an ItemBase object to define title, sub-title and icon of dialog header.
     */
    protected ItemBase getTitleItem(ItemBase item) {
        return item;
    }

    /**
     * Obtains a toolbar view which will be placed between the title bar and the list.
     *
     * @return a view object.
     */
    protected View getToolBarView() {
        return null;
    }

    /**
     * Indicates if the picker is in multiple selection mode.
     *
     * @return a boolean value who indicates if the picker is in multiple selection mode.
     */
    protected boolean isMultiSelectionMode() {
        return false;
    }

    /**
     * Called for validation of the selection.
     *
     * @param items collection of picked items.
     */
    protected void onValidateSelection(Collection<PickableItem> items) {}

    /* ---- Protected Methods ---- */

    /** Reload the list. */
    protected void reload() {
        if (this.mRootItem == null) this.navigateToItem(this.getRootItem());
        else this.navigateToItem(this.mRootItem);
    }

    /** Provide a controller for a list picker dialog. */
    protected static class PickerParams {
        // Attributes

        /** Theme id for dialog. */
        final int theme;

        /** Default context. */
        final Context context;

        /** Resource id of title dialog. */
        int iconId = 0;

        /** Title dialog. */
        CharSequence title;

        /** Caption of positive button. */
        CharSequence positiveButtonText;

        /** Listener to be invoked when the positive button of the dialog is pressed. */
        DialogInterface.OnClickListener positiveButtonListener;

        /** Caption of negative button. */
        CharSequence negativeButtonText;

        /** Listener to be invoked when the negative button of the dialog is pressed. */
        DialogInterface.OnClickListener negativeButtonListener;

        /** Dialog is cancelable or not. Default is true. */
        boolean cancelable;

        /** Callback that will be called if the dialog is canceled. */
        DialogInterface.OnCancelListener onCancelListener;

        /** Callback that will be called when the dialog is dismissed for any reason. */
        DialogInterface.OnDismissListener onDismissListener;

        /** Callback that will be called if a key is dispatched to the dialog. */
        DialogInterface.OnKeyListener onKeyListener;

        /**
         * Creates a picker params for a list picker dialog that uses the default picker dialog
         * theme.
         *
         * @param context the parent context
         */
        public PickerParams(@NonNull Context context) {
            this(context, R.style.ListPickerDialogBase);
        }

        /**
         * Creates a controller for a list picker dialog that uses an explicit theme resource.
         *
         * @param context the parent context
         * @param themeResId the resource ID of the theme against which to inflate this dialog
         */
        public PickerParams(@NonNull Context context, @StyleRes int themeResId) {
            this.context = context;
            this.theme = themeResId;
            this.cancelable = true;

            this.negativeButtonText =
                    context.getString(R.string.list_picker_dialog_base_negative_button);
            this.positiveButtonText =
                    context.getString(R.string.list_picker_dialog_base_positive_button);
        }
    }
}
