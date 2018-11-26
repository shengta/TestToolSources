#include "srs_librtmp.h"
#include "stdio.h"
#include "stdlib.h"
#include <chrono>
#include <thread>
#include <iostream>
#include <QCoreApplication>
#include <QImage>
#include <QDir>

int currentClientCount = 0;

void incrementer(int milliseconds) {
    while(true) {
        currentClientCount++;
        usleep(milliseconds*1000);
    }
}

void saveFrame(QImage &img) {
    int64_t now = std::chrono::duration_cast<std::chrono::milliseconds>(std::chrono::system_clock::now().time_since_epoch()).count();
    QString fileName = QDir::homePath() + QString("/test/") + QString::number(now) + QString(".png");
    img.save(fileName, "png", 100);
}

int main(int argc, char *argv[])
{
    int size;
    char type;
    char* data = new char[100000];
    u_int32_t timestamp, pts;
    srs_rtmp_t rtmp;

    //printf("%s %s %s\n", argv[1], argv[2], argv[3]);

    int clientCount, rampUp;
    rtmp = srs_rtmp_create(argv[1]);

    clientCount = atoi(argv[2]);
    rampUp = atoi(argv[3]);

    int incPeriod = rampUp * 1000 / clientCount;

    std::thread counter(incrementer, incPeriod);
    counter.detach();

    if (srs_rtmp_handshake(rtmp) == 0) {
        //srs_human_trace("simple handshake success");

        if (srs_rtmp_connect_app(rtmp) == 0) {
            //srs_human_trace("connect vhost/app success");
            if (srs_rtmp_play_stream(rtmp) == 0) {
                //srs_human_trace("play stream success");
                int64_t t0 = std::chrono::duration_cast<std::chrono::milliseconds>(std::chrono::system_clock::now().time_since_epoch()).count();
                printf("Time,Type,Frame Type,DTS,PTS,Size,TotalSize(25f),DTS(25f),Time(25f),Client Count\n");
                int counter = 0;
                int totalSize = 0;
                int tempDTS = 0;
                int64_t tempTime = t0;
                while(true) {
                    srs_rtmp_read_packet(rtmp, &type, &timestamp, &data, &size);
                    srs_utils_parse_timestamp(timestamp, type, data, size, &pts);

                    totalSize += size;
                    //printf("%d %d  ",i,totalSize);

                    if(strcmp(srs_human_flv_tag_type2string(type), "Video") == 0) {
                        char ft = srs_utils_flv_video_frame_type(data, size);
                        int64_t t1 = std::chrono::duration_cast<std::chrono::milliseconds>(std::chrono::system_clock::now().time_since_epoch()).count();
                        printf("%d,%s,%s,%d,%d,%d\n", t1-t0,srs_human_flv_tag_type2string(type), srs_human_flv_video_frame_type2string(ft),
                               timestamp, pts, size);

                        if(counter++ == 24)
                        {
                            printf("-,-,-,-,-,-,%d,%d,%d,%d\n",totalSize,timestamp-tempDTS,t1-tempTime,currentClientCount);
                            totalSize = 0;
                            tempDTS = timestamp;
                            tempTime = t1;
                            counter = 0;
                        }
                    }

                    /*
                    if(strcmp(srs_human_flv_tag_type2string(type), "Video") == 0) {
                        char t = srs_utils_flv_video_avc_packet_type(data, size);
                        char codec = srs_utils_flv_video_codec_id(data, size);


                        srs_human_trace("avc packet type=%s codec=%s\n",
                                       srs_human_flv_video_avc_packet_type2string(t),
                                       srs_human_flv_video_codec_id2string(codec));

                        //QImage img((uchar*)data, 640, 480, QImage::Format_ARGB32 );
                        //saveFrame(img);
                    }
                    */

                }
            }
            else {
                //srs_human_trace("play stream failed.");
            }
        }
        else {
            //srs_human_trace("connect vhost/app failed.");
        }
    }
    else {
        //srs_human_trace("simple handshake failed.");
    }


    free(data);
    srs_rtmp_destroy(rtmp);

    return 0;
}

