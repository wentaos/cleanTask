package com.winchannel.utils.cleanUtil;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.winchannel.cleanData.Constant;
import com.winchannel.utils.sysUtils.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;


public class OptionPropUtil {
    private static LogUtil logger = new LogUtil().log(OptionPropUtil.class);
    /**
     * 资源文件
     */
    private static String resourceFilePath = "spring/config/option.properties";
    private static Properties prop = new Properties();


    /**
     * THREAD_NUM
     * 线程数
     */
    public static int THREAD_NUM(){
        int def = 10;// 默认使用10个线程
        try{
            String THREAD_NUM = getValue(Constant.THREAD_NUM);
            if (THREAD_NUM!=null && THREAD_NUM.trim().length()>0){
                return Integer.parseInt(THREAD_NUM);
            }
            return def;
        }catch (Exception e){
            e.printStackTrace();
            return def;
        }
    }

    /**
     * 数据库路径中的分隔符
     */

    public static String DB_PATH_SEPARATOR(){
        try{
            String DB_PATH_SEPARATOR = getValue(Constant.DB_PATH_SEPARATOR);
            return DB_PATH_SEPARATOR;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    /**
     * PHOTO_PATH
     * 获取
     */
    public static String PHOTO_PATH(){
        try{
            String PHOTO_PATH = getValue(Constant.PHOTO_PATH);
            return PHOTO_PATH;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * RUN_TASK_TIME_LEN
     * 定时任务运行时长
     */
    public static String RUN_TASK_TIME_LEN(){
        try{
            String RUN_TASK_TIME_LEN = getValue(Constant.RUN_TASK_TIME_LEN);
            return RUN_TASK_TIME_LEN;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * REDUCE_ID_NUM
     * 一个线程一次处理 的ID数
     * */
    public static int REDUCE_ID_NUM(){
        try{
            String REDUCE_ID_NUM = getValue(Constant.REDUCE_ID_NUM);
            if(REDUCE_ID_NUM!=null){
                return Integer.parseInt(REDUCE_ID_NUM);
            }
            return 0;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * IS_TEST
     * 是否是测试
     */
    public static boolean IS_TEST(){
        try{
            String IS_TEST = getValue(Constant.IS_TEST);
            if (IS_TEST!=null){
                return "1".equals(IS_TEST) ? true : false;
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return true;
        }
    }


    /**
     * DB_TYPE
     * 数据库类型
     */
    public static String DB_TYPE(){
        try{
            String DB_TYPE = getValue(Constant.DB_TYPE);
            return DB_TYPE;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * LOOP_SAVE_COUNT
     * 遍历多少条记录保存一次ID信息:数据越小越利于重启连续；过小也会增加数据库update访问次数
     */
    public static int LOOP_SAVE_COUNT(){
        int def = 100;
        try{
            String LOOP_SAVE_COUNT = getValue(Constant.LOOP_SAVE_COUNT);
            if(LOOP_SAVE_COUNT!=null && LOOP_SAVE_COUNT.trim().length()>0){
                return Integer.parseInt(LOOP_SAVE_COUNT);
            }
            return def;// 默认100
        }catch (Exception e){
            e.printStackTrace();
            return def;
        }
    }


    /**
     * IS_MYSQL
     */
    public static boolean IS_MYSQL(){
        try{
            String DB_TYPE = getValue(Constant.DB_TYPE).trim();
            return Constant.MYSQL.equals(DB_TYPE)? true:false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * IS_SQLSERVER
     */
    public static boolean IS_SQLSERVER(){
        try{
            String DB_TYPE = getValue(Constant.DB_TYPE).trim();
            return Constant.SQLSERVER.equals(DB_TYPE)? true:false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * ORACLE
     */
    public static boolean IS_ORACLE(){
        try{
            String DB_TYPE = getValue(Constant.DB_TYPE).trim();
            return Constant.ORACLE.equals(DB_TYPE)? true:false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * ID_INFO_PATH
     * 存放 ID_INFO 的主目录
     * 如果是Windows D:/xxx/  如果是Linux 直接写 /usr/xxx/
     */
    public static String ID_INFO_PATH(){
        try{
            String ID_INFO_PATH = getValue(Constant.ID_INFO_PATH);
            return ID_INFO_PATH;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    /**
     * IS_UPDATE_DB
     * 是否需要更新数据库
     */
    public static boolean IS_UPDATE_DB(){
        try{
            String IS_UPDATE_DB = getValue(Constant.IS_UPDATE_DB);
            if(IS_UPDATE_DB!=null && IS_UPDATE_DB.length()>0){
                return Boolean.parseBoolean(IS_UPDATE_DB);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    /**
     * IS_DELETE_DB
     * 是否需要删除数据库记录
     */
    public static boolean IS_DELETE_DB(){
        try{
            String IS_DELETE_DB = getValue(Constant.IS_DELETE_DB);
            if(IS_DELETE_DB!=null && IS_DELETE_DB.length()>0){
                return Boolean.parseBoolean(IS_DELETE_DB);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    /**
     * IS_DELETE_IMG
     * 是否需要删除磁盘图片文件
     */
    public static boolean IS_DELETE_IMG(){
        try{
            String IS_DELETE_IMG = getValue(Constant.IS_DELETE_IMG);
            if(IS_DELETE_IMG!=null && IS_DELETE_IMG.length()>0){
                return Boolean.parseBoolean(IS_DELETE_IMG);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }


    /**
     * IS_BAK_IMG
     * 是否需要备份需要删除图片
     */
    public static boolean IS_BAK_IMG(){
        try{
            String IS_BAK_IMG = getValue(Constant.IS_BAK_IMG);
            if(IS_BAK_IMG!=null && IS_BAK_IMG.length()>0){
                return Boolean.parseBoolean(IS_BAK_IMG);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    /**
     * BAK_IMG_PATH
     * 备份图片的目录
     */
    public static String BAK_IMG_PATH(){
        try{
            String BAK_IMG_PATH = getValue(Constant.BAK_IMG_PATH);
            if(BAK_IMG_PATH!=null && BAK_IMG_PATH.length()>0){
                return BAK_IMG_PATH;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        // 如果没有配置就默认使用目的路径+"photo_bak"
        return PHOTO_PATH()+ File.separator+"photo_bak";
    }


    /**
     * IS_DELETE_OLD_IMG
     * 是否需要原来的图片文件
     */
    public static boolean IS_DELETE_OLD_IMG(){
        try{
            String IS_DELETE_OLD_IMG = getValue(Constant.IS_DELETE_OLD_IMG);
            if(IS_DELETE_OLD_IMG!=null && IS_DELETE_OLD_IMG.length()>0){
                return Boolean.parseBoolean(IS_DELETE_OLD_IMG);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    /**
     * IS_ONLY_PATH
     * 是否移动到唯一路径
     */
    public static boolean IS_ONLY_PATH(){
        try{
            String IS_ONLY_PATH = getValue(Constant.IS_ONLY_PATH);
            if(IS_ONLY_PATH!=null && IS_ONLY_PATH.length()>0){
                return Boolean.parseBoolean(IS_ONLY_PATH);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    /**
     * ONLY_DIST_PATH
     * 移动到唯一目录的路径
     */
    public static String ONLY_DIST_PATH(){
        try{
            String ONLY_DIST_PATH = getValue(Constant.ONLY_DIST_PATH);
            return ONLY_DIST_PATH;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * ONLY_FUNC_CODE
     * 移动到唯一目录的路径时，分配的FUNC_CODE
     */
    public static String ONLY_FUNC_CODE(){
        try{
            String ONLY_FUNC_CODE = getValue(Constant.ONLY_FUNC_CODE);
            return ONLY_FUNC_CODE;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }







    /**
     * 核心方法
     * 获取属性值
     */
    public static String getValue(String key){
        String value = null;
        try{
            InputStream in = OptionPropUtil.class.getClassLoader().getResourceAsStream(resourceFilePath);
            prop.load(in);
            value = prop.getProperty(key);
        }catch (Exception e){
            e.printStackTrace();
        }
        return value;
    }

    public static void setValue(String key,String value){
        try{
            String path = OptionPropUtil.class.getClassLoader().getResource(resourceFilePath).getPath();
            OutputStream out = new FileOutputStream(path,false);
            prop.setProperty(key,value);
            prop.store(out, key);
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }



}
