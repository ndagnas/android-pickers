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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.ndagnas.pickers.FilePickerDialog;
import com.github.ndagnas.pickers.JSonPickerDialog;
import com.github.ndagnas.pickers.PickerInterface;

import org.json.JSONObject;

import java.io.File;

/** Defines main activity. */
public class MainActivity extends AppCompatActivity {

    static final int EXTERNAL_READ_PERMISSION_GRANT = 1;

    /**
     * Called on dialog create.
     *
     * @param savedInstanceState instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		this.setContentView ( R.layout.activity_main );

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
        if (requestCode == EXTERNAL_READ_PERMISSION_GRANT) {
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

        FilePickerDialog.Builder builder = new FilePickerDialog.Builder(this).setRequestCode ( EXTERNAL_READ_PERMISSION_GRANT );

        switch (((RadioGroup) this.findViewById(R.id.activity_main_selection_type))
                .getCheckedRadioButtonId()) {
            case R.id.activity_main_selection_type_file:
                builder.setSelectionMode(FilePickerDialog.FILES);
                break;
            case R.id.activity_main_selection_type_directory:
                builder.setSelectionMode(FilePickerDialog.DIRECTORIES);
                break;
            case R.id.activity_main_selection_type_files_and_directories:
                builder.setSelectionMode(FilePickerDialog.FILES_AND_DIRECTORIES);
                break;
        }

        CharSequence pattern = ((TextView) this.findViewById(R.id.activity_main_pattern)).getText();

        if (!TextUtils.isEmpty(pattern)) builder.addFilesPattern(pattern);

        CharSequence rootDir =
                ((TextView) this.findViewById(R.id.activity_main_root_directory)).getText();

        if (!TextUtils.isEmpty(rootDir)) builder.setRootDir(new File(rootDir.toString()));

        switch (((RadioGroup) this.findViewById(R.id.activity_main_selection_mode))
                .getCheckedRadioButtonId()) {
            case R.id.activity_main_selection_mode_single:
                builder.setOnSingleChoiceValidationListener(
                                new PickerInterface.OnSingleChoiceValidationListener<String>() {
                                    @Override
                                    public void onClick(PickerInterface sender, String result) {
                                        Toast.makeText(
                                                        MainActivity.this,
                                                        result,
                                                        Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                })
                        .show();

                break;
            case R.id.activity_main_selection_mode_plutiple:
                builder.setOnMultiChoiceValidationListener(
                                new PickerInterface.OnMultiChoiceValidationListener<String>() {
                                    @Override
                                    public void onClick(PickerInterface sender, String[] result) {
                                        boolean isFirst = true;

                                        StringBuilder content = new StringBuilder();

                                        for (String file : result) {
                                            if (!isFirst) content.append(", ");

                                            content.append(file);

                                            isFirst = false;
                                        }

                                        Toast.makeText(
                                                        MainActivity.this,
                                                        content.toString(),
                                                        Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                })
                        .show();

                break;
        }
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

            new JSonPickerDialog.Builder(this)
                    .setRootNode(jsonObject)
                    .setTitleNodeName("title")
                    .setSubTitleNodeName("subtitle")
                    .setChildrenNodeName("children")
                    .setSelectionType(JSonPickerDialog.NODE_WITHOUT_CHILD_SELECT)
					.setSortOrder (JSonPickerDialog.SORT_ORDER_NORMAL)
					.setSortBy (JSonPickerDialog.SORT_BY_HAVE_CHILDREN)
                    .setOnSingleChoiceValidationListener(
                            new PickerInterface.OnSingleChoiceValidationListener<JSONObject>() {
                                @Override
                                public void onClick(PickerInterface sender, JSONObject result) {
                                    try {
                                        Toast.makeText(
                                                        MainActivity.this,
                                                        result.toString(),
                                                        Toast.LENGTH_SHORT)
                                                .show();
                                    } catch (Exception Err) {
                                        Log.e(
                                                "JsonPickerDialog.getChildrenFor",
                                                "Exception: " + Err.toString());
                                    }
                                }
                            })
                    .show();
        } catch (Exception Err) {
            Log.e("JsonPickerDialog.getChildrenFor", "Exception: " + Err.toString());
        }
    }
}
