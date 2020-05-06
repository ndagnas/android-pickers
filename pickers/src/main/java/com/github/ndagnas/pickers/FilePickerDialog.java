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
 *    Created by Nicolas Dagnas on 01-05-2020.
 *
 */

package com.github.ndagnas.pickers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileFilter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

/** Defines a file picker dialog. */
@SuppressWarnings({"unused", "WeakerAccess"})
public class FilePickerDialog extends ListPickerDialogBase {
    private static java.util.Locale DEF_LOCAL = java.util.Locale.getDefault();

    /** Defines constant to check for external read permission. */
    public static final int EXTERNAL_READ_PERMISSION_GRANT = 1;

    /** Defines a listener to be informed of the validation of the selection. */
    public interface ValidateSelectionListener {
        /**
         * Called on dialog validation.
         *
         * @param files list of selected files/directories.
         */
        void onValidateSelection(String[] files);
    }

    // SELECTION_MODES

    /**
     * SINGLE_MODE specifies that a single File/Directory has to be selected from the list of
     * Files/Directories. It is the default Selection Mode.
     */
    public static final int SINGLE_MODE = 0;

    /**
     * MULTI_MODE specifies that multiple Files/Directories has to be selected from the list of
     * Files/Directories.
     */
    public static final int MULTI_MODE = 1;

    // SELECTION_TYPES

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

    /**
     * Descriptor class to define properties of the Dialog. Actions are performed upon these
     * Properties.
     */
    @SuppressWarnings("WeakerAccess")
    public static class Properties {
        /** Object initialisation. */
        public Properties() {
            this.selectionMode = FilePickerDialog.SINGLE_MODE;
            this.selectionType = FilePickerDialog.FILE_SELECT;

            this.root = new File(FilePickerDialog.DEFAULT_DIR);
            this.errorDir = new File(FilePickerDialog.DEFAULT_DIR);
            this.offsetDir = new File(FilePickerDialog.DEFAULT_DIR);

            this.patterns = null;

            this.sortBy = FilePickerDialog.SORT_BY_NAME;
            this.sortOrder = FilePickerDialog.SORT_ORDER_NORMAL;

            this.showToolbar = true;
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
            result.root = this.root;
            result.errorDir = this.errorDir;
            result.offsetDir = this.offsetDir;
            result.patterns = this.patterns;
            result.sortBy = this.sortBy;
            result.sortOrder = this.sortOrder;
            result.showToolbar = this.showToolbar;

            return result;
        }

        /**
         * Selection Mode defines whether a single of multiple Files/Directories have to be
         * selected.
         *
         * <p>SINGLE_MODE and MULTI_MODE are the two selection modes.
         *
         * <p>Set to SINGLE_MODE as default value by constructor.
         */
        public int selectionMode;

        /**
         * Selection Type defines that whether a File/Directory or both of these has to be selected.
         *
         * <p>FILE_SELECT, DIR_SELECT, FILE_AND_DIR_SELECT are the three selection types.
         *
         * <p>Set to FILE_SELECT as default value by constructor.
         */
        public int selectionType;

        /**
         * The Parent/Root Directory. List of Files are populated from here. Can be set to any
         * readable directory. /sdcard is the default location.
         *
         * <p>EX. /sdcard
         *
         * <p>Ex. /mnt
         */
        public File root;

        /**
         * The Directory is used when Root Directory is not readable or accessible. /sdcard is the
         * default location.
         *
         * <p>Ex. /sdcard
         *
         * <p>Ex. /mnt
         */
        public File errorDir;

        /**
         * The Directory can be used as an offset. It is the first directory that is shown in
         * dialog. Consider making it Root's sub-directory.
         *
         * <p>Ex. Root: /sdcard
         *
         * <p>Ex. Offset: /sdcard/Music/Country
         */
        public File offsetDir;

        /**
         * An Array of String containing patterns, Files with only that will be shown. Others will
         * be ignored. Set to null as default value by constructor.
         *
         * <p>Ex. String ext={"*.jpg","*.jpeg","file*.png","pictures*-*.gif"};
         */
        public String[] patterns;

        /**
         * Sort by defines the sort order of the items.
         *
         * <p>SORT_BY_NAME, SORT_BY_LAST_MODIFIED, SORT_BY_SIZE are the three sort by.
         *
         * <p>Set to SORT_BY_NAME as default value by constructor.
         */
        public int sortBy;

