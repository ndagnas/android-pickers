/*
 * Copyright (C) 2020 Nicolas Dagnas
 *
 * Project inspired by Angad Singh, in project: https://github.com/Angads25/android-filepicker
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
 */

package com.github.ndagnas.pickersdemo;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.ndagnas.pickers.FilePickerDialog;
import com.github.ndagnas.pickers.JsonPickerDialog;

import org.json.JSONObject;

import java.io.File;

/** Defines main activity. */
public class MainActivity extends AppCompatActivity {
    private EditText mPatterns;
    private EditText mRootDirectory;

    private FilePickerDialog.Properties mProperties = new FilePickerDialog.Properties();

    /**
     * Called on dialog create.
     *
     * @param savedInstanceState instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_main);

        RadioGroup selectionMode = this.findViewById(R.id.activity_main_selection_mode);

        selectionMode.check(R.id.activity_main_selection_mode_single);

        selectionMode.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.activity_main_selection_mode_single:
                                {
                                    MainActivity.this.mProperties.selectionMode =
                                            FilePickerDialog.SINGLE_MODE;

                                    break;
                                }

                            case R.id.activity_main_selection_mode_plutiple:
                                {
                                    MainActivity.this.mProperties.selectionMode =
                                            FilePickerDialog.MULTI_MODE;

                                    break;
                                }
                        }
                    }
                });

        RadioGroup selectionType = this.findViewById(R.id.activity_main_selection_type);

        selectionType.check(R.id.activity_main_selection_type_files_and_directories);

        selectionType.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.activity_main_selection_type_file:
                                {
                                    MainActivity.this.mProperties.selectionType =
                                            FilePickerDialog.FILE_SELECT;

                                    break;
                                }
                            case R.id.activity_main_selection_type_directory:
                                {
                                    MainActivity.this.mProperties.selectionType =
                                            FilePickerDialog.DIR_SELECT;

                                    break;
                                }
                            case R.id.activity_main_selection_type_files_and_directories:
                                {
                                    MainActivity.this.mProperties.selectionType =
                                            FilePickerDialog.FILE_AND_DIR_SELECT;

                                    break;
                                }
                        }
                    }
                });

        this.mPatterns = this.findViewById(R.id.activity_main_patterns);
        this.mRootDirectory = this.findViewById(R.id.activity_main_root_directory);

        Button showFilePickerDialogButton =
                this.findViewById(R.id.activity_main_show_file_picker_dialog);

        showFilePickerDialogButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainActivity.this.showFilePickerDialog();
                    }
                });

        Button showJsonPickerDialogButton =
                this.findViewById(R.id.activity_main_show_json_picker_dialog);

        showJsonPickerDialogButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainActivity.this.showJsonPickerDialog();
                    }
                });
    }

    /** Add this method to show Dialog when the required permission has been granted to the app. */
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FilePickerDialog.EXTERNAL_READ_PERMISSION_GRANT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.showFilePickerDialog();
            } else {
                Toast.makeText(
                                this,
                                "Permission is Required for getting list of files",
                                Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    /** Show file picker dialog. */
    public void showFilePickerDialog() {
        String rootDirectory = this.mRootDirectory.getText().toString();

        if (rootDirectory.length() == 0) rootDirectory = FilePickerDialog.DEFAULT_DIR;

        MainActivity.this.mProperties.offsetDir = new File(rootDirectory);
        MainActivity.this.mProperties.root = new File(FilePickerDialog.DEFAULT_DIR);
        MainActivity.this.mProperties.errorDir = new File(FilePickerDialog.DEFAULT_DIR);

        if (this.mPatterns.length() > 0)
            MainActivity.this.mProperties.patterns =
                    new String[] {this.mPatterns.getText().toString()};

        FilePickerDialog Dialog = new FilePickerDialog(this, MainActivity.this.mProperties);

        Dialog.setValidateSelectionListener(
                new FilePickerDialog.ValidateSelectionListener() {
                    @Override
                    public void onValidateSelection(String[] files) {
                        boolean isFirst = true;

                        StringBuilder result = new StringBuilder();

                        for (String file : files) {
                            if (!isFirst) result.append(", ");

                            result.append(file);

                            isFirst = false;
                        }

                        Toast.makeText(MainActivity.this, result.toString(), Toast.LENGTH_SHORT)
                                .show();
                    }
                });

        Dialog.show();
    }

    /** Show json picker dialog. */
    public void showJsonPickerDialog() {
        String Content =
                "{\"title\": \"Root\", \"subtitle\": \"ROOT\", \"children\": "
                        + " ["
                        + " {\"title\": \"Node A\", \"subtitle\": \"internal node A\", \"children\": "
                        + "  ["
                        + "  {\"title\": \"Node A.1\", \"subtitle\": \"internal node A.1\", \"children\":"
                        + "   ["
                        + "   {\"title\": \"Node A.1.1\", \"subtitle\": \"internal node A.1.1\", \"children\": []},"
                        + "   {\"title\": \"Node A.1.2\", \"subtitle\": \"internal node A.1.2\", \"children\": []},"
                        + "   {\"title\": \"Node A.1.3\", \"subtitle\": \"internal node A.1.3\", \"children\": []}"
                        + "   ]},"
                        + "  {\"title\": \"Node A.2\", \"subtitle\": \"internal node A.2\", \"children\":"
                        + "   ["
                        + "   {\"title\": \"Node A.2.1\", \"subtitle\": \"internal node A.2.1\", \"children\": []},"
                        + "   {\"title\": \"Node A.2.2\", \"subtitle\": \"internal node A.2.2\", \"children\": []},"
                        + "   {\"title\": \"Node A.2.3\", \"subtitle\": \"internal node A.2.3\", \"children\": []}"
                        + "   ]}"
                        + "  ]},"
                        + " {\"title\": \"Node B\", \"subtitle\": \"internal node B\", \"children\": "
                        + "  ["
                        + "  {\"title\": \"Node B.1\", \"subtitle\": \"internal node B.1\", \"children\":"
                        + "   ["
                        + "   {\"title\": \"Node B.1.1\", \"subtitle\": \"internal node B.1.1\", \"children\": []},"
                        + "   {\"title\": \"Node B.1.2\", \"subtitle\": \"internal node B.1.2\", \"children\": []},"
                        + "   {\"title\": \"Node B.1.3\", \"subtitle\": \"internal node B.1.3\", \"children\": []}"
                        + "   ]},"
                        + "  {\"title\": \"Node B.2\", \"subtitle\": \"internal node B.2\", \"children\":"
                        + "   ["
                        + "   {\"title\": \"Node B.2.1\", \"subtitle\": \"internal node B.2.1\", \"children\": []},"
                        + "   {\"title\": \"Node B.2.2\", \"subtitle\": \"internal node B.2.2\", \"children\": []},"
                        + "   {\"title\": \"Node B.2.3\", \"subtitle\": \"internal node B.2.3\", \"children\": []}"
                        + "   ]}"
                        + "  ]},"
                        + " {\"title\": \"Node C\", \"subtitle\": \"internal node C\", \"children\": []}"
                        + " ]}";

        try {
            JSONObject jsonObject = new JSONObject(Content);

            JsonPickerDialog.Properties Props = new JsonPickerDialog.Properties();

            Props.root = jsonObject;
            Props.jsonTitleNodeName = "title";
            Props.jsonSubTitleNodeName = "subtitle";
            Props.jsonChildrenNodeName = "children";
            Props.titleMask = null;
            Props.selectionType = JsonPickerDialog.NODE_WITHOUT_CHILD_SELECT;
            Props.sortBy = JsonPickerDialog.SORT_BY_HAVE_CHILDREN;
            Props.sortOrder = JsonPickerDialog.SORT_ORDER_NORMAL;

            JsonPickerDialog Dialog = new JsonPickerDialog(this, Props);

            Dialog.setValidateSelectionListener(
                    new JsonPickerDialog.ValidateSelectionListener() {
                        @Override
                        public void onValidateSelection(JSONObject[] objects) {
                            Toast.makeText(
                                            MainActivity.this,
                                            objects[0].toString(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });

            Dialog.show();
        } catch (Exception Err) {
            Log.e("JsonPickerDialog.getChildrenFor", "Exception: " + Err.toString());
        }
    }
}
