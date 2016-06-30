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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.localdbdemo.R;
import com.example.localdbdemo.adapter.EmployeeAdapter;
import com.example.localdbdemo.bean.EmployeeBean;
import com.example.localdbdemo.bll.EmployeeBll;
import com.example.localdbdemo.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by user on 28/6/16.
 */
public class EmployeeList extends Activity implements View.OnClickListener {

    private ListView empListView;
    private RelativeLayout llEditView;
    private EditText edtEditname, edtEditemail, edtEditdate;
    private ImageView updateImage;
    private ImageButton imageButton;
    private Button btnUpdateData;

    Calendar cal;
    int mCurrentYear, mCurrentMonth, mCurrentDay;
    int mYear, mMonth, mDay;
    DatePickerDialog mDatePickerDialog;

    EmployeeBean employeeBean;
    EmployeeBll employeeBll;
    EmployeeAdapter employeeAdapter;
    ArrayList<EmployeeBean> employeeList;

    int updateID;
    private EmployeeBean getEmpBean;


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
        setContentView(R.layout.activity_list);

        updateImage = (ImageView) findViewById(R.id.updateImage);
        empListView = (ListView) findViewById(R.id.empListView);
        llEditView = (RelativeLayout) findViewById(R.id.llEditView);
        edtEditname = (EditText) findViewById(R.id.edtEditname);
        edtEditemail = (EditText) findViewById(R.id.edtEditemail);
        edtEditdate = (EditText) findViewById(R.id.edtEditdate);
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        btnUpdateData = (Button) findViewById(R.id.btnUpdateData);


        employeeBean = new EmployeeBean();
        if (employeeBll == null) {
            employeeBll = new EmployeeBll(EmployeeList.this);
        }
        employeeList = employeeBll.getEmployeeList();
        System.out.println("===empList.size()===" + employeeList.size());
        employeeAdapter = new EmployeeAdapter(this, employeeList);
        empListView.setAdapter(employeeAdapter);

        cal = Calendar.getInstance();
        mCurrentYear = cal.get(Calendar.YEAR);
        mCurrentMonth = cal.get(Calendar.MONTH);
        mCurrentDay = cal.get(Calendar.DAY_OF_MONTH);

        mDatePickerDialog = new DatePickerDialog(this, mDateSetListener, mCurrentYear, mCurrentMonth, mCurrentDay);

        updateImage.setOnClickListener(this);
        imageButton.setOnClickListener(this);
        btnUpdateData.setOnClickListener(this);
    }


    DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            int month = monthOfYear + 1;
            mDay = dayOfMonth;
            edtEditdate.setText(mDay + "-" + month + "-" + mYear);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.updateImage:
                try {
                    Util.verifyCategoryPath(Environment.getExternalStorageDirectory()
                            .getPath() + "/MyDatabaseNew/");
                    showImgAlert(EmployeeList.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.imageButton:
                mDatePickerDialog.show();
                break;

            case R.id.btnUpdateData:
                String result = validate();
                if (result != null) {
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                } else {
                    employeeBean.id = updateID;
                    employeeBean.name = edtEditname.getText().toString();
                    employeeBean.email = edtEditemail.getText().toString();
                    employeeBean.date = edtEditdate.getText().toString();
                    employeeBean.image = fileStoragePath;
                    employeeBll.update(employeeBean);
//                startActivity(getIntent());
                    startActivity(new Intent(EmployeeList.this, EmployeeList.class));
                    finish();
                }
                break;

            case R.id.btnUpdate:
                getEmpBean = (EmployeeBean) v.getTag();
                System.out.println("---------getEmpBean.id--------" + getEmpBean.id);
                updateEmployee(getEmpBean.id, getEmpBean.name, getEmpBean.email, getEmpBean.date, getEmpBean.image);
                break;

            case R.id.btnDelete:
                getEmpBean = (EmployeeBean) v.getTag();
                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.delete_dialog);
                dialog.setTitle("Delete Data");
                Button btnYes = (Button) dialog.findViewById(R.id.btnYes);
                Button btnNo = (Button) dialog.findViewById(R.id.btnNo);

                btnYes.setTag(getEmpBean);
                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EmployeeBean empBean = (EmployeeBean) v.getTag();
                        System.out.println("=====employeeBll.delete(empBean.id)=========" + empBean.id);
                        employeeBll.delete(empBean.id);
                        startActivity(getIntent());
                        finish();
                        dialog.dismiss();
                    }
                });
                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;

        }
    }

    private void updateEmployee(int id, String name, String email, String date, String image) {
        System.out.println("---------name--------" + name);
        empListView.setVisibility(View.GONE);
        llEditView.setVisibility(View.VISIBLE);
        updateID = id;
        edtEditname.setText(name);
        edtEditemail.setText(email);
        edtEditdate.setText(date);
        updateImage.setImageBitmap(BitmapFactory.decodeFile(image));
        fileStoragePath = image;
    }


    @SuppressWarnings("unused")
    private String validate() {

        String valid = null;
        if (edtEditname.getText().toString().trim() == null
                || edtEditname.getText().toString().trim().equals("")) {
            valid = "Please enter name";
            edtEditname.requestFocus();
            edtEditname.setSelection(edtEditname.length());
        } else if (edtEditemail.getText().toString().trim() == null
                || edtEditemail.getText().toString().trim().equals("")) {
            valid = "Please enter email";
            edtEditemail.requestFocus();
            edtEditemail.setSelection(edtEditemail.length());
        } else if (!Util.EMAIL_PATTERN.matcher(
                edtEditemail.getText().toString()).matches()) {
            valid = "Please enter valid email address";
            edtEditemail.requestFocus();
            edtEditemail.setSelection(edtEditemail.length());
        } else if (edtEditdate.getText().toString().trim() == null
                || edtEditdate.getText().toString().trim().equals("")) {
            valid = "Please select date";
            edtEditdate.requestFocus();
            edtEditdate.setSelection(edtEditdate.length());
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
                    // oldFileName = System.currentTimeMillis() + ".png";
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


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_CAPTURE) { // for camera
                try {

                    System.out
                            .println(" -----------ShareExp Activity img uri :::: "
                                    + imgUri);

                    filename = Util.compressImage(String.valueOf(imgUri),
                            EmployeeList.this);
                    fileStoragePath = filename;

                    System.out
                            .println("::::::::::::::::ShareExp Activity filename ::: "
                                    + filename);
                    updateImage.setImageBitmap(BitmapFactory
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
                                        EmployeeList.this);
                                fileStoragePath = filename;

                            }
                        }

                        updateImage.setImageBitmap(BitmapFactory
                                .decodeFile(filename));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

}
