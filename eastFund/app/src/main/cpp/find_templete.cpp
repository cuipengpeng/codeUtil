#include <jni.h>
#include <algorithm>
#include "find_templete.h"
#include "android/log.h"
#define TAG  "########"
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)


/*
 * sort the vector score, get the ID and score of topN
 * @param score:input, the similarity of short videos and template
 * @param topN:input, get the video ID of the topN of the score
 * @param SingleResult:output, video ID and score of topN
*/
//int getTopN(std::vector<float> &Score, int TopN, std::vector<int> &SingleResult) {
//  SingleResult[i].first means ID, SingleResult[i].second means score
int getTopN(std::vector<float> &Score, int TopN, std::vector<std::pair<int, float>> &SingleResult) {
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
        SingleResult.push_back(ScoreAndId[i]);
    }
    return 0;

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
int temLsitScores(const xiaomo_ptz_camera::basic_vision::LabelSetInfo &VideoLabel, std::vector<xiaomo_ptz_camera::basic_vision::LabelSetInfo> &TemplateLabels,
                         int TopN, std::vector<std::pair<int, float>> &TopNId) {
    TopNId.clear();
    std::vector<float> VScores;
    VScores.clear();

    for (int i = 0; i < TemplateLabels.size(); i++) {
        float Score = singleTemScore(VideoLabel, TemplateLabels[i]);
        VScores.push_back(Score);
    }

    // get score topN id
    getTopN(VScores, TopN, TopNId);

    return 0;
}

/*
 * videos labels vector and template labels vector
 * @param ShortVideos, input, the structuring videos result
 * @param TemplateLabels, input
 * @param topN, input
 * @param HighLightIdAndScore, output, every template have not more than topN videos group
*/
int similarityScore(const std::vector<xiaomo_ptz_camera::basic_vision::LabelSetInfo> &VideosLabels, std::vector<xiaomo_ptz_camera::basic_vision::LabelSetInfo> &TemplateLabels,
                         int TopN, std::vector<std::vector<std::pair<int, float>> > &HighLightId) {
    HighLightId.clear();

    for (int i = 0; i < VideosLabels.size(); i++) {
        std::vector<std::pair<int, float>> TopNId;
        TopNId.clear();
        temLsitScores(VideosLabels[i], TemplateLabels, TopN, TopNId);
        HighLightId.push_back(TopNId);
    }

    return 0;
}

int getId(const std::vector<std::vector<std::pair<int, float>> > &HighLightScoreId, std::vector<std::vector<int> > &HighLightId){
    HighLightId.clear();
    for (int i = 0; i < HighLightScoreId.size(); i++) {
        std::vector<int> IdResult;
        IdResult.clear();

        for (int j = 0; j < HighLightScoreId[i].size(); j++) {
            IdResult.push_back(HighLightScoreId[i][j].first);
        }

        HighLightId.push_back(IdResult);
    }

    return 0;
}



#ifdef __cplusplus
extern "C" {
#endif

char*   Jstring2CStr(JNIEnv*   env,   jstring   jstr)
{
     char*   rtn   =   NULL;
     jclass   clsstring   =   env->FindClass("java/lang/String");
     jstring   strencode   =   env->NewStringUTF("GB2312");
     jmethodID   mid   =   env->GetMethodID(clsstring,   "getBytes",   "(Ljava/lang/String;)[B");
     jbyteArray   barr=   (jbyteArray)env->CallObjectMethod(jstr,mid,strencode); // String .getByte("GB2312");
     jsize   alen   =   env->GetArrayLength(barr);
     jbyte*   ba   =   env->GetByteArrayElements(barr,JNI_FALSE);
     if(alen   >   0)
     {
      rtn   =   (char*)malloc(alen+1);         //"\0"
      memcpy(rtn,ba,alen);
      rtn[alen]='\0';
     }
     env->ReleaseByteArrayElements(barr,ba,0);  //
      env->DeleteLocalRef(clsstring);
      env->DeleteLocalRef(strencode);
     return rtn;
}

#ifdef __cplusplus
}
#endif



