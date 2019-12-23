/*
 * Copyright (c) 2015, 张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.oschina.app.emoji;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;

import net.oschina.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 表情页，每页的显示
 *
 * @author kymjs (http://www.kymjs.com)
 */
@SuppressLint("ValidFragment")
public class EmojiPageFragment extends Fragment {
    private List<Emojicon> datas;
    private GridView sGrid;
    private EmojiGridAdapter adapter;
    private OnEmojiClickListener listener;

    public EmojiPageFragment(int index, int type, OnEmojiClickListener l) {
        initData(index, type);
        this.listener = l;
    }

    private void initData(int index, int type) {
        datas = new ArrayList<Emojicon>();
        if (KJEmojiFragment.EMOJI_TAB_CONTENT > 1) {
            datas = DisplayRules.getAllByType(type);
        } else {
            List<Emojicon> dataAll = DisplayRules.getAllByType(type);
            int max = Math.min((index + 1) * KJEmojiConfig.COUNT_IN_PAGE,
                    dataAll.size());
            for (int i = index * KJEmojiConfig.COUNT_IN_PAGE; i < max; i++) {
                datas.add(dataAll.get(i));
            }
            datas.add(new Emojicon(KJEmojiConfig.DELETE_EMOJI_ID, 1, "delete:",
                    "delete:"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        sGrid = new GridView(getActivity());
        sGrid.setNumColumns(KJEmojiConfig.COLUMNS);
        adapter = new EmojiGridAdapter(getActivity(), datas);
        sGrid.setAdapter(adapter);
        sGrid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                EditText editText = (EditText) getActivity().findViewById(
                        R.id.emoji_titile_input);
                if (listener != null) {
                    listener.onEmojiClick((Emojicon) parent.getAdapter()
                            .getItem(position));
                }
                if (editText != null)
                    InputHelper.input2OSC(editText, (Emojicon) parent.getAdapter()
                            .getItem(position));
            }
        });
        sGrid.setSelector(R.drawable.ic_material);
        return sGrid;
    }

    public GridView getRootView() {
        return sGrid;
    }
}
