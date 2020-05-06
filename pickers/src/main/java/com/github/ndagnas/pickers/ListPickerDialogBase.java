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
 *    Created by Nicolas Dagnas on 01-05-2020, updated on 02-05-2020.
 *
 */

package com.github.ndagnas.pickers;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
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

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;

/** Defines a base picker dialog. */
@SuppressWarnings({"unused", "WeakerAccess"})
public class ListPickerDialogBase extends Dialog {
    /** Defines a base item for picker. */
    public class ItemBase {
        private final int mIconResource;
        private final String mSubTitle;
        private final Object mTag;
        private final String mTitle;

        /**
         * Object initialisation.
         *
         * @param title text displayed in the title of the item.
         * @param subTitle text displayed in the sub-title of the item.
         * @param iconResource resourceId of the icon displayed on the left of the item.
         */
        public ItemBase(@NonNull String title, @NonNull String subTitle, int iconResource) {
            this.mIconResource = iconResource;
            this.mSubTitle = subTitle;
            this.mTag = null;
            this.mTitle = title;
        }

        /**
         * Object initialisation.
         *
         * @param title text displayed in the title of the item.
         * @param subTitle text displayed in the sub-title of the item.
         * @param iconResource resourceId of the icon displayed on the left of the item.
         * @param tag object associate with the item.
         */
        public ItemBase(
                @NonNull String title, @NonNull String subTitle, int iconResource, Object tag) {
            this.mIconResource = iconResource;
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
            return this.mTitle;
        }

        /**
         * Get resourceid of the icon of item.
         *
         * @return a integer contains the resourceid of the icon of item.
         */
        public int getIconResource() {
            return this.mIconResource;
        }

        /**
         * Get sub-title of the item.
         *
         * @return a string contains sub-title of the item.
         */
        public String getSubTitle() {
            return this.mSubTitle;
        }

        /**
         * Get title of the item.
         *
         * @return a string contains title of the item.
         */
        public String getTitle() {
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
         * @param iconResource resourceId of the icon displayed on the left of the item.
         */
        public BackItem(@NonNull String title, @NonNull String subTitle, int iconResource) {
            super(title, subTitle, iconResource);
        }

        /**
         * Object initialisation.
         *
         * @param title text displayed in the title of the item.
         * @param subTitle text displayed in the sub-title of the item.
         * @param iconResource resourceId of the icon displayed on the left of the item.
         * @param tag object associate with the item.
         */
        public BackItem(
                @NonNull String title, @NonNull String subTitle, int iconResource, Object tag) {
            super(title, subTitle, iconResource, tag);
        }
    }

    /** Defines a item for navigation. */
    public class Item extends ItemBase {
        private final boolean mHasChildren;

        /**
         * Object initialisation.
         *
         * @param title text displayed in the title of the item.
         * @param subTitle text displayed in the sub-title of the item.
         * @param iconResource resourceId of the icon displayed on the left of the item.
         * @param hasChildren indicates if the item has children.
         */
        public Item(
                @NonNull String title,
                @NonNull String subTitle,
                int iconResource,
                boolean hasChildren) {
            super(title, subTitle, iconResource);

            this.mHasChildren = hasChildren;
        }

