#include "QTimerOneShot.hpp"

QTimerOneShot::QTimerOneShot(QObject* parent) : QThread(parent), _seconds(-1)
{
}

QTimerOneShot::~QTimerOneShot(void)
{
    // Nothing to do here ...
}

void QTimerOneShot::setInterval(int seconds)
{
    this->_seconds = seconds;
}

void QTimerOneShot::run(void)
{
    if (this->_seconds > 0)
    {
        QThread::sleep(this->_seconds);
        emit timeout();
    }
}
