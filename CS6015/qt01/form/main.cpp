#include <QApplication>
#include <mainwidget.h>

int main(int argc, char *argv[])
{
    QApplication app(argc, argv);
    mainWidget window;
    window.setWindowTitle("User Information Form");
    window.resize(600, 400);
    window.show();
    return app.exec();
}