int convertLabelSetInfoModel(JNIEnv * jenv,  jobjectArray videoLableArray, std::vector<xiaomo_ptz_camera::basic_vision::LabelSetInfo> &lableSetInfoVector) {
    jsize count = jenv->GetArrayLength(videoLableArray);
    for(int i = 0; i < count; i++){
                xiaomo_ptz_camera::basic_vision::LabelSetInfo lableSetInfo;
                jobject videoTag= jenv->GetObjectArrayElement(videoLableArray, i);
                jclass paramInClass = jenv->GetObjectClass(videoTag);
                if(paramInClass){
                       jboolean iscopy;
                     //  jfieldID intId = jenv->GetFieldID(paramInClass, "respon", "I");
                     //  jint num = (int)jenv->GetIntField(videoTag, intId);
                     //  LOGD("respon = %d", num);

                    jfieldID strId = jenv->GetFieldID(paramInClass, "emotion", "Ljava/lang/String;");
                    jstring str = (jstring)jenv->GetObjectField(videoTag, strId);
                    const char *locstr = jenv->GetStringUTFChars(str, &iscopy);
                    LOGD("emotion = %s", locstr);
//                    std::bitset<28> bit1(atoi(locstr));
  //                  lableSetInfo.emotion_class = bit1;
                    lableSetInfo.emotion_class = atoi(locstr);
                    jenv->ReleaseStringUTFChars(str,locstr);


                     strId = jenv->GetFieldID(paramInClass, "object_detect", "Ljava/lang/String;");
                     str = (jstring)jenv->GetObjectField(videoTag, strId);
                    locstr = jenv->GetStringUTFChars(str, &iscopy);
                    LOGD("object_detect = %s", locstr);
                    lableSetInfo.object_detect = atoi(locstr);
                    jenv->ReleaseStringUTFChars(str,locstr);

                     strId = jenv->GetFieldID(paramInClass, "face_num", "Ljava/lang/String;");
                    str = (jstring)jenv->GetObjectField(videoTag, strId);
                    locstr = jenv->GetStringUTFChars(str, &iscopy);
                   LOGD("face_num = %s", locstr);
                    lableSetInfo.face_num = atoi(locstr);
                     // LOGD("face_num 11str = %s", lableSetInfo.face_num.to_string().c_str());
                      jenv->ReleaseStringUTFChars(str,locstr);

                     strId = jenv->GetFieldID(paramInClass, "ori_face", "Ljava/lang/String;");
                     str = (jstring)jenv->GetObjectField(videoTag, strId);
                    locstr = jenv->GetStringUTFChars(str, &iscopy);
                    LOGD("ori_face = %s", locstr);
                    lableSetInfo.ori_face = atoi(locstr);
                    //LOGD("ori_face 11str = %s", lableSetInfo.ori_face.to_string().c_str());
                    jenv->ReleaseStringUTFChars(str,locstr);

                     strId = jenv->GetFieldID(paramInClass, "gender_class", "Ljava/lang/String;");
                     str = (jstring)jenv->GetObjectField(videoTag, strId);
                    locstr = jenv->GetStringUTFChars(str, &iscopy);
                    LOGD("gender_class = %s", locstr);
                    lableSetInfo.gender_class = atoi(locstr);
                    jenv->ReleaseStringUTFChars(str,locstr);

                     strId = jenv->GetFieldID(paramInClass, "object_class", "Ljava/lang/String;");
                     str = (jstring)jenv->GetObjectField(videoTag, strId);
                    locstr = jenv->GetStringUTFChars(str, &iscopy);
                    LOGD("object_class = %s", locstr);
                     //char object_class[] = "11,55,66";
                     //object_classPointer = strtok(object_class, ",");
                     char *object_classPointer = Jstring2CStr(jenv, str);
                    LOGD("object_classPointer = %s", object_classPointer);
                     object_classPointer = strtok(object_classPointer, ",");
                     while(object_classPointer)
                     {
                          LOGD("str11 = %s", object_classPointer);
                           int a = atoi(object_classPointer);
                          LOGD("str22 = %d", a);
                          object_classPointer = strtok(NULL, ",");
                          lableSetInfo.object_class.push_back(a);
                     }
                     //jenv->ReleaseStringUTFChars(str,object_classPointer);

                     str = (jstring)jenv->GetObjectField(videoTag, strId);
                     strId = jenv->GetFieldID(paramInClass, "scene_class", "Ljava/lang/String;");
                    locstr = jenv->GetStringUTFChars(str, &iscopy);
                    LOGD("scene_class = %s", locstr);
                   //char scene_class[] = "777,888,999";
                   //char *scene_classPointer;
                   //scene_classPointer = strtok(scene_class, ",");
                   char *scene_classPointer = Jstring2CStr(jenv, str);
                   LOGD("scene_classPointer = %s", scene_classPointer);
                    //scene_classPointer = strtok(object_class, ",");
                    scene_classPointer = strtok(scene_classPointer, ",");
                   while(scene_classPointer)
                   {
                        LOGD("str777 = %s", scene_classPointer);
                         int a = atoi(scene_classPointer);
                        LOGD("str777 = %d", a);
                        scene_classPointer = strtok(NULL, ",");
                        lableSetInfo.scene_class.push_back(a);
                   }
                   //jenv->ReleaseStringUTFChars(str,scene_classPointer);

                }
                lableSetInfoVector.push_back(lableSetInfo);
                jenv->DeleteLocalRef(videoTag);
                jenv->DeleteLocalRef(paramInClass);
            }
    return 0;
}


