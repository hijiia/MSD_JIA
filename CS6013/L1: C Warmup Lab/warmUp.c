/*********************************************************************
 *
 * Jia Gao  12 Jan  cs6013
 
 *
 */

#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h> // For strlen()

/*********************************************************************
 *
 * The C functions in this lab use patterns and functionality often
 * found in operating system code. Your job is to implement them.
 * Additionally, write some test cases for each function (stick them in
 * functions [called xyzTests(), etc or similar]) Call your abcTests(),
 * etc functions in main().
 *
 * Write your own tests for each function you are testing, then
 * share/combine tests with a classmate.  Try to come up with tests that
 * will break people's code!  Easy tests don't catch many bugs! [Note
 * this is one specific case where you may share code directly with
 * another student.  The test function(s) from other students must be
 * clearly marked (add '_initials' to the function name) and commented
 * with the other student's name.
 *
 * Note: you may NOT use any global variables in your solution.
 *
 * Errata:
 *   - You can use global vars in your testing functions (if necessary).
 *   - Don't worry about testing the free_list(), or the draw_me() functions.
 *
 * You must compile in C mode (not C++).  If you compile from the
 * commandline run clang, not clang++. Submit your solution files on
 * Canvas.
 *
 *********************************************************************/

/*********************************************************************
 *
 * byte_sort()
 *
 * specification: byte_sort() treats its argument as a sequence of
 * 8 bytes, and returns a new unsigned long integer containing the
 * same bytes, sorted numerically, with the smaller-valued bytes in
 * the lower-order byte positions of the return value
 *
 * EXAMPLE: byte_sort (0x 0 4 0 3 deadbeef0201) returns 0xefdebead04030201
 16x4
 0000 0100 0000 0011 1101 1110 1010 1101 1011 1110 1110 1111 0000 0010 0000 0001
 * Ah, the joys of using bitwise operators!
 *
 * Hint: you may want to write helper functions for these two functions
 *
 *********************************************************************/

unsigned long byte_sort( unsigned long arg )
{
    //read each byte
    unsigned char bytes[8];
    for (int i = 0; i < 8; i++){
        bytes[i] = (arg >> (8 * i)) & 0xFF;
    }
    //sort byte
    for (int i = 0; i < 8; i++){
        for (int j = i + 1; j < 8; j++){
            if (bytes[i] > bytes[j]){
                unsigned char temp =  bytes[i];
                bytes[i] = bytes[j];
                bytes[j] = temp;
            }
        }
    }
    //return
    unsigned long result = 0;
    for (int i = 0; i < 8; i++){
        result |= ((unsigned char)bytes[i] << (8 * i));
    }
    return result;
}

/*********************************************************************
 *
 * nibble_sort()
 *
 * specification: nibble_sort() treats its argument as a sequence of 16 4-bit
 * numbers, and returns a new unsigned long integer containing the same nibbles,
 * sorted numerically, with smaller-valued nibbles towards the "small end" of
 * the unsigned long value that you return
 *
 * the fact that nibbles and hex digits correspond should make it easy to
 * verify that your code is working correctly
 *
 * EXAMPLE: nibble_sort( 0x0403deadbeef0201 ) returns 0xfeeeddba43210000
 *
 *********************************************************************/

unsigned long nibble_sort( unsigned long arg )
{
    //read each 4 bits
    unsigned char nibbles[16];
    for (int i = 0; i < 16; i++){
        nibbles[i] = (arg >> (4 * i)) & 0xF;
    }
    //sort
    for (int i = 0; i < 16; i++){
        for (int j = i + 1; j < 16; j++){
            if (nibbles[i] > nibbles[j]){
                unsigned char temp =  nibbles[i];
                nibbles[i] = nibbles[j];
                nibbles[j] = temp;
            }
        }
    }
    //return
    unsigned long result = 0;
    for (int i = 0; i < 16; i++){
        result |= ((unsigned char)nibbles[i] << (4 * i));
    }
    return result;
}

/*********************************************************************/

typedef struct elt {
  char val;
  struct elt *link;
} Elt;

/*********************************************************************/

/* Forward declaration of "free_list()"...
 *    This allows you to call free_list() in name_list() [if you'd like].
 */

