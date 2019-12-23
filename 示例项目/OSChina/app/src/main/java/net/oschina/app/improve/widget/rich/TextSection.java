package net.oschina.app.improve.widget.rich;

/**
 * 文本段落
 * Created by huanghaibin on 2017/8/3.
 */

@SuppressWarnings("all")
public class TextSection extends Section implements Cloneable {
    private static final long serialVersionUID = -94489191465446041L;
    public static final int LEFT = 0;
    public static final int CENTER = 1;
    public static final int RIGHT = 2;


    private String text;

    private int textSize = 14;

    private boolean isBold;

    private boolean isMidLine;

    private boolean isItalic;

    private int alignment;

    private String colorHex;

    public TextSection() {
        type = TYPE_TEXT;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }


    public int getAlignment() {
        return alignment;
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    public String getColorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

    public boolean isBold() {
        return isBold;
    }

    public void setBold(boolean bold) {
        isBold = bold;
    }

    public boolean isMidLine() {
        return isMidLine;
    }

    public void setMidLine(boolean midLine) {
        isMidLine = midLine;
    }

    public boolean isItalic() {
        return isItalic;
    }

    public void setItalic(boolean italic) {
        isItalic = italic;
    }

    public boolean isHeader() {
        return textSize != 18;
    }


    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public TextSection cloneTextSelection() {
        try {
            return (TextSection) clone();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}