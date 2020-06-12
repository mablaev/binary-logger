package com.logger.api.impl;

import com.logger.api.RollingPolicy;

import java.io.File;


public class FileSizeRollingPolicy implements RollingPolicy {
    private static final long DEFAULT_FILE_SIZE = 10 * 1024 * 1024;
    private final long maxFileSize;
    private final String originalFilePath;
    private int index = 0;
    private File currentFile;

    public FileSizeRollingPolicy(String pathToFile) {
        this(pathToFile, DEFAULT_FILE_SIZE);
    }

    public FileSizeRollingPolicy(String pathToFile, long maxFileSize) {
        if (maxFileSize == 0) {
            throw new IllegalArgumentException("maxFileSize cannot be null");
        }
        this.maxFileSize = maxFileSize;
        this.originalFilePath = pathToFile;
        this.currentFile = new File(pathToFile);
        System.out.println("Max file size is " + maxFileSize);
    }

    @Override
    public File getCurrentFile() {
        return currentFile;
    }

    @Override
    public boolean tryRollover() {
        if (currentFile.length() < maxFileSize) {
            return false;
        } else {
            index++;
            currentFile = rolloverFileName();
            return true;
        }
    }

    private File rolloverFileName() {
        File file = new File(originalFilePath);
        return new File(file.getAbsoluteFile().getParent() + File.separator +
                getFileName(file.getName()) + index + "." + getExtension(originalFilePath));
    }

    private String getFileName(String fileName) {
        if (fileName.contains(".")) {
            return fileName.substring(0, fileName.lastIndexOf("."));
        }
        return fileName;
    }

    private String getExtension(String fileName) {
        if (fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        return "";
    }
}
