package com.wl1217.cjsw;

import rxhttp.wrapper.annotation.DefaultDomain;

public class Url {

    @DefaultDomain() //设置为默认域名
    public static String baseUrl = "http://114.220.179.71:81/";

    public static String updateVserion = baseUrl + "down/version.txt";

    public static String APP_NAME= "cjsw.apk";

    public static String downApk=  baseUrl + "down/cjsw.apk";;

}
