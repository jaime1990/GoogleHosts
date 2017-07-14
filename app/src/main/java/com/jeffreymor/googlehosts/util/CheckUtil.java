package com.jeffreymor.googlehosts.util;

import android.util.Log;

import com.jeffreymor.googlehosts.MyConstants;
import com.stericson.RootTools.RootTools;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

/**
 * Created by Mor on 2017/6/10.
 */

public class CheckUtil {
    private static final String TAG = "CheckUtil";
    private static final int sLineNumber = 3;


    public static String readLineOfUpdateTime(File sourceFile) throws IOException { //没有修改过hosts将返回null
        FileReader in = new FileReader(sourceFile);
        LineNumberReader reader = new LineNumberReader(in);
        String s = "";

        int lines = 0;
        while (s != null) {
            lines++;
            s = reader.readLine();
            if ((lines - sLineNumber) == 0) {
                Log.d(TAG, "readLineOfUpdateTime: " + s);
                return s;
            }
        }
        reader.close();
        in.close();
        return s;
    }

    public static int checkRootMethod() {
        boolean isMagiskExists = RootTools.exists("/magisk", true);
        boolean isSystemlessHostsExists = RootTools.exists("/magisk/.core/hosts");
        Log.d(TAG, MyConstants.VOID_HOST_VALUE);
        if (!isMagiskExists) {
            return MyConstants.ROOT_NORMAL;
        } else {
            if (isSystemlessHostsExists) {
                return MyConstants.ROOT_MAGISK;
            } else {
                return MyConstants.ROOT_MAGISK_HOSTS_OFF;
            }
        }
    }
}