        /**
         * Object initialisation.
         *
         * @param title text displayed in the title of the item.
         * @param subTitle text displayed in the sub-title of the item.
         * @param iconResource resourceId of the icon displayed on the left of the item.
         * @param tag object associate with the item.
         * @param hasChildren indicates if the item has children.
         */
        public Item(
                @NonNull String title,
                @NonNull String subTitle,
                int iconResource,
                Object tag,
                boolean hasChildren) {
            super(title, subTitle, iconResource, tag);

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
    public class PickableItem extends Item {
        private boolean mIsPicked = false;

        /**
         * Object initialisation.
         *
         * @param title text displayed in the title of the item.
         * @param subTitle text displayed in the sub-title of the item.
         * @param iconResource resourceId of the icon displayed on the left of the item.
         */
        public PickableItem(@NonNull String title, @NonNull String subTitle, int iconResource) {
            super(title, subTitle, iconResource, false);
        }

        /**
         * Object initialisation.
         *
         * @param title text displayed in the title of the item.
         * @param subTitle text displayed in the sub-title of the item.
         * @param iconResource resourceId of the icon displayed on the left of the item.
         * @param hasChildren indicates if the item has children.
         */
        public PickableItem(
                @NonNull String title,
                @NonNull String subTitle,
                int iconResource,
                boolean hasChildren) {
            super(title, subTitle, iconResource, hasChildren);
        }

        /**
         * Object initialisation.
         *
         * @param title text displayed in the title of the item.
         * @param subTitle text displayed in the sub-title of the item.
         * @param iconResource resourceId of the icon displayed on the left of the item.
         * @param tag object associate with the item.
         */
        public PickableItem(
                @NonNull String title, @NonNull String subTitle, int iconResource, Object tag) {
            super(title, subTitle, iconResource, tag, false);
        }

        /**
         * Object initialisation.
         *
         * @param title text displayed in the title of the item.
         * @param subTitle text displayed in the sub-title of the item.
         * @param iconResource resourceId of the icon displayed on the left of the item.
         * @param tag object associate with the item.
         * @param hasChildren indicates if the item has children.
         */
        public PickableItem(
                @NonNull String title,
                @NonNull String subTitle,
                int iconResource,
                Object tag,
                boolean hasChildren) {
            super(title, subTitle, iconResource, tag, hasChildren);
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
        private final View SelfView;
        private final ImageView mIcon;
        private final TextView mTitle;
        private final TextView mSubTitle;
        private final CheckBox mcheckbox;

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
            this.mcheckbox = View.findViewById(R.id.list_picker_dialog_base_item_checkbox);
        }

        /**
         * Apply datas of item on current ViewItem.
         *
         * @param item associate item.
         */
        void applyDatas(@NonNull ItemBase item) {
            this.mIcon.setImageResource(item.getIconResource());
            this.mTitle.setText(item.getTitle());

            String subTitle = item.getSubTitle();

            if (subTitle != null && subTitle.length() > 0) {
                this.mSubTitle.setText(subTitle);

                this.mSubTitle.setVisibility(View.VISIBLE);
            } else {
                this.mSubTitle.setText("");

                this.mSubTitle.setVisibility(View.GONE);
            }

            if (item instanceof PickableItem) {
                this.mcheckbox.setChecked(((PickableItem) item).isPicked());

                this.mcheckbox.setVisibility(View.VISIBLE);
            } else {
                this.mcheckbox.setChecked(false);

                this.mcheckbox.setVisibility(View.GONE);
            }
        }
    }

    /** Defines a adapter object use in ListView. */
    static class PickerAdapter extends BaseAdapter {
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
        public void replaceAll(BackItem backItem, @NonNull Collection<Item> items) {
            this.mItems.clear();

            this.notifyDataSetChanged();

            if (backItem != null) this.mItems.add(backItem);

            for (Item item : items) {
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
                Holder.applyDatas(item);

                Holder.SelfView.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (item instanceof PickableItem && !((Item) item).hasChildren()) {
                                    PickableItem pickableItem = (PickableItem) item;

                                    boolean picked = !pickableItem.isPicked();

                                    pickableItem.setPicked(picked);

                                    // If picked, it's necessary to check others items

                                    if (picked)
                                        PickerAdapter.this.checkOtherPickedItems(pickableItem);

                                    PickerAdapter.this.notifyDataSetChanged();

                                    PickerAdapter.this.mOwner.actualizeSelectButtonLabel();
                                } else {
                                    PickerAdapter.this.mOwner.onItemClick(item);
                                }
                            }
                        });

                if (item instanceof PickableItem) {
                    Holder.mcheckbox.setOnCheckedChangeListener(
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

                                    PickerAdapter.this.mOwner.actualizeSelectButtonLabel();
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

            if (!this.mOwner.isMultiSelectMode()) {
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

    private PickerAdapter mAdapter = null;
    private ItemBase mRootItem = null;
    private BackItem mBackItem = null;
    private String mTitle = null;
    private String mBtnCancelLabel = null;
    private String mBtnSelectLabel = null;
    private ListView mListView = null;
    private ImageView mImgTitle = null;
    private TextView mTxtTitle = null;
    private TextView mTxtSubTitle = null;
    private Button mBtnCancel = null;
    private Button mBtnSelect = null;

    /**
     * Object initialisation.
     *
     * @param context current context.
     */
    protected ListPickerDialogBase(@NonNull Context context) {
        super(context, R.style.ListPickerDialogBase);
    }

    /**
     * Object initialisation.
     *
     * @param context current context.
     * @param themeResId style resource id.
     */
    protected ListPickerDialogBase(@NonNull Context context, int themeResId) {
        super(context, themeResId);
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

        this.mImgTitle = this.findViewById(R.id.list_picker_dialog_base_icon);
        this.mTxtTitle = this.findViewById(R.id.list_picker_dialog_base_title);
        this.mTxtSubTitle = this.findViewById(R.id.list_picker_dialog_base_sub_title);
        this.mListView = this.findViewById(R.id.list_picker_dialog_base_list);
        this.mBtnCancel = this.findViewById(R.id.list_picker_dialog_base_cancel_button);
        this.mBtnSelect = this.findViewById(R.id.list_picker_dialog_base_select_button);

        this.mBtnCancel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View View) {
                        ListPickerDialogBase.this.dismiss();
                    }
                });

        this.mBtnSelect.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View View) {
                        ListPickerDialogBase.this.onValidateSelection(
                                ListPickerDialogBase.this.mAdapter.getPickedItem());

                        ListPickerDialogBase.this.dismiss();
                    }
                });

        this.mAdapter = new PickerAdapter(this);

