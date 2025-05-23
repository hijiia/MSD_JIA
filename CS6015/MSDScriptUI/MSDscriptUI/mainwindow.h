#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QmainWindow>
#include <QTextEdit>
#include <QPushButton>
#include <QRadioButton>
#include <QLabel>
#include <QVBoxLayout>
#include <QWidget>

class mainWindow : public QMainWindow
{
    Q_OBJECT

public:
    mainWindow(QWidget *parent = nullptr);
    ~mainWindow();

private slots:
    void handleSubmit();
    void handleReset();
    void handleLoadFile();

private:
    QTextEdit *inputTextEdit;
    QTextEdit *outputTextEdit;
    QRadioButton *interpRadio;
    QRadioButton *printRadio;
    QPushButton *submitButton;
    QPushButton *resetButton;
    QPushButton *loadFileButton;

};

#endif // MAINWINDOW_H
