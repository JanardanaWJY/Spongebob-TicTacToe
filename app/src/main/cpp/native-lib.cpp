#include <jni.h>
#include <string>
#include <fcntl.h>
#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
#include <assert.h>
#include <jni.h>

#define TEXTLCD_LINE1 5
#define TEXTLCD_LINE2 6
#define TEXTLCD_CLEAR 4


extern "C" JNIEXPORT void JNICALL
Java_com_example_spongebobtictactoe_MainActivity_updateLED(JNIEnv* env, jobject, jint length) {
    char leds[8];
    for (int i = 0; i < 8; i++) {
        if (i < length) {
            leds[7 - i] = '1';
        } else {
            leds[7 - i] = '0';
        }
    }

    int file;
    unsigned char value = strtol(leds, NULL, 2);
    file = open("/dev/fpga_led", O_RDWR);
    if (file >= 0) {
        write(file, &value, 1);
        close(file);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_spongebobtictactoe_MainActivity_setLCD(JNIEnv* env, jobject, jstring line1, jstring line2) {
    const char *line1_str = env->GetStringUTFChars(line1, JNI_FALSE);
    const char *line2_str = env->GetStringUTFChars(line2, JNI_FALSE);

    int file = open("/dev/fpga_textlcd", O_WRONLY);
    if (file < 0) {
        perror("Failed to open /dev/fpga_textlcd");
        return;
    }

    ioctl(file, TEXTLCD_CLEAR);
    usleep(10000);

    ioctl(file, TEXTLCD_LINE1);
    write(file, line1_str, strlen(line1_str));

    ioctl(file, TEXTLCD_LINE2);
    write(file, line2_str, strlen(line2_str));

    close(file);

    env->ReleaseStringUTFChars(line1, line1_str);
    env->ReleaseStringUTFChars(line2, line2_str);
}
