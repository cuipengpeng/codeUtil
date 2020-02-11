#ifndef _PTZCAMERA_BASIC_STRUCT_DEF_H_
#define _PTZCAMERA_BASIC_STRUCT_DEF_H_

#include <vector>
#include <string>
#include <bitset>
namespace xiaomo_ptz_camera {
namespace basic_vision {
//information of object classification
typedef struct ObjectClassificationInfo {
    std::vector<int> ids;
    std::vector<float> scores;
    std::vector<std::string> names;
    //int topN;
} ObjectClassificationInfo;

//information of scene classification
typedef struct SceneInfo {
    int id;
    float score;
    std::string name;
} SceneInfo;

//information of face
typedef struct SingleFaceInfo {
    float x, y, w, h;
    float face_detection_score;
    float facial_landmarks[10];
    float facial_features[128];
    float face_orientation[3];
    int gender;
    int emotion;
} SingleFaceInfo;
typedef std::vector<SingleFaceInfo> MultiFaceInfo;

//information of face_track
typedef struct MultiFaceTrackInfo {
    float x, y, w, h;          //left-top point and weight and height
    float score;
    float center_x;
    float center_y;         //the center point after smooth
    int face_id;            //the track id
    int islost;             //0 losted 1 tracked
} MultiFaceTrackInfo;
typedef std::vector<MultiFaceTrackInfo> MultiFaceTrackResult;

// information of object tracking
typedef struct ObjectTrackingInfo {
    float x, y, w, h;
    float object_tracking_score;
    float center_x;
    float center_y;
    int islost;
} ObjectTrackingInfo;

//information of object deteciton
typedef struct ObjectDetectionInfo {
    float x, y, w, h;
    float score;
    int cls;
} ObjectDetectionInfo;
typedef std::vector<ObjectDetectionInfo> ObjectDetectionResult;

//information of multi-object track
typedef struct MultiObjectTrackInfo {
    float x, y, w, h;                  //left-top point and weight and height
    float score;
    int cls;                        //1 cat 2 dog 3 person
    float center_x;
    float center_y;                 //the center point after smooth
    int object_id;                  //the track id
    int face_id;                    //not used
    int islost;                     //0 losted 1 tracked
} MultiObjectTrackInfo;
typedef std::vector<MultiObjectTrackInfo> MultiObjectTrackResult;

//information of video-structuring
typedef struct VideoDuration {
    long long start_time; // short video start time
    long long end_time; // short video end time
} VideoDuration;

typedef struct LabelSetInfo {
    std::vector<int> object_class;
    std::vector<int> scene_class;
    std::bitset<8> face_num; // 00000001: no face; 00000010: one face; 00000100: two face; 00001000: >= three face
    std::bitset<24> object_detect; // person(8) cat(8) dog(8), each same as face_num
    std::bitset<16> ori_face; // forward(8) profile(8), each same as face_num
    std::bitset<16> gender_class; // female male, each same as face_num
    std::bitset<28> emotion_class; // happy(4) surprise(4) angry(4) disgust(4) fear(4) sad(4) neutral(4)
} LabelSetInfo;

typedef struct SameLabelVideosInfo {
    VideoDuration videos_duration;
    LabelSetInfo label;
} SameLabelVideosInfo;
typedef std::vector<SameLabelVideosInfo> VideoStructResult;

typedef struct ImageBlurInfo {
    bool if_blur; // if true, current image is blur
    float score;
} ImageBlurInfo;

}  //end basic_vision

typedef enum PtzError
{
    SUCCESS = 0,
    NNIE_READWK_ERROR,
    NNIE_LOADMODEL_ERROR,
    NNIE_MALLOC_ERROR,
    NNIE_FORWARD_ERROR,
    NNIE_QUERY_ERROR,
    IVE_ERROR,
    ARGS_IMGEMPTY,
    ARGS_CHANNEL_MISMATCH,
    ARGS_OTHERS_ERROR,
    MEM_MALLOC_ERROR,
    OTHERS_ERROR
}PtzError;

typedef struct ImageInfo {
    basic_vision::ObjectClassificationInfo  object_class;             //image classification result
    basic_vision::SceneInfo                 scene_info;               //scene classification result
    basic_vision::MultiFaceInfo             face_info;                //face result(include detect,alignment,features,gender,emotion)
    basic_vision::MultiFaceTrackResult      face_track_res;           //face track result
    basic_vision::ObjectTrackingInfo        single_track_res;         //image track result(input the track box)
    basic_vision::ObjectDetectionResult     object_detect_res;        //object detect result
    basic_vision::MultiObjectTrackResult    multi_track_res;          //multi-object track result(cat dog person)
    basic_vision::VideoStructResult         video_struct_res;         //video structuring result(labels, starttimes, endtimes)
    basic_vision::ImageBlurInfo             blur_info;                //image is blur or not
} ImageInfo;

typedef struct ImageInfoFlag {
    int face_detect;
    int face_align;
    int face_ver;
    int face_gender;
    int face_emotion;
    int face_ori;
    int object_class;
    int scene_class;
    int single_track;
    int object_detect;
    int multi_track;
    int face_track;
    int video_struct;
    int image_blur;
} ImageInfoFlag;

} //end xiaomo_ptz_camera
#endif // _PTZCAMERA_BASIC_STRUCT_DEF_H_
