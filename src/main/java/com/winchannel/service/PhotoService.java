package com.winchannel.service;


import com.winchannel.bean.Photo;

import javax.swing.*;
import java.util.List;

public interface PhotoService {

    /****************新版本功能********************/
    String getFuncCodeByXml(Photo photo);

    List<Photo> getPhotoListByBaseQuery();// 获取baseQuery中所有的Photo数据

    Long getMaxIdByBaseQuery();

    List<Long> getNextIdPoolFromBaseQueryByEndId(Long endId);

    List<Long> getPhotoIdByFcQuerys();

    boolean updateRptPhoto(Photo photo);

    boolean deletePhoto(Photo photo);
    boolean deleteRptPhoto(Photo photo);


    /****************原功能********************/



    Photo getPhotoOne(long id);

    Photo getFirstPhotoOne();

    @Deprecated
    String getFuncCodeByPhoto(Photo photo);

    boolean updatePhoto(Photo photo);

    long getPhotoMinId();

    long getPhotoMaxId();

    void updatePhotoImgId(long ID);




}
