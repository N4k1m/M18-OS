#ifndef __MAINWINDOW_HPP__
#define __MAINWINDOW_HPP__

#include <QWidget>
#include <QFileDialog>
#include "ThreadServeur.hpp"

// Parser (configuration file)
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

    private slots:

        void setWidgetsEnable(bool serverRunning);
        void displayMessage(const QString& msg);

        // Auto-connect private slots
        void on_pushButtonStart_clicked(void);
        void on_pushButtonStop_clicked(void);

    private:

        Ui::MainWindow *ui;
        ThreadServeur* _threadServeur;
};

#endif // MAINWINDOW_HPP
