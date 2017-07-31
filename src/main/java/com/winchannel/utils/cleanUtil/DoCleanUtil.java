package com.winchannel.utils.cleanUtil;

import com.winchannel.bean.Photo;
import com.winchannel.service.PhotoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@SuppressWarnings("all")
@Component("cleanUtil")
public class DoCleanUtil {
    private static final Logger logger = LoggerFactory.getLogger(DoCleanUtil.class);

    @Autowired
    private PhotoService photoService;


    /**
     * 根据T_ID_POOL处理对应的图片资源
     */
    public boolean cleanPathHandler(long curr_id) {

        System.out.println("reduce ID ========>>> "+curr_id);
        System.out.println("reduce ID ========>>> "+curr_id);
        System.out.println("reduce ID ========>>> "+curr_id);


        // 获取的目的路径：后面可作为判断是否正确路径使用
        String PHOTO_PATH = OptionPropUtil.PHOTO_PATH();
        boolean IS_DELETE_DB = OptionPropUtil.IS_DELETE_DB();// 是否删除数据库记录数据
        boolean IS_DELETE_IMG = OptionPropUtil.IS_DELETE_IMG();// 是否删除磁盘图片文件
        boolean IS_DELETE_OLD_IMG = OptionPropUtil.IS_DELETE_OLD_IMG();// 是否删除原来的图片文件
        boolean IS_ONLY_PATH = OptionPropUtil.IS_ONLY_PATH();// 是否移动到独立路径
        boolean IS_UPDATE_DB = OptionPropUtil.IS_UPDATE_DB();// 是否更新数据库记录

        boolean  IS_BAK_IMG = OptionPropUtil.IS_BAK_IMG();
        String BAK_IMG_PATH = OptionPropUtil.BAK_IMG_PATH();

        String ONLY_DIST_PATH=null;
        String ONLY_FUNC_CODE=null;
        if(IS_ONLY_PATH){
            ONLY_DIST_PATH = OptionPropUtil.ONLY_DIST_PATH();
            ONLY_FUNC_CODE = OptionPropUtil.ONLY_FUNC_CODE();
        }


        Photo photo = null;
        synchronized (DoCleanUtil.class){
            photo = photoService.getPhotoOne(curr_id);
           // 获取其bizDate
            String date = CleanFileTool.getDatePathFromUrl(photo.getImgUrl());
            photo.setBizDate(date);
        }

        logger.info("photo.getImgUrl() ====》 "+photo.getImgUrl());
        logger.info("photo.getImgUrl() ====》 "+photo.getImgUrl());
        logger.info("photo.getImgUrl() ====》 "+photo.getImgUrl());

        if (photo == null) {
            return true;// 直接忽略
        }

        String absolutePath = photo.getImgAbsPath();

        String imgUrl = photo.getImgUrl();



        boolean containsDot2B = false;
        // 判断是否包含 dot2B
        if (imgUrl != null && imgUrl.contains("dot2B")) {// 说明是新数据
            containsDot2B = true;
        }


        // 如果需要移动到某一个固定的目录路径中，就不需要单独去获取FUNC_CODE了，直接移动到这个独立的目录路径即可
        // 根据Photo的IMG_ID 去查询其他表中的 FUNC_CODE信息
        String FUNC_CODE = "";
        if(!IS_ONLY_PATH && !IS_DELETE_IMG){// 如果是删除数据的就不用获取了
            synchronized (DoCleanUtil.class){
                // 使用新版本查询方法
                FUNC_CODE = photoService.getFuncCodeByXml(photo);
//            FUNC_CODE = photoService.getFuncCodeByPhoto(photo);
            }
        }

        // 当需要移动到固定的路径时，指定FUNC_CODE
        if (IS_ONLY_PATH && ONLY_FUNC_CODE!=null){
            FUNC_CODE = ONLY_FUNC_CODE;
        }



        // 删除磁盘数据文件
        if(IS_DELETE_IMG){
            // 直接根据绝对路径删除磁盘文件和remove数据库记录
            String DB_PATH_SEP = OptionPropUtil.DB_PATH_SEPARATOR();
            String sysSourcePath = absolutePath.replace(DB_PATH_SEP, File.separator);// 换成系统的分隔符
            boolean isOK = CleanFileTool.deletePhoto(sysSourcePath);

            // 删除数据库
            if (IS_DELETE_DB){// 是否删除数据库记录
                photoService.deletePhoto(photo);// 删除VISIT_PHOTO表记录
                photoService.deleteRptPhoto(photo);// 删除RPT_PHOTO表记录
            }

            return isOK;

            // // 当使用独立目录 且 独立目录不在原有的photos目录中时
            // 这时候需要将图片移动到新的路径中
        }else if (FUNC_CODE !=null && IS_ONLY_PATH && ONLY_DIST_PATH!=null
                &&
                (!ONLY_DIST_PATH.contains(PHOTO_PATH) ||  !CleanFileTool.compPathIsSame(ONLY_DIST_PATH, PHOTO_PATH + File.separator + FUNC_CODE) )
                ){

            // 判断该Photo是否已经是一个符合规则的路径
            photo.setFuncCode(FUNC_CODE);
            if (CleanFileTool.isTruePathForOnly(photo)) {
                // 如果是符合规则的路径，就继续下一个Photo
                return true;
            }

            // 是否包含FUNC_CODE
            boolean isContainsFunccode = ONLY_DIST_PATH.contains(FUNC_CODE);

            // clean FUNC_CODE path 得到 D:/Photo_Test/photos/FUNC_CODE 这层目录
            @SuppressWarnings("unused")
            String funcCodeFullPath = CleanFileTool.cleanFuncCodePathForOnly(isContainsFunccode,FUNC_CODE);

            // 处理日期目录  得到 D:/Photo_Test/photos/FUNC_CODE/2017-01-23 这层目录
            String funccodeDatePath = CleanFileTool.cleanDatePathForOnly(funcCodeFullPath, photo.getImgUrl());

            if(funccodeDatePath!=null && (funccodeDatePath.contains("//") ||funccodeDatePath.contains("\\\\"))){
                funccodeDatePath.replaceAll("//","/");
                funccodeDatePath.replaceAll("\\\\","\\");
            }

            // 开始move文件
            // 在原绝对路径基础上加上FUNC_CODE目录
            String newAbsPath = "";
            // 移动文件到新目录
            // 注意：方法内需要对路径中的分隔符处理
            boolean moveFileOk = false;

            // 如果需要备份，在移除之前备份
            if (IS_BAK_IMG){
                String fileName = CleanFileTool.getFileNamePath(absolutePath);
                String fcunccodeDate = funccodeDatePath.replace(PHOTO_PATH,"");

                String bakDestPath = BAK_IMG_PATH + File.separator +  fcunccodeDate + File.separator + fileName;
                // 创建备份的全路径
                File bakDestPathFile = new File(BAK_IMG_PATH + File.separator +  fcunccodeDate);
                bakDestPathFile.mkdir();
                CleanFileTool.copyPhoto(absolutePath,bakDestPath);
            }

            if (containsDot2B) {// 有绝对路径数据
                newAbsPath = CleanFileTool.getNewAbsPathForOnly(absolutePath, funccodeDatePath);
                moveFileOk = CleanFileTool.movePhoto(IS_DELETE_OLD_IMG,IS_DELETE_IMG,absolutePath, newAbsPath);
            } else {
                newAbsPath = CleanFileTool.getNewAbsPathForOnly(new String[]{imgUrl,funccodeDatePath});
                moveFileOk = CleanFileTool.movePhotoForOnly(IS_DELETE_OLD_IMG,IS_DELETE_IMG,new String[]{imgUrl, newAbsPath});
            }

            if (moveFileOk) {
                // 更新数据库:需要更新photo的 absolute_path 和 img_url
                String newImgUrl = CleanFileTool.getNewImgUrlForOnly(photo.getImgUrl(),funccodeDatePath);

                photo.setImgAbsPath(newAbsPath);// 修改绝对路径
                if(newImgUrl!=null){
                    photo.setImgUrl(newImgUrl);// 修改img_url
                }

                // 检查是否需要更新数据库
                synchronized (DoCleanUtil.class){
                    // 是否更新数据库
                    if (IS_UPDATE_DB){
                        photoService.updatePhoto(photo);// 修改VISIT_PHOTO表
                        photoService.updateRptPhoto(photo);// 修改RPT_PHOTO表(一个字段imgUrl)
                    } else if (IS_DELETE_DB){// 是否删除数据库记录
                        photoService.deletePhoto(photo);// 删除VISIT_PHOTO表记录
                        photoService.deleteRptPhoto(photo);// 删除RPT_PHOTO表记录
                    }
                }
            }

            return true;


        } else if (FUNC_CODE != null && FUNC_CODE.length() > 0) { // 获取到 FUNC_CODE | 当使用独立目录时，检查是否是常规的在原photos目录中 固定某个FUNC_CODE的目录


            // 判断该Photo是否已经是一个符合规则的路径
            photo.setFuncCode(FUNC_CODE);
            if (CleanFileTool.isTruePath2(photo)) {
                // 如果是符合规则的路径，就继续下一个Photo
                return true;
            }

            // clean FUNC_CODE path 得到 D:/Photo_Test/photos/FUNC_CODE 这层目录
            @SuppressWarnings("unused")
            String funcCodeFullPath = CleanFileTool.cleanFuncCodePath(FUNC_CODE);

            // 处理日期目录  得到 D:/Photo_Test/photos/FUNC_CODE/2017-01-23 这层目录
            String date = CleanFileTool.cleanDatePath(FUNC_CODE, photo.getImgUrl());

            // 如果需要备份，在移除之前备份
            if (IS_BAK_IMG){
                String bakPath = BAK_IMG_PATH + File.separator + FUNC_CODE + File.separator + date ;
                String fileName = CleanFileTool.getFileNamePath(absolutePath);
                String bakDestPath = bakPath + File.separator + fileName;
                CleanFileTool.copyPhoto(absolutePath,bakDestPath);
            }


            // 开始move文件
            // 在原绝对路径基础上加上FUNC_CODE目录
            String newAbsPath = "";
            // 移动文件到新目录
            // 注意：方法内需要对路径中的分隔符处理
            boolean moveFileOk = false;

            if (containsDot2B) {// 有绝对路径数据
                newAbsPath = CleanFileTool.getNewAbsPath(absolutePath, funcCodeFullPath,date);
                moveFileOk = CleanFileTool.movePhoto(IS_DELETE_OLD_IMG,IS_DELETE_IMG,absolutePath, newAbsPath);
            } else {
                newAbsPath = CleanFileTool.getNewAbsPath(new String[]{imgUrl, funcCodeFullPath,date});
                moveFileOk = CleanFileTool.movePhoto(IS_DELETE_OLD_IMG,IS_DELETE_IMG,new String[]{imgUrl, newAbsPath});
            }

            if (moveFileOk) {
                // 更新数据库:需要更新photo的 absolute_path 和 img_url
                String newImgUrl = CleanFileTool.getNewImgUrl(photo.getImgUrl(), FUNC_CODE,date);

                photo.setImgAbsPath(newAbsPath);// 修改绝对路径
                if(newImgUrl!=null){
                    photo.setImgUrl(newImgUrl);// 修改img_url
                }

                // 检查是否需要更新数据库
                synchronized (DoCleanUtil.class){
                    // 是否更新数据库
                    if (IS_UPDATE_DB){
                        photoService.updatePhoto(photo);// 修改VISIT_PHOTO表
                        photoService.updateRptPhoto(photo);// 修改RPT_PHOTO表(一个字段imgUrl)
                    } else if (IS_DELETE_DB){// 是否删除数据库记录
                        photoService.deletePhoto(photo);// 删除VISIT_PHOTO表记录
                        photoService.deleteRptPhoto(photo);// 删除RPT_PHOTO表记录

                    }

                }



            }
            return true;
        }

        return false;

    }

    public void wait(int million){
        try{
            Thread.sleep(million);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
