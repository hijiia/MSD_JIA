global myGTOD

myGTOD:
    sub rsp, 16               ; Allocate space for the timeval struct
    mov rax, 96               ; Syscall number for gettimeofday
    mov rdi, rsp              ; Address of timeval struct
    xor rsi, rsi              ; Null pointer for timezone
    syscall                   ; Make the syscall
    mov rax, [rsp]            ; Move tv_sec into rax
    mov rdx, [rsp + 8]        ; Move tv_usec into rdx
    add rsp, 16               ; Deallocate space
    ret                       ; Return 
