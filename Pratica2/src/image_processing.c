#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "process_rgb.h"
#include "process_grayscale.h"
#include "process_binary.h"
#include "image_processing.h"

/********************************************//**
 *  FUNCTION DEFINITIONS
 ***********************************************/
BinaryImage* convert_gray_to_bin(GrayscaleImage *image, unsigned char threshold) {
    BinaryImage *res;
    res = (BinaryImage *)malloc(sizeof(BinaryImage)); 
    
    if(image->width %8 != 0){
        printf("Image width not divisible by 8, due to bit stuffing issues, it wouldn't be converted correctly\n");
        return res;
    }

    res->height = image->height;
    res->width = image->width;

    res->bin_array = (unsigned char *)malloc(res->height*res->width);

    for (int i = 0; i < res->height*res->width; i++){
        res->bin_array[i] = (image->pixel_array[i].Gray < threshold) <<7;
    }

    return res;
}

GrayscaleImage* convert_rgb_to_grayscale(RGBImage *rgb_image){
    GrayscaleImage *gray_img;
    Grayscale gray;
    Colour colour;
    gray_img = (GrayscaleImage *)malloc(sizeof(GrayscaleImage)); //Allocate memory for our image struct


    gray_img->height = rgb_image->height;
    gray_img->width = rgb_image->width;
    gray_img->pixel_array = (Grayscale *)malloc(sizeof(Grayscale) * gray_img->width * gray_img->height); //Allocate memory for image's pixels


    for(int i = 0 ; i < rgb_image->height * rgb_image->width ; i++ ){
        //Grayscale = (R + G + B / 3)

        colour = rgb_image->pixel_array[i];

        gray.Gray = (colour.R + colour.G + colour.B)/3;

        gray_img->pixel_array[i] = gray;
    }

    return gray_img;
}


GrayscaleImage** convert_rgb_to_three_grayscale(RGBImage *rgb_image){
    GrayscaleImage **gray_images;
    GrayscaleImage *gray_img_r, *gray_img_g, *gray_img_b;
    Grayscale gray;
    Colour colour;

    gray_img_r = (GrayscaleImage *)malloc(sizeof(GrayscaleImage)); //Allocate memory for our image struct
    gray_img_g = (GrayscaleImage *)malloc(sizeof(GrayscaleImage)); //Allocate memory for our image struct
    gray_img_b = (GrayscaleImage *)malloc(sizeof(GrayscaleImage)); //Allocate memory for our image struct
    gray_images = (GrayscaleImage **)malloc(sizeof(GrayscaleImage)*3);

    gray_img_r->height = rgb_image->height;
    gray_img_g->height = rgb_image->height;
    gray_img_b->height = rgb_image->height;

    gray_img_r->width = rgb_image->width;
    gray_img_g->width = rgb_image->width;
    gray_img_b->width = rgb_image->width;

    gray_img_r->pixel_array = (Grayscale *)malloc(sizeof(Grayscale) * rgb_image->width * rgb_image->height); //Allocate memory for image's pixels
    gray_img_g->pixel_array = (Grayscale *)malloc(sizeof(Grayscale) * rgb_image->width * rgb_image->height); //Allocate memory for image's pixels
    gray_img_b->pixel_array = (Grayscale *)malloc(sizeof(Grayscale) * rgb_image->width * rgb_image->height); //Allocate memory for image's pixels

    for(int i = 0 ; i < rgb_image->height * rgb_image->width ; i++ ){
        colour = rgb_image->pixel_array[i];

        gray.Gray = colour.R;
        gray_img_r->pixel_array[i] = gray ;

        gray.Gray = colour.G;
        gray_img_g->pixel_array[i] = gray ;

        gray.Gray = colour.B;
        gray_img_b->pixel_array[i] = gray ;
    }

    gray_images[0] = gray_img_r;
    gray_images[1] = gray_img_g;
    gray_images[2] = gray_img_b;


    return gray_images;
}