void free_list( Elt* head ); // [No code goes here!]

/*********************************************************************
 *
 * name_list( name )
 *
 * specification: allocate and return a pointer to a linked list of
 * struct elts
 *
 * - the first element in the list should contain in its "val" field the
 *   first letter of the passed in 'name'; the second element the second
 *   letter, etc.;
 *
 * - the last element of the linked list should contain in its "val" field
 *   the last letter of the passed in name and its "link" field should be a
 *   null pointer.
 *
 * - each element must be dynamically allocated using a malloc() call
 *
 * - you must use the "name" variable (change it to be your name).
 *
 * Note, since we're using C, not C++ you can't use new/delete!
 * The analog to delete is the free() function
 *
 * - if any call to malloc() fails, your function must return NULL and must
 *   also free any heap memory that has been allocated so far; that is, it
 *   must not leak memory when allocation fails.
 *
 * Implement print_list and free_list which should do what you expect.
 * Printing or freeing a nullptr should do nothing.
 *
 * Note: free_list() might be useful for error handling for name_list()...
 *
 *********************************************************************/

Elt *name_list( char * name )
{
    //empty name
    if(name == NULL){
    return NULL;
}
    Elt *head = NULL;
    Elt *current = NULL;
    for (int i = 0; name[i]!='\0'; i++){
        Elt *newnode = (Elt *)malloc(sizeof(Elt));
        if(newnode == NULL){
            free_list(head);
            return NULL;
        }
        newnode -> val = name[i];
        newnode->link = NULL;
        if(head == NULL) {
            head = newnode;
        }else {
            current->link = newnode;
        }
        current = newnode;
    }
    return head;
}

/*********************************************************************/

void print_list( Elt* head )
{
        if (head == NULL) {
            printf("The list is empty.\n");
            return;
        }

        Elt* current = head;
        while (current != NULL) {
            printf("%c -> ", current->val);
            current = current->link;
        }
}

/*********************************************************************/

void free_list( Elt* head )
{
    while (head != NULL) {
            Elt *temp = head;
            head = head->link;
            free(temp);
        }
}

/*********************************************************************
 *
 * draw_me()
 *
 * This function creates a file called 'me.txt' which contains an ASCII-art
 * picture of you (it does not need to be very big).
 *
 * Use the C stdlib functions: fopen, fclose, fprintf, etc which live in stdio.h
 * - Don't use C++ iostreams
 *
 *********************************************************************/

void draw_me(void)
{
    FILE *file = fopen("me.txt","w");
    if(file == NULL){
        printf("Error opening file.\n");
        return;
    }
    fprintf(file, "   *****\n");
    fprintf(file, "  * _ _ *\n");
    fprintf(file, " *  O O  *\n");
    fprintf(file, "*    ^    *\n");
    fprintf(file, " *  ---  *\n");
    fprintf(file, "  ** ** **\n");
    fclose(file);

}

/*********************************************************************
 *
 * Test Code - Place your test functions in this section:
 *
 *     Remember, when testing name_list(), you should create a 'myName'
 *     variable and pass it in.
 */

// bool testByteSort() { ... }
// ...
// ...

/*********************************************************************
 *
 * main()
 *
 * The main driver program.  You can place your main() method in this
 * file to make compilation easier, or have it in a separate file.
 *
 *********************************************************************/
void testByteSort(void);
void testNibbleSort(void);
void testNameList(void);

int main(void)
{
    testByteSort();
    testNibbleSort();
    testNameList() ;
    draw_me();
}

void testByteSort(void){
    unsigned long test = 0x0403deadbeef0201;
    unsigned long result = byte_sort(test);
    printf("Byte Sort Test: Input = 0x%lx, Output = 0x%lx\n", test, result);
    }
 
void testNibbleSort(void) {
    unsigned long test = 0x0403deadbeef0201;
    unsigned long result = nibble_sort(test);
    printf("Nibble Sort Test: Input = 0x%lx, Output = 0x%lx\n", test, result);
}
void testNameList(void) {
    char myName[] = "Jia";
    Elt *list = name_list(myName);
    print_list(list);
    free_list(list);
}

   


