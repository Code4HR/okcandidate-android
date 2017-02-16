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

import android.app.Fragment;
import android.os.AsyncTask;

import com.example.jsonparser.JSONParser;

import org.json.JSONTokener;




//downloads election list in new thread and populates an ArrayAdapter
public class DownloadJSON extends AsyncTask<String, Void, JSONTokener> {

    public interface PostExecuteCallback {
        public void Callback(JSONTokener result, Fragment fragment);
    }

    private Fragment mFragment;
    private PostExecuteCallback mPostExecuteCallback;

    public DownloadJSON(Fragment fragment, PostExecuteCallback postExecutCallback) {
        mFragment = fragment;
        mPostExecuteCallback = postExecutCallback;
    }

    @Override
    protected JSONTokener doInBackground(String... urls) {

        JSONParser parser = new JSONParser();
        JSONTokener jsonData = parser.getJSONFromUrl(urls[0]);
        return jsonData;
    }

    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(JSONTokener result) {
        mPostExecuteCallback.Callback(result,mFragment);
    }
}

