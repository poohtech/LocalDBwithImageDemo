package com.example.localdbdemo.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.localdbdemo.R;
import com.example.localdbdemo.bean.EmployeeBean;
import com.example.localdbdemo.bll.EmployeeBll;
import com.example.localdbdemo.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by user on 28/6/16.
 */
public class AddEmployeeActivity extends Activity implements View.OnClickListener {

    ImageView editImage;
    EditText editname, editemail, editdate;
    ImageButton imageButton;
    Button btnsubmit, btnshowdata;

    Calendar cal;
    int mCurrentYear, mCurrentMonth, mCurrentDay;
    int mYear, mMonth, mDay;
    DatePickerDialog mDatePickerDialog;

    EmployeeBean employeeBean;
    EmployeeBll employeeBll;

    /* Upload Pic */
    private Intent intent;
    private static String oldFileName;
    private static Uri imgUri;
    private int CAMERA_CAPTURE = 1000;
    private int GET_FROM_GALLERY = 2000;
    private static String newImgName;
    String filename = "";
    private String fileStoragePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        editImage = (ImageView) findViewById(R.id.editImage);
        editname = (EditText) findViewById(R.id.editname);
        editemail = (EditText) findViewById(R.id.editemail);
        editdate = (EditText) findViewById(R.id.editdate);
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        btnsubmit = (Button) findViewById(R.id.btnsubmit);
        btnshowdata = (Button) findViewById(R.id.btnshowdata);

        employeeBean = new EmployeeBean();
        employeeBll = new EmployeeBll(AddEmployeeActivity.this);

        cal = Calendar.getInstance();
        mCurrentYear = cal.get(Calendar.YEAR);
        mCurrentMonth = cal.get(Calendar.MONTH);
        mCurrentDay = cal.get(Calendar.DAY_OF_MONTH);

        mDatePickerDialog = new DatePickerDialog(this, mDateSetListener, mCurrentYear, mCurrentMonth, mCurrentDay);

        editImage.setOnClickListener(this);
        imageButton.setOnClickListener(this);
        btnsubmit.setOnClickListener(this);
        btnshowdata.setOnClickListener(this);

        System.out
                .println(" -----------ON CREATE :::: "
                );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.editImage:
                try {
                    Util.verifyCategoryPath(Environment.getExternalStorageDirectory()
                            .getPath() + "/MyDatabaseNew/");
                    showImgAlert(AddEmployeeActivity.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.imageButton:
                mDatePickerDialog.show();
                break;

            case R.id.btnsubmit:
                String result = validate();
                if (result != null) {
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                } else {
                    employeeBean.name = editname.getText().toString();
                    employeeBean.email = editemail.getText().toString();
                    employeeBean.date = editdate.getText().toString();
                    System.out
                            .println("::::::::::::btnsubmit::::fileStoragePath ::: "
                                    + fileStoragePath);
                    employeeBean.image = fileStoragePath;
                    employeeBll.verify(employeeBean);

                    Toast.makeText(this, "Added Successfully", Toast.LENGTH_LONG).show();
                    editname.setText("");
                    editemail.setText("");
                    editdate.setText("");
                    editImage.setImageDrawable(getResources().getDrawable(R.color.gray));
                }
                break;

            case R.id.btnshowdata:
                Intent i = new Intent(AddEmployeeActivity.this, EmployeeList.class);
                startActivity(i);
                break;

        }
    }

    @SuppressWarnings("unused")
    private String validate() {

        String valid = null;
        if (editname.getText().toString().trim() == null
                || editname.getText().toString().trim().equals("")) {
            valid = "Please enter name";
            editname.requestFocus();
            editname.setSelection(editname.length());
        } else if (editemail.getText().toString().trim() == null
                || editemail.getText().toString().trim().equals("")) {
            valid = "Please enter email";
            editemail.requestFocus();
            editemail.setSelection(editemail.length());
        } else if (!Util.EMAIL_PATTERN.matcher(
                editemail.getText().toString()).matches()) {
            valid = "Please enter valid email address";
            editemail.requestFocus();
            editemail.setSelection(editemail.length());
        } else if (editdate.getText().toString().trim() == null
                || editdate.getText().toString().trim().equals("")) {
            valid = "Please select date";
            editdate.requestFocus();
            editdate.setSelection(editdate.length());
        } else if (checkFileSize(fileStoragePath) == false) {
            valid = "Image must not greater than 2 MB";
        }
        return valid;

    }

    private boolean checkFileSize(String path) {
        File file = new File(path);
        long fileSizeInMB = 0; // Convert the KB to MegaBytes (1 MB =
        // 1024 KBytes)
        if (file.exists())
            fileSizeInMB = (file.length() / 1024) / 1024;

        if (fileSizeInMB < 2) {
            return true;
        } else {
            return false;
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_CAPTURE) { // for camera
                try {

                    System.out
                            .println(" -----------ShareExp Activity img uri :::: "
                                    + imgUri);

                    filename = Util.compressImage(String.valueOf(imgUri),
                            AddEmployeeActivity.this);
                    fileStoragePath = filename;

                    System.out
                            .println("::::::::::::::::ShareExp Activity filename ::: "
                                    + filename);
                    editImage.setImageBitmap(BitmapFactory
                            .decodeFile(filename));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == GET_FROM_GALLERY) {
                // Pick From Gallery
                try {

                    if (data != null) {

                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver()
                                .query(selectedImage, filePathColumn, null,
                                        null, null);
                        if (cursor != null && cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            int columnIndex = cursor
                                    .getColumnIndex(filePathColumn[0]);
                            newImgName = cursor.getString(columnIndex);
                            cursor.close();
                            System.out.println("==========filePath :: "
                                    + newImgName);
                            if (newImgName != null
                                    && new File(newImgName).exists()) {

                                filename = Util.compressImage(
                                        String.valueOf(newImgName),
                                        AddEmployeeActivity.this);
                                fileStoragePath = filename;

                            }
                        }

                        editImage.setImageBitmap(BitmapFactory
                                .decodeFile(filename));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            int month = monthOfYear + 1;
            mDay = dayOfMonth;
            editdate.setText(mDay + "-" + month + "-" + mYear);
        }
    };


    private void showImgAlert(Context context) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        // WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.upload_image_dialog);
        dialog.setCancelable(false);

        Button btn_gallerylft = (Button) dialog.findViewById(R.id.btnGallery);
        Button btn_camerargt = (Button) dialog.findViewById(R.id.btnCamera);

        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                }
                return true;
            }

        });

        btn_camerargt.setOnClickListener(new View.OnClickListener() {
            // Camera
            @Override
            public void onClick(View v) {
                try {
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES).getPath();
                    oldFileName = Environment.getExternalStorageDirectory()
                            .getPath() + "/MyDatabaseNew/"
                            + System.currentTimeMillis() + ".jpeg";

                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, oldFileName);
                    imgUri = getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            values);

                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
                    startActivityForResult(intent, CAMERA_CAPTURE);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();

            }
        });

        btn_gallerylft.setOnClickListener(new View.OnClickListener() {

            // Galley
            @Override
            public void onClick(View v) {
                intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, GET_FROM_GALLERY);
                dialog.dismiss();
            }
        });
        dialog.show();
    }


}
