#include <jni.h>
#include <algorithm>
#include "find_templete.h"
#include "android/log.h"
#define TAG  "########"
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)

#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_meetvr_aicamera_cameraclip_CameraClipActivity
 * Method:    getTemplete
 * Signature: ([Lcom/meetvr/aicamera/cameraclip/bean/VideoTag;)[I
 */
JNIEXPORT jintArray JNICALL Java_com_meetvr_aicamera_cameraclip_CameraClipActivity_getTemplete(JNIEnv * jenv, jobject thiz, jobjectArray tagArray){
        jsize count = jenv->GetArrayLength(tagArray);
        for(int i = 0; i < count; i++){
            jobject videoTag= jenv->GetObjectArrayElement(tagArray, i);
            jclass paramInClass = jenv->GetObjectClass(videoTag);
            if( paramInClass){
               jboolean iscopy;
        //       jfieldID intId = jenv->GetFieldID(paramInClass, "mInt", "I");
        //       jint num = (int)jenv->GetIntField(paramIn, intId);
        //       LOGD("num = %d", num);

                jfieldID strId = jenv->GetFieldID(paramInClass, "emotion", "Ljava/lang/String;");
                jstring str = (jstring)jenv->GetObjectField(videoTag, strId);
                const char *locstr = jenv->GetStringUTFChars(str, &iscopy);
                LOGD("str = %s", locstr);

                jfieldID strIddetect = jenv->GetFieldID(paramInClass, "object_detect", "Ljava/lang/String;");
                jstring strdetect = (jstring)jenv->GetObjectField(videoTag, strIddetect);
                const char *locstrabc = jenv->GetStringUTFChars(strdetect, &iscopy);
                LOGD("str = %s", locstrabc);

                 strId = jenv->GetFieldID(paramInClass, "face_num", "Ljava/lang/String;");
                str = (jstring)jenv->GetObjectField(videoTag, strId);
                locstr = jenv->GetStringUTFChars(str, &iscopy);
                LOGD("str = %s", locstr);
        //        jfieldID strId = jenv->GetFieldID(paramInClass, "ori_face", "Ljava/lang/String;");
        //        jstring str = (jstring)jenv->GetObjectField(videoTag, strId);
        //        locstr = jenv->GetStringUTFChars(str, &iscopy);
        //        LOGD("str = %s", locstr);
        //        jfieldID strId = jenv->GetFieldID(paramInClass, "gender_class", "Ljava/lang/String;");
        //        jstring str = (jstring)jenv->GetObjectField(videoTag, strId);
        //        locstr = jenv->GetStringUTFChars(str, &iscopy);
        //        LOGD("str = %s", locstr);
        //        jfieldID strId = jenv->GetFieldID(paramInClass, "object_class", "Ljava/lang/String;");
        //        jstring str = (jstring)jenv->GetObjectField(videoTag, strId);
        //        locstr = jenv->GetStringUTFChars(str, &iscopy);
        //        LOGD("str = %s", locstr);
        //        jfieldID strId = jenv->GetFieldID(paramInClass, "scene_class", "Ljava/lang/String;");
        //        jstring str = (jstring)jenv->GetObjectField(videoTag, strId);
        //        locstr = jenv->GetStringUTFChars(str, &iscopy);
        //        LOGD("str = %s", locstr);

        }

        //std::vector<xiaomo_ptz_camera::basic_vision::LabelSetInfo> lableSetInfo

        //jclass jclzMyObject = jenv->FindClass("com/meetvr/aicamera/cameraclip/bean/VideoTag");
        //jmethodID jmethodConstructID = jenv->GetMethodID(jclzMyObject, "<init>", "()V");
        ////进行实例创建
        //jobjectArray array = jenv->NewObjectArray(myObjects.length, jclzMyObject,NULL);
        //for(int i = 0; i < 2; i++){
        //    jobject jobjMyObj = jenv->NewObject(jclzMyObject, jmethodConstructID);
        //    jfieldID nameField = jenv->GetFieldID(jclzMyObject, "name","Ljava/lang/String;");
        //    jfieldID numberField = jenv->GetFieldID(jclzMyObject,, "number","I");
        //    jenv->SetObjectField(jobjMyObj, nameField, myObjects[i].name);
        //    jenv->SetIntField(jobjMyObj, numberField, myObjects[i].number);
        //    jenv->SetObjectArrayElement(array, i, jobjMyObj);
        //    jenv->DeleteLocalRef(jobjMyObj);
        //}
    }
    jintArray arr = jenv->NewIntArray(count);
    jint buf[count];
        for (int i = 0; i < count; ++i) {
            buf[i] = i * 2;
        }
    jenv->SetIntArrayRegion(arr, 0, count, buf);
    return arr;
  }

#ifdef __cplusplus
}
#endif

/*
 * sort the vector score, get the ID and score of topN 
 * @param score:input, the similarity of short videos and template
 * @param topN:input, get the video ID of the topN of the score
 * @param SingleResult:output, video ID and score of topN
*/

xiaomo_ptz_camera::PtzError getTopN(std::vector<float> &Score, int TopN, std::vector<int> &SingleResult) {
    int CopyTopN = TopN;
    SingleResult.clear();
    if (CopyTopN > Score.size()) {
        CopyTopN = Score.size();
    }
    std::vector<std::pair<int, float>> ScoreAndId;
    std::pair<int, float> OneScore;

    for (int i = 0; i < Score.size(); i++) {
        OneScore.first = i;
        OneScore.second = Score[i];
        ScoreAndId.push_back(OneScore);
    }

    // sort by score
    sort(ScoreAndId.begin(), ScoreAndId.end(), [](std::pair<int, float> p1, std::pair<int, float> p2){return p1.second > p2.second;});
    
    // get topN
    for (int i = 0; i < CopyTopN; i++) {
        SingleResult.push_back(ScoreAndId[i].first);
    }
    return xiaomo_ptz_camera::SUCCESS;
    
}

// statistic two vector same number
int findSame(const std::vector<int> &Left, const std::vector<int> &Right) {
    int SameNum = 0;
    for (std::vector<int>::const_iterator Iterator = Left.begin(); Iterator != Left.end(); Iterator++) {
        if (std::find(Right.begin(), Right.end(), *Iterator) != Right.end()) {
            SameNum++;
        }        
    }
    return SameNum;
}

// similarity of one labelInfo and one template labelInfo
float singleTemScore(const xiaomo_ptz_camera::basic_vision::LabelSetInfo &VideoLabel, 
                    xiaomo_ptz_camera::basic_vision::LabelSetInfo &TemplateLabel) {
    float Score;
    int ObjectClassSame = findSame(VideoLabel.object_class, TemplateLabel.object_class);
    int SceneClassSame = findSame(VideoLabel.scene_class, TemplateLabel.scene_class);

    int TemCount = VideoLabel.emotion_class.count() + VideoLabel.face_num.count() + VideoLabel.gender_class.count()
                    + VideoLabel.object_class.size() + VideoLabel.object_detect.count() + VideoLabel.ori_face.count()
                    + VideoLabel.scene_class.size();
    
    int ComCount = (VideoLabel.emotion_class & TemplateLabel.emotion_class).count() +
                    (VideoLabel.face_num & TemplateLabel.face_num).count() +
                    (VideoLabel.gender_class & TemplateLabel.gender_class).count() + ObjectClassSame +
                    (VideoLabel.object_detect & TemplateLabel.object_detect).count() +
                    (VideoLabel.ori_face & TemplateLabel.ori_face).count() + SceneClassSame;

    Score = (float)ComCount / TemCount;

    return Score;
}

// similarity of one labelInfo and template labelInfo list
xiaomo_ptz_camera::PtzError temLsitScores(const xiaomo_ptz_camera::basic_vision::LabelSetInfo &VideoLabel, std::vector<xiaomo_ptz_camera::basic_vision::LabelSetInfo> &TemplateLabels, 
                         int TopN, std::vector<int> &TopNId) {
    TopNId.clear();
    std::vector<float> VScores;
    VScores.clear();

    for (int i = 0; i < TemplateLabels.size(); i++) {
        float Score = singleTemScore(VideoLabel, TemplateLabels[i]);
        VScores.push_back(Score);
    }

    // get score topN id
    getTopN(VScores, TopN, TopNId);

    return xiaomo_ptz_camera::SUCCESS;
}

/*
 * videos labels vector and template labels vector
 * @param ShortVideos, input, the structuring videos result
 * @param TemplateLabels, input 
 * @param topN, input 
 * @param HighLightIdAndScore, output, every template have not more than topN videos group
*/
xiaomo_ptz_camera::PtzError similarityScore(const std::vector<xiaomo_ptz_camera::basic_vision::LabelSetInfo> &VideosLabels, std::vector<xiaomo_ptz_camera::basic_vision::LabelSetInfo> &TemplateLabels, 
                         int TopN, std::vector<std::vector<int>> &HighLightId) {
    HighLightId.clear();
    
    for (int i = 0; i < VideosLabels.size(); i++) {
        std::vector<int> TopNId;
        TopNId.clear();
        temLsitScores(VideosLabels[i], TemplateLabels, TopN, TopNId);
        HighLightId.push_back(TopNId);
    }

    return xiaomo_ptz_camera::SUCCESS;
}