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

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseIntArray;

class ParcelableSparseIntArray extends SparseIntArray implements Parcelable {
    public static final Creator<ParcelableSparseIntArray> CREATOR = new Creator<ParcelableSparseIntArray>() {
        @Override
        public ParcelableSparseIntArray createFromParcel(Parcel source) {
            ParcelableSparseIntArray out = new ParcelableSparseIntArray();
            int size = source.readInt();
            int[] keys = new int[size];
            int[] values = new int[size];
            source.readIntArray(keys);
            source.readIntArray(values);
            for (int i = 0; i < size; i++) {
                out.put(keys[i], values[i]);
            }
            return out;
        }

        @Override
        public ParcelableSparseIntArray[] newArray(int size) {
            return new ParcelableSparseIntArray[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        int size = size();
        int[] keys = new int[size];
        int[] values = new int[size];
        for (int i = 0; i < size; i++) {
            keys[i] = keyAt(i);
            values[i] = valueAt(i);
        }
        dest.writeInt(size);
        dest.writeIntArray(keys);
        dest.writeIntArray(values);
    }
}
