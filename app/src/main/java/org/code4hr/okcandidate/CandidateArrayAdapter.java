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

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.InputStream;

/**
 * Created by Nathan on 1/22/2017.
 */

public class CandidateArrayAdapter extends ArrayAdapter<CandidateItem> {
    Context mContext;
    int mLayoutResourceId;
    CandidateItem mCandidates[];

    CandidateArrayAdapter(Context context, int layoutResourceId, CandidateItem[] candidates) {
        super(context, layoutResourceId, candidates);

        mContext = context;
        mLayoutResourceId = layoutResourceId;
        mCandidates = candidates;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(mLayoutResourceId, parent, false);
        }

        CandidateItem candidate = mCandidates[position];

        TextView name = (TextView) convertView.findViewById(R.id.candidate_name);
        ProgressBar match = (ProgressBar) convertView.findViewById(R.id.candidate_match);
        TextView website = (TextView) convertView.findViewById(R.id.candidate_website);
        ImageView image = (ImageView) convertView.findViewById(R.id.candidate_picture);

        name.setText(candidate.name());
        match.setMax(100);
        match.setProgress(candidate.match());

        website.setText(candidate.webURL());

        new DownloadImageTask(image).execute(candidate.imageURL());

        return convertView;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
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
            bmImage.setImageBitmap(result);
        }
    }
}
