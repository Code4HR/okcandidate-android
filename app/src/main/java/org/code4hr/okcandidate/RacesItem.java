/*Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.*/

package org.code4hr.okcandidate;

import android.os.Parcel;
import android.os.Parcelable;

public class RacesItem implements Comparable<RacesItem>, Parcelable {
    String mName;
    CandidateItem[] mCandidates;

    public RacesItem (String name, CandidateItem[] candidates) {
        mName = name;
        mCandidates = candidates;
    }

    public String name() {
        return mName;
    }

    public CandidateItem[] candidates() {
        return mCandidates;
    }

    @Override
    public int compareTo(RacesItem t1) {
        return name().compareTo(t1.name());
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mName);
        out.writeInt(mCandidates.length);
        for(CandidateItem candidateItem : mCandidates) {
            out.writeParcelable(candidateItem, flags);
        }
    }

    public static final Parcelable.Creator<RacesItem> CREATOR =
            new Parcelable.Creator<RacesItem>() {
                public RacesItem createFromParcel(Parcel in) {
                    return new RacesItem(in);
                }

                public RacesItem[] newArray(int size) {
                    return new RacesItem[size];
                }
            };

    private RacesItem(Parcel in) {
        mName = in.readString();
        int candidateItemSize = in.readInt();
        mCandidates = new CandidateItem[candidateItemSize];
        for(int i = 0; i < candidateItemSize; i++) {
            mCandidates[i] = in.readParcelable(CandidateItem.class.getClassLoader());
        }
    }
}
