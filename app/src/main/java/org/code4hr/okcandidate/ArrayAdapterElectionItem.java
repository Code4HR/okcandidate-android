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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Nathan on 11/1/2016.
 */
public class ArrayAdapterElectionItem extends ArrayAdapter<ElectionItem> {
    Context mContext;
    int mLayoutResourceId;

    ArrayList<ElectionItem> mData = null;

    public ArrayAdapterElectionItem(Context context, int layoutResourceId, ArrayList<ElectionItem> data) {
        super(context, layoutResourceId, data);
        mContext = context;
        mLayoutResourceId = layoutResourceId;
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mLayoutResourceId, parent, false);

        }

        ElectionItem electionItem = mData.get(position);
        TextView electionTextView = (TextView) convertView.findViewById(R.id.elections_list_text);
        electionTextView.setText(electionItem.toString());
        electionTextView.setTag(electionItem.getId());

        return convertView;
    }



}
