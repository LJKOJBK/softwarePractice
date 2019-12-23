package net.oschina.app.team.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import net.oschina.app.R;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.team.bean.TeamActive;
import net.oschina.app.ui.OSCPhotosActivity;
import net.oschina.app.util.StringUtils;
import net.oschina.app.widget.AvatarView;
import net.oschina.app.widget.TweetTextView;

/**
 * Team动态界面ListView适配器 (kymjs123@gmail.com)
 *
 * @author kymjs (https://github.com/kymjs)
 */
public class TeamActiveAdapter extends ListBaseAdapter<TeamActive> {
    private final Context context;
    private RequestManager mManager;

    public TeamActiveAdapter(Context cxt) {
        this.context = cxt;
        mManager = Glide.with(context);
    }

    static class ViewHolder {
        AvatarView img_head;
        TextView tv_name;
        TweetTextView tv_content;
        TextView tv_client;
        TextView tv_date;
        TextView tv_commit;
        TextView tv_title;
        ImageView iv_pic;
    }

    @Override
    protected View getRealView(int position, View v, ViewGroup parent) {
        super.getRealView(position, v, parent);
        ViewHolder holder = null;
        TeamActive data = mDatas.get(position);
        if (v == null || v.getTag() == null) {
            v = View.inflate(context, R.layout.list_cell_team_active, null);
            holder = new ViewHolder();
            holder.img_head = (AvatarView) v
                    .findViewById(R.id.event_listitem_userface);
            holder.tv_name = (TextView) v
                    .findViewById(R.id.event_listitem_username);
            holder.tv_title = (TextView) v.findViewById(R.id.title);
            holder.tv_content = (TweetTextView) v
                    .findViewById(R.id.event_listitem_content);
            holder.tv_client = (TextView) v
                    .findViewById(R.id.event_listitem_client);
            holder.iv_pic = (ImageView) v.findViewById(R.id.iv_pic);
            holder.tv_date = (TextView) v
                    .findViewById(R.id.event_listitem_date);
            holder.tv_commit = (TextView) v.findViewById(R.id.tv_comment_count);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        //holder.img_head.setAvatarUrl(data.getAuthor().getPortrait());
        mManager.load(data.getAuthor().getPortrait()).asBitmap().into(holder.img_head);
        holder.img_head.setUserInfo(data.getAuthor().getId(), data.getAuthor()
                .getName());
        holder.tv_name.setText(data.getAuthor().getName());
        setContent(holder.tv_content, stripTags(data.getBody().getTitle()));

        String date = StringUtils.formatDayWeek(data.getCreateTime());
        String preDate = "";
        if (position > 0) {
            preDate = StringUtils.formatDayWeek(mDatas.get(position - 1)
                    .getCreateTime());
        }
        if (preDate.equals(date)) {
            holder.tv_title.setVisibility(View.GONE);
        } else {
            holder.tv_title.setText(date);
            holder.tv_title.setVisibility(View.VISIBLE);
        }

        holder.tv_content.setMaxLines(3);
        holder.tv_date.setText(StringUtils.formatSomeAgo(data.getCreateTime()));
        holder.tv_commit.setText(data.getReply());

        String imgPath = data.getBody().getImage();
        if (!StringUtils.isEmpty(imgPath)) {
            holder.iv_pic.setVisibility(View.VISIBLE);
            setTweetImage(holder.iv_pic, imgPath);
        } else {
            holder.iv_pic.setVisibility(View.GONE);
        }
        return v;
    }

    /**
     * 移除字符串中的Html标签
     *
     * @param pHTMLString
     * @return
     * @author kymjs (https://github.com/kymjs)
     */
    public static String stripTags(final String pHTMLString) {
        // String str = pHTMLString.replaceAll("\\<.*?>", "");
        String str = pHTMLString.replaceAll("\\t", "");
        str = str.replaceAll("<\\s*img\\s+([^>]*)\\s*>", "").trim();
        return str;
    }

    @Override
    public TeamActive getItem(int arg0) {
        super.getItem(arg0);
        return mDatas.get(arg0);
    }

    /**
     * 动态设置图片显示样式
     *
     * @author kymjs
     */
    private void setTweetImage(final ImageView pic, final String url) {
        pic.setVisibility(View.VISIBLE);

        Glide.with(context).load(url)
                .placeholder(R.drawable.pic_bg)
                .into(pic);

        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OSCPhotosActivity.showImagePreview(context, url);
            }
        });
    }
}