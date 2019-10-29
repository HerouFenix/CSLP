#include <stdio.h>
#include <stdlib.h>
#include <string.h>

//Constants
#define LENGTH 12
#define HELLO "Hello"
#define WORLD "World"

//Variable declaration:
extern char* message;
extern char* buff;
extern char* imgtype;
extern int* bits;

//Function declaration:
void echo(char* msg);

int main(){
    /* Hello World Program */

    //Variable definition and initialization
    char *message = calloc(LENGTH, sizeof(char));
    strcat(message, HELLO);
    strcat(message, " ");
    strcat(message, WORLD);
    //Function call
    echo(message);
    //Open file
    FILE *fp = fopen("lena.ppm", "r");
    char buff[255], imgtype[2];

    //Get image type
    fgets(imgtype, 3, (FILE *) fp);
    printf("%s\n", imgtype );

    //Skip white space
    fgets(buff, 255, (FILE *) fp);

    //Get height and width
    int height, width;
    fscanf(fp, "%d %d", &height, &width);

    printf("Height: %d, Width: %d\n", height, width);

    //Skip white space
    fgets(buff, 255, (FILE *) fp);

    //Get range of value
    int range;
    fscanf(fp, "%d", &range);
    printf("Range: %d", range);

    //Skip white space
    fgets(buff, 255, (FILE *) fp);

    //
    int bits[height];
    fgets(buff, 255, (FILE *) fp);
    echo(buff);
/*
    while (1){
        fgets(buff, 255, (FILE *) fp);
        
    }*/
    fclose(fp);
    return 0;
}

//Function definition
void echo(char* msg){
    printf("%s \n", msg);
}
