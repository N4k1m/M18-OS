#ifndef __MAINWINDOW_HPP__
#define __MAINWINDOW_HPP__

#include <QWidget>
#include <QFileDialog>
//#include "ThreadServer.hpp"       // Monothread
#include "ThreadServerPool.hpp"     // Multithreads

// Parser (default settings file)
#include "../Utils/Parser/IniParser.hpp"

namespace Ui
{
    class MainWindow;
}

class MainWindow : public QWidget
{
        Q_OBJECT

    public:

        explicit MainWindow(QWidget* parent = NULL);
        virtual ~MainWindow(void);

    private:

        void stopServer(void);

    private slots:

        void setWidgetsEnable(bool serverRunning);
        void displayMessage(const QString& msg);

        void threadServerStarted(void);
        void threadServerFinished(void);

        // Auto-connect private slots
        void on_pushButtonStart_clicked(void);
        void on_pushButtonStop_clicked(void);

    private:

        Ui::MainWindow *ui;
        //ThreadServer* _threadServeur;     // Monothread
        ThreadServerPool* _threadServeur;   // Multithreads
};

#endif /* __MAINWINDOW_HPP__ */
