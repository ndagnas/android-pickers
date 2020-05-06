# Picker
Android Library to create picker dialogs.

### Developed by
[Nicolas Dagnas](https://www.github.com/ndagnas) ([@NDagnas](https://www.twitter.com/NDagnas))

### Inspired by
[Angad Singh - filepicker](https://www.github.com/angads25/android-filepicker) and [Aefyr - filepicker](https://www.github.com/Aefyr/android-filepicker)

### Where to Find:
[ ![Download](https://api.bintray.com/packages/ndagnas/maven/Picker-Dialogs/images/download.svg) ](https://bintray.com/ndagnas/maven/Picker-Dialogs/_latestVersion)

### Picker Dialog Base Features
* Easy to Implement.
* No permissions required.
* Dark theme supported.

### File Picker Features
* Easy to use.
* Files, Directory Selection.
* Single or Multiple File selection.
* Column sort.
* RegEx filters

### Json Picker Features
* Select by node.

### Installation
* Library is also Available in JCenter, So just put this in your app dependencies to use it:
```gradle
    repositories {
		...
        jcenter()
		...
    }
```

```gradle
    compile 'com.github.ndagnas:pickers:0.1.2'
```

### Usage
## FilePickerDialog
1. Start by creating an instance of `FilePickerDialog.Properties`.

    ```java
        FilePickerDialog.Properties properties = new FilePickerDialog.Properties();
    ```

    Now 'Properties' has certain parameters.

2. Assign values to each Dialog Property using `FilePickerDialog` class.

    ```java
        properties.selectionMode = FilePickerDialog.SINGLE_MODE;
        properties.selectionType = FilePickerDialog.FILE_SELECT;
        properties.root = new File(FilePickerDialog.DEFAULT_DIR);
        properties.errorDir = new File(FilePickerDialog.DEFAULT_DIR);
        properties.offsetDir = new File(FilePickerDialog.DEFAULT_DIR);
        properties.patterns = new String[] {"(.*)\\.txt"};
        properties.sortBy = FilePickerDialog.SORT_BY_NAME;
        properties.sortOrder = FilePickerDialog.SORT_ORDER_NORMAL;
        properties.showToolbar = true;
    ```

3. Next create an instance of `FilePickerDialog`, and pass `Context` and `Properties` references as parameters. Optional: You can change the title of dialog. Default is current directory name. Set the select button string. Default is Select. Set the cancel button string. Defalut is Cancel.

    ```java
        FilePickerDialog dialog = new FilePickerDialog(MainActivity.this,properties);
        dialog.setTitle("Select a File");
    ```

4.  Next, Attach `FilePickerDialog.ValidateSelectionListener` to `FilePickerDialog` as below,
    ```java
        dialog.setValidateSelectionListener(new FilePickerDialog.ValidateSelectionListener() {
            @Override
            public void onValidateSelection(String[] files) {
                //files is the array of the paths of files selected by the Application User.
            }
        });
    ```
    An array of paths is returned whenever user press the `select` button`.

5. Use ```dialog.show()``` method to show dialog.

### NOTE:
Marshmallow and above requests for the permission on runtime. You should override `onRequestPermissionsResult` in Activity/AppCompatActivity class and show the dialog only if permissions have been granted.

```java
        //Add this method to show Dialog when the required permission has been granted to the app.
        @Override
        public void onRequestPermissionsResult(int requestCode,@NonNull String permissions[],@NonNull int[] grantResults) {
            switch (requestCode) {
                case FilePickerDialog.EXTERNAL_READ_PERMISSION_GRANT: {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if(dialog!=null)
                        {   //Show dialog if the read permission has been granted.
                            dialog.show();
                        }
                    }
                    else {
                        //Permission has not been granted. Notify the user.
                        Toast.makeText(MainActivity.this,"Permission is Required for getting list of files",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
```

### Important:
* `defaultValue`, `error_dir`, `root_dir`, `offset_dir` must have valid directory/file paths.
* `defaultValue` paths should end with ':'.
* `defaultValue` can have multiple paths, there should be a ':' between two paths.
* `patterns` must not have '.'.
* `patterns` must are regexes.

### Screenshot
<p align="center">
 </br>
 <img src="screenshots/file_picker.jpg" width="250">
 &nbsp;
 <img src="screenshots/file_picker_dark.jpg" width="250">
 &nbsp;
 <img src="screenshots/json_picker.jpg" width="250">
</p>

### License
    Copyright (C) 2020 Nicolas Dagnas

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.