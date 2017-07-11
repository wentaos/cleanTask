package com.winchannel.cleanData;

public class Constant {

	/**
	 * THREAD_NUM:线程数
	 */
	public static final String THREAD_NUM = "THREAD_NUM";

	/**
	 * 程序运行时指定当前的线程数，在项目重启时需要根据线程数获取IDInfo信息
	 */
	public static final String HIS_THREAD_NUM = "HIS_THREAD_NUM";

	/**
	 * PHOTO_PATH：需要处理的文件目录
	 */
	public static final String PHOTO_PATH = "PHOTO_PATH";

	/**
	 * RUN_TASK_TIME_LEN:定时任务运行时长 分钟
	 */
	public static final String RUN_TASK_TIME_LEN = "RUN_TASK_TIME_LEN";

	/**
	 * REDUCE_ID_NUM:一个线程一次处理的ID数
	 */
	public static final String REDUCE_ID_NUM = "REDUCE_ID_NUM";

	/**
	 * IS_TEST:是否使用测试数据库中测试数据表 XXX_T
	 */
	public static final String IS_TEST = "IS_TEST";

	/**
	 * LOOP_SAVE_COUNT :遍历多少条记录保存一次ID信息:数据越小越利于重启连续；过小也会增加数据库update访问次数
	 */
	public static final String LOOP_SAVE_COUNT = "LOOP_SAVE_COUNT";

	/**
	 * 数据库类型
	 */
	public static final String ORACLE = "ORACLE";
	public static final String SQLSERVER = "SQLSERVER";
	public static final String MYSQL = "MYSQL";
	public static final String DB_TYPE="DB_TYPE";

	/**
	 * 数据库ID信息唯一标志
	 */
	public static final String IMG_ID_FALG = "IMG_ID_FALG";

	/**
	 * 存放ID_INFO信息的目录
	 */
	public static final String ID_INFO_PATH = "ID_INFO_PATH";

	/**
	 * ID POOL：用于存储再次运行程序时无法分配的ID
	 */
	public static final String ID_POOL = "ID_POOL";

	/**
	 * 是否使用 处理一部分 FUNC_CODE 的功能
	 */
	public static final String IS_FUNC_CODE_PART = "IS_FUNC_CODE_PART";

	/**
	 * 需要处理的部分 FUNC_CODE
	 */
	public static final String FUNC_CODE_LIST = "FUNC_CODE_LIST";

	/**
	 * 是否需要更新数据库
	 */
	public static final String IS_UPDATE_DB = "IS_UPDATE_DB";

	/**
	 * 是否需要删除数据库记录
	 */
	public static final String IS_DELETE_DB = "IS_DELETE_DB";

	/**
	 * 是否需要删除磁盘图片文件
	 */
	public static final String IS_DELETE_IMG = "IS_DELETE_IMG";

	/**
	 * 是否备份需要删除的图片数据
	 */
	public static final String IS_BAK_IMG = "IS_BAK_IMG";
	/**
	 * 如果备份的话，用户配置的备份目录
	 */
	public static final String BAK_IMG_PATH = "BAK_IMG_PATH";

	/**
	 * 数据库中的分隔符
	 */
	public static final String DB_PATH_SEPARATOR = "DB_PATH_SEPARATOR";


	/**
	 * 是否移动到唯一路径
	 * 唯一的目的存放路径
	 */
	public static final String IS_ONLY_PATH = "IS_ONLY_PATH";
	public static final String ONLY_DIST_PATH = "ONLY_DIST_PATH";
	public static final String ONLY_FUNC_CODE = "ONLY_FUNC_CODE";
	public  static final String IS_DELETE_OLD_IMG = "IS_DELETE_OLD_IMG";

}
