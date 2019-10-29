/**
 * @file
 * @brief File containing Grayscale related methods and structures
*/

//STRUCTURE DECLARATIONS

/// Structure used to represent Grayscale pixels
/**
 *  Unsigned Char since we only need to specify values 
 * from 0 to 255, which can be done with 1 byte (which corresponds to the size of an unsigned char)
*/
typedef struct
{
    unsigned char Gray;
} Grayscale;

/// Structure used to represent Grayscale images
/**
 *  The way we chose to represent our pixels was through the use of an array of chars (1 byte structures). 
 *  Instead of thinking of the image as a bidimensional array, we simply used a linear array noting that the image position can be given by the formula:
 *  position = line*noOfColumns+column
*/
typedef struct
{
    int width, height;
    Grayscale *pixel_array;
} GrayscaleImage;

//FUNCTION DECLARATIONS

/********************************************/ /**
 * Function used to save a Grayscale image to a file
 *
 * @param file_name The File's path on which we'll be saving the image
 * @param image The Grayscale Image we want to save
 ***********************************************/
int save_grayscale_to_file(char *file_name, GrayscaleImage *image);

/********************************************/ /**
 * Function used to load a Grayscale image
 *
 * @param file_name The File's path containing a Grayscale Image (PGM - P5)
 ***********************************************/
GrayscaleImage *load_grayscale_file(char *file_name);

/********************************************/ /**
 * Function used to acess a specific pixel within a Grayscale Image
 * If a row or col that goes outside the image's bounds is given,
 * the function returns a Null_Pixel (a Grayscale Pxel with it's gray value set to 0)
 *
 * @param image The Grayscale image we want to subsect
 * @param row The pixel's row 
 * @param col The pixel's column
 ***********************************************/
Grayscale get_grayscale_pixel(GrayscaleImage *image, int line, int col);

/*
 * Function used to acess a subsection of image
 *
 * @param image The Grayscale image we want to subsect
 * @param pos_start An array containing the subsection's starting starting (left-top corner) x and y coordinates
 * @param pos_end An array containing the subsection's ending (bottom-right corner) x and y coordinates
*/
GrayscaleImage *get_grayscale_subsection(GrayscaleImage *image, int pos_start[2], int pos_end[2]);

/********************************************/ /**
 * Function used to clone an image
 *
 * @param image The Image we want to clone
 ***********************************************/
GrayscaleImage *copy_grayscale_image(GrayscaleImage *image);

/********************************************/ /**
 * Function used to change a Grayscale Image's intensity
 *
 * @param image The Image we want to change the intensity of
 * @param pixel_intensity Our intensity modifier
 ***********************************************/
void change_gray_intensity(GrayscaleImage *image, int pixel_intensity);