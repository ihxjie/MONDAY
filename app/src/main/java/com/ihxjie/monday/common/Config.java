package com.ihxjie.monday.common;

public class Config {
    // To run the sample correctly, the following variables must have valid values.
    // The endpoint value below is just the example. Please use proper value according to your region

    // 访问的endpoint地址
    public static final String OSS_ENDPOINT = "https://oss-cn-beijing.aliyuncs.com";


    public static final String BUCKET_NAME = "monday-antd";
    public static final String OBJECT_NAME = "monday/";
    public static final String OSS_ACCESS_KEY_ID = "LTAI5tDQLBemTKmyNWFpfTCK";;
    public static final String OSS_ACCESS_KEY_SECRET = "qEJS8MzbKsmTd7lqR4I75RLEQYR0AR";

    public static final int DOWNLOAD_SUC = 1;
    public static final int DOWNLOAD_Fail = 2;
    public static final int UPLOAD_SUC = 3;
    public static final int UPLOAD_Fail = 4;
    public static final int UPLOAD_PROGRESS = 5;
    public static final int LIST_SUC = 6;
    public static final int HEAD_SUC = 7;
    public static final int RESUMABLE_SUC = 8;
    public static final int SIGN_SUC = 9;
    public static final int BUCKET_SUC = 10;
    public static final int GET_STS_SUC = 11;
    public static final int MULTIPART_SUC = 12;
    public static final int STS_TOKEN_SUC = 13;
    public static final int FAIL = 9999;
    public static final int REQUESTCODE_AUTH = 10111;
    public static final int REQUESTCODE_LOCALPHOTOS = 10112;
}
