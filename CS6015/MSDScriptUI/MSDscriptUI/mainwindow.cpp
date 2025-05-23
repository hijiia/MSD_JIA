#include "mainwindow.h"
#include "../msdLib/val.h"
#include "../msdLib/expr.h"

#include <QHBoxLayout>
#include <QGroupBox>
#include <QMessageBox>
#include <sstream>
#include <QFileDialog>
#include <QFile>

mainWindow::mainWindow(QWidget *parent)
    : QMainWindow(parent)
{
    QWidget *central = new QWidget(this);
    QVBoxLayout *mainLayout = new QVBoxLayout();

    // Input section
    QLabel *exprLabel = new QLabel("Expression:");
    inputTextEdit = new QTextEdit();
    mainLayout->addWidget(exprLabel);
    mainLayout->addWidget(inputTextEdit);

    // Radio buttons
    interpRadio = new QRadioButton("Interp");
    printRadio = new QRadioButton("Pretty Print");
    QHBoxLayout *radioLayout = new QHBoxLayout();
    radioLayout->addWidget(interpRadio);
    radioLayout->addWidget(printRadio);
    mainLayout->addLayout(radioLayout);

    // Submit button
    submitButton = new QPushButton("Submit");
    mainLayout->addWidget(submitButton);
    connect(submitButton, &QPushButton::clicked, this, &mainWindow::handleSubmit);

    // Result section
    QLabel *resultLabel = new QLabel("Result:");
    outputTextEdit = new QTextEdit();
    outputTextEdit->setReadOnly(true);
    mainLayout->addWidget(resultLabel);
    mainLayout->addWidget(outputTextEdit);

    // Reset button
    resetButton = new QPushButton("Reset");
    mainLayout->addWidget(resetButton);
    connect(resetButton, &QPushButton::clicked, this, &mainWindow::handleReset);

    central->setLayout(mainLayout);
    setCentralWidget(central);
    setWindowTitle("MSDscript UI");

    // load file
    loadFileButton = new QPushButton("Load From File");
    mainLayout->addWidget(loadFileButton);
    connect(loadFileButton, &QPushButton::clicked, this, &mainWindow::handleLoadFile);
}

mainWindow::~mainWindow() {}

void mainWindow::handleSubmit()
{
    std::string input = inputTextEdit->toPlainText().toStdString();
    try {
        PTR(Expr) expr = parse_str(input);
        std::ostringstream out;

        if (interpRadio->isChecked()) {
            PTR(Val) result = expr->interp(Env::empty);
            out << result->to_string();
        } else if (printRadio->isChecked()) {
            expr->pretty_print(out);
        } else {
            out << "Please select Interp or Pretty Print.";
        }

        outputTextEdit->setPlainText(QString::fromStdString(out.str()));
    } catch (std::exception& e) {
        outputTextEdit->setPlainText(QString("Error: ") + e.what());
    }
}

void mainWindow::handleReset()
{
    inputTextEdit->clear();
    outputTextEdit->clear();
    interpRadio->setAutoExclusive(false);
    printRadio->setAutoExclusive(false);
    interpRadio->setChecked(false);
    printRadio->setChecked(false);
    interpRadio->setAutoExclusive(true);
    printRadio->setAutoExclusive(true);
}
void mainWindow::handleLoadFile()
{
    QString fileName = QFileDialog::getOpenFileName(this, "Open Expression File", "", "Text Files (*.txt);;All Files (*)");
    if (fileName.isEmpty())
        return;

    QFile file(fileName);
    if (!file.open(QIODevice::ReadOnly | QIODevice::Text)) {
        QMessageBox::warning(this, "Error", "Could not open file");
        return;
    }

    QTextStream in(&file);
    QString contents = in.readAll();
    inputTextEdit->setPlainText(contents);
}