        this.mListView.setAdapter(this.mAdapter);

        View header = this.createToolBar();

        if (header != null) {
            LinearLayout headerLayout = this.findViewById(R.id.list_picker_dialog_base_toolbar);

            if (headerLayout != null) {
                headerLayout.addView(header);
            }
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

    /** Actualize cancel button label. */
    private void actualizeCancelButtonLabel() {
        if (this.mBtnCancel != null) {
            String btnName = this.mBtnCancelLabel;

            if (btnName == null || btnName.length() == 0)
                btnName =
                        super.getContext()
                                .getString(R.string.list_picker_dialog_base_cancel_button);

            this.mBtnCancel.setText(btnName);
        }
    }

    /** Actualize select button label. */
    private void actualizeSelectButtonLabel() {
        if (this.mBtnSelect != null && this.mAdapter != null) {
            String btnName = this.mBtnSelectLabel;

            if (btnName == null || btnName.length() == 0)
                btnName =
                        super.getContext()
                                .getString(R.string.list_picker_dialog_base_select_button);

            int pickedItemCount = this.mAdapter.getPickedItemCount();

            if (pickedItemCount > 0) {
                this.mBtnSelect.setEnabled(true);

                if (pickedItemCount > 1) {
                    this.mBtnSelect.setText(
                            String.format(
                                    DEF_LOCAL,
                                    "%s (%d)",
                                    btnName,
                                    this.mAdapter.getPickedItemCount()));
                } else {
                    this.mBtnSelect.setText(btnName);
                }
            } else {
                this.mBtnSelect.setEnabled(false);

                this.mBtnSelect.setText(btnName);
            }
        }
    }

    /** Actualize header dialog. */
    private void actualizeHeaderDialog() {
        if (this.mImgTitle != null && this.mTxtTitle != null && this.mTxtSubTitle != null) {

            ItemBase headerItem = this.getHeaderItem(this.mRootItem);

            if (headerItem != null) {
                if (this.mTitle != null && this.mTitle.length() > 0)
                    this.mTxtTitle.setText(this.mTitle);
                else this.mTxtTitle.setText(headerItem.getTitle());

                this.mImgTitle.setImageResource(headerItem.getIconResource());
                this.mTxtSubTitle.setText(headerItem.getSubTitle());
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

            this.actualizeHeaderDialog();

            this.mListView.setAdapter(null);

            this.mAdapter.replaceAll(this.mBackItem, this.getChildrenFor(rootItem));

            this.mListView.setAdapter(this.mAdapter);

            this.actualizeCancelButtonLabel();
            this.actualizeSelectButtonLabel();
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
        } else if (item instanceof Item && ((Item) item).hasChildren()) {
            this.navigateToItem(item);
        }
    }

    /* ---- Protected Methods, to be overridden in subclasses ---- */

    /**
     * generates a toolbar which will be placed between the title bar and the list.
     *
     * @return a view object.
     */
    protected View createToolBar() {
        return null;
    }

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
    protected Collection<Item> getChildrenFor(ItemBase item) {
        return new ArrayList<>();
    }

    /**
     * Obtains an item to display dialog header.
     *
     * @param item root item of the list to display.
     * @return a ItemBase objet to define title, sub-title and icon of dialog header.
     */
    protected ItemBase getHeaderItem(ItemBase item) {
        return item;
    }

    /**
     * Obtains an item used to create first item in list for return to parent item.
     *
     * <p>Exemple: new ItemBase ("...", "Parent directory", R.drawable...)
     *
     * @return a ItemBase objet to used to create first item in list for return to parent item.
     */
    protected BackItem getBackItem(ItemBase item) {
        return null;
    }

    /**
     * Obtains an item used to initialize the selector.
     *
     * @return a ItemBase objet to used to initialize the selector.
     */
    protected ItemBase getRootItem() {
        return null;
    }

    /**
     * Indicates if the picker is in multiple selection mode.
     *
     * @return a boolean value who indicates if the picker is in multiple selection mode.
     */
    protected boolean isMultiSelectMode() {
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

    /* ---- Public Methods ---- */

    /**
     * Set cancel button label.
     *
     * @param value new cancel button label.
     */
    public void setCancelButtonLabel(CharSequence value) {
        this.mBtnCancelLabel = (value != null && value.length() > 0) ? value.toString() : null;

        this.actualizeCancelButtonLabel();
    }

    /**
     * Set select button label.
     *
     * @param value new select button label.
     */
    public void setSelectButtonLabel(CharSequence value) {
        this.mBtnSelectLabel = (value != null && value.length() > 0) ? value.toString() : null;

        this.actualizeSelectButtonLabel();
    }

    /**
     * Set dialog title.
     *
     * @param value new cancel button label.
     */
    public void setTitle(CharSequence value) {
        this.mTitle = (value != null && value.length() > 0) ? value.toString() : null;

        this.actualizeHeaderDialog();
    }
}