        /**
         * Sort order defines the sort direction of the items.
         *
         * <p>SORT_ORDER_NORMAL, SORT_ORDER_REVERSE are the two sort orders.
         *
         * <p>Set to SORT_ORDER_NORMAL as default value by constructor.
         */
        public int sortOrder;

        /** indicates whether to display the toolbar used to manage sorting. */
        public boolean showToolbar;
    }

    /** Class to filter the list of files. */
    static class ExtensionFilter implements FileFilter {
        private final FilePickerDialog mOwner;
        private final Properties mProperties;
        private final Pattern[] mValidPatterns;

        /**
         * Object initialisation.
         *
         * @param owner owner of this object.
         * @param properties picker dialog properties.
         */
        ExtensionFilter(@NonNull FilePickerDialog owner, @NonNull Properties properties) {
            this.mOwner = owner;
            this.mProperties = properties;

            if (properties.patterns != null && properties.patterns.length > 0) {
                this.mValidPatterns = new Pattern[properties.patterns.length];

                for (int Index = 0; Index < this.mValidPatterns.length; Index++)
                    this.mValidPatterns[Index] = Pattern.compile(properties.patterns[Index]);
            } else {
                this.mValidPatterns = null;
            }
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
            //
            // Only directory has to be selected from the list, then all files are ignored.

            if (this.mProperties.selectionType == FilePickerDialog.DIR_SELECT) return false;

            // Validate if no filter

            if (this.mValidPatterns == null) return true;

            // Check whether name of the file ends with the extension. Added if it does.

            String fileName = File.getName().toLowerCase(Locale.getDefault());

            for (Pattern pattern : this.mValidPatterns) {
                if (pattern.matcher(fileName).matches()) return true;
            }

            return false;
        }
    }

    private final Context mContext;
    private final Properties mProperties;
    private final ExtensionFilter mFilter;
    private Comparator<File> mSorter;
    private ValidateSelectionListener mListener;
    private LinearLayout mToolbar = null;
    private TextView mNameColumn = null;
    private TextView mDateColumn = null;
    private TextView mSizeColumn = null;
    private static DecimalFormat mSizeDecimalFormat;

    /**
     * Object initialisation.
     *
     * @param context current context.
     * @param properties picker properties.
     */
    public FilePickerDialog(@NonNull Context context, @NonNull Properties properties) {
        super(context, R.style.ListPickerDialogBase);

        this.mContext = context;
        this.mProperties = properties.cloneProperties();
        this.mFilter = new ExtensionFilter(this, this.mProperties);
        this.mSorter = createComparator(this.mProperties);
    }

    /**
     * Object initialisation.
     *
     * @param context current context.
     * @param properties picker properties.
     * @param themeResId style resource id.
     */
    public FilePickerDialog(
            @NonNull Context context, @NonNull Properties properties, int themeResId) {
        super(context, themeResId);

        this.mContext = context;
        this.mProperties = properties.cloneProperties();
        this.mFilter = new ExtensionFilter(this, this.mProperties);
        this.mSorter = createComparator(this.mProperties);
    }

    /* ---- Derived Methods ---- */

