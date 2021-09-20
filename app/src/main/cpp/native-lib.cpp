#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring

JNICALL
Java_com_minhhop_easygolf_framework_common_Contains_urlApiDevelopment(
        JNIEnv *env,
        jobject /* this */) {
    std::string url = "https://dev.api.easygolf.vn/";
    return env->NewStringUTF(url.c_str());
}
#ifdef __cplusplus
extern "C"{
#endif
jstring Java_com_minhhop_easygolf_framework_common_Contains_urlApiProduct(
        JNIEnv *env,
        jobject){
    std::string key = "https://api.easygolf.vn/";
    return env->NewStringUTF(key.c_str());
}
}

#ifdef __cplusplus
extern "C"{
#endif
jstring Java_com_minhhop_easygolf_framework_common_Contains_encryptionKeyRealm(
        JNIEnv *env,
        jobject){
    std::string key = "easygolf_realm_db1234567890123456789012345678900971237265478888";
    return env->NewStringUTF(key.c_str());
}
}


