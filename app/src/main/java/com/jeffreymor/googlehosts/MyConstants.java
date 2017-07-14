package com.jeffreymor.googlehosts;

/**
 * Created by chenzhiyong on 16/4/17.
 */
public interface MyConstants {

    String SYSTEM_HOST_FILE_PATH_NORMAL = "/system/etc/hosts";
    String SYSTEM_HOST_FILE_PATH_MAGISK = "/magisk/.core/hosts";
    String VOID_HOST_NAME = "void_host";
    String DOWNLOAD_HOST_NAME = "download_host";
    String VOID_HOST_VALUE = "127.0.0.1 localhost \n::1       ip6-localhost";

    int ROOT_NORMAL = 0;
    int ROOT_MAGISK = 1;
    int ROOT_MAGISK_HOSTS_OFF = 3;
    /*
    普通的非magisk的system root--0
    采用magisk的system_less_root--1
    采用magisk, 未开启systemless hosts--2
    */
}
