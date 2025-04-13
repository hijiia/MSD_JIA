#ifndef MAINWIDGET_H
#define MAINWIDGET_H

// QLabel, QLineEdit, QDialogButtonBox, QRadioButton, QPushButton, and QTextEdit objects
#include <QLabel>
#include <QLineEdit>
#include <QDialogButtonBox>
#include <QRadioButton>
#include <QTextEdit>
#include <QSpinBox>
#include <QWidget>
#include <QPushButton>
#include <QLayout>
#include <QGroupBox>
class mainWidget : public QWidget
{
    Q_OBJECT
public:
    explicit mainWidget(QWidget *parent = nullptr);

private:
    QLabel *firstName;
    QLabel *lastName;
    QLineEdit *inputFN;
    QLineEdit *inputLN;
    QLabel *age;
    QSpinBox *setAge;
    QLabel *gender;
    QRadioButton *female;
    QRadioButton *male;
    QGroupBox *genderBox;
    QPushButton *refreshB;
    QTextEdit *summary;
    QPushButton *finishB;
    QGridLayout *gridLayout;
    QVBoxLayout *vLayout;
    void setGridLayout();
    void setVerticalLayout();
    void fillSummary();
    void clearAll();

signals:
};

#endif // MAINWIDGET_H
