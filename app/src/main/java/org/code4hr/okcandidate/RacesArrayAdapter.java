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
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.InputStream;


public class RacesArrayAdapter extends ArrayAdapter<RacesItem> {
    Context mContext;
    int mLayoutResourceId;
    RacesItem mRaces[];

    RacesArrayAdapter(Context context, int layoutResourceId, RacesItem[] races) {
        super(context, layoutResourceId, races);

        mContext = context;
        mLayoutResourceId = layoutResourceId;
        mRaces = races;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(mLayoutResourceId, parent, false);
        }

        RacesItem race = mRaces[position];

        TextView name = (TextView) convertView.findViewById(R.id.races_list_text);
        name.setText(race.name());

        LinearLayout layout = (LinearLayout)convertView.findViewById(R.id.candidates_layout);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        layout.removeAllViews();

        CandidateItem[] candidateItems = race.candidates();

        for(CandidateItem candidate : candidateItems) {
            View child = new View(mContext);
            child = inflater.inflate(R.layout.candidates_list_layout, null);
            TextView candidate_name = (TextView) child.findViewById(R.id.candidate_name);
            TextView candidate_website = (TextView) child.findViewById(R.id.candidate_website);
            ProgressBar candidate_match = (ProgressBar) child.findViewById(R.id.candidate_match);
            TextView candidate_match_text = (TextView) child.findViewById(R.id.candidate_match_text);
            ImageView candidate_picture = (ImageView) child.findViewById(R.id.candidate_picture);

            candidate_name.setText( candidate.name());
            candidate_match.setMax(100);
            candidate_match.setProgress(candidate.match());
            if(candidate.match() >= 80) {
                candidate_match.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
            }
            else if(candidate.match() >= 60) {
                candidate_match.getProgressDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.MULTIPLY);
            }
            else if(candidate.match() >= 50) {
                candidate_match.getProgressDrawable().setColorFilter(0xFFFC9F31, PorterDuff.Mode.MULTIPLY);
            }
            else {
                candidate_match.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
            }
            candidate_match_text.setText(Integer.toString(candidate.match()) + "% Match");
            candidate_website.setText(candidate.webURL());

            new ShowPicture(candidate_picture).execute(candidate);

            layout.addView(child);
        }


        return convertView;
    }

    private class ShowPicture extends AsyncTask<CandidateItem, Void, Void> {
        ImageView mCandidatePicture;
        CandidateItem mCandidate;

        ShowPicture(ImageView candidatePicture) {
            mCandidatePicture = candidatePicture;
        }

        @Override
        protected Void doInBackground(CandidateItem... candidateItems) {
            mCandidate = candidateItems[0];
            while(mCandidate.picture() == null);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mCandidatePicture.setImageBitmap(mCandidate.picture());
        }
    }

}
