package com.bemetoy.bp.plugin.personalcenter.model;

/**
 * Created by Tom on 2016/6/6.
 */
public class Avatar {

    private String categoryName;
    private String baseUrl;
    private int size;
    private String fileName;

    public Avatar(String categoryName, String baseUrl, int size, String fileName) {
        this.categoryName = categoryName;
        this.baseUrl = baseUrl;
        this.size = size;
        this.fileName = fileName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getIconUrl(){
        return baseUrl + size+ "/"+ fileName;
    }


}
