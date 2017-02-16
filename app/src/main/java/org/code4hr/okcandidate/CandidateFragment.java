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
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;



public class CandidateFragment extends Fragment {
    private static final String ARG_SURVEY_ID = "survey_id";

    private int mSurveyId;

    private OnFragmentInteractionListener mListener;

    public CandidateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param surveyId The id for the survey
     * @return A new instance of fragment CandidateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CandidateFragment newInstance(int surveyId) {
        CandidateFragment fragment = new CandidateFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SURVEY_ID, surveyId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSurveyId = getArguments().getInt(ARG_SURVEY_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_candidate, container, false);

        if(savedInstanceState == null) {
            new DownloadJSON(this, new DownloadCandidateMatches()).execute(getString(R.string.api_url) + getString(R.string.candidate_match_url) + "/" + Integer.toString(mSurveyId));
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        ListView racesList = (ListView) getView().findViewById(R.id.races_list);
        if(racesList != null) {
            outState.putSerializable("racesList", ((RacesArrayAdapter) racesList.getAdapter()).mRaces);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            RacesItem[] races =  (RacesItem[])savedInstanceState.getSerializable("racesList");
            RacesArrayAdapter adapter = new RacesArrayAdapter(getActivity(), R.layout.races_list_layout, races);
            ListView listView = (ListView) getView().findViewById(R.id.races_list);
            if(listView != null) {
                listView.setAdapter(adapter);
            }
        }
        super.onViewStateRestored(savedInstanceState);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private class DownloadCandidateMatches implements DownloadJSON.PostExecuteCallback {
        @Override
        public void Callback(JSONTokener result, android.app.Fragment fragment) {

            String[] candidateNames = getResources().getStringArray(R.array.candidate_names);
            String[] candidateImages = getResources().getStringArray(R.array.candidate_images);
            Map<String, String> candidateImageMatch = new HashMap<String,String>();
            String imagePath = getString(R.string.candidate_image_path);
            for(int i = 0; i < candidateNames.length && i < candidateImages.length; i++) {
                candidateImageMatch.put(candidateNames[i], imagePath + candidateImages[i]);
            }

            try {
                JSONObject candidate_match = (JSONObject) result.nextValue();
                JSONArray race_array = (JSONArray) candidate_match.getJSONArray("survey");
                RacesItem[] races = new RacesItem[race_array.length()];
                for(int i = 0; i < race_array.length(); i++) {
                    JSONObject raceJSON = race_array.getJSONObject(i);
                    String raceName = raceJSON.getString("candidateTypeName");
                    JSONArray candidatesJSON = raceJSON.getJSONArray("candidates");
                    CandidateItem candidates[] = new CandidateItem[candidatesJSON.length()];
                    for (int j = 0; j < candidatesJSON.length(); j++) {
                        JSONObject candidate = candidatesJSON.getJSONObject(j);
                        String name = candidate.getString("candidateName");
                        String website = candidate.getString("candidateWebsite");
                        int match = candidate.getInt("compositeMatchScore");
                        candidates[j] = new CandidateItem(name, match, website, candidateImageMatch.get(name));
                    }
                    Arrays.sort(candidates);
                    races[i] = new RacesItem(raceName, candidates);
                }

                Arrays.sort(races);
                RacesArrayAdapter racesArrayAdapter = new RacesArrayAdapter(getActivity(), R.layout.races_list_layout, races);

                ListView races_list = (ListView) getView().findViewById(R.id.races_list);
                races_list.setAdapter(racesArrayAdapter);
            }
            catch (JSONException e) {
                Log.e("DownloadJSON", "Exception found: " + e.toString());
                return;
            }
        }
    }
}
