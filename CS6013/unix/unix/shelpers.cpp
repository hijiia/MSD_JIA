#include "shelpers.hpp"
#include <iostream>
#include <vector>
#include <string>
#include <unistd.h>
#include <sys/wait.h>
#include <fcntl.h>
#include <cassert>
#include <cstring>

using namespace std;

// Splits a token on a symbol (>, <, |, &)
bool splitOnSymbol(vector<string>& words, int i, char c) {
    if (words[i].size() < 2) {
        return false;
    }
    int pos;
    if ((pos = words[i].find(c)) != string::npos) {
        if (pos == 0) {
            // Starts with symbol.
            words.insert(words.begin() + i + 1, words[i].substr(1, words[i].size() - 1));
            words[i] = words[i].substr(0, 1);
        } else {
            // Symbol in middle or end.
            words.insert(words.begin() + i + 1, std::string(1, c));
            string after = words[i].substr(pos + 1, words[i].size() - pos - 1);
            if (!after.empty()) {
                words.insert(words.begin() + i + 2, after);
            }
            words[i] = words[i].substr(0, pos);
        }
        return true;
    }
    return false;
}

// Tokenizes a string into a vector of strings.
vector<string> tokenize(const string& s) {
    vector<string> ret;
    int pos = 0;
    int space;
    // Split on spaces:
    while ((space = s.find(' ', pos)) != string::npos) {
        string word = s.substr(pos, space - pos);
        if (!word.empty()) {
            ret.push_back(word);
        }
        pos = space + 1;
    }
    string lastWord = s.substr(pos, s.size() - pos);
    if (!lastWord.empty()) {
        ret.push_back(lastWord);
    }
    // Check for special symbols after tokenizing
    for (int i = 0; i < ret.size(); ++i) {
        for (char c : {'&', '<', '>', '|'}) {
            if (splitOnSymbol(ret, i, c)) {
                --i;
                break;
            }
        }
    }
    return ret;
}

// Outputs a Command structure to stream (for debugging purposes)
ostream& operator<<(ostream& outs, const Command& c) {
    outs << c.execName << " [argv: ";
    for (const auto& arg : c.argv) {
        if (arg) {
            outs << arg << ' ';
        } else {
            outs << "NULL ";
        }
    }
    outs << "] -- FD, in: " << c.inputFd << ", out: " << c.outputFd << " "
         << (c.background ? "(background)" : "(foreground)");
    return outs;
}

// Parses the tokenized input into Command structures, handling redirection, piping, etc.
vector<Command> getCommands(const vector<string>& tokens) {
    vector<Command> commands(count(tokens.begin(), tokens.end(), "|") + 1); // 1 + number of pipes
    int first = 0;
    int last = find(tokens.begin(), tokens.end(), "|") - tokens.begin();
    bool error = false;

    for (int cmdNumber = 0; cmdNumber < commands.size(); ++cmdNumber) {
        const string& token = tokens[first];
        if (token == "&" || token == "<" || token == ">" || token == "|") {
            error = true;
            break;
        }

        Command& command = commands[cmdNumber]; // Get reference to current Command struct.
        command.execName = token;
        command.argv.push_back(strdup(token.c_str())); // argv[0] == program name
        command.inputFd = STDIN_FILENO;
        command.outputFd = STDOUT_FILENO;
        command.background = false;

        for (int j = first + 1; j < last; ++j) {
            if (tokens[j] == ">") {
                // Handle output redirection
                if (cmdNumber != commands.size() - 1) {
                    // Only the last command can have output redirection
                    error = true;
                    break;
                }
                if (j + 1 >= tokens.size() || tokens[j + 1] == "|" || tokens[j + 1] == "<" || tokens[j + 1] == ">") {
                    // Invalid output redirection
                    error = true;
                    break;
                }
                command.outputFd = open(tokens[j + 1].c_str(), O_WRONLY | O_CREAT | O_TRUNC, 0644);
                if (command.outputFd < 0) {
                    perror("open");
                    error = true;
                    break;
                }
                j++; // Skip the filename token
            } else if (tokens[j] == "<") {
                // Handle input redirection
                if (cmdNumber != 0) {
                    // Only the first command can have input redirection
                    error = true;
                    break;
                }
                if (j + 1 >= tokens.size() || tokens[j + 1] == "|" || tokens[j + 1] == "<" || tokens[j + 1] == ">") {
                    // Invalid input redirection
                    error = true;
                    break;
                }
                command.inputFd = open(tokens[j + 1].c_str(), O_RDONLY);
                if (command.inputFd < 0) {
                    perror("open");
                    error = true;
                    break;
                }
                j++; // Skip the filename token
            } else if (tokens[j] == "&") {
                // Handle background command
                command.background = true;
            } else if (tokens[j] == "|") {
                // Handle pipe (already handled by the outer loop)
                error = true;
                break;
            } else {
                // Normal command argument
                command.argv.push_back(strdup(tokens[j].c_str()));
            }
        }

        if (!error) {
            if (cmdNumber > 0) {
                // Create a pipe for commands after the first
                int pipefd[2];
                if (pipe(pipefd)) {
                    perror("pipe");
                    error = true;
                    break;
                }
                // Connect the previous command's output to this command's input
                commands[cmdNumber - 1].outputFd = pipefd[1];
                command.inputFd = pipefd[0];
            }

            // Execvp requires argv to have a nullptr at the end
            command.argv.push_back(nullptr);

            // Find the next pipe character
            first = last + 1;
            if (first < tokens.size()) {
                last = find(tokens.begin() + first, tokens.end(), "|") - tokens.begin();
            }
        } // end if !error
    } // end for( cmdNumber = 0 to commands.size )

    if (error) {
        // Clean up any open file descriptors
        for (Command& cmd : commands) {
            if (cmd.inputFd != STDIN_FILENO) close(cmd.inputFd);
            if (cmd.outputFd != STDOUT_FILENO) close(cmd.outputFd);
        }
        return std::vector<Command>(); 
    }

    return commands;
}
