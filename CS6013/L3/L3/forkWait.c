//
//  forkWait.c
//  L3
//
//  Created by Jia Gao on 2/3/25.
//

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>

int main(void) {
    pid_t pid = fork();

    if (pid < 0) {
        perror("fork");
        exit(1);
    }

    if (pid == 0) {
        // Child process
        printf("child\n");
        exit(0);
    } else {
        // Parent process
        printf("parent\n");
        waitpid(pid, NULL, 0); // Wait for the child to exit
    }

    return 0;
}
