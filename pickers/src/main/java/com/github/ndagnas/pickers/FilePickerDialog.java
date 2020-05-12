/*
 * Copyright (C) 2020 Nicolas Dagnas
 *
 * Object inspired by Angad Singh, in project: https://github.com/Angads25/android-filepicker
 *
 *                 and Aefyr, in https://github.com/Aefyr/android-filepicker
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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;

import java.io.File;
import java.io.FileFilter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

/** Defines a file picker dialog. */
@SuppressWarnings({"unused", "WeakerAccess"})
public class FilePickerDialog extends ListPickerDialogBase implements PickerInterface {

    /* SELECTION_TYPES */

    /**
     * FILE_SELECT specifies that from list of Files/Directories a File has to be selected. It is
     * the default Selection Type.
     */
    public static final int FILE_SELECT = 0;

    /** DIR_SELECT specifies that from list of Files/Directories a Directory has to be selected. */
    public static final int DIR_SELECT = 1;

    /** FILE_AND_DIR_SELECT specifies that from list of Files/Directories both can be selected. */
    public static final int FILE_AND_DIR_SELECT = 2;

    /** Directory separator. */
    public static final String DIRECTORY_SEPERATOR = "/";

    /** Storage root directory. */
    public static final String STORAGE_DIR = "mnt";

    /** DEFAULT_DIR is the default mount point of the SDCARD. It is the default mount point. */
    public static final String DEFAULT_DIR = DIRECTORY_SEPERATOR + STORAGE_DIR;

    /** SORT_BY_NAME specifies that list of Files/Directories is sorted by name. */
    public static final int SORT_BY_NAME = 0;

    /**
     * SORT_BY_LAST_MODIFIED specifies that list of Files/Directories is sorted by modified date.
     */
    public static final int SORT_BY_LAST_MODIFIED = 1;

    /** SORT_BY_SIZE specifies that list of Files/Directories is sorted by size. */
    public static final int SORT_BY_SIZE = 2;

    /** SORT_ORDER_NORMAL specifies that list of Files/Directories is sorted by normal order. */
    public static final int SORT_ORDER_NORMAL = 0;

    /** SORT_ORDER_NORMAL specifies that list of Files/Directories is sorted by reverse order. */
    public static final int SORT_ORDER_REVERSE = 1;

    /** Defines an item of the list. */
    static class FileItem {
        /**
         * Object initialisation.
         *
         * @param object json node object.
         */
        public FileItem(File object, FileItem parent) {
            this.object = object;
            this.parent = parent;
            this.listViewState = null;
        }

        /** Json node object. */
        public File object;

        /** List view state. */
        public Parcelable listViewState;

        /** Json node parent. */
        public FileItem parent;
    }

    /** Class to filter the list of files. */
    static class ExtensionFilter implements FileFilter {
        // Attributes

        private final FilePickerDialog mDialog;

        /**
         * Object initialisation.
         *
         * @param dialog owner of this object.
         */
        ExtensionFilter(@NonNull FilePickerDialog dialog) {
            this.mDialog = dialog;
        }

        /**
         * Function to filter files based on defined rules.
         *
         * @param File File to check.
         */
        @Override
        public boolean accept(File File) {
            // All directories are added in the least that can be read by the Application

            if (File.isDirectory() && File.canRead()) return true;

            // True for files, If the selection type is Directory type, ie.
            // Only directory has to be selected from the list, then all files are ignored.

            if (this.mDialog.mSelectionType == FilePickerDialog.DIR_SELECT) return false;

            // Validate if no filter

            if (this.mDialog.mPatterns == null || this.mDialog.mPatterns.length == 0) return true;

            // Check whether name of the file ends with the extension. Added if it does.

            String fileName = File.getName().toLowerCase(Locale.getDefault());

            for (Pattern pattern : this.mDialog.mPatterns) {
                if (pattern.matcher(fileName).matches()) return true;
            }

            return false;
        }
    }

    // Constants

    private static java.util.Locale DEF_LOCAL = java.util.Locale.getDefault();

    // Attributes

