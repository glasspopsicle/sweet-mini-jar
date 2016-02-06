/*
 * Copyright 2016 Glasspopsicle
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.github.glasspopsicle.scenerecyclerview;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.AutoTransition;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import junit.framework.Assert;

import java.util.ArrayList;

class TestAdapterActivity extends AppCompatActivity {

    private MyAdapter mMyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<String> items = new ArrayList<>();
        for (int i = 1; i <= 100; i++) items.add("Item " + i);
        mMyAdapter = new MyAdapter(items);
        recyclerView.setAdapter(mMyAdapter);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mMyAdapter.restoreFromBundleState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMyAdapter.putBundleState(outState);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public View c1, c2;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text);
            c1 = itemView.findViewById(R.id.c1);
//            c2 = itemView.findViewById(R.id.c2);
        }
    }

    static class MyAdapter extends RecyclerView.Adapter<ViewHolder> implements OnTriggerSceneTransitionsListener<ViewHolder> {
        private final SceneRecyclerViewAdapterDelegate<ViewHolder> mDelegate = new SceneRecyclerViewAdapterDelegate<>();
        private final ArrayList<String> mItems = new ArrayList<>();

        public MyAdapter(ArrayList<String> items) {
            mItems.addAll(items);
        }

        public void putBundleState(Bundle out) {
            mDelegate.putBundleState(out);
        }

        public void restoreFromBundleState(Bundle in) {
            mDelegate.restoreFromBundleState(in);
        }

        @Override
        public void onViewRecycled(ViewHolder holder) {
            mDelegate.onViewRecycled(holder);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            int layoutResId = 0;
            switch (viewType) {
                case 0:
                    layoutResId = R.layout.row1;
                    break;
                case 1:
                    layoutResId = R.layout.row2;
                    break;
                default:
                    Assert.fail();
            }
            view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.textView.setText(mItems.get(position));
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SceneData sceneData = SceneData.create(holder, R.id.c1, R.layout.content2);
                    mDelegate.triggerSceneTransitions(MyAdapter.this, sceneData);
                }
            });
        }

        @Override
        public int getItemViewType(int position) {
            return mDelegate.getItemViewType(position);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        @Override
        public void onTriggerSceneTransitions(final ViewHolder holder, SparseArray<Scene> scenes) {
            if (SceneRecyclerViewAdapterDelegate.areScenesCompatible()) {
                final Scene scene = scenes.get(R.layout.content2);
                scene.setEnterAction(new Runnable() {
                    @SuppressLint("NewApi")
                    @Override
                    public void run() {
                        ViewGroup sceneRoot = scene.getSceneRoot();
                        TextView textView = (TextView) sceneRoot.findViewById(R.id.text);
                        textView.setText(mItems.get(holder.getAdapterPosition()));
                    }
                });
                Transition transition = new AutoTransition();
                transition.addListener(new TransitionListenerAdapter() {
                    @Override
                    public void onTransitionStart(Transition transition) {
                        mDelegate.dispatchSceneChangeBeginning(holder, 1);
                    }

                    @Override
                    public void onTransitionEnd(Transition transition) {
                        mDelegate.dispatchSceneChangeFinished(holder, 1);
                    }
                });
                TransitionManager.go(scene, transition);
            } else {
                mDelegate.dispatchSceneChangeBeginning(holder, 1);
                holder.c1.setVisibility(View.GONE);
                holder.c2.setVisibility(View.VISIBLE);
                mDelegate.dispatchSceneChangeFinished(holder, 1);
            }
        }

    }
}