unsigned int bin2int (std::string strBin)
{
    unsigned int i = 0;
    const char *pch = strBin.c_str();
    while (*pch == '0' || *pch == '1') {
      i <<= 1;
      i |= *pch++ - '0';
    }
    return i;
}


#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_test_bank_view_activity_MainActivity
 * Method:    getTemplete
 * Signature: ([Lcom/test/bank/view/activity/VideoTag;)[I
 jintArray
 */
JNIEXPORT jobjectArray JNICALL Java_com_test_bank_view_activity_MainActivity_getTemplete
(JNIEnv * jenv, jobject thiz, jobjectArray videoLableArray, jobjectArray templeteLableArray){

        std::vector<xiaomo_ptz_camera::basic_vision::LabelSetInfo> lableSetInfoVector;
        jsize videoCount = jenv->GetArrayLength(videoLableArray);
        convertLabelSetInfoModel(jenv, videoLableArray, lableSetInfoVector);


        std::vector<xiaomo_ptz_camera::basic_vision::LabelSetInfo> templateLabelsVector;
        jsize templeteCount = jenv->GetArrayLength(videoLableArray);
        convertLabelSetInfoModel(jenv, templeteLableArray, templateLabelsVector);
        //std::vector<std::vector<int>> HighLightId;
        std::vector<std::vector<std::pair<int, float>> > HighLightId;


        similarityScore(lableSetInfoVector, templateLabelsVector, 2, HighLightId);


        jclass videoTagClass = jenv->FindClass("com/test/bank/bean/TempleteScoreBean");
        jmethodID jmethodConstructID = jenv->GetMethodID(videoTagClass, "<init>", "()V");
        jfieldID score = jenv->GetFieldID(videoTagClass, "score","F");
        jfieldID code = jenv->GetFieldID(videoTagClass, "templetePosition","I");
        jfieldID numberField = jenv->GetFieldID(videoTagClass, "scene_class","Ljava/lang/String;");

      //jobjectArray array = jenv->NewObjectArray(HighLightId.size(), videoTagClass,NULL);
      jobjectArray array = jenv->NewObjectArray(5, videoTagClass,NULL);
      //std::pair<int, float> OneScore;
      //for (int i = 0; i < HighLightId.size(); i++) {
      for (int i = 0; i < 5; i++) {
           // OneScore = HighLightId[i][0];
          jobject videoTagObj = jenv->NewObject(videoTagClass, jmethodConstructID);
          jenv->SetObjectField(videoTagObj, numberField, jenv->NewStringUTF("654321"));
          //jenv->SetIntField(videoTagObj, code, OneScore.first);
          //jenv->SetFloatField(videoTagObj, score, OneScore.second );
          jenv->SetIntField(videoTagObj, code, i);
          jenv->SetFloatField(videoTagObj, score, 56.123);
          jenv->SetObjectArrayElement(array, i, videoTagObj);
          jenv->DeleteLocalRef(videoTagObj);
      }

        ////进行实例创建
      // jobjectArray array = jenv->NewObjectArray(videoCount, videoTagClass,NULL);
      // for(int i = 0; i < videoCount; i++){
      //     jobject videoTagObj = jenv->NewObject(videoTagClass, jmethodConstructID);
      //     jenv->SetObjectField(videoTagObj, numberField, jenv->NewStringUTF("654321"));
      //     jenv->SetIntField(videoTagObj, code, 222);
      //     jenv->SetFloatField(videoTagObj, score, 111.123);
      //     jenv->SetObjectArrayElement(array, i, videoTagObj);
      //     jenv->DeleteLocalRef(videoTagObj);
      // }
        return array;

//   jintArray arr = jenv->NewIntArray(count);
//   jint buf[count];
//   for (int i = 0; i < count; ++i) {
//       buf[i] = i * 2;
//   }
//   jenv->SetIntArrayRegion(arr, 0, count, buf);
//   return arr;
  }
#ifdef __cplusplus
}
#endif