    private final Context mContext;
    private final int mRequestCode;
    private final int mSelectionType;
    private final File mRootDir;
    private final File mErrorDir;
    private final File mOffsetDir;
    private final Pattern[] mPatterns;
    private int mSortBy;
    private int mSortOrder;
    private final boolean mToolbarIsVisible;
    private final OnSingleChoiceValidationListener<String> mOnSingleChoiceValidationListener;
    private final OnMultiChoiceValidationListener<String> mOnMultiChoiceValidationListener;
    private final ExtensionFilter mFilter;
    private Comparator<FileItem> mSorter;
    private LinearLayout mToolbarView = null;
    private TextView mNameColumn = null;
    private TextView mDateColumn = null;
    private TextView mSizeColumn = null;
    private static DecimalFormat mSizeDecimalFormat;

    /**
     * Create a file picker dialog.
     *
     * @param builder a builder object contains dialog parameters.
     */
    private FilePickerDialog(@NonNull Builder builder) {
        super(builder.P);

        this.mContext = builder.P.context;
        this.mRequestCode = builder.mRequestCode;
        this.mSelectionType = builder.mSelectionType;
        this.mRootDir = builder.mRootDir;
        this.mErrorDir = builder.mErrorDir;
        this.mOffsetDir = builder.mOffsetDir;
        this.mPatterns = builder.mPatterns;
        this.mSortBy = builder.mSortBy;
        this.mSortOrder = builder.mSortOrder;
        this.mToolbarIsVisible = builder.mToolbarIsVisible;
        this.mOnSingleChoiceValidationListener = builder.mOnSingleChoiceValidationListener;
        this.mOnMultiChoiceValidationListener = builder.mOnMultiChoiceValidationListener;

        this.mFilter = new ExtensionFilter(this);
        this.mSorter = createComparator(this);
    }

    /* ---- Derived Methods ---- */

    /** Called on dialog show. */
    @Override
    public void show() {
        if (!this.checkStorageAccessPermissions()
                && this.mContext instanceof Activity
                && this.mRequestCode != 0) {
            ((Activity) this.mContext)
                    .requestPermissions(
                            new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                            this.mRequestCode);
        } else {
            super.show();
        }
    }

    /* ---- Privates Methods ---- */

