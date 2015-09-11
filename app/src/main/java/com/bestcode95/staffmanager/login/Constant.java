package com.bestcode95.staffmanager.login;

import android.os.Environment;

/**
 * Created by mima123 on 15/8/13.
 */
public final class Constant {
    public static final String USERNAME_KEY = "tel";
    public static final String PASSWORD_KEY = "password";
    public static final String RESPONSE_KEY = "result";

    public static final int REQUEST_SUCCESS = 1;
    public static final int REQUEST_FAILED = 0;

    //推荐人的电话号码键(key),作为各个Activity之间传递电话号码的key
    public static final String REFERRER_KEY = "referrer";
    public static final String POSITION_KEY = "position_key";

    public static final String TEL_SHARE_NAME = "tel_share_name";
    public static final String USE_KEY = "use_key";
    public static final String TEMP_KEY = "temp_key";
    public static final String COUNT_SHARE_NAME = "count_share_name";
    public static final String COUNT_KEY = "count_key";

    public static final String ROOT_URL = "http://192.168.1.135:8080/";//http://115.28.85.146";
    public static final String LOGIN_URL = ROOT_URL + "/Licai/servlet/LoginServlet";//"http://leizbio.com:3000/mobile.php?act=login";
    public static final String ADD_STAFF_URL = ROOT_URL + "/Licai/servlet/AddServlet";
    public static final String CHECK_STAFF_URL = ROOT_URL + "/Licai/servlet/ListServlet";
    public static final String DELETE_STAFF_URL = ROOT_URL + "/Licai/servlet/DeleteServlet"; //
    public static final String CHECK_STAFF_SEARCH_URL = ROOT_URL + "/Licai/servlet/SearchServlet";
    public static final String MODIFY_PWD_URL = ROOT_URL + "/Licai/servlet/ModifyServlet";
    public static final String STAFF_DETAIL_URL = ROOT_URL +"/Licai/servlet/InfoServlet";
    public static final String MODIFY_SALARY_URL = ROOT_URL + "/Licai/servlet/SalaryServlet"; //
    /**
     * 文件方面
     */
    public static final String rootDirectory = Environment
            .getExternalStorageDirectory().toString() + "/StaffManager/";
    public static final String checkStaffDir = rootDirectory + "checkStaff/";
    public static final String checkStaffNameDir = checkStaffDir + "name/";
    public static final String checkStaffTelDir = checkStaffDir + "tel/";


    /**
     * 查看员工列表界面
     */
    public static final String NAME_FLAG = "name_flag";
    public static final String BUTTON_FLAG = "button_flag";
}
