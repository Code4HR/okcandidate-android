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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.io.Serializable;

public class CandidateItem implements Comparable<CandidateItem>, Parcelable {
    private String mName;
    private int mMatch;
    private String mWebURL;
    private String mImageURL;
    private Bitmap mPicture;

    CandidateItem(String name, int match, String webURL, String imageURL) {
        mName = name;
        mMatch = match;
        mWebURL = webURL;
        mImageURL = imageURL;
        new DownloadImageTask().execute(imageURL);
    }

     String name() {
         return mName;
     }

    int match() {
        return mMatch;
    }

    String webURL(){
        return mWebURL;
    }

    String imageURL() { return  mImageURL; };

    Bitmap picture() { return mPicture; };

    @Override
    public int compareTo(CandidateItem candidateItem) {
        return new Integer(candidateItem.mMatch).compareTo(new Integer(mMatch));
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        public DownloadImageTask() {
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            mPicture = result;
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mName);
        out.writeInt(mMatch);
        out.writeString(mWebURL);
        out.writeString(mImageURL);
    }

    public static final Parcelable.Creator<CandidateItem> CREATOR =
            new Parcelable.Creator<CandidateItem>() {
                public CandidateItem createFromParcel(Parcel in) {
                    return new CandidateItem(in);
                }

                public CandidateItem[] newArray(int size) {
                    return new CandidateItem[size];
                }
            };

    private CandidateItem(Parcel in) {
        mName = in.readString();
        mMatch = in.readInt();
        mWebURL = in.readString();
        mImageURL = in.readString();
        new DownloadImageTask().execute(mImageURL);
    }
}