int apply_rgb_filter(RGBImage *image, double *kernel, int filter_dimension[2])
{
    if (filter_dimension[0] % 2 == 0 || filter_dimension[1] % 2 == 0)
    {
        fprintf(stderr, "ERROR: The filter kernel's dimensions must be odd numbers!\n");
        return 1;
    }

    int row_offset, col_offset, current_pixel_index, image_row, subsection_row, kernel_row, image_col, subsection_col, kernel_col, new_red, new_green, new_blue;
    Colour new_pixel, current_pixel;
    Colour *new_pixel_array;

    row_offset = filter_dimension[0] / 2; //How many pixels to our left/right do we move from the central pixel
    col_offset = filter_dimension[1] / 2; //How many pixels to our up/down do we move from the central pixel

    new_pixel_array = (Colour *)malloc(sizeof(Colour) * image->width * image->height);

    current_pixel_index = 0;
    //We apply a filter by multiplying all pixels around a center pixel with its correspondant in the filter's kernel and summing all values up
    //Iterate over all of the image's pixels
    for (current_pixel_index = 0; current_pixel_index < image->height * image->width; current_pixel_index++)
    {

        image_col = current_pixel_index % image->height;
        image_row = current_pixel_index / image->width;

        new_pixel.R = 0;
        new_pixel.G = 0;
        new_pixel.B = 0;

        new_red = 0;
        new_green = 0;
        new_blue = 0;

        //Iterate over a subsection of the image around the current (central) pixel
        kernel_row = 0;
        for (subsection_row = image_row - row_offset; subsection_row <= image_row + row_offset; subsection_row++)
        {
            kernel_col = 0;
            for (subsection_col = image_col - col_offset; subsection_col <= image_col + col_offset; subsection_col++)
            {
                current_pixel = get_rgb_pixel(image, subsection_row, subsection_col); //Our pixel in the original

                new_red += current_pixel.R * kernel[kernel_row * filter_dimension[1] + kernel_col];
                new_green += current_pixel.G * kernel[kernel_row * filter_dimension[1] + kernel_col];
                new_blue += current_pixel.B * kernel[kernel_row * filter_dimension[1] + kernel_col];

                kernel_col++;
            }
            kernel_row++;
        }

        if (new_red > 255)
        {
            new_red = 255;
        }
        else if (new_red < 0)
        {
            new_red = 0;
        }

        if (new_green > 255)
        {
            new_green = 255;
        }
        else if (new_green < 0)
        {
            new_green = 0;
        }

        if (new_blue > 255)
        {
            new_blue = 255;
        }
        else if (new_blue < 0)
        {
            new_blue = 0;
        }

        new_pixel.R = new_red;
        new_pixel.G = new_green;
        new_pixel.B = new_blue;

        new_pixel_array[current_pixel_index] = new_pixel;
    }

    image->pixel_array = new_pixel_array;

    return 0;
}

int apply_grayscale_filter(GrayscaleImage *image, double *kernel, int filter_dimension[2])
{
    if (filter_dimension[0] % 2 == 0 || filter_dimension[1] % 2 == 0)
    {
        fprintf(stderr, "ERROR: The filter kernel's dimensions must be odd numbers!\n");
        return 1;
    }

    int row_offset, col_offset, current_pixel_index, image_row, subsection_row, kernel_row, image_col, subsection_col, kernel_col, new_gray;
    Grayscale new_pixel, current_pixel;
    Grayscale *new_pixel_array;

    row_offset = filter_dimension[0] / 2; //How many pixels to our left/right do we move from the central pixel
    col_offset = filter_dimension[1] / 2; //How many pixels to our up/down do we move from the central pixel

    new_pixel_array = (Grayscale *)malloc(sizeof(Grayscale) * image->width * image->height);

    current_pixel_index = 0;
    //We apply a filter by multiplying all pixels around a center pixel with its correspondant in the filter's kernel and summing all values up
    //Iterate over all of the image's pixels
    for (current_pixel_index = 0; current_pixel_index < image->height * image->width; current_pixel_index++)
    {
        image_col = current_pixel_index % image->height;
        image_row = current_pixel_index / image->width;

        new_pixel.Gray = 0;

        new_gray = 0;

        //Iterate over a subsection of the image around the current (central) pixel
        kernel_row = 0;
        for (subsection_row = image_row - row_offset; subsection_row <= image_row + row_offset; subsection_row++)
        {
            kernel_col = 0;
            for (subsection_col = image_col - col_offset; subsection_col <= image_col + col_offset; subsection_col++)
            {
                current_pixel = get_grayscale_pixel(image, subsection_row, subsection_col); //Our pixel in the original

                new_gray += current_pixel.Gray * kernel[kernel_row * filter_dimension[1] + kernel_col];

                kernel_col++;
            }
            kernel_row++;
        }

        if (new_gray > 255)
        {
            new_gray = 255;
        }
        else if (new_gray < 0)
        {
            new_gray = 0;
        }

        new_pixel.Gray = new_gray;

        new_pixel_array[current_pixel_index] = new_pixel;
    }

    image->pixel_array = new_pixel_array;

    return 0;
}

