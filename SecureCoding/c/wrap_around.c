#include <stdio.h>
#include <stdlib.h>

void my_function(unsigned int ui_a, unsigned int ui_b) {
  unsigned int udiff = ui_a + ui_b;


  printf("%d\n", udiff);
}


// To compile: gcc -o wrap_around wrap_around.c
int main(int argc, char *argv[]) {
    // Invoke function with possible wrap around
    unsigned int num1 = 4294967295;
    unsigned int num2 = 2;
    my_function(num1, num2);

    // You can perform further operations with num1 and num2 here
    return 0; // Exit with success
}
