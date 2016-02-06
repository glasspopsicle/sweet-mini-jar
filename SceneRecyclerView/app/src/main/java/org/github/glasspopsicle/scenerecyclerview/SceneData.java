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
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.transition.Scene;
import android.util.SparseArray;
import android.view.ViewGroup;

public class SceneData {
    @NonNull
    private final SparseArray<Scene> mSceneData = new SparseArray<>();
    @Nullable
    private Scene mFirstScene;
    @Nullable
    RecyclerView.ViewHolder mViewHolder;

    public static <VH extends RecyclerView.ViewHolder> SceneData create(VH viewHolder, @IdRes int parentViewGroupId, int... sceneLayoutResIds) {
        SceneData sceneData = new SceneData();
        sceneData.mViewHolder = viewHolder;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return sceneData;
        if (sceneLayoutResIds.length == 0) return sceneData;
        ViewGroup itemView = (ViewGroup) viewHolder.itemView;
        //noinspection deprecation
        Scene firstScene = new Scene(itemView, (ViewGroup) itemView.findViewById(parentViewGroupId));
        // Bind first scene with view holder root
        sceneData.mFirstScene = firstScene;
        sceneData.mSceneData.put(parentViewGroupId, firstScene);
        for (int layoutResId : sceneLayoutResIds) {
            sceneData.mSceneData.put(layoutResId, Scene.getSceneForLayout(itemView, layoutResId, itemView.getContext()));
        }
        return sceneData;
    }

    @Nullable
    Scene getFirstScene() {
        return mFirstScene;
    }

    SparseArray<Scene> getScenes() {
        return mSceneData.clone();
    }
}
