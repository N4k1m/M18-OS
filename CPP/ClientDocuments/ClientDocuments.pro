#-------------------------------------------------
#
# Project created by QtCreator 2014-11-10T18:50:19
#
#-------------------------------------------------

QT       += core gui

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

TARGET = ClientDocuments
TEMPLATE = app

CONFIG += c++11

SOURCES += main.cpp\
        Widget.cpp \
    ../Utils/Sockets/Socket.cpp \
    ../Utils/Sockets/SocketException.cpp \
    ../Utils/Sockets/SocketServer.cpp \
    ../Utils/Sockets/TCPSocketClient.cpp \
    ../Utils/Sockets/TCPSocketServer.cpp \
    ../Utils/GDOCP/GDOCP.cpp \
    ../Utils/Hash/Hash.cpp \
    ../Utils/Hash/RandomPrimeGenerator.cpp \
    ../Utils/Parser/IniParser.cpp \
    ../Utils/Exceptions/Exception.cpp \
    ThreadAdmin.cpp

HEADERS  += Widget.hpp \
    ../Utils/Sockets/Socket.hpp \
    ../Utils/Sockets/SocketException.hpp \
    ../Utils/Sockets/SocketServer.hpp \
    ../Utils/Sockets/TCPSocketClient.hpp \
    ../Utils/Sockets/TCPSocketServer.hpp \
    ../Utils/GDOCP/GDOCP.hpp \
    ../Utils/Hash/Hash.hpp \
    ../Utils/Hash/RandomPrimeGenerator.hpp \
    ../Utils/Parser/IniParser.hpp \
    ../Utils/Exceptions/Exception.hpp \
    ThreadAdmin.hpp \
    ../Utils/AGDOCP/AGDOCProtocol.hpp

FORMS    += Widget.ui

DISTFILES += \
    client_documents.conf