    /** Called on dialog show. */
    @Override
    public void show() {
        if (!this.checkStorageAccessPermissions() && this.mContext instanceof Activity) {
            ((Activity) this.mContext)
                    .requestPermissions(
                            new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                            EXTERNAL_READ_PERMISSION_GRANT);
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
        return (super.getContext()
                        .checkCallingOrSelfPermission("android.permission.READ_EXTERNAL_STORAGE")
                == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Create comparator for sort objects in list.
     *
     * @return A comparator object for sort list.
     */
    private static Comparator<File> createComparator(Properties properties) {
        final Comparator<File> comparator;

        final int reversed =
                ((properties.sortOrder == FilePickerDialog.SORT_ORDER_REVERSE) ? -1 : 1);

        switch (properties.sortBy) {
            case FilePickerDialog.SORT_BY_LAST_MODIFIED:
                {
                    comparator =
                            new Comparator<File>() {
                                @Override
                                public int compare(File lht, File rht) {
                                    if (rht.isDirectory() && lht.isDirectory()) {
                                        if (lht.getName().equals("...")) return -1 * reversed;
                                        if (rht.getName().equals("...")) return reversed;

                                        return -Long.compare(lht.lastModified(), rht.lastModified())
                                                * reversed;
                                    }

                                    // If the comparison is not between two directories, return the
                                    // file with alphabetic order first.

                                    if (!rht.isDirectory() && !lht.isDirectory())
                                        return -Long.compare(lht.lastModified(), rht.lastModified())
                                                * reversed;

                                    // If the comparison is between a directory and a file, return
                                    // the directory.

                                    if (rht.isDirectory() && !lht.isDirectory()) return reversed;

                                    // Same as above but order of occurrence is different.

                                    return -1 * reversed;
                                }
                            };

                    break;
                }
            case FilePickerDialog.SORT_BY_SIZE:
                {
                    comparator =
                            new Comparator<File>() {
                                @Override
                                public int compare(File lht, File rht) {
                                    if (rht.isDirectory() && lht.isDirectory()) {
                                        if (lht.getName().equals("...")) return -1 * reversed;
                                        if (rht.getName().equals("...")) return reversed;

                                        return lht.getName()
                                                        .toLowerCase()
                                                        .compareTo(rht.getName().toLowerCase())
                                                * reversed;
                                    }

                                    // If the comparison is not between two directories, return the
                                    // file with alphabetic order first.

                                    if (!rht.isDirectory() && !lht.isDirectory())
                                        return -Long.compare(lht.length(), rht.length()) * reversed;

                                    // If the comparison is between a directory and a file, return
                                    // the directory.

                                    if (rht.isDirectory() && !lht.isDirectory()) return reversed;

                                    // Same as above but order of occurrence is different.

                                    return -1 * reversed;
                                }
                            };

                    break;
                }

            default:
                {
                    comparator =
                            new Comparator<File>() {
                                @Override
                                public int compare(File lht, File rht) {
                                    if (rht.isDirectory() && lht.isDirectory()) {
                                        if (lht.getName().equals("...")) return -1 * reversed;
                                        if (rht.getName().equals("...")) return reversed;

                                        String Filename = lht.getName().toLowerCase();

                                        return Filename.compareTo(rht.getName().toLowerCase())
                                                * reversed;
                                    }

                                    // If the comparison is not between two directories, return the
                                    // file with alphabetic order first.

                                    if (!rht.isDirectory() && !lht.isDirectory()) {
                                        String Filename = lht.getName().toLowerCase();

                                        return Filename.compareTo(rht.getName().toLowerCase())
                                                * reversed;
                                    }

                                    // If the comparison is between a directory and a file, return
                                    // the directory.

                                    if (rht.isDirectory() && !lht.isDirectory()) return reversed;

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

        if (this.mProperties.sortBy == FilePickerDialog.SORT_BY_NAME) {
            if (this.mProperties.sortOrder == FilePickerDialog.SORT_ORDER_NORMAL)
                this.mNameColumn.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, R.drawable.ic_file_picker_up, 0);
            else
                this.mNameColumn.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, R.drawable.ic_file_picker_down, 0);
        } else {
            this.mNameColumn.setCompoundDrawables(null, null, null, null);
        }

        // Sort by date

        if (this.mProperties.sortBy == FilePickerDialog.SORT_BY_LAST_MODIFIED) {
            if (this.mProperties.sortOrder == FilePickerDialog.SORT_ORDER_NORMAL)
                this.mDateColumn.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, R.drawable.ic_file_picker_up, 0);
            else
                this.mDateColumn.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, R.drawable.ic_file_picker_down, 0);
        } else {
            this.mDateColumn.setCompoundDrawables(null, null, null, null);
        }

        // Sort by size

        if (this.mProperties.sortBy == FilePickerDialog.SORT_BY_SIZE) {
            if (this.mProperties.sortOrder == FilePickerDialog.SORT_ORDER_NORMAL)
                this.mSizeColumn.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, R.drawable.ic_file_picker_up, 0);
            else
                this.mSizeColumn.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, R.drawable.ic_file_picker_down, 0);
        } else {
            this.mSizeColumn.setCompoundDrawables(null, null, null, null);
        }
    }

    /**
     * Actualize item comparator with new sorter order.
     *
     * @param sortOrder new sorter order.
     */
    private void actualizeComparator(int sortOrder) {
        if (this.mProperties.sortBy == sortOrder) {
            if (this.mProperties.sortOrder == FilePickerDialog.SORT_ORDER_NORMAL)
                this.mProperties.sortOrder = FilePickerDialog.SORT_ORDER_REVERSE;
            else this.mProperties.sortOrder = FilePickerDialog.SORT_ORDER_NORMAL;
        } else {
            this.mProperties.sortBy = sortOrder;

            switch (sortOrder) {
                case FilePickerDialog.SORT_BY_LAST_MODIFIED:
                    this.mProperties.sortOrder = FilePickerDialog.SORT_ORDER_REVERSE;
                    break;
                case FilePickerDialog.SORT_BY_SIZE:
                    this.mProperties.sortOrder = FilePickerDialog.SORT_ORDER_REVERSE;
                    break;
                default:
                    this.mProperties.sortOrder = FilePickerDialog.SORT_ORDER_NORMAL;
                    break;
            }
        }

        this.mSorter = createComparator(this.mProperties);

        this.actualizeToolbar();
        this.reload();
    }

    /**
     * Get toolbar from view.
     *
     * @param toolbar toolbar resource.
     * @return a view contains toolbar.
     */
    private View getToolbar(View toolbar) {
        if (!(toolbar instanceof LinearLayout)) return null;

        this.mToolbar = (LinearLayout) toolbar;

        this.mNameColumn = this.mToolbar.findViewById(R.id.file_picker_dialog_sort_name_label);
        this.mDateColumn = this.mToolbar.findViewById(R.id.file_picker_dialog_sort_date_label);
        this.mSizeColumn = this.mToolbar.findViewById(R.id.file_picker_dialog_sort_size_label);

        this.actualizeToolbar();

        // Name column

        RelativeLayout nameButton = this.mToolbar.findViewById(R.id.file_picker_dialog_sort_name);

        nameButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FilePickerDialog.this.actualizeComparator(FilePickerDialog.SORT_BY_NAME);
                    }
                });

        // Date column

        RelativeLayout dateButton = this.mToolbar.findViewById(R.id.file_picker_dialog_sort_date);

        dateButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FilePickerDialog.this.actualizeComparator(
                                FilePickerDialog.SORT_BY_LAST_MODIFIED);
                    }
                });

        // Size column

        RelativeLayout sizeButton = this.mToolbar.findViewById(R.id.file_picker_dialog_sort_size);

        sizeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FilePickerDialog.this.actualizeComparator(FilePickerDialog.SORT_BY_SIZE);
                    }
                });

        // Actualize button

        RelativeLayout actualizeButton =
                this.mToolbar.findViewById(R.id.file_picker_dialog_actualize);

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
     * generates a toolbar which will be placed between the title bar and the list.
     *
     * @return a view object.
     */
    @SuppressLint("InflateParams")
    @Override
    protected View createToolBar() {
        if (!this.mProperties.showToolbar) {
            return null;
        }

        if (this.mToolbar == null && this.mContext != null) {
            LayoutInflater inflater = LayoutInflater.from(this.mContext);

            return this.getToolbar(inflater.inflate(R.layout.file_picker_dialog_toolbar, null));
        }

        return this.mToolbar;
    }

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

            if (itemTag instanceof File) {
                File path = (File) itemTag;

                if (path.isDirectory() && path.canRead()) {
                    String rootPath = this.mProperties.root.getAbsolutePath();
                    String otherPath = path.getAbsolutePath();

                    if (otherPath.equals(rootPath) || otherPath.startsWith(rootPath)) {
                        File[] files = ((File) itemTag).listFiles(this.mFilter);

                        ArrayList<File> sortedObjects = new ArrayList<>();

                        if (files != null) {
                            for (File file : files) {
                                if (!file.isHidden() && file.canRead()) sortedObjects.add(file);
                            }

                            Collections.sort(sortedObjects, this.mSorter);

                            for (File newObject : sortedObjects) {
                                itemList.add(this.createItem(newObject));
                            }
                        }
                    }
                } else {
                    Toast.makeText(
                                    this.getContext(),
                                    R.string.file_picker_dialog_error_dir_access,
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

            if (itemTag instanceof File)
                return new ItemBase(
                        ((File) itemTag).getName(),
                        ((File) itemTag).getAbsolutePath(),
                        R.drawable.ic_file_picker_header);
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

            if (itemTag instanceof File) {
                File path = (File) itemTag;

                if (path.isDirectory() && path.canRead()) {
                    String rootPath = this.mProperties.root.getAbsolutePath();
                    String otherPath = path.getAbsolutePath();

                    if (!otherPath.equals(rootPath) && otherPath.startsWith(rootPath)) {
                        File parent = ((File) itemTag).getParentFile();

                        if (parent != null && parent.canRead()) {
                            return new BackItem(
                                    super.getContext()
                                            .getString(
                                                    R.string.file_picker_dialog_parent_directory),
                                    super.getContext()
                                            .getString(
                                                    R.string
                                                            .file_picker_dialog_parent_directory_text),
                                    R.drawable.ic_file_picker_folder,
                                    parent);
                        }
                    }
                }
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
        if (this.checkStorageAccessPermissions()) {
            if (this.mProperties.root.isDirectory() && this.mProperties.offsetDir.isDirectory()) {
                String rootPath = this.mProperties.root.getAbsolutePath();
                String offsetPath = this.mProperties.offsetDir.getAbsolutePath();

                if (offsetPath.equals(rootPath) || offsetPath.startsWith(rootPath)) {
                    if (this.mProperties.offsetDir.canRead())
                        return this.createItem(this.mProperties.offsetDir);
                }
            }

            if (this.mProperties.root.isDirectory() && this.mProperties.root.canRead())
                return this.createItem(this.mProperties.offsetDir);
        }

        Toast.makeText(
                        this.getContext(),
                        R.string.file_picker_dialog_error_dir_access,
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
        return (this.mProperties.selectionMode == FilePickerDialog.MULTI_MODE);
    }

    /**
     * Called for validation of the selection.
     *
     * @param items collection of picked items.
     */
    @Override
    protected void onValidateSelection(Collection<PickableItem> items) {
        if (this.mListener != null) {
            ArrayList<String> result = new ArrayList<>();

            for (PickableItem item : items) {
                Object itemTag = item.getTag();

                if (itemTag instanceof File) {
                    result.add(((File) itemTag).getAbsolutePath());
                }
            }

            this.mListener.onValidateSelection(result.toArray(new String[0]));
        }
    }

    /* ---- Privates Methods ---- */

    /**
     * Create picker item corresponding to the specified file.
     *
     * @param file file.
     * @return a picker item corresponding to the specified file.
     */
    private Item createItem(File file) {
        String strDateFormat =
                super.getContext().getString(R.string.file_picker_dialog_date_format);

        SimpleDateFormat dateFormat = new SimpleDateFormat(strDateFormat, Locale.getDefault());

        String itemDate = dateFormat.format(new Date(file.lastModified()));

        if (file.isDirectory()) {
            String Label =
                    super.getContext().getString(R.string.file_picker_dialog_last_edit_directory);

            String subTitle = String.format(Locale.getDefault(), Label, itemDate);

            switch (this.mProperties.selectionType) {
                case FilePickerDialog.DIR_SELECT:
                case FilePickerDialog.FILE_AND_DIR_SELECT:
                    return new PickableItem(
                            file.getName(), subTitle, R.drawable.ic_file_picker_folder, file, true);
                default:
                    return new Item(
                            file.getName(), subTitle, R.drawable.ic_file_picker_folder, file, true);
            }
        } else {
            String Label = super.getContext().getString(R.string.file_picker_dialog_last_edit_file);

            String fileSize = this.formatSize(file.length());

            String subTitle = String.format(DEF_LOCAL, Label, fileSize, itemDate);

            return new PickableItem(
                    file.getName(), subTitle, R.drawable.ic_file_picker_file, file, false);
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

    /* ---- Public Methods ---- */

    /**
     * Get current properties.
     *
     * @return a properties object.
     */
    public Properties getProperties() {
        return this.mProperties.cloneProperties();
    }

    /**
     * Allows you to be informed when validating the file selection.
     *
     * @param listener listener.
     */
    public void setValidateSelectionListener(ValidateSelectionListener listener) {
        this.mListener = listener;
    }
}
