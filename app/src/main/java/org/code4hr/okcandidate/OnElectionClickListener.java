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

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

public class OnElectionClickListener implements AdapterView.OnItemClickListener {
    ElectionsFragmentCallback mCallback;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Context context = view.getContext();
        TextView ElectionTextView = (TextView) view.findViewById(R.id.elections_list_text);
        mCallback.openSurvey(Integer.parseInt(ElectionTextView.getTag().toString()));
    }

    public void setCallback(ElectionsFragmentCallback mCallback){

        this.mCallback = mCallback;
    }


    public interface ElectionsFragmentCallback {

        public void openSurvey(int id);
    }
}
