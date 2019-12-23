package net.oschina.app.improve.main.tweet.emoji;

import net.oschina.app.R;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.face.FacePanelView;
import net.oschina.app.improve.media.Util;
import net.oschina.app.improve.widget.Keyboard;

import butterknife.Bind;

/**
 * 新版本emoji界面
 * Created by huanghaibin on 2018/1/3.
 */

public class EmojiFragment extends BaseFragment {
    @Bind(R.id.faceView)
    FacePanelView mFaceView;

    public static EmojiFragment newInstance() {
        return new EmojiFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_emoji;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Keyboard.KEYBOARD_HEIGHT >= 300) {
            mRoot.getLayoutParams().height = Keyboard.KEYBOARD_HEIGHT;
        } else {
            mRoot.getLayoutParams().height = Util.dipTopx(mContext, 230);
        }
    }
}
