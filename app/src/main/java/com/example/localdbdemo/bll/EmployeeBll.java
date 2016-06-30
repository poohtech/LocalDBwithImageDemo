package com.example.localdbdemo.bll;

import android.content.Context;
import android.database.Cursor;

import com.example.localdbdemo.bean.EmployeeBean;
import com.example.localdbdemo.util.DBHelper;

import java.util.ArrayList;

/**
 * Created by user on 28/6/16.
 */
public class EmployeeBll {

    private Context context;

    public EmployeeBll(Context mContext) {
        this.context = mContext;
    }

    public void verify(EmployeeBean employeeBean) {
        DBHelper dbHelper = null;
        String sql = null;
        Cursor cur = null;
        try {
            sql = "SELECT id FROM Employee WHERE id = " + employeeBean.id;
            dbHelper = new DBHelper(this.context);
            cur = dbHelper.query(sql);
            if (cur != null && cur.getCount() > 0) {
                System.out.println("=====verify BLL::::::::update=======" + employeeBean.id);
                update(employeeBean);
            } else {
                System.out.println("=====verify BLL::::::::insert=======" + employeeBean.id);
                insert(employeeBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cur != null && !cur.isClosed()) {
                cur.close();
            }
            if (dbHelper != null) {
                dbHelper.close();
            }
            dbHelper = null;
            cur = null;
            sql = null;
            System.gc();
        }
    }

    public void insert(EmployeeBean employeeBean) {
        DBHelper dbHelper = null;
        String sql = null;
        try {
            sql = "INSERT INTO Employee(name,email,date,image)" + " VALUES ('" + employeeBean.name + "','" + employeeBean.email + "','" + employeeBean.date + "','" + employeeBean.image + "')";
            dbHelper = new DBHelper(context);
            dbHelper.execute(sql);

            System.out.println("---------insert:::sql--------" + sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dbHelper != null) {
                dbHelper.close();
            }
            dbHelper = null;
            sql = null;
            System.gc();
        }

    }

    public void update(EmployeeBean employeeBean) {
        DBHelper dbHelper = null;
        String sql = null;
        try {
            sql = "UPDATE Employee SET " + "name='" + employeeBean.name + "',email='" + employeeBean.email + "',date='" + employeeBean.date + "',image='" + employeeBean.image + "' WHERE id = " + employeeBean.id;
            dbHelper = new DBHelper(context);
            dbHelper.execute(sql);

            System.out.println("---------update:::sql--------" + sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dbHelper != null) {
                dbHelper.close();
            }
            dbHelper = null;
            sql = null;
            System.gc();
        }
    }

    public void delete(int id) {
        DBHelper dbHelper = null;
        String sql = null;
        try {
            sql = "DELETE FROM Employee WHERE id = " + id;
            dbHelper = new DBHelper(context);
            dbHelper.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dbHelper != null) {
                dbHelper.close();
            }
            dbHelper = null;
            sql = null;
            System.gc();
        }
    }

    public ArrayList<EmployeeBean> getEmployeeList() {
        DBHelper dbHelper = null;
        String sql = null;
        Cursor cur = null;
        EmployeeBean employeeBean = null;
        ArrayList<EmployeeBean> empList = null;

        try {
            sql = "SELECT id, name, email, date, image FROM Employee";
            dbHelper = new DBHelper(context);
            cur = dbHelper.query(sql);

            empList = new ArrayList<EmployeeBean>();
            if (cur != null && cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    employeeBean = new EmployeeBean();
                    employeeBean.id = cur.getInt(0);
                    employeeBean.name = cur.getString(1);
                    employeeBean.email = cur.getString(2);
                    employeeBean.date = cur.getString(3);
                    employeeBean.image = cur.getString(4);
                    empList.add(employeeBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dbHelper != null) {
                dbHelper.close();
            }
            dbHelper = null;
            sql = null;
            cur = null;
            employeeBean = null;
            System.gc();
        }

        return empList;
    }


}
