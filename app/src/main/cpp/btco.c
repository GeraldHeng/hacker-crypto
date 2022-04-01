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

//JNIEXPORT void JNICALL
JNIEXPORT jstring JNICALL
Java_edu_singaporetech_btco_BTCOActivity_chainMethod(JNIEnv *env,
                                                     jobject thiz,
                                                     jint difficulty,
                                                     jstring message,
                                                     jint blocks) {
    // Log difficulty and message.
    __android_log_print(ANDROID_LOG_INFO, TAG, "difficulty=%d message=\"%s\"",
                        difficulty,
                        (*env)->GetStringUTFChars(env, message, 0));

    // Set values.
    BlockHeader *prevHeader = NULL;
    char *strMessage = (*env)->GetStringUTFChars(env, message, 0);
    int i;

    // Create chained blocks. Blocks are chained by prevHeader.
    for (i = 0; i < blocks; i++) {
        BlockHeader currHeader = addBlockWithPrevPtr(prevHeader, strMessage, sizeof(strMessage) + 1,
                                                     difficulty);
        __android_log_print(ANDROID_LOG_INFO, TAG, "Created block with timestamp=%u nonce=%d",
                            currHeader.timestamp, currHeader.nonce);
        prevHeader = &currHeader;
    }

    // Hash to string.
    char hashString[HASH_LEN * 2 + 1];
    BlockHeader header = *prevHeader;

    makeCStringFromBytes(header.dataHash, hashString, HASH_LEN);

    return (*env)->NewStringUTF(env, hashString);
}


JNIEXPORT jstring JNICALL
Java_edu_singaporetech_btco_BTCOActivity_genesisMethod(JNIEnv *env, jobject thiz,
                                                       jint difficulty) {
    // Block creation.
    const char data[] = "The Times 03/Jan/2009 Chancellor on brink of second bailout for banks";
    BlockHeader currHeader = addBlockWithPrevPtr(NULL, data, sizeof(data), difficulty);
    __android_log_print(ANDROID_LOG_INFO, TAG, "Created block with timestamp=%u nonce=%d",
                        currHeader.timestamp, currHeader.nonce);

    // Hash conversion to string.
    char hashString[HASH_LEN * 2 + 1];
    makeCStringFromBytes(currHeader.dataHash, hashString, HASH_LEN);
    return (*env)->NewStringUTF(env, hashString);
}