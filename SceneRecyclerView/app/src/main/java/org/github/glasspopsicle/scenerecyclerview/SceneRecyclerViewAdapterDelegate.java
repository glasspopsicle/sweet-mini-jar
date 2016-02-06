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

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.util.SparseArray;

import java.util.Map;
import java.util.WeakHashMap;

public final class SceneRecyclerViewAdapterDelegate<VH extends RecyclerView.ViewHolder> {
    private final Map<RecyclerView.ViewHolder, SceneData> mViewHolderScenesMap = new WeakHashMap<>();
    private ParcelableSparseIntArray mPositionToSceneItemTypeMap = new ParcelableSparseIntArray();
    private final Handler mHandler = new Handler();

    public SceneRecyclerViewAdapterDelegate() {
    }

    public static boolean areScenesCompatible() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public void putBundleState(Bundle out) {
        out.putParcelable("posToItemTypeMap", mPositionToSceneItemTypeMap);
    }

    public void restoreFromBundleState(@Nullable Bundle in) {
        if (in != null) {
            ParcelableSparseIntArray parcelable = in.getParcelable("posToItemTypeMap");
            if (parcelable != null) {
                mPositionToSceneItemTypeMap = parcelable;
            }
        }
    }

    public void triggerSceneTransitions(OnTriggerSceneTransitionsListener<VH> listener, SceneData sceneData) {
        RecyclerView.ViewHolder holder;
        if (areScenesCompatible()) {
            mViewHolderScenesMap.put(sceneData.mViewHolder, sceneData);
        }
        holder = sceneData.mViewHolder;
        if (listener != null) {
            SceneData data = mViewHolderScenesMap.get(holder);
            //noinspection unchecked
            listener.onTriggerSceneTransitions((VH) holder, data != null ? data.getScenes() : new SparseArray<Scene>());
        }
        sceneData.mViewHolder = null;
    }

    public void dispatchSceneChangeFinished(final RecyclerView.ViewHolder holder, int sceneItemType) {
        // Must be put into the message queue otherwise
        // recycling may happen too early leading to unusual intermediary views
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                holder.setIsRecyclable(true);
            }
        }, 1000L);
        mPositionToSceneItemTypeMap.put(holder.getLayoutPosition(), sceneItemType);
    }

    @SuppressWarnings("UnusedParameters")
    public void dispatchSceneChangeBeginning(RecyclerView.ViewHolder holder, int sceneItemType) {
        holder.setIsRecyclable(false);
    }

    public void onViewRecycled(VH holder) {
        if (areScenesCompatible()) {
            SceneData sceneData = mViewHolderScenesMap.get(holder);
            if (sceneData != null) {
                Scene firstScene = sceneData.getFirstScene();
                if (firstScene != null) {
                    if (firstScene.getSceneRoot() != holder.itemView) {
                        throw new RuntimeException("The Scene root must also be the ViewHolder's root.");
                    }
                    // Reset scene
                    TransitionManager.go(firstScene);
                }
            }
        }
    }

    public int getItemViewType(int position) {
        return mPositionToSceneItemTypeMap.get(position);
    }
}
