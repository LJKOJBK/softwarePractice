package net.oschina.app.widget;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.bumptech.glide.Glide;

import net.oschina.app.R;
import net.oschina.app.util.UIHelper;


public class AvatarView extends CircleImageView {
    public static final String AVATAR_SIZE_REG = "_[0-9]{1,3}";
    public static final String MIDDLE_SIZE = "_100";
    public static final String LARGE_SIZE = "_200";

    private int id;
    private String name;
    private Activity aty;

    public AvatarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public AvatarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AvatarView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        aty = (Activity) context;
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(name)) {
                    UIHelper.showUserCenter(getContext(), id, name);
                }
            }
        });
    }

    public void setUserInfo(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setAvatarUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            setImageResource(R.mipmap.widget_default_face);
            return;
        }
        // 由于头像地址默认加了一段参数需要去掉
        int end = url.indexOf('?');
        final String headUrl;
        if (end > 0) {
            headUrl = url.substring(0, end);
        } else {
            headUrl = url;
        }

        if (aty != null) {
            Glide.with(aty).load(headUrl)
                    .error(R.mipmap.widget_default_face)
                    .placeholder(R.mipmap.widget_default_face)
                    .into(this);
        }
    }

    public static String getSmallAvatar(String source) {
        return source;
    }

    public static String getMiddleAvatar(String source) {
        if (source == null)
            return "";
        return source.replaceAll(AVATAR_SIZE_REG, MIDDLE_SIZE);
    }

    public static String getLargeAvatar(String source) {
        if (source == null)
            return "";
        return source.replaceAll(AVATAR_SIZE_REG, LARGE_SIZE);
    }
}
