#include <stdio.h>

// Compiling: gcc -w -o overflow2 overflow2.c
// The -w is to disable the warning (that's intentional)
int main(int argc, char *argv[]){
    // chars range from -128 to 127
    char c = 128; 
    char d = -129;
    printf("c=%d\n", c); // what would it print?
    printf("d=%d\n", d); // what would it print?

    return 0;
}