QT += core gui widgets

CONFIG += c++11

SOURCES += \
    main.cpp \
    mainwindow.cpp \
    ../msdLib/val.cpp \
    ../msdLib/expr.cpp \
    ../msdLib/env.cpp

HEADERS += \
    mainwindow.h \
    ../msdLib/val.h \
    ../msdLib/expr.h \
    ../msdLib/env.h
     ../msdLib/pointer.h

INCLUDEPATH += ../msdLib
