#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "process_grayscale.h"

/********************************************/ /**
 *  FUNCTION DEFINITIONS
 ***********************************************/
int save_grayscale_to_file(char *file_name, GrayscaleImage *image)
{
    FILE *fp;
    int width, height, colour_range;
    char *img_type;

    fp = fopen(file_name, "wb"); //Open File
    if (!fp)
    {
        fprintf(stderr, "ERROR: An error occurred finding the file '%s'\n", file_name);
        return 1;
    }

    img_type = "P5";
    height = image->height, width = image->width, colour_range = 255;

    //Write the PPM's headers
    fprintf(fp, "%s\n", img_type);
    fprintf(fp, "%d %d\n", width, height);
    fprintf(fp, "%d\n", colour_range);

    //Write the image's pixels
    fwrite(image->pixel_array, width, height, fp);

    fclose(fp);
    return 0;
};

//TO DO: VERIFY IF COMMENTS CAN BE PLACED ON ANY PLACE IN THE DOCUMENT OR WHATS THE DEAL WITH THAT!
GrayscaleImage *load_grayscale_file(char *file_name)
{
    char image_type[8];
    int comments, colour_range = 0;
    FILE *fp;
    GrayscaleImage *image;

    fp = fopen(file_name, "rb"); //Open File
    if (!fp)
    {
        fprintf(stderr, "ERROR: An error occurred finding the file '%s'\n", file_name);
        exit(1);
    }

    if ((!fgets(image_type, sizeof(image_type), fp)) && (image_type[0] != 'P' && image_type[1] != '5'))
    { //Get PPM Type (Goes from P0-P6)
        fprintf(stderr, "ERROR: The PGM image file seems to be malformed or is incorrect - Should be P5\n");
        exit(1);
    }

    while ((comments = getc(fp)) && (comments == '#')) //Remove comments (lines starting in #)
    {
        while (getc(fp) != '\n') //Get the entire line char by char until we find a breakline
            ;
    }
    ungetc(comments, fp); //If we break out of the last loop it's cus the current char isn't in a line starting with #, so we should unget it

    image = (GrayscaleImage *)malloc(sizeof(GrayscaleImage)); //Allocate memory for our image struct

    if (fscanf(fp, "%d %d", &image->width, &image->height) != 2)
    { //Get Size (WIDTHxHEIGHT)
        fprintf(stderr, "ERROR: The PGM image seems to have an invalid image size (Should be Width x Height)\n");
        exit(1);
    }

    if ((fscanf(fp, "%d", &colour_range) != 1) && (colour_range != 255))
    { //Get colour range (should be 255)
        fprintf(stderr, "ERROR: The PGM image seems to have an invalid grayscale range - Should be 255\n");
        exit(1);
    }

    while (fgetc(fp) != '\n')
        ; //Remove blank spaces

    image->pixel_array = (Grayscale *)malloc(sizeof(Grayscale) * image->width * image->height); //Allocate memory for image's pixels

    if (fread(image->pixel_array, image->width, image->height, fp) != image->height)
    { //Read all remaining data from the image (Remember each grayscale pixel is just a value from 0-255)
        fprintf(stderr, "ERROR: At least one line couldn't be read from the image\n");
        exit(1);
    }

    fclose(fp);
    return image;
};


GrayscaleImage *copy_grayscale_image(GrayscaleImage *image){
    GrayscaleImage *copied_image = (GrayscaleImage *)malloc(sizeof(GrayscaleImage));

    copied_image->width = image->width;
    copied_image->height = image->height;

    copied_image->pixel_array = (Grayscale *)malloc(sizeof(Grayscale) * image->width * image->height); //Allocate memory for image's pixels

    memcpy(copied_image->pixel_array, image->pixel_array, image->width*image->height*sizeof(Grayscale));

    return copied_image;
}


Grayscale get_grayscale_pixel(GrayscaleImage *image, int row, int col)
{
    if(row > image->height || col > image->width || row < 0 || col < 0){
        Grayscale null_pixel;
        null_pixel.Gray = 0;

        return null_pixel;
    }

    int index = row * image->width + col; //The index of the pixel is given by the formula: pixel_line * no_columns + pixel_column
    return image->pixel_array[index];
}

GrayscaleImage *get_grayscale_subsection(GrayscaleImage *image, int pos_start[2], int pos_end[2])
{
    //Verify that pos_start isn't infront of pos_end
    if (pos_start[0] > pos_end[0] || pos_start[1] > pos_end[1])
    {
        fprintf(stderr, "ERROR: The starting position can't come after the ending position!\n");
        exit(1);
    }

    int pixels_per_row, rows_travelled, subsection_offset, current_index_image;

    GrayscaleImage *subsect = (GrayscaleImage *)malloc(sizeof(GrayscaleImage)); //Allocate memory for our image struct

    subsect->width = pos_end[1] - pos_start[1] + 1;
    subsect->height = pos_end[0] - pos_start[0] + 1;

    subsect->pixel_array = (Grayscale *)malloc(sizeof(Grayscale) * subsect->width * subsect->height); //Allocate memory for image's pixels

    pixels_per_row = pos_end[1] - pos_start[1] + 1; //Number of pixels that each row of the subsection is going to have
    rows_travelled = 0;                             //Number of rows we've already travelled (we can't use the row variable because pos_start[0] might not always be 0!)

    for (int row = pos_start[0]; row <= pos_end[0]; row++)
    {
        current_index_image = row * image->width + pos_start[1]; //Starting index of the current row's first pixel on the image's pixel array
        subsection_offset = pixels_per_row * rows_travelled++;   //Offset of our subsections pixel_array (used so that we don't overwrite already written pixels)

        memmove(subsect->pixel_array + subsection_offset, &image->pixel_array[current_index_image], pixels_per_row * sizeof(Grayscale)); //Copy from our image's pixel array to our subsection's pixel array
    }

    return subsect;
}


void change_gray_intensity(GrayscaleImage *image, int pixel_intensity)
{
    int length,index, new_colour;
    
    length = image->height*image->width;

    for (index = 0; index < length; index++)
    {   
        //Change Gray intensity
        new_colour = image->pixel_array[index].Gray + pixel_intensity;
        if (new_colour > 255){
            new_colour = 255;
        }else if(new_colour < 0){
            new_colour = 0;
        }

        image->pixel_array[index].Gray = new_colour;
    }
}

/*
int main()
{
    GrayscaleImage *image = load_grayscale_file("../galaxy.ascii.pgm");

    int start[2], end[2];

    start[0] = 482, start[1] = 0;
    end[0] = 965, end[1] = 482;

    Grayscale pixel = get_grayscale_pixel(image, image->width / 2, image->height / 2);
    printf("Gray%d\n", pixel.Gray);

    GrayscaleImage *subsect = get_grayscale_subsection(image, start, end);
    save_grayscale_to_file("subsection.ppm", subsect);

    int intensity = 100;

    GrayscaleImage* copy = copy_grayscale_image(image);
    change_gray_intensity(image, intensity);
    save_grayscale_to_file("galaxy.pgm", image);
    save_grayscale_to_file("galaxy0G.pgm", copy);

    return 0;
}
*/
