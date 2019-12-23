package net.oschina.app.improve.widget.rich;

/**
 * 图片段落
 * Created by huanghaibin on 2017/8/3.
 */

public class ImageSection extends Section {
    private static final long serialVersionUID = -91787654415366445L;

    private String fileName;

    private int height;

    private int width;

    private String filePath;
    public ImageSection() {
        type = TYPE_IMAGE;
    }
    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    public int getWidth() {
        return width;
    }
    public void setWidth(int width) {
        this.width = width;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}