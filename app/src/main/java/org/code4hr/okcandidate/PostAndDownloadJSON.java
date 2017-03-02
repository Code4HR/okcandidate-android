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
import android.util.StringBuilderPrinter;

import com.example.jsonparser.JSONParser;

import org.json.JSONTokener;

public class PostAndDownloadJSON extends DownloadJSON {

    private String mJSON;

    public PostAndDownloadJSON(Fragment fragment, PostExecuteCallback postExecutCallback, String JSON) {
        super(fragment,postExecutCallback);
        mJSON = JSON;
    }

    @Override
    protected JSONTokener doInBackground(String... urls) {
        JSONParser parser = new JSONParser();
        try {
            JSONTokener jsonData = parser.postJSONFromUrl(urls[0], mJSON);
            return jsonData;
        } catch (Exception e) {
            mException = e;
            return null;
        }
    }
}