    /**
     * Post Devices require permissions on Runtime (Risky Ones), even though it has been specified
     * in the uses-permission tag of manifest. checkStorageAccessPermissions method checks whether
     * the READ EXTERNAL STORAGE permission has been granted to the Application.
     *
     * @return a boolean value notifying whether the permission is granted or not.
     */
    private boolean checkStorageAccessPermissions() {
        return (this.mContext.checkCallingOrSelfPermission(
                        "android.permission.READ_EXTERNAL_STORAGE")
                == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Create comparator for sort objects in list.
     *
     * @return A comparator object for sort list.
     */
    private static Comparator<FileItem> createComparator(FilePickerDialog dialog) {
        final Comparator<FileItem> comparator;

        final int reversed = ((dialog.mSortOrder == FilePickerDialog.SORT_ORDER_REVERSE) ? -1 : 1);
        switch (dialog.mSortBy) {
            case FilePickerDialog.SORT_BY_LAST_MODIFIED:
                {
                    comparator =
                            new Comparator<FileItem>() {
                                @Override
                                public int compare(FileItem lht, FileItem rht) {
                                    if (rht.object.isDirectory() && lht.object.isDirectory()) {
                                        if (lht.object.getName().equals("..."))
                                            return -1 * reversed;
                                        if (rht.object.getName().equals("...")) return reversed;

                                        return Long.compare(
                                                        lht.object.lastModified(),
                                                        rht.object.lastModified())
                                                * reversed;
                                    }

                                    // If the comparison is not between two directories, return the
                                    // file with alphabetic order first.

                                    if (!rht.object.isDirectory() && !lht.object.isDirectory()) {
                                        int result =
                                                Long.compare(
                                                                lht.object.lastModified(),
                                                                rht.object.lastModified())
                                                        * reversed;

                                        if (result == 0)
                                            return lht.object
                                                            .getName()
                                                            .compareToIgnoreCase(
                                                                    rht.object.getName())
                                                    * reversed;

                                        return result;
                                    }

                                    // If the comparison is between a directory and a file, return
                                    // the directory.

                                    if (lht.object.isDirectory() && !rht.object.isDirectory())
                                        return -1 * reversed;
                                    if (!lht.object.isDirectory() && rht.object.isDirectory())
                                        return reversed;

                                    // Same as above but order of occurrence is different.

                                    return -1 * reversed;
                                }
                            };

                    break;
                }
            case FilePickerDialog.SORT_BY_SIZE:
                {
                    comparator =
                            new Comparator<FileItem>() {
                                @Override
                                public int compare(FileItem lht, FileItem rht) {
                                    if (rht.object.isDirectory() && lht.object.isDirectory()) {
                                        if (lht.object.getName().equals("..."))
                                            return -1 * reversed;
                                        if (rht.object.getName().equals("...")) return reversed;

                                        return lht.object
                                                        .getName()
                                                        .compareToIgnoreCase(rht.object.getName())
                                                * reversed;
                                    }

                                    // If the comparison is not between two directories, return the
                                    // file with alphabetic order first.

                                    if (!rht.object.isDirectory() && !lht.object.isDirectory()) {
                                        int result =
                                                Long.compare(
                                                                lht.object.length(),
                                                                rht.object.length())
                                                        * reversed;

                                        if (result == 0)
                                            return lht.object
                                                            .getName()
                                                            .compareToIgnoreCase(
                                                                    rht.object.getName())
                                                    * reversed;

                                        return result;
                                    }

                                    // If the comparison is between a directory and a file, return
                                    // the directory.

                                    if (lht.object.isDirectory() && !rht.object.isDirectory())
                                        return -1 * reversed;
                                    if (!lht.object.isDirectory() && rht.object.isDirectory())
                                        return reversed;

                                    // Same as above but order of occurrence is different.

                                    return -1 * reversed;
                                }
                            };

                    break;
                }
            default:
                {
                    comparator =
                            new Comparator<FileItem>() {
                                @Override
                                public int compare(FileItem lht, FileItem rht) {
                                    if (rht.object.isDirectory() && lht.object.isDirectory()) {
                                        if (lht.object.getName().equals("..."))
                                            return -1 * reversed;
                                        if (rht.object.getName().equals("...")) return reversed;

                                        return lht.object
                                                        .getName()
                                                        .compareToIgnoreCase(rht.object.getName())
                                                * reversed;
                                    }

                                    // If the comparison is not between two directories, return the
                                    // file with alphabetic order first.

                                    if (!rht.object.isDirectory() && !lht.object.isDirectory())
                                        return lht.object
                                                        .getName()
                                                        .compareToIgnoreCase(rht.object.getName())
                                                * reversed;

                                    // If the comparison is between a directory and a file, return
                                    // the directory.

                                    if (lht.object.isDirectory() && !rht.object.isDirectory())
                                        return -1 * reversed;
                                    if (!lht.object.isDirectory() && rht.object.isDirectory())
                                        return reversed;

                                    // Same as above but order of occurrence is different.

                                    return -1 * reversed;
                                }
                            };

                    break;
                }
        }
        return comparator;
    }

    /* ---- Toolbar Methods ---- */

    /** Actualize toolbar. */
    private void actualizeToolbar() {
        // Sort by name

        if (this.mSortBy == FilePickerDialog.SORT_BY_NAME) {
            if (this.mSortOrder == FilePickerDialog.SORT_ORDER_NORMAL)
                this.mNameColumn.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, R.drawable.ic_file_picker_sort_normal, 0);
            else
                this.mNameColumn.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, R.drawable.ic_file_picker_sort_reverse, 0);
        } else {
            this.mNameColumn.setCompoundDrawables(null, null, null, null);
        }

        // Sort by date

        if (this.mSortBy == FilePickerDialog.SORT_BY_LAST_MODIFIED) {
            if (this.mSortOrder == FilePickerDialog.SORT_ORDER_NORMAL)
                this.mDateColumn.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, R.drawable.ic_file_picker_sort_normal, 0);
            else
                this.mDateColumn.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, R.drawable.ic_file_picker_sort_reverse, 0);
        } else {
            this.mDateColumn.setCompoundDrawables(null, null, null, null);
        }

        // Sort by size

        if (this.mSortBy == FilePickerDialog.SORT_BY_SIZE) {
            if (this.mSortOrder == FilePickerDialog.SORT_ORDER_NORMAL)
                this.mSizeColumn.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, R.drawable.ic_file_picker_sort_normal, 0);
            else
                this.mSizeColumn.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, R.drawable.ic_file_picker_sort_reverse, 0);
        } else {
            this.mSizeColumn.setCompoundDrawables(null, null, null, null);
        }
    }

    /**
     * Actualize item comparator with new sort order.
     *
     * @param sortBy new sorter order.
     */
    private void actualizeComparator(int sortBy) {
        if (this.mSortBy == sortBy) {
            if (this.mSortOrder == FilePickerDialog.SORT_ORDER_NORMAL)
                this.mSortOrder = FilePickerDialog.SORT_ORDER_REVERSE;
            else this.mSortOrder = FilePickerDialog.SORT_ORDER_NORMAL;
        } else {
            this.mSortBy = sortBy;

            switch (sortBy) {
                case FilePickerDialog.SORT_BY_LAST_MODIFIED:
                case FilePickerDialog.SORT_BY_SIZE:
                    this.mSortOrder = FilePickerDialog.SORT_ORDER_REVERSE;
                    break;
                default:
                    this.mSortOrder = FilePickerDialog.SORT_ORDER_NORMAL;
                    break;
            }
        }
        this.mSorter = createComparator(this);

        this.actualizeToolbar();
        this.reload();
    }

    /**
     * Get toolbar from view.
     *
     * @param toolbar toolbar view.
     * @return a view contains toolbar.
     */
    private View getToolbarView(View toolbar) {
        if (!(toolbar instanceof LinearLayout)) return null;
        this.mToolbarView = (LinearLayout) toolbar;

        this.mNameColumn = this.mToolbarView.findViewById(R.id.file_picker_dialog_sort_name_label);
        this.mDateColumn = this.mToolbarView.findViewById(R.id.file_picker_dialog_sort_date_label);
        this.mSizeColumn = this.mToolbarView.findViewById(R.id.file_picker_dialog_sort_size_label);

        this.actualizeToolbar();

        // Name column

        RelativeLayout nameButton =
                this.mToolbarView.findViewById(R.id.file_picker_dialog_sort_name);

        nameButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FilePickerDialog.this.actualizeComparator(FilePickerDialog.SORT_BY_NAME);
                    }
                });

        // Date column

        RelativeLayout dateButton =
                this.mToolbarView.findViewById(R.id.file_picker_dialog_sort_date);

        dateButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FilePickerDialog.this.actualizeComparator(
                                FilePickerDialog.SORT_BY_LAST_MODIFIED);
                    }
                });

        // Size column
        RelativeLayout sizeButton =
                this.mToolbarView.findViewById(R.id.file_picker_dialog_sort_size);

        sizeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FilePickerDialog.this.actualizeComparator(FilePickerDialog.SORT_BY_SIZE);
                    }
                });

        // Actualize button

        RelativeLayout actualizeButton =
                this.mToolbarView.findViewById(R.id.file_picker_dialog_actualize);

        actualizeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FilePickerDialog.this.reload();
                    }
                });

        return toolbar;
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

            if (itemTag instanceof FileItem) {
                FileItem fileItem = (FileItem) itemTag;

                if (fileItem.object.isDirectory() && fileItem.object.canRead()) {
                    String rootPath = this.mRootDir.getAbsolutePath();
                    String otherPath = fileItem.object.getAbsolutePath();

                    if (!otherPath.equals(rootPath) && otherPath.startsWith(rootPath)) {
                        FileItem parentItem = fileItem.parent;

                        if (parentItem == null || !parentItem.object.canRead()) {
                            File parent = fileItem.object.getParentFile();

                            if (parent != null && parent.canRead()) {
                                parentItem = new FileItem(parent, null);
                            }
                        }

                        if (parentItem != null) {
                            if (!(item instanceof BackItem))
                                parentItem.listViewState = super.getListViewState();

                            return new BackItem(
                                    this.mContext.getString(
                                            R.string.file_picker_dialog_parent_directory),
                                    this.mContext.getString(
                                            R.string.file_picker_dialog_parent_directory_text),
                                    R.drawable.ic_file_picker_folder,
                                    parentItem.listViewState,
                                    parentItem);
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * Obtains an item list of items corresponding to the children of the root item.
     *
     * @param item item of the list to display.
     * @return a collection of PickerItem objects to load in list.
     */
    @Override
    protected Collection<PickerItem> getChildrenFor(ItemBase item) {
        ArrayList<PickerItem> itemList = new ArrayList<>();

        if (item != null) {
            Object itemTag = item.getTag();

            if (itemTag instanceof FileItem) {
                FileItem fileItem = (FileItem) itemTag;

                if (fileItem.object.isDirectory() && fileItem.object.canRead()) {
                    String rootPath = this.mRootDir.getAbsolutePath();
                    String otherPath = fileItem.object.getAbsolutePath();

                    if (otherPath.equals(rootPath) || otherPath.startsWith(rootPath)) {
                        File[] files = fileItem.object.listFiles(this.mFilter);

                        ArrayList<FileItem> sortedObjects = new ArrayList<>();

                        if (files != null) {
                            for (File file : files) {
                                if (!file.isHidden() && file.canRead())
                                    sortedObjects.add(new FileItem(file, fileItem));
                            }

                            Collections.sort(sortedObjects, this.mSorter);

                            for (FileItem newObject : sortedObjects) {
                                itemList.add(this.createItem(newObject));
                            }
                        }
                    }
                } else {
                    Toast.makeText(
                                    this.mContext,
                                    R.string.file_picker_dialog_error_dir_access,
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
        if (this.checkStorageAccessPermissions()) {
            if (this.mRootDir.isDirectory() && this.mOffsetDir.isDirectory()) {
                String rootPath = this.mRootDir.getAbsolutePath();
                String offsetPath = this.mOffsetDir.getAbsolutePath();

                if (offsetPath.equals(rootPath) || offsetPath.startsWith(rootPath)) {
                    if (this.mOffsetDir.canRead())
                        return this.createItem(new FileItem(this.mOffsetDir, null));
                }
            }

            if (this.mRootDir.isDirectory() && this.mRootDir.canRead())
                return this.createItem(new FileItem(this.mRootDir, null));
            if (this.mErrorDir.isDirectory() && this.mErrorDir.canRead())
                return this.createItem(new FileItem(this.mErrorDir, null));
        }

        Toast.makeText(
                        this.mContext,
                        R.string.file_picker_dialog_error_dir_access,
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
        if (item != null) {
            Object itemTag = item.getTag();

            if (itemTag instanceof FileItem) {
                FileItem fileItem = (FileItem) itemTag;

                return new ItemBase(
                        fileItem.object.getName(),
                        fileItem.object.getAbsolutePath(),
                        R.drawable.ic_file_picker_header);
            }
        }

        return null;
    }

    /**
     * Obtains a toolbar view which will be placed between the title bar and the list.
     *
     * @return a view object.
     */
    @SuppressLint("InflateParams")
    @Override
    protected View getToolBarView() {
        if (!this.mToolbarIsVisible) return null;

        if (this.mToolbarView == null) {
            LayoutInflater inflater = LayoutInflater.from(this.mContext);

            return this.getToolbarView(inflater.inflate(R.layout.file_picker_dialog_toolbar, null));
        }

        return this.mToolbarView;
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
    protected void onValidateSelection(Collection<PickableItem> items) {
        if (items.size() > 0) {
            ArrayList<String> result = new ArrayList<>();

            for (PickableItem item : items) {
                Object itemTag = item.getTag();

                if (itemTag instanceof FileItem)
                    result.add(((FileItem) itemTag).object.getAbsolutePath());
            }

            if (this.mOnSingleChoiceValidationListener != null)
                this.mOnSingleChoiceValidationListener.onClick(this, result.get(0));

            if (this.mOnMultiChoiceValidationListener != null)
                this.mOnMultiChoiceValidationListener.onClick(this, result.toArray(new String[0]));
        }
    }

    /* ---- Privates Methods ---- */

    /**
     * Create picker item corresponding to the specified file.
     *
     * @param fileItem fileItem.
     * @return a picker item corresponding to the specified file.
     */
    private PickerItem createItem(FileItem fileItem) {
        String strDateFormat = this.mContext.getString(R.string.file_picker_dialog_date_format);

        SimpleDateFormat dateFormat = new SimpleDateFormat(strDateFormat, Locale.getDefault());

        String itemDate = dateFormat.format(new Date(fileItem.object.lastModified()));
        if (fileItem.object.isDirectory()) {
            String Label = this.mContext.getString(R.string.file_picker_dialog_last_edit_directory);

            String subTitle = String.format(Locale.getDefault(), Label, itemDate);

            switch (this.mSelectionType) {
                case FilePickerDialog.DIR_SELECT:
                case FilePickerDialog.FILE_AND_DIR_SELECT:
                    return new PickableItem(
                            fileItem.object.getName(),
                            subTitle,
                            R.drawable.ic_file_picker_folder,
                            fileItem,
                            true);
                default:
                    return new PickerItem(
                            fileItem.object.getName(),
                            subTitle,
                            R.drawable.ic_file_picker_folder,
                            fileItem,
                            true);
            }
        } else {
            String Label = this.mContext.getString(R.string.file_picker_dialog_last_edit_file);

            String fileSize = this.formatSize(fileItem.object.length());

            String subTitle = String.format(DEF_LOCAL, Label, fileSize, itemDate);

            return new PickableItem(
                    fileItem.object.getName(),
                    subTitle,
                    R.drawable.ic_file_picker_file,
                    fileItem,
                    false);
        }
    }

    /**
     * Convert file size on string.
     *
     * @param bytes size of file.
     * @return A string representing the size of the file.
     */
    private String formatSize(long bytes) {
        if (mSizeDecimalFormat == null) {
            mSizeDecimalFormat = new DecimalFormat("#.##");

            mSizeDecimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
        }

        String[] units =
                super.getContext()
                        .getResources()
                        .getStringArray(R.array.file_picker_dialog_size_units);

        for (int Index = 0; Index < units.length; Index++) {
            float size = (float) bytes / (float) Math.pow(1024, Index);

            if (size < 1024)
                return String.format("%s %s", mSizeDecimalFormat.format(size), units[Index]);
        }

        return bytes + " B";
    }

    /** Provide a builder for a file picker dialog. */
    @SuppressWarnings("UnusedReturnValue")
    public static class Builder {

        // Attributes

        private final PickerParams P;
        private int mRequestCode = 0;
        private int mSelectionType = FilePickerDialog.FILE_AND_DIR_SELECT;
        private File mRootDir = new File(FilePickerDialog.DEFAULT_DIR);
        private File mErrorDir = new File(FilePickerDialog.DEFAULT_DIR);
        private File mOffsetDir = new File(FilePickerDialog.DEFAULT_DIR);
        private Pattern[] mPatterns = new Pattern[0];
        private int mSortBy = FilePickerDialog.SORT_BY_NAME;
        private int mSortOrder = FilePickerDialog.SORT_ORDER_NORMAL;
        private boolean mToolbarIsVisible = true;
        private OnSingleChoiceValidationListener<String> mOnSingleChoiceValidationListener = null;
        private OnMultiChoiceValidationListener<String> mOnMultiChoiceValidationListener = null;

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

        /* File Picker Properties */

        /**
         * Sets the code used for request permission.
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setRequestCode(int requestCode) {
            this.mRequestCode = requestCode;
            return this;
        }

        /**
         * Sets selection Type defines that whether a File/Directory or both of these has to be
         * selected. Default value is FILE_SELECT.
         *
         * <p>FILE_SELECT, DIR_SELECT, FILE_AND_DIR_SELECT are the three selection types.
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setSelectionType(int selectionType) {
            this.mSelectionType = selectionType;
            return this;
        }

        /**
         * Sets parent/root directory. List of Files are populated from here. Can be set to any
         * readable directory. /sdcard is the default location.
         *
         * <p>EX. /sdcard
         *
         * <p>Ex. /mnt
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setRootDir(@NonNull CharSequence rootDir) {
            this.mRootDir = new File(rootDir.toString());
            return this;
        }

        /**
         * Sets parent/root directory. List of Files are populated from here. Can be set to any
         * readable directory. /sdcard is the default location.
         *
         * <p>EX. /sdcard
         *
         * <p>Ex. /mnt
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setRootDir(@NonNull File rootDir) {
            this.mRootDir = rootDir;
            return this;
        }

        /**
         * Sets directory is used when root directory is not readable or accessible. /sdcard is the
         * default location.
         *
         * <p>Ex. /sdcard
         *
         * <p>Ex. /mnt
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setErrorDir(@NonNull CharSequence errorDir) {
            this.mErrorDir = new File(errorDir.toString());
            return this;
        }

        /**
         * Sets directory is used when root directory is not readable or accessible. /sdcard is the
         * default location.
         *
         * <p>Ex. /sdcard
         *
         * <p>Ex. /mnt
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setErrorDir(@NonNull File errorDir) {
            this.mErrorDir = errorDir;
            return this;
        }

        /**
         * Sets directory can be used as an offset. It is the first directory that is shown in
         * dialog. Consider making it Root's sub-directory.
         *
         * <p>Ex. Root: /sdcard
         *
         * <p>Ex. Offset: /sdcard/Music/Country
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setOffsetDir(@NonNull CharSequence offsetDir) {
            this.mOffsetDir = new File(offsetDir.toString());
            return this;
        }

        /**
         * Sets directory can be used as an offset. It is the first directory that is shown in
         * dialog. Consider making it Root's sub-directory.
         *
         * <p>Ex. Root: /sdcard
         *
         * <p>Ex. Offset: /sdcard/Music/Country
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setOffsetDir(@NonNull File offsetDir) {
            this.mOffsetDir = offsetDir;
            return this;
        }

        /**
         * Add pattern to filter files.
         *
         * <p>Ex. "(.*)\\.jpg"
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder addPattern(@NonNull CharSequence pattern) {
            return this.addPattern(Pattern.compile(pattern.toString()));
        }

        /**
         * Add pattern to filter files.
         *
         * <p>Ex. "(.*)\\.jpg"
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder addPattern(@NonNull Pattern pattern) {
            ArrayList<Pattern> patterns = new ArrayList<>(Arrays.asList(this.mPatterns));

            patterns.add(pattern);

            this.mPatterns = patterns.toArray(new Pattern[0]);

            return this;
        }

        /**
         * Sort by defines the sort order of the items. Default value is SORT_BY_NAME.
         *
         * <p>SORT_BY_NAME, SORT_BY_LAST_MODIFIED, SORT_BY_SIZE are the three sort by.
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setSortBy(int sortBy) {
            this.mSortBy = sortBy;
            return this;
        }

        /**
         * Sort order defines the sort direction of the items. Default value is SORT_ORDER_NORMAL.
         *
         * <p>SORT_ORDER_NORMAL, SORT_ORDER_REVERSE are the two sort orders.
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setSortOrder(int sortOrder) {
            this.mSortOrder = sortOrder;
            return this;
        }

        /**
         * Show/Hide toolbar view. Default value is true.
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setToolbarIsVisible(boolean toolbarIsVisible) {
            this.mToolbarIsVisible = toolbarIsVisible;
            return this;
        }

        /**
         * Sets the callback that will be called if the dialog is validated (single selection mode).
         *
         * @return this builder object to allow for chaining of calls to set methods
         */
        public Builder setOnSingleChoiceValidationListener(
                OnSingleChoiceValidationListener<String> listener) {
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
                OnMultiChoiceValidationListener<String> listener) {
            this.mOnMultiChoiceValidationListener = listener;

            return this;
        }

        /**
         * Creates an {@link FilePickerDialog} with the arguments supplied to this builder and
         * immediately displays the dialog.
         *
         * <p>Calling this method is functionally identical to:
         *
         * @return a FilePickerDialog dialog.
         */
        public FilePickerDialog show() {
            final FilePickerDialog dialog = new FilePickerDialog(this);

            dialog.show();

            return dialog;
        }
    }
}
