package com.RGu0000;

//http://www.rgagnon.com/javadetails/java-0483.html

import java.io.File;

class DeleteDirectory {

    static public void deleteDirectory(File path) {
        if( path.exists() ) {
            File[] files = path.listFiles();
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                }
                else {
                    files[i].delete();
                }
            }
        }
    }
}