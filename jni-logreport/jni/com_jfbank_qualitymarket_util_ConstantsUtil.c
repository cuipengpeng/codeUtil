#include <string.h>
#include"com_jfbank_qualitymarket_util_ConstantsUtil.h"



JNIEXPORT jstring JNICALL Java_com_jfbank_qualitymarket_util_ConstantsUtil_sayHello(JNIEnv * env, jobject thiz)
{
    return (*env)->NewStringUTF(env, "Hello passwd !");
}
