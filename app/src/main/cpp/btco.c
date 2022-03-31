//
// Created by Gerald Heng on 30/3/22.
//

#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <android/log.h>
#include "blockchain.h"

static const char *TAG = "BTCONATIVE";

//void chainMethod(int difficulty, char *message);
//void genesisMethod(char *difficulty, char *message);

//JNIEXPORT void JNICALL
JNIEXPORT jstring JNICALL
Java_edu_singaporetech_btco_BTCOActivity_chainMethod(JNIEnv *env,
                                                     jobject thiz,
                                                     jint difficulty,
                                                     jstring message,
                                                     jint blocks) {
    __android_log_print(ANDROID_LOG_INFO, TAG, "difficulty=%d message=%s",
                        difficulty,
                        (*env)->GetStringUTFChars(env, message, 0));

    BlockHeader *prevHeader = NULL;
    char *strMessage = (*env)->GetStringUTFChars(env, message, 0);

    int i;
    for (i = 0; i < blocks; i++) {
        BlockHeader currHeader = addBlockWithPrevPtr(prevHeader, strMessage, sizeof(strMessage) + 1,
                                                     difficulty);
        prevHeader = &currHeader;
    }

    char *hashString;
    BlockHeader header = *prevHeader;
    makeCStringFromBytes(header.dataHash, hashString,
                         sizeof(header.dataHash) / sizeof(uint8_t));

//    __android_log_print(ANDROID_LOG_INFO, TAG, "hash=%s",
//                        hashString);

    return (*env)->NewStringUTF(env, hashString);
}


JNIEXPORT jstring JNICALL
Java_edu_singaporetech_btco_BTCOActivity_genesisMethod(JNIEnv *env, jobject thiz,
                                                       jint difficulty) {
    const char data[] = "The Times 03/Jan/2009 Chancellor on brink of second bailout for banks";
    BlockHeader currHeader = addBlockWithPrevPtr(NULL, data, sizeof(data) + 1, difficulty);
    char *hashString;
    makeCStringFromBytes(currHeader.dataHash, hashString,
                         sizeof(currHeader.dataHash) / sizeof(uint8_t));

//    __android_log_print(ANDROID_LOG_INFO, TAG, "hash=%s",
//                        hashString);

    return (*env)->NewStringUTF(env, hashString);
}