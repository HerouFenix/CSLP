#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "process_rgb.h"


/********************************************/ /**
 *  FUNCTION DEFINITION
 ***********************************************/
int save_rgb_to_file(char *file_name, RGBImage *image)
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

    img_type = "P6";
    height = image->height, width = image->width, colour_range = 255;

    //Write the PPM's headers
    fprintf(fp, "%s\n", img_type);
    fprintf(fp, "%d %d\n", width, height);
    fprintf(fp, "%d\n", colour_range);

    //Write the image's pixels
    fwrite(image->pixel_array, 3 * width, height, fp);

    fclose(fp);
    return 0;
};

RGBImage *load_rgb_file(char *file_name)
{
    char image_type[8];
    int colour_range, comments;
    FILE *fp;
    RGBImage *image;

    fp = fopen(file_name, "rb"); //Open File
    if (!fp)
    {
        fprintf(stderr, "ERROR: An error occurred finding the file '%s'\n", file_name);
        exit(1);
    }

    if ((!fgets(image_type, sizeof(image_type), fp)) && (image_type[0] != 'P' && image_type[1] != '6'))
    { //Get PPM Type (Goes from P0-P6)
        fprintf(stderr, "ERROR: The PPM image file seems to be malformed\n");
        exit(1);
    }

    image = (RGBImage *)malloc(sizeof(RGBImage)); //Allocate memory for our image struct

    if (fscanf(fp, "%d %d", &image->width, &image->height) != 2)
    { //Get Size (WIDTHxHEIGHT)
        fprintf(stderr, "ERROR: The PPM image seems to have an invalid image size (Should be Width x Height)\n");
        exit(1);
    }

    if ((fscanf(fp, "%d", &colour_range) != 1) && (colour_range != 255))
    { //Get colour range (should be 255)
        fprintf(stderr, "ERROR: The PPM image seems to have an invalid rgb range - Should be 255\n");
        exit(1);
    }

    while ((comments = getc(fp)) && (comments == '#')) //Remove comments (lines starting in #)
    {
        while (getc(fp) != '\n') //Get the entire line char by char until we find a breakline
            ;
    }
    ungetc(comments, fp); //If we break out of the last loop it's cus the current char isn't in a line starting with #, so we should unget it

    while (fgetc(fp) != '\n')
        ; //Remove blank spaces

    image->pixel_array = (Colour *)malloc(sizeof(Colour) * image->width * image->height); //Allocate memory for image's pixels

    if (fread(image->pixel_array, 3 * image->width, image->height, fp) != image->height)
    { //Read all remaining data from the image (we multiply the width by 3 because each pixel holds 3 chars - r,g,b)
        fprintf(stderr, "ERROR: At least one line couldn't be read from the image\n");
        exit(1);
    }

    fclose(fp);
    return image;
};


RGBImage *copy_rgb_image(RGBImage *image){
    RGBImage *copied_image = (RGBImage *)malloc(sizeof(RGBImage));

    copied_image->width = image->width;
    copied_image->height = image->height;

    copied_image->pixel_array = (Colour *)malloc(sizeof(Colour) * image->width * image->height); //Allocate memory for image's pixels

    memcpy(copied_image->pixel_array, image->pixel_array, image->width*image->height*sizeof(Colour));

    return copied_image;
}


Colour get_rgb_pixel(RGBImage *image, int row, int col)
{
    if(row > image->height || col > image->width || row < 0 || col < 0){
        Colour null_pixel;
        null_pixel.R = 0;
        null_pixel.G = 0;
        null_pixel.B = 0;

        return null_pixel;
    }
    int index = row * image->width + col; //The index of the pixel is given by the formula: pixel_line * no_columns + pixel_column
    return image->pixel_array[index];
}

RGBImage *get_rgb_subsection(RGBImage *image, int pos_start[2], int pos_end[2])
{
    //Verify that pos_start isn't infront of pos_end
    if(pos_start[0] > pos_end[0] || pos_start[1] > pos_end[1]){
        fprintf(stderr, "ERROR: The starting position can't come after the ending position!\n");
        exit(1);
    }

    int pixels_per_row, rows_travelled, subsection_offset, current_index_image;

    RGBImage *subsect = (RGBImage *)malloc(sizeof(RGBImage)); //Allocate memory for our image struct

    subsect->width = pos_end[1] - pos_start[1] + 1;
    subsect->height = pos_end[0] - pos_start[0] + 1;

    subsect->pixel_array = (Colour *)malloc(sizeof(Colour) * subsect->width * subsect->height); //Allocate memory for image's pixels

    pixels_per_row = pos_end[1] - pos_start[1] + 1; //Number of pixels that each row of the subsection is going to have
    rows_travelled = 0;                             //Number of rows we've already travelled (we can't use the row variable because pos_start[0] might not always be 0!)
    
    for (int row = pos_start[0]; row <= pos_end[0]; row++)
    {
        current_index_image = row * image->width + pos_start[1]; //Starting index of the current row's first pixel on the image's pixel array
        subsection_offset = pixels_per_row * rows_travelled++;   //Offset of our subsections pixel_array (used so that we don't overwrite already written pixels)

        memmove(subsect->pixel_array + subsection_offset, &image->pixel_array[current_index_image], pixels_per_row * sizeof(Colour)); //Copy from our image's pixel array to our subsection's pixel array
    }

    return subsect;
}

void change_rgb_intensity(RGBImage *image, int *pixel_intensity)
{
    int length,index, new_colour;
    
    length = image->height*image->width;

    for (index = 0; index < length; index++)
    {   
        //Change Red intensity
        new_colour = image->pixel_array[index].R + pixel_intensity[0];
        if (new_colour > 255){
            new_colour = 255;
        }else if(new_colour < 0){
            new_colour = 0;
        }

        image->pixel_array[index].R = new_colour;

        //Change Green intensity
        new_colour = image->pixel_array[index].G + pixel_intensity[1];
        if (new_colour > 255){
            new_colour = 255;
        }else if(new_colour < 0){
            new_colour = 0;
        }

        image->pixel_array[index].G = new_colour;


        //Change Blue intensity
        new_colour = image->pixel_array[index].B + pixel_intensity[2];
        if (new_colour > 255){
            new_colour = 255;
        }else if(new_colour < 0){
            new_colour = 0;
        }

        image->pixel_array[index].B = new_colour;
    }
}


/*
int main()
{
    RGBImage *image = load_rgb_file("../lena.ppm");

    int start[2], end[2], intensity[3];

    start[0] = 0, start[1] = 0;
    end[0] = 255, end[1] = 511;

    Colour pixel = get_rgb_pixel(image, image->width / 2, image->height / 2);
    printf("R%d G%d B%d\n", pixel.R, pixel.G, pixel.B);
    
    RGBImage *subsect = get_rgb_subsection(image, start, end);
    save_rgb_to_file("subsection.ppm", subsect);

    intensity[0] = -100;
    intensity[1] = -100;
    intensity[2] = 255;

    RGBImage* copy = copy_rgb_image(image);
    change_rgb_intensity(image, intensity);
    save_rgb_to_file("lena.ppm", image);
    save_rgb_to_file("lenaOG.ppm", copy);

    return 0;
}
*/