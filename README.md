# OKCandidate Android

OKCandidate Android is an Android app that interacts with the OKCandidate api (https://github.com/Code4HR/okcandidate).

##License
OKCandidate Android is licensed under the Apache 2.0 License.  A copy of the full license may be found in the `LICENSE` file or at http://www.apache.org/licenses/LICENSE-2.0

##Installation
1. Clone the github repository
2. Open folder in Android Studio or favorite Android development enviornment.

##Configuration
1. Edit `app/src/main/res/values/strings.xml`
 1. Change `api_url` to the correct url for your api
 2. Change `candidate_image_path` to the correct path for your images
 3. Make any other changes as neccessary.
2. Edit `app/src/main/res/values/candidate_images.xml` with the correct candidate names and associated images
3. Edit `app/src/main/res/values/neighborhood_list.xml` with all neighborhoods your build targets
