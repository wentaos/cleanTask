package com.winchannel.utils.cleanUtil;

import com.winchannel.bean.Photo;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 内容中：涉及到的路径是基于 /usr/opt/aaa/bbb/2017-02-12/j23h43h24234324h32ui4hf.jpg 这种格式
 * headPath         /usr/opt/aaa/bbb/
 * datePath         2017-02-12
 * dateFullPath     /usr/opt/aaa/bbb/2017-02-12
 */
public class CleanFileTool {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CleanFileTool.class);

    public static String DB_PATH_SEP = OptionPropUtil.DB_PATH_SEPARATOR();

    /**
     * 判断对应的 FUNC_CODE + DATE + photoname 是否存在
     * 如果存在就师傅和规则的，不用处理了
     */
    public static boolean isTruePath_bak(Photo photo) {
        String absPath = photo.getImgAbsPath();

        // 如果没有绝对路径，直接忽略掉
        if(absPath==null || absPath.trim().length()==0){
            return true;
        }
        // absPath 如：/usr/opt/Photo_Test/photos/2017-01-12/101.jpg
        String date = getDatePathFromUrl(absPath);
        if(date!=null){
            String photos = absPath.split(date)[0];// 得到 /usr/opt/Photo_Test/photos/
            String tail = photos.split("photos")[1];// 得到 /
            // 如果长度够长，中间会包含FUNC_CODE目录
            if(tail.length()>3){
                return true;
            }
        }
        return false;
    }

    // 通用版本：对于老数据，没有 ABSOLUTE_PATH 数据
    public static boolean isTruePath2(Photo photo) {
        String func_code = photo.getFuncCode();
        // 2012年的数据没有ABSOLUTE_PATH数据,并且没有dot2B 这种分隔符
        String imgUrl = photo.getImgUrl();
        // 数据格式：.../photos/2012-01-01/xxx.jpg 	/media/Ddot1Adot2BPhoto_Testdot2Bphotosdot2B2017-01-01dot2B8.jpg

        // 为空直接忽略掉
        if(imgUrl==null || imgUrl.trim().length()==0){
            return true;
        }

        // 如果
        if(imgUrl.contains("dot2B")){// 新型数据
            return isTurePath("dot2B",imgUrl,func_code);
        }else{
            return isTurePath(DB_PATH_SEP,imgUrl,func_code);
        }

    }





    public static boolean isTurePath(String DB_PATH_SEP,String imgUrl,String trueFuncCode){
        // 为空直接忽略掉
        if(imgUrl==null || imgUrl.trim().length()==0){
            return true;
        }

        String afterStr = imgUrl.split("photos"+DB_PATH_SEP)[1];

        String[] arr = afterStr.split(DB_PATH_SEP);

        int flag_num = arr.length-1;// afterStr中 dot2B 的个数
        if(flag_num>=2){// 可能有错误路径 F0sdsadas_dsdafasddfsad/date/xxx.jpg
            String date = getDatePathFromUrl(afterStr);
            if(date!=null && date.trim().length()>0) {
                String funccode_id = afterStr.split(DB_PATH_SEP + date)[0];
                // 日期前面的目录 不是错误的目录，和正确的FUNC_CODE一样
                if (funccode_id.toLowerCase().trim().startsWith((trueFuncCode.toLowerCase().trim()))
                        && funccode_id.trim().length()<32){// 太长就是错误的路径
                    return true;
                }else {
                    return false;
                }

            }

        }else if(flag_num==1){// afterStr 说明剩下的格式是: date/xxx.jpg

            // 先得到日期：如果防寒日期，则不是正确的路径格式
            String date = getDatePathFromUrl(afterStr);
            if(date!=null){
                return false;
            }

        }else{// 其他的不做考虑，认为是正确的
            return true;
        }
        return true;
    }






    /**
     * 判断创建 FUNC_CODE 对应的目录
     */
    public static String cleanFuncCodePath(String FUNC_CODE) {
        String PHOTO_PATH = OptionPropUtil.PHOTO_PATH();
        String funcCodePath = PHOTO_PATH + DB_PATH_SEP + FUNC_CODE;
        funcCodePath = createPath(funcCodePath);
        return funcCodePath;
    }

    public static String cleanFuncCodePathForOnly(boolean isContainsFunccode,String FUNC_CODE){
        String PHOTO_PATH = OptionPropUtil.ONLY_DIST_PATH();
        String funcCodePath = isContainsFunccode?PHOTO_PATH:PHOTO_PATH + DB_PATH_SEP + FUNC_CODE;
        funcCodePath = createPath(funcCodePath);
        return funcCodePath;
    }


    /**
     * 对日期目录进行处理
     * 这里使用 imgUrl：在老数据中 ABSOLUTE_PATH 没有数据
     * imgUrl：/photos/2013-01-01/xxx.jpg
     */
    public static String cleanDatePath(String funcCodePath, String imgUrl) {
        String PHOTO_PATH = OptionPropUtil.PHOTO_PATH();
        String code_date_path = "";
        String date = "";
        if(imgUrl!=null && imgUrl.trim().length()>0){
            date = getDatePathFromUrl(imgUrl);
            if(date!=null){
                code_date_path=funcCodePath+ File.separator+date;
                code_date_path = createPath(PHOTO_PATH+ File.separator +code_date_path);
            }

        }
        return date;
    }

    public static String cleanDatePathForOnly(boolean isContainsFunccode,String funcCode, String imgUrl) {
        String PHOTO_PATH = OptionPropUtil.ONLY_DIST_PATH();
        String code_date_path = "";
        String date = "";
        if(imgUrl!=null && imgUrl.trim().length()>0){
            date = getDatePathFromUrl(imgUrl);
            if(date!=null){
                code_date_path=funcCode+ File.separator+date;
                code_date_path = createPath(isContainsFunccode?PHOTO_PATH+File.separator+date : PHOTO_PATH+ File.separator +code_date_path);
            }

        }
        return date;
    }

    /**
     * 得到 headPath
     */
    public static String getHeadPath(String absolutePath) {
        if (absolutePath != null && absolutePath.length() > 0) {
            int lastP1 = absolutePath.lastIndexOf(DB_PATH_SEP);
            String subPath = absolutePath.substring(0, lastP1);
            int lastP2 = subPath.lastIndexOf(DB_PATH_SEP);
            String headPath = subPath.substring(0, lastP2);
            return headPath;
        }
        return null;
    }

    /**
     * 获取文件名部分apth
     */
    public static String getFileNamePath(String url) {
        if (url == null) {
            return null;
        }
        String fileNamePath = url.substring(url.lastIndexOf(DB_PATH_SEP) + 1);
        return fileNamePath;
    }


    /**
     * 组装新的file 绝对路径
     * 加上 FUNC_CODE_PATH
     */
    public static String getNewAbsPath(String absolutePath, String funcCodePath,String date) {
        String headPath = OptionPropUtil.PHOTO_PATH();// getHeadPath(absolutePath);
//        String datePath = getDatePathFromUrl(absolutePath);
        String fileNamePath = getFileNamePath(absolutePath);
        String newAbsPath = headPath + funcCodePath + DB_PATH_SEP + date + DB_PATH_SEP + fileNamePath;
        return newAbsPath;
    }


    public static String getNewAbsPathForOnly(boolean isContainsFuncode,String absolutePath, String funcCodePath,String date) {
        String headPath = OptionPropUtil.ONLY_DIST_PATH();// getHeadPath(absolutePath);
//        String datePath = getDatePathFromUrl(absolutePath);
        String fileNamePath = getFileNamePath(absolutePath);
        String newAbsPath = headPath + (isContainsFuncode?"":funcCodePath) + DB_PATH_SEP + date + DB_PATH_SEP + fileNamePath;
        return newAbsPath;
    }



    public static String getNewAbsPath(String[] paths) {
        String headPath = OptionPropUtil.PHOTO_PATH();
//        String datePath = getDatePathFromUrl(paths[0]);
        String fileNamePath = getFileNamePath(paths[0]);
        String newAbsPath = headPath + paths[1] + DB_PATH_SEP + paths[2] + DB_PATH_SEP + fileNamePath;
        return newAbsPath;
    }


    public static String getNewAbsPathForOnly(boolean isContainsFuncode,String[] paths) {
        String headPath = OptionPropUtil.ONLY_DIST_PATH();
//        String datePath = getDatePathFromUrl(paths[0]);
        String fileNamePath = getFileNamePath(paths[0]);
        String newAbsPath = headPath + (isContainsFuncode?"":paths[1] )+ DB_PATH_SEP + paths[2] + DB_PATH_SEP + fileNamePath;
        return newAbsPath;
    }

    /**
     * 得到新的 IMG_URL
     * IMG_URL: /media/Ddot1Adot2BAPPdot2BSFA_demodot2Bwebappsdot2BROOTdot2B photosdot2B  这里可能有错误目录dot2B  2016-07-28dot2Bb5f6ebf8-eb7f-44ef-bd5d-8843bcd74706dot4Djpg.jpg
     * 老数据是 photos/2017-01-01/xxx.jpg
     */
    public static String getNewImgUrl(String oldImgUrl, String funcCodePath,String date) {
        String newImgUrl = null;
        if (oldImgUrl != null) {
//        	String date = getDatePathFromUrl(oldImgUrl);
            newImgUrl = oldImgUrl;// 保证原来的数据没问题

            if (date!=null) {
                // 替换
                String flag = "dot2B";
                if(oldImgUrl.contains("dot2B")){
                    flag = "dot2B";
                }else{// 就是 / 分割
                    flag = DB_PATH_SEP;
                }
                String var0 = oldImgUrl.split("photos"+flag)[1];
                if(var0!=null && var0.trim().length()>0){
                    String var1 = var0.split(date)[0];// 得到的可能是错误目录
                    if(var1!=null && var1.trim().length()>0){// 说明存在错误目录
                        newImgUrl = oldImgUrl.replace(var1+date, funcCodePath + flag + date);// 整体替换
                    }
                }

            }
        }
        return newImgUrl;
    }

    public static String getNewImgUrlForOnly(String oldImgUrl, String funcCodePath,String date) {
        String ONLY_DIST_PATH = OptionPropUtil.ONLY_DIST_PATH();
        if (ONLY_DIST_PATH.contains(DB_PATH_SEP)){
            ONLY_DIST_PATH = DB_PATH_SEP+"media"+DB_PATH_SEP+ONLY_DIST_PATH.replace(":"+DB_PATH_SEP,"dot1Adot2B").replaceAll(DB_PATH_SEP,"dot2B") + oldImgUrl.split("photos")[1];
        }

        ONLY_DIST_PATH = ONLY_DIST_PATH.replace("dot2Bdot2B","dot2B");

        return ONLY_DIST_PATH;

    }

    /**
     * 截取图片日期部分 2016-07-28
     * @param  /usr/opt/APP/SFA_demo/webapps/ROOT/photos/2016-07-28/b5f6ebf8-eb7f-44ef-bd5d-8843bcd74706.jpg
     */
    public static String getDatePathFromUrl(String url) {
        String date = "";
        Pattern p = Pattern.compile("2\\d{3}-?[0-1][0-9]-?[0-3][0-9]");
        Matcher m = p.matcher(url);
        if(m.find()){
            date = m.group();
            return date;
        }
        return null;
    }






    /**
     * 判断目录是否存在，不存在则创建
     */
    public static String createPath(String path) {

        path = path.replaceAll(DB_PATH_SEP, File.separator);

        File p = new File(path);
        boolean flag = p.exists() && p.isDirectory();
        if (!flag) {// 不存在该目录
            p.mkdirs();
        }
        return path;
    }




    /**
     * 剪切文件
     */
    public static boolean movePhoto(boolean IS_DELETE_OLD_IMG,boolean IS_DELETE_IMG,String sourcePath, String destPath) {

        String sysSourcePath = sourcePath.replace(DB_PATH_SEP, File.separator);// 换成系统的分隔符
        String sysDestPath = destPath.replace(DB_PATH_SEP, File.separator);// 换成系统的分隔符

        // 复制0
        boolean copyOk = false;
        if (IS_DELETE_IMG){// 如果需要彻底删除，就不用复制了
            copyOk = true;
        }else{
            copyOk = copyPhoto(sysSourcePath, sysDestPath);
        }

        try {
            if (copyOk && (IS_DELETE_OLD_IMG || IS_DELETE_IMG) ) {// 如果是删除源文件
                // 删除源文件
                deletePhoto(sysSourcePath);
            }
            // eg: D:/aaa/2017-02-23
            String dateFullPath = sysSourcePath.substring(0, sysSourcePath.lastIndexOf(File.separator));
            // 检查对应的原日期目录中是否还有图片，没有图片，删除整个日期目录
            if (isEmptyPath(dateFullPath)) {
                // 删除日期目录
                new File(dateFullPath).delete();
                return true;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    /**
     * 剪切文件
     */
    public static boolean movePhoto(boolean IS_DELETE_OLD_IMG,boolean IS_DELETE_IMG,String[] paths) {
        String PHOTO_PATH = OptionPropUtil.PHOTO_PATH();
        String sysSourcePath = PHOTO_PATH  + paths[0].replace(DB_PATH_SEP, File.separator);// imgUrl 换成系统的分隔符
        String sysDestPath = paths[1].replace(DB_PATH_SEP, File.separator);// 换成系统的分隔符

        // 复制0
        boolean copyOk = false;
        if (IS_DELETE_IMG){// 如果需要彻底删除，就不用复制了
            copyOk = true;
        }else{
            copyOk = copyPhoto(sysSourcePath, sysDestPath);
        }

        try {
            if (copyOk && (IS_DELETE_OLD_IMG || IS_DELETE_IMG)) {
                // 删除源文件
                deletePhoto(sysSourcePath);
            }
            // eg: D:/aaa/2017-02-23
            String dateFullPath = sysSourcePath.substring(0, sysSourcePath.lastIndexOf(File.separator));
            // 检查对应的原日期目录中是否还有图片，没有图片，删除整个日期目录
            if (isEmptyPath(dateFullPath)) {
                // 删除日期目录
                new File(dateFullPath).delete();
                return true;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    /**
     * 检查目录中是否还存在文件
     */
    public static boolean isEmptyPath(String checkPath) {
        File path = new File(checkPath);
        String[] fileList = null;
        // 检查目录合法性
        if (checkPath != null && path.isDirectory()) {
            fileList = path.list();
        }
        // 没有文件
        if (fileList == null || fileList.length == 0) {
            return true;
        }
        return false;
    }


    /**
     * 复制单个文件
     */
    public static boolean copyPhoto(String sourcePath, String destPath) {
        logger.info("开始复制Photo ...");
        try {
            File photo = new File(sourcePath);

            // 判断photo是否是一个存在的文件，防止发生"系统找不到指定的路径"
            if(!photo.exists()){
                return false;
            }

            // 如果存在 再执行操作
            if (photo.exists()) {
                InputStream in = new FileInputStream(sourcePath); //读入原文件
                OutputStream out = new FileOutputStream(destPath);
                byte[] buffer = new byte[1024 * 10];
                int len = 0;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                in.close();
                out.close();
                logger.info("复制Photo完成 ...");
                logger.info("文件大小 Length ：" + photo.length());
            }

            return true;
        } catch (IOException ioe) {
            logger.error("复制Photo失败 ...");
            ioe.printStackTrace();
            return false;
        }

    }


    /**
     * 文件删除
     */
    public static boolean deletePhoto(String sourcePath) {
        File photo = new File(sourcePath);
        if (photo.exists()) {
            System.out.print(sourcePath);
            photo.delete();
            return true;
        } else if (!photo.isAbsolute()) {// 不是绝对路径删除不了
            return false;
        }
        return false;
    }

    /**
     * 判断两个路径是否等效
     * 比如  D:/abc/d/     D:/abc/d
     */
    public static boolean compPathIsSame(String path1,String path2){
        File f1 = new File(path1);
        File f2 = new File(path2);
        String p1 = f1.getPath();
        String p2 = f2.getPath();
        return p1.equals(p2)?true:false;
    }


    @Test
    public void sdsa(){
        File f1 = new File("D:/abc/d/");
        File f2 = new File("D:/abc/d");
        String p1 = f1.getPath();
        String p2 = f2.getPath();
        System.out.print(p1+"-----"+p2);
    }








}
