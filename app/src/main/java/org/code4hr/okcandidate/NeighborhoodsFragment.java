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

import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class NeighborhoodsFragment extends Fragment implements CandidateFragment.OnFragmentInteractionListener {

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private OnFragmentInteractionListener mListener;

    public NeighborhoodsFragment() {
        // Required empty public constructor
    }

    public static NeighborhoodsFragment newInstance(String param1, String param2) {
        NeighborhoodsFragment fragment = new NeighborhoodsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_neighborhoods, container, false);
        String[] neihborhoods = getResources().getStringArray(R.array.neighborhoods);
        ArrayAdapter<String> neigborhoods_array_adapter = new ArrayAdapter<String>
                (getActivity(),R.layout.neighborhood_list_layout, neihborhoods);
        ListView neighborhood_listview = (ListView) view.findViewById(R.id.neighborhood_list);
        neighborhood_listview.setAdapter(neigborhoods_array_adapter);
        neighborhood_listview.setOnItemClickListener(new OnNeighborhoodClickListener());
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void openElection(String neighborhoodName) {
        FragmentTransaction tx =  getFragmentManager().beginTransaction();
        tx.replace(R.id.content_frame, ElectionsFragment.newInstance(neighborhoodName));
        tx.commit();
    }

    private class OnNeighborhoodClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TextView NeighborhoodText = (TextView) view.findViewById(R.id.neighborhood_text);
            openElection(NeighborhoodText.getText().toString());
        }
    }
}
