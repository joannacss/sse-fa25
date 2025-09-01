#include <stdio.h>
#include <string.h>
// gcc -g -o simple_overflow simple_overflow.c
int main(int argc, char *argv[]){
    char buffer[500];
    strcpy(buffer, argv[1]);
    return 0;
}