void add_logo_to_image(RGBImage *rgb_image, RGBImage *logo, int* pixel, float intensity){
    // For loop variables
    int img_x, img_y, logo_x, logo_y;

    for (img_x = pixel[0], logo_x=0; img_x < pixel[0]+logo->width; img_x++, logo_x++){
        for (img_y = pixel[1], logo_y=0; img_y < pixel[1]+logo->height; img_y++, logo_y++){
            if (img_x < 0 || img_y < 0) continue;
            if (img_x >= rgb_image->width || img_y >= rgb_image->height) break;

            Colour logo_pixel = get_rgb_pixel(logo, logo_y, logo_x);
            Colour img_pixel = get_rgb_pixel(rgb_image, img_y, img_x);
            img_pixel.R = (unsigned char)((1-intensity)*img_pixel.R + intensity*logo_pixel.R);
            img_pixel.G = (unsigned char)((1-intensity)*img_pixel.G + intensity*logo_pixel.G);
            img_pixel.B = (unsigned char)((1-intensity)*img_pixel.B + intensity*logo_pixel.B);
            rgb_image->pixel_array[img_y * rgb_image->width + img_x] = img_pixel;
        }
    }
}

RGBImage* reduce_image(RGBImage* image, int new_height, int new_width){
    RGBImage *result;
    int width_ratio, height_ratio;

    width_ratio = image->width/new_width;
    height_ratio = image->height/new_height;

    result = (RGBImage *)malloc(sizeof(RGBImage));
    result->width = new_width;
    result->height = new_height;
    
    result->pixel_array = (Colour *)calloc(result->width*result->height, sizeof(Colour));

    for (int i =0; i < result->width*result->height; i++){
        int r = 0, g = 0, b = 0;
        int col = i%result->height;
        int row = i/result->width;
        for (int j = 0; j<height_ratio; j++){
            for (int k = 0; k<width_ratio;k++){
                Colour curr_pixel = get_rgb_pixel(image,row*width_ratio+k, col*height_ratio+j);
                r += curr_pixel.R;
                g += curr_pixel.G;
                b += curr_pixel.B;
            }
        }
        result->pixel_array[i].R = r/(height_ratio*width_ratio);
        result->pixel_array[i].G = g/(height_ratio*width_ratio);
        result->pixel_array[i].B = b/(height_ratio*width_ratio);
        
    }

    return result;
}

BinaryImage* convert_gray_to_bin_otsu(GrayscaleImage *image){
    int *histogram;

    histogram = (int*) calloc(1<<8, sizeof(int));
    for (int i = 0; i < image->width; i++){
        for (int j = 0; j < image->height; j++){
            int hist_index = get_grayscale_pixel(image, i, j).Gray;
            histogram[hist_index]++;
        }
    }

    int top = 256, wb = 0, wf=0, level=0;
    int total = image->height*image->width;
    float sumB = 0.0, sum = 0.0;
    float max = 0.0;

    for (int i = 0; i < top; i++){
        sum += i * histogram[i];
    }

    for (int i = 0; i<top; i++){
        
        wb += histogram[i];
        if (wb == 0) continue;
        wf = total - wb;
        if (wf == 0) break;
        sumB +=(i*histogram[i]);

        float mB = sumB/wb;
        float mF = (sum - sumB) / wf;
        float val =(float) wb*(float)wf*(mB - mF)*(mB- mF);
        if (val >= max){
            level = i;
            max = val;
        }
    }

    return convert_gray_to_bin(image, level);
}
/*


int main()
{
    RGBImage *image = load_rgb_file("../lena.ppm");

    GrayscaleImage *gray_img = convert_rgb_to_grayscale(image);

    save_grayscale_to_file("lenaR.pgm", gray_img);


    GrayscaleImage **gray_images;
    gray_images = convert_rgb_to_three_grayscale(image);

    save_grayscale_to_file("lenaR.pgm", gray_images[0]);
    save_grayscale_to_file("lenaG.pgm", gray_images[1]);
    save_grayscale_to_file("lenaB.pgm", gray_images[2]);

    int start[2], end[2];
    start[0] = 0, start[1] = 0;
    end[0] = 255, end[1] = 511;
    RGBImage *subsect = get_rgb_subsection(image, start, end);
    save_rgb_to_file("subsection.ppm", subsect);

    int filter_dimension[2] = {3, 3};

    
    double emboss_kernel[9] = {-2, -1, 0, -1, 1, 1, 0, 1, 2};

    apply_rgb_filter(image, emboss_kernel, filter_dimension);
    save_rgb_to_file("filtered_images/lenaEmboss.ppm", image);

    apply_grayscale_filter(gray_img, emboss_kernel, filter_dimension);
    save_grayscale_to_file("filtered_images/lenaEmbossGray.pgm", gray_img);
    
    int filter_dimension[2] = {3, 3};
    RGBImage* logo = load_rgb_file("../logo.ppm");
    RGBImage* new_logo = reduce_image(logo, 115,115);
    save_rgb_to_file("new_small_logo.ppm", new_logo);
    save_rgb_to_file("logo.ppm", logo);
    
    double sobel_kernel[9] = {-1, -2, -1, 0, 0, 0, 1, 2, 1};

    apply_rgb_filter(image, sobel_kernel, filter_dimension);
    save_rgb_to_file("filtered_images/lenaSobelEmboss.ppm", image);

    apply_grayscale_filter(gray_img, sobel_kernel, filter_dimension);
    save_grayscale_to_file("filtered_images/lenaSobelEmbossGray.pgm", gray_img);
    

    double sharpen_kernel[9] = {0, -1, 0, -1, 5, -1, 0, -1, 0};

    apply_rgb_filter(image, sharpen_kernel, filter_dimension);
    save_rgb_to_file("filtered_images/lenaSharpen.ppm", image);

    apply_grayscale_filter(gray_img, sharpen_kernel, filter_dimension);
    save_grayscale_to_file("filtered_images/lenaSharpenGra7.pgm", gray_img);
    
    double outline_kernel[9] = {-1, -1, -1, -1, 8, -1, -1, -1, -1};

    apply_rgb_filter(image, outline_kernel, filter_dimension);
    save_rgb_to_file("filtered_images/lenaOutline.ppm", image);

    apply_grayscale_filter(gray_img, outline_kernel, filter_dimension);
    save_grayscale_to_file("filtered_images/lenaOutlineGray.pgm", gray_img);

    
    double blur_kernel[9] = {0.0625, 0.125, 0.0625, 0.125, 0.25, 0.125, 0.0625, 0.125, 0.0625};

    apply_rgb_filter(image, blur_kernel, filter_dimension);
    save_rgb_to_file("filtered_images/lenaBlur.ppm", image);

    apply_grayscale_filter(gray_img, blur_kernel, filter_dimension);
    save_grayscale_to_file("filtered_images/lenaBlurGray.pgm", gray_img);
    
    int pos[2] = {233,150};
    add_logo_to_image(image, logo, pos, 0.7);

    return 0;
}
*/