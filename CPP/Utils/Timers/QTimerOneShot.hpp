#ifndef __QTIMERONESHOT_HPP__
#define __QTIMERONESHOT_HPP__

#include <QThread>

class QTimerOneShot : public QThread
{
    Q_OBJECT

    public:

        explicit QTimerOneShot(QObject* parent = NULL);
        virtual ~QTimerOneShot(void);

    public slots:

        void setInterval(int seconds);

    signals:

        void timeout(void);

    protected:

        void run(void);

    protected:

        int _seconds;
};

#endif /* __QTIMERONESHOT_HPP__ */
