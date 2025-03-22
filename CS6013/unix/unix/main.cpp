#include "shelpers.hpp"
#include <iostream>
#include <unistd.h>
#include <sys/wait.h>
#include <fcntl.h> // For open()

int main() {
    std::string input;
    while (true) {
        std::cout << "myshell> ";
        if (!std::getline(std::cin, input)) {
            break;
        }
        if (input == "exit") {
            break; // exit
        }

        std::vector<std::string> tokens = tokenize(input);
        std::vector<Command> commands = getCommands(tokens);

        if (commands.empty()) {
            std::cerr << "Error: Invalid command\n";
            continue;
        }

        // cd
        if (commands[0].execName == "cd") {
            if (commands[0].argv.size() < 2) {
                std::cerr << "cd: missing argument\n";
            } else if (chdir(commands[0].argv[1]) != 0) {  // Fixed here
                perror("cd");
            }
            continue;
        }

        // fork
        pid_t pid = fork();
        if (pid == 0) {
            // Child process

            // Handle input/output redirection
            if (commands[0].inputFd != STDIN_FILENO) {
                if (dup2(commands[0].inputFd, STDIN_FILENO) == -1) {
                    perror("dup2 inputFd");
                    exit(1);
                }
                close(commands[0].inputFd); // Close the original file descriptor after redirection
            }
            if (commands[0].outputFd != STDOUT_FILENO) {
                if (dup2(commands[0].outputFd, STDOUT_FILENO) == -1) {
                    perror("dup2 outputFd");
                    exit(1);
                }
                close(commands[0].outputFd); // Close the original file descriptor after redirection
            }

            // Execute the command
            execvp(commands[0].execName.c_str(), const_cast<char* const*>(commands[0].argv.data()));
            perror("execvp"); // If execvp fails
            exit(1); // Exit if execvp fails
        } else if (pid > 0) {
            // Parent process

            // Handle background process execution
            if (!commands[0].background) {
                waitpid(pid, nullptr, 0); // Wait for the child process to finish
            }
        } else {
            perror("fork"); // Fork failed
        }
    }
    return 0;
}
