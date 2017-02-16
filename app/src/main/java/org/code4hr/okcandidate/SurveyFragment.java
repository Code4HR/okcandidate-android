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
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


public class SurveyFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    private int mSurveyId = 0;
    private JSONArray mSurveyQuestions = null;
    private int mSurveyQuestionIndex;
    private int mQuestionId;
    private String mNeighborhoodName;

    private class questionAnswer {
        private int mQuestionId;
        private int mAnswerId;
        private int mIntensity;
        private boolean mEmpty;

        //TODO: throw exceptions on all getters if empty

        public questionAnswer() {
            mEmpty = true;
        }

        public int questionId() {
            return mQuestionId;
        }

        public int answerId() {
            return mAnswerId;
        }

        public int intensity() {
            return mIntensity;
        }

        public void setAnswer( int questionId, int answerId, int intensity) {
            mQuestionId = questionId;
            mAnswerId = answerId;
            mIntensity = intensity;
            mEmpty = false;
        }

        public boolean ismEmpty() {
            return mEmpty;
        }

    }

    private questionAnswer mAnswers[];

    public SurveyFragment() {
        // Required empty public constructor
        mAnswers = null;
    }

    // TODO: Rename and change types and number of parameters
    public static SurveyFragment newInstance(int surveyId, String neighborhoodName) {
        SurveyFragment fragment = new SurveyFragment();
        fragment.mSurveyId = surveyId;
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_survey, container, false);

        Button skipButton = (Button)view.findViewById(R.id.skip_button);
        skipButton.setOnClickListener(this);

        Button previousButton = (Button)view.findViewById(R.id.prev_button);
        previousButton.setOnClickListener(this);

        Button nextButton = (Button)view.findViewById(R.id.next_button);
        nextButton.setOnClickListener(this);

        Button submitButton = (Button) view.findViewById(R.id.submit_button);
        submitButton.setOnClickListener(this);

        if(savedInstanceState == null) {
            new DownloadJSON(this, new DownloadSurvey()).execute(getString(R.string.api_url) + getString(R.string.survey_url) + "/" + Integer.toString(mSurveyId));
        }

        return view;
    }

    public void onClick(View v) {
        switch ( v.getId() ) {
            case R.id.skip_button:
                nextQuestion();
                break;
            case R.id.prev_button:
                previousQuestion();
                break;
            case R.id.next_button:
                saveQuestion();
                nextQuestion();
                break;
            case R.id.submit_button:
                saveQuestion();
                submitSurveyResponse();
                break;
            default:
                break;
        }
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

    private void submitSurveyResponse() {
        String JSON =
                "{" +
                    "\"surveyId\": " + mSurveyId + "," +
                    "\"geographyId\": 1," +
                    "\"userEmail\": null," +
                    "\"neighborhood\": \"" + mNeighborhoodName + "\"" +
                "}";
        new PostAndDownloadJSON(this, new DownloadSubmittedSurveyResponse(), JSON).execute(getString(R.string.api_url) + getString(R.string.survey_response_url));

    }

    private void submitAnswers( int surveyResponseId ) {
        String JSON =
               "{" +
                   "\"responses\": [";

        boolean first = true;
        for(int i = 0; i < mAnswers.length; i++) {
            if(!mAnswers[i].ismEmpty()) {
                if (!first) {
                    JSON += ",";
                }
                first = false;
                JSON += "{" +
                            "\"surveyResponseId\": " + surveyResponseId + "," +
                            "\"questionId\": " + Integer.toString(mAnswers[i].questionId()) + "," +
                            "\"answerId\": " + Integer.toString(mAnswers[i].answerId()) + "," +
                            "\"intensity\": "+ Integer.toString(mAnswers[i].intensity()) +
                        "}";
            }
        }
        JSON +=
                   "]" +
               "}";

        new PostAndDownloadJSON(this, new DownloadSubmittedAnswerResponse(), JSON).execute(getString(R.string.api_url) + getString(R.string.survey_answer_url));

    }

    private void setQuestions(JSONArray questions) {
        mSurveyQuestions = questions;
        mSurveyQuestionIndex = 0;
    }

    private void saveQuestion() {

        RadioGroup surveyAnswerRadioGroup = (RadioGroup) getView().findViewById(R.id.survey_answers_radio_group);
        int surveyAnswerRadioButtonId = surveyAnswerRadioGroup.getCheckedRadioButtonId();
        RadioButton surveyAnswerRadioButton  = (RadioButton) surveyAnswerRadioGroup.findViewById(surveyAnswerRadioButtonId);

        RatingBar ratingBar = (RatingBar)getView().findViewById(R.id.ratingBar);

        mAnswers[mSurveyQuestionIndex - 1].setAnswer(
                mQuestionId,
                ( surveyAnswerRadioButton != null )?Integer.parseInt(surveyAnswerRadioButton.getTag().toString()):0,
                (int) ratingBar.getRating() );
    }

    private void nextQuestion() {
        getQuestion(mSurveyQuestionIndex++);
    }

    private void previousQuestion() {
        getQuestion(--mSurveyQuestionIndex - 1);
    }

    private void getQuestion(int questionNumber) {
        if(mSurveyQuestions != null) {
            TextView questionTextView = (TextView) getView().findViewById(R.id.survey_question);
            RadioGroup surveyAnswerRadioGroup = (RadioGroup) getView().findViewById(R.id.survey_answers_radio_group);
            RadioGroup.LayoutParams surveyAnswerRadioGroupLayoutParams;
            try {
                JSONObject question = mSurveyQuestions.getJSONObject(questionNumber);
                mQuestionId = question.getInt("id");
                questionTextView.setText(question.getString("questionText"));

                surveyAnswerRadioGroup.clearCheck();
                surveyAnswerRadioGroup.removeAllViews();

                questionAnswer answer;
                if(mAnswers[questionNumber].ismEmpty()) {
                    answer = new questionAnswer();
                    answer.setAnswer(0,0,3);
                }
                else {
                    answer = mAnswers[questionNumber];
                }

                JSONArray answers = question.getJSONArray("answers");
                int selectedAnswerId = 0;
                for(int i = 0; i < answers.length(); i++) {
                    JSONObject JsonAnswer = answers.getJSONObject(i);
                    RadioButton answerRadio = new RadioButton(getActivity());
                    answerRadio.setText(JsonAnswer.getString("answerLabel"));
                    int answerId = JsonAnswer.getInt("id");
                    answerRadio.setId(answerId);
                    answerRadio.setTag(Integer.toString(answerId));
                    if(answerId == answer.answerId()) {
                        //answerRadio.setChecked(true);
                        selectedAnswerId = answerRadio.getId();
                    }
                    surveyAnswerRadioGroupLayoutParams = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    surveyAnswerRadioGroup.addView(answerRadio,surveyAnswerRadioGroupLayoutParams);
                }

                surveyAnswerRadioGroup.check(selectedAnswerId);

                RatingBar ratingBar = (RatingBar)getView().findViewById(R.id.ratingBar);
                ratingBar.setRating((float) answer.intensity());

                Button previousButton = (Button)getView().findViewById(R.id.prev_button);
                Button skipButton = (Button)getView().findViewById(R.id.skip_button);
                Button nextButton = (Button)getView().findViewById(R.id.next_button);

                if(mSurveyQuestionIndex == mSurveyQuestions.length()) {
                    skipButton.setEnabled(false);
                    nextButton.setEnabled(false);
                }
                else {
                    skipButton.setEnabled(true);
                    nextButton.setEnabled(true);
                }

                if(mSurveyQuestionIndex == 1) {

                    previousButton.setEnabled(false);
                }
                else {
                    previousButton.setEnabled(true);
                }

            } catch (JSONException e) {
                Log.e("DownloadJSON", "Exception found: " + e.toString());
                return;
            }
        }
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



    private class DownloadSurvey implements DownloadJSON.PostExecuteCallback {
        public void Callback(JSONTokener result, Fragment surveyFragment) {
            try {
                SurveyFragment lSurveyFragment = (SurveyFragment) surveyFragment;
                JSONObject election = (JSONObject) result.nextValue();
                JSONArray questions = (JSONArray) election.getJSONArray("questions");
                lSurveyFragment.mAnswers = new questionAnswer[questions.length()];
                for(int i = 0; i < questions.length(); i++) {
                    lSurveyFragment.mAnswers[i] = new questionAnswer();
                }
                lSurveyFragment.setQuestions(questions);
                lSurveyFragment.nextQuestion();
            }

            catch (JSONException e) {
                Log.e("DownloadSurvey", "Exception found: " + e.toString());
                return;
            }
        }
    }

    private class DownloadSubmittedSurveyResponse implements DownloadJSON.PostExecuteCallback {
        @Override
        public void Callback(JSONTokener result, Fragment fragment) {
            try {
                JSONObject response = (JSONObject) result.nextValue();
                int response_id = response.getInt("id");
                Log.d("SURVEY_RESPONSE","id: " + Integer.toString(response_id));
                ((SurveyFragment)fragment).submitAnswers(response_id);
            }
            catch (JSONException e) {
                Log.e("DownloadSurvey", "Exception found: " + e.toString());
                return;
            }
        }
    }

    private class DownloadSubmittedAnswerResponse implements DownloadJSON.PostExecuteCallback {
        @Override
        public void Callback(JSONTokener result, Fragment fragment) {
            try {
                int survey_id = 0;
                JSONArray responses = (JSONArray) result.nextValue();
                for(int i = 0; i < responses.length(); i++) {
                    JSONObject response = responses.getJSONObject(i);
                    int response_id = response.getInt("id");
                    survey_id = response.getInt("survey_response_id");
                    Log.d("ANSWER_RESPONSE", "id: " + Integer.toString(response_id));
                }

                SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor sharedPreferenceEditor = sharedPreferences.edit();
                sharedPreferenceEditor.putInt("survey_response_id", survey_id);
                sharedPreferenceEditor.commit();

                FragmentTransaction tx = getFragmentManager().beginTransaction();
                tx.replace(R.id.content_frame, CandidateFragment.newInstance(survey_id));
                tx.commit();
            }
            catch (JSONException e) {
                Log.e("DownloadSurvey", "Exception found: " + e.toString());
                return;
            }
        }
    }

}
