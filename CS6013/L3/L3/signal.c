//
//  signal.c
//  L3
//
//  Created by Jia Gao on 2/3/25.
//
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <signal.h>
#include <sys/wait.h>

volatile sig_atomic_t ack_received = 0;

void handle_ack(int sig) {
    ack_received = 1;
}

int main(int argc, char *argv[]) {
    if (argc < 2) {
        fprintf(stderr, "Usage: %s <initial_message>\n", argv[0]);
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

        signal(SIGUSR1, handle_ack);

        while (1) {
            char buffer[256];
            ssize_t count = read(pipefd[0], buffer, sizeof(buffer));
            if (count == -1) {
                perror("read");
                exit(1);
            }

            buffer[count] = '\0'; // Null-terminate the string
            printf("child received: %s\n", buffer);

            if (strcmp(buffer, "quit") == 0) {
                break;
            }

            // Send acknowledgment to parent
            kill(getppid(), SIGUSR1);
        }

        close(pipefd[0]); // Close the read end of the pipe
        exit(0);
    } else {
        // Parent process
        close(pipefd[0]); // Close the read end of the pipe

        signal(SIGUSR1, handle_ack);

        // Send initial message
        char *message = argv[1];
        ssize_t len = strlen(message);
        if (write(pipefd[1], message, len) != len) {
            perror("write");
            exit(1);
        }

        // Wait for acknowledgment
        while (!ack_received) {
            pause();
        }
        ack_received = 0;

        // Continuous communication
        while (1) {
            char input[256];
            printf("Enter a message (or 'quit' to exit): ");
            if (fgets(input, sizeof(input), stdin) == NULL) {
                perror("fgets");
                exit(1);
            }

            input[strcspn(input, "\n")] = '\0'; // Remove newline

            len = strlen(input);
            if (write(pipefd[1], input, len) != len) {
                perror("write");
                exit(1);
            }

            if (strcmp(input, "quit") == 0) {
                break;
            }

            // Wait for acknowledgment
            while (!ack_received) {
                pause();
            }
            ack_received = 0;
        }

        close(pipefd[1]); // Close the write end of the pipe
        waitpid(pid, NULL, 0); // Wait for the child to exit
    }

    return 0;
}
