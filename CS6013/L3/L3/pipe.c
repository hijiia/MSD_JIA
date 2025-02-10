//
//  pipe.c
//  L3
//
//  Created by Jia Gao on 2/3/25.
//

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/wait.h>

int main(int argc, char *argv[]) {
    if (argc < 2) {
        fprintf(stderr, "Usage: %s <message>\n", argv[0]);
        exit(1);
    }

    int pipefd[2];
    if (pipe(pipefd) == -1) {
        perror("pipe");
        exit(1);
    }

    pid_t pid = fork();

    if (pid < 0) {
        perror("fork");
        exit(1);
    }

    if (pid == 0) {
        // Child process
        close(pipefd[1]); // Close the write end of the pipe

        char buffer[256];
        ssize_t count = read(pipefd[0], buffer, sizeof(buffer));
        if (count == -1) {
            perror("read");
            exit(1);
        }

        buffer[count] = '\0'; // Null-terminate the string
        printf("child received: %s\n", buffer);

        close(pipefd[0]); // Close the read end of the pipe
        exit(0);
    } else {
        // Parent process
        close(pipefd[0]); // Close the read end of the pipe

        char *message = argv[1];
        ssize_t len = strlen(message);
        if (write(pipefd[1], message, len) != len) {
            perror("write");
            exit(1);
        }

        close(pipefd[1]); // Close the write end of the pipe
        waitpid(pid, NULL, 0); // Wait for the child to exit
    }

    return 0;
}
