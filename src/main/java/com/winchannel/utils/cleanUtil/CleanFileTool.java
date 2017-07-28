package com.winchannel.utils.cleanUtil;

import com.sun.org.apache.bcel.internal.generic.RETURN;
import com.winchannel.bean.Photo;
import oracle.jdbc.OracleDriver;
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


    /**
     * 判断是否是移动到固定目录的数据并且已经被移动了
     */
    public static boolean isTruePathForOnly(Photo photo) {
        String DB_PATH_SEPARATOR= OptionPropUtil.DB_PATH_SEPARATOR();
        // 固定目录路径：包含了FUNC_CODE
        String ONLY_DIST_PATH = OptionPropUtil.ONLY_DIST_PATH();
        // 是否包含FUNC_CODE
        String func_code = photo.getFuncCode();
        // 2012年的数据没有ABSOLUTE_PATH数据,并且没有dot2B 这种分隔符
        String imgUrl = photo.getImgUrl();
        // 为空直接忽略掉
        if(imgUrl==null || imgUrl.trim().length()==0){
            return true;
        }
        // 如果
        if(imgUrl.contains("dot2B")){// 新型数据
            imgUrl = imgUrl.replaceAll("dot2B",DB_PATH_SEPARATOR);

        }
        imgUrl.replaceAll(DB_PATH_SEPARATOR+DB_PATH_SEPARATOR,DB_PATH_SEPARATOR);
        if(imgUrl.contains(ONLY_DIST_PATH) && imgUrl.contains(func_code) ){
            return true;
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


    /**
     * 直接根据PHOTO_PATH 目的路路径+FUNC_CODE...判断
     */
    public static boolean isTurePath(String PATH_SEP,String imgUrl,String trueFuncCode){

        logger.info("isTurePath ==> imgUrl : "+imgUrl);

        // 为空直接忽略掉
        if(imgUrl==null || imgUrl.trim().length()==0){
            return true;
        }

        String date = getDatePathFromUrl(imgUrl);

        // 使用 目的路径匹配，将目的路径转化成带有dot2B这种分隔符的短路径
        boolean IS_ONLY_PATH = OptionPropUtil.IS_ONLY_PATH();
        String destPath = OptionPropUtil.PHOTO_PATH();
        if(IS_ONLY_PATH){
            destPath = OptionPropUtil.ONLY_DIST_PATH();
            // /media/dot2B efex dot2B app02 dot2B resources dot2B photos dot2B
        }
        // 转换路径
        destPath = "/media/"+destPath.replaceAll("/",PATH_SEP)+"dot2B";
        if(destPath.contains("dot2Bdot2B")){
            destPath = destPath.replaceAll("dot2Bdot2B","dot2B");
        }

        String afterStr = "";
        // 在imgUrl中判断是否包含 FUNC_CODE在内的路径
        if(imgUrl.contains(destPath+trueFuncCode)){
            return true;
            /*afterStr = imgUrl.replace(destPath+trueFuncCode+PATH_SEP,"");

            String[] arr = afterStr.split(PATH_SEP);

            int flag_num = arr.length-1;// afterStr中 dot2B 的个数
            if(flag_num>=2){// 可能有错误路径 F0sdsadas_dsdafasddfsad/date/xxx.jpg

                if(date!=null && date.trim().length()>0) {
                    String funccode_id = afterStr.split(PATH_SEP + date)[0];
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
                String date1 = getDatePathFromUrl(afterStr);
                if(date1!=null){
                    return false;
                }

            }else{// 其他的不做考虑，认为是正确的
                return true;
            }*/

        }else{// 不包含就是错误的，没有处理的
            return false;
        }

//        String afterStr = imgUrl.split("photos"+PATH_SEP)[1];

    }


    /**
     * 根据imgUrl得到该照片数据的主目录(FUNC_CODE上一层)
     * 只能实现含有photos的目录
     * 其他的无法自动判断是不是上一级目录
     * 该方法应该在判断imgUrl中是否含有FUNC_CODE之后再调用判断：如果已经含有了FUNC_CODE就没必要再获取了，获取的目的这里就死为了创建FUNC_CODE目录
     */
    public static String getPhotoSrcPath(String imgUrl){
        String rootImgPath = null;
        String date = getDatePathFromUrl(imgUrl);
        imgUrl = imgUrl.replaceAll("dot2B",DB_PATH_SEP);
        if(imgUrl.contains("photos")){
            rootImgPath = imgUrl.split("photos")[0]+"photos";
        }
        return rootImgPath;
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
        String ONLY_DIST_PATH = OptionPropUtil.ONLY_DIST_PATH();
        String funcCodePath = isContainsFunccode?ONLY_DIST_PATH:ONLY_DIST_PATH + DB_PATH_SEP + FUNC_CODE;
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


    public static String cleanDatePathForOnly(String funcCodeFullPath, String imgUrl) {
        String code_date_path = "";
        String date = "";
        if(imgUrl!=null && imgUrl.trim().length()>0){
            date = getDatePathFromUrl(imgUrl);
            if(date!=null){
                code_date_path = createPath(funcCodeFullPath+File.separator+date);
            }
        }
        return code_date_path;
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
     * 获取文件名部分 path
     */
    public static String getFileNamePath(String url) {
        if (url == null) {
            return null;
        }
        String fileNamePath = null;
        if(url.contains("dot2B")){
            fileNamePath = url.substring(url.lastIndexOf("dot2B") + 1);
        }else {
            fileNamePath = url.substring(url.lastIndexOf(DB_PATH_SEP) + 1);
        }

        return fileNamePath;
    }


    /**
     * 组装新的file 绝对路径
     * 加上 FUNC_CODE_PATH
     */
    public static String getNewAbsPath(String absolutePath, String funcCodeFullPath,String date) {
        String fileNamePath = getFileNamePath(absolutePath);
        String newAbsPath = funcCodeFullPath + DB_PATH_SEP + date + DB_PATH_SEP + fileNamePath;
        return newAbsPath;
    }


    public static String getNewAbsPathForOnly(String absolutePath, String funccodeDatePath) {
        String fileNamePath = getFileNamePath(absolutePath);
        String newAbsPath = funccodeDatePath + DB_PATH_SEP + fileNamePath;
        return newAbsPath;
    }



    public static String getNewAbsPath(String[] paths) {
        String fileNamePath = getFileNamePath(paths[0]);
        String newAbsPath = paths[1] + DB_PATH_SEP + paths[2] + DB_PATH_SEP + fileNamePath;
        return newAbsPath;
    }


    public static String getNewAbsPathForOnly(String[] paths) {
        String fileNamePath = getFileNamePath(paths[0]);
        String newAbsPath = paths[1] + DB_PATH_SEP + fileNamePath;
        return newAbsPath;
    }



    /**
     * 专门为某个FUNC_CODE：在Only目的路径中已经有了FUNC_CODE
     */
    public static String getNewImgUrlForOnly(String oldImgUrl,String funccodeDatePath) {
        String ONLY_DIST_PATH = OptionPropUtil.ONLY_DIST_PATH();
        String newImgUrl = null;
        if (ONLY_DIST_PATH.contains(DB_PATH_SEP)){
            newImgUrl = DB_PATH_SEP+"media"+DB_PATH_SEP + funccodeDatePath.replaceAll("/","dot2B") + "dot2B"+getFileNamePath(oldImgUrl) ;
        }

        newImgUrl = newImgUrl.replace("dot2Bdot2B","dot2B");

        return newImgUrl;
    }


    /**
     * 普通情况
     */
    public static String getNewImgUrl(String oldImgUrl,String FUNC_CODE,String date){
        String DB_PATH_SEPARATOR = OptionPropUtil.DB_PATH_SEPARATOR();
        String newImgUrl = null;
        //linux中的imgUrl路径,只有/ 没有:/
        //  /media/dot2B opt dot2B app datedot2B photos dot2B F20S01_01_V20S01 dot2B 2016-11-08 dot2B d3e2b736-8d1c-49d1-87a6-ce45fc603e30dot4Djpg.jpg
        String urlHead = "/media/";
        String PHOTO_PATH = OptionPropUtil.PHOTO_PATH().replaceAll(DB_PATH_SEPARATOR,"dot2B");
        newImgUrl = urlHead + PHOTO_PATH + "dot2B"+FUNC_CODE+"dot2B"+date+"dot2B"+getFileNamePath(oldImgUrl);

        return newImgUrl;
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
     * TODO ???
     */
    public static boolean movePhotoForOnly(boolean IS_DELETE_OLD_IMG,boolean IS_DELETE_IMG,String[] paths) {
        String sysSourcePath = paths[0].replace(DB_PATH_SEP, File.separator);// imgUrl 换成系统的分隔符
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

    public static boolean movePhoto(boolean IS_DELETE_OLD_IMG,boolean IS_DELETE_IMG,String[] paths) {
        String PHOTO_PATH = OptionPropUtil.PHOTO_PATH();
        String sysSourcePath = paths[0].replace(DB_PATH_SEP, File.separator);// imgUrl 换成系统的分隔符
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
                OutputStream out = new FileOutputStream(new File(destPath));
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
