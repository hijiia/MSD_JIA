#include "mainwidget.h"
#include <QSpacerItem>

mainWidget::mainWidget(QWidget *parent)
    : QWidget{parent}
{
    // – Create the widget objects

    firstName = new QLabel("First Name");
    lastName = new QLabel("Last Name");
    gender = new QLabel("gender");
    age = new QLabel("age");
    inputFN = new QLineEdit();
    inputLN = new QLineEdit();
    setAge = new QSpinBox();
    setAge->setMinimum(0);
    setAge->setMaximum(120);

    male = new QRadioButton("Male");
    female = new QRadioButton("Female");
    genderBox = new QGroupBox();

    refreshB = new QPushButton("Refresh");
    finishB = new QPushButton("Finish");

    summary = new QTextEdit();
    summary->setReadOnly(true);


    // – Choose and create a layout for your design
    gridLayout = new QGridLayout;
    vLayout = new QVBoxLayout;


    // – Set the window layout to the created layout
    setGridLayout();
    setVerticalLayout();
    setLayout(vLayout);

    // - Connect signals to slots
    connect (refreshB, &QPushButton::clicked, this, &mainWidget::fillSummary);
    connect(finishB, &QPushButton::clicked, this, &mainWidget::clearAll);

}


    void mainWidget::setGridLayout(){
    // - add labels to grid
    gridLayout->addWidget(firstName, 0, 0);
    gridLayout->addWidget(lastName, 1, 0);
    gridLayout->addWidget(gender, 2, 0);
     // - add input fields to grid
    gridLayout->addWidget(inputFN, 0, 1, 1, 2);
    gridLayout->addWidget(inputLN, 1, 1, 1, 2);

     // - add gender radio buttons in group box
    QVBoxLayout *genderLayout = new QVBoxLayout();
    genderLayout->addWidget(male);
    genderLayout->addWidget(female);
    genderBox->setLayout(genderLayout);
    gridLayout->addWidget(genderBox, 2, 1, 1, 2);

    // - Add age label and spin box
    gridLayout->addWidget(age, 0, 3);
    gridLayout->addWidget(setAge, 0, 4);

    // Add refresh button
    gridLayout->addWidget(refreshB, 3, 0);

    // Add spacing
    gridLayout->addItem(new QSpacerItem(50, 10), 0, 2, 1, 1);

    }

    void mainWidget::setVerticalLayout()
    {
        // Add grid layout to vertical layout
        vLayout->addLayout(gridLayout);

        // Add summary text edit
        vLayout->addWidget(summary);

        // Add finish button
        vLayout->addWidget(finishB);
    }

    void mainWidget::fillSummary () {
        QString summaryText = "First Name: " + inputFN->text() + "\n";
        summaryText += "Last Name: " + inputLN->text() + "\n";
        summaryText += "Age: " + QString::number(setAge->value()) + "\n";

        // Determine gender
        QString gender = "Not specified";
        if (male->isChecked()) {
            gender = "Male";
        } else if (female->isChecked()) {
            gender = "Female";
        }

        summaryText += "Gender: " + gender;
        summary->setText(summaryText);

    }




    void mainWidget::clearAll () {

         // Clear input fields
        inputFN->clear();
        inputLN->clear();
        setAge->setValue(0);

        // Clear radio
        // Disable exclusivity to uncheck both radio buttons
        male->setAutoExclusive(false);
        female->setAutoExclusive(false);

        male->setChecked(false);
        female->setChecked(false);

        male->setAutoExclusive(true);
        female->setAutoExclusive(true);

        // Clear summary
        summary->clear();

    }



    // QLabel *firstName;
    // QLabel *lastName;
    // QLineEdit *inputFN;
    // QLineEdit *inputLN;
    // QLabel *age;
    // QSpinBox *setAge;
    // QLabel *gender;
    // QRadioButton *female;
    // QRadioButton *male;
    // QPushButton *refreshB;
    // QTextEdit *text;
    // QPushButton *finishB;


