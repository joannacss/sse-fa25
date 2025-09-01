#include <stdio.h>

// Compiling: gcc -w -o overflow1 overflow1.c
// The -w is to disable the warning (that's intentional)
int main(int argc, char *argv[]){
    // chars range from 0 to 255
    unsigned char a = -1; 
    unsigned char b = 256;
    printf("%d\n", a); // output?
    printf("%d\n", b); // output?
}
