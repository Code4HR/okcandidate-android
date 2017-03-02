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

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

import android.util.Log;
import android.widget.ListView;

public class ElectionsFragment extends Fragment implements OnElectionClickListener.ElectionsFragmentCallback {

    private OnFragmentInteractionListener mListener;
    private ArrayAdapterElectionItem mElectionsArray;
    private String mNeighborhoodName;

    public ElectionsFragment() {
        // Required empty public constructor
    }


    public static ElectionsFragment newInstance(String neighborhoodName) {
        ElectionsFragment fragment = new ElectionsFragment();
        fragment.mNeighborhoodName = neighborhoodName;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_elections, container, false);



        mElectionsArray = new ArrayAdapterElectionItem (getActivity(), R.layout.elections_list_layout, new ArrayList<ElectionItem>());
        ListView electionsList = (ListView) view.findViewById(R.id.electionsList);
        electionsList.setAdapter(mElectionsArray);

        OnElectionClickListener clickListener = new OnElectionClickListener();
        clickListener.setCallback(this);
        electionsList.setOnItemClickListener(clickListener);

        //download the JSON data
        new DownloadJSON(this, new DownloadElections()).execute(getString(R.string.api_url) + getString(R.string.survey_url));

        return view;
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    //Callback function of OnElectionClickListener to open SurveyFragment
    public void openSurvey(int id) {
        FragmentTransaction tx =  getFragmentManager().beginTransaction();
        tx.replace(R.id.content_frame, SurveyFragment.newInstance(id, mNeighborhoodName));
        tx.addToBackStack(null);
        tx.commit();
    }

    private class DownloadElections implements DownloadJSON.PostExecuteCallback {
        @Override
        public void Callback(JSONTokener result, Fragment fragment, Exception exception) {
            if(exception != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.error_title))
                        .setMessage(getString(R.string.error_message_connection))
                        .setPositiveButton(getString(R.string.ok_text), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getActivity().onBackPressed();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
                return;
            }

            try {
                JSONArray elections = (JSONArray) result.nextValue();
                for(int i = 0; i < elections.length(); i++) {
                    JSONObject election = elections.getJSONObject(i);
                    ((ElectionsFragment) fragment).mElectionsArray.add(
                            new ElectionItem(
                                    election.getInt("id"), election.getString("surveyName")
                            )
                    );
                }
            }

            catch (JSONException e) {
                Log.e("DownloadJSON", "Exception found: " + e.toString());
                return;
            }
        }
    }


}


