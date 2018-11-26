QT += core multimedia
QT -= gui

QMAKE_CXXFLAGS += -std=gnu++11

TARGET = RTMPClient
CONFIG += console
CONFIG -= app_bundle

TEMPLATE = app

SOURCES += main.cpp \
    srs_librtmp.cpp

HEADERS += \
    srs_librtmp.h

