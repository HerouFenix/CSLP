/**
 * @file
 * @brief File containing RGB related methods and structures
*/

//STRUCTURE DECLARATIONS

/// Structure used to represent RGB pixels
/**
 *  Unsigned Char since we only need to specify values from 0 to 255, 
 * which can be done with 1 byte (which corresponds to the size of an unsigned char)
*/
typedef struct
{
    unsigned char R;
    unsigned char G;
    unsigned char B;
} Colour;

/// Structure used to represent an RGB image
/**
 *  The way we chose to represent our pixels was through the use of an array of chars (1 byte structures). 
 *  Instead of thinking of the image as a bidimensional array, we simply used a linear array noting that the image position can be given by the formula:
 *  position = line*noOfColumns+column
*/
typedef struct
{
    int width, height;
    Colour *pixel_array;
} RGBImage;


//FUNCTION DECLARATIONS

/********************************************/ /**
 * Function used to save an RGB image to a file
 *
 * @param file_name The File's path on which we'll be saving the image
 * @param image The RGB Image we want to save
 ***********************************************/
int save_rgb_to_file(char *file_name, RGBImage *image);

/********************************************/ /**
 * Function used to load an RGB image from a file
 *
 * @param file_name The File's path on which we'll be saving the image
 ***********************************************/
RGBImage *load_rgb_file(char *file_name);

/********************************************/ /**
 * Function used to access a specific pixel within an RGB image
 * If a row or col that goes outside the image's bounds is given,
 * the function returns a Null_Pixel (an RGB Pixel with all it's values set to 0)
 *
 * @param image The RGB Image we want to save
 * @param row The pixel's row
 * @param col The pixel's column
 ***********************************************/
Colour get_rgb_pixel(RGBImage *image, int line, int col);

/********************************************/ /**
 * Function used to access a subsection of image
 *
 * @param image The RGB image we want to subsect
 * @param pos_start An array containing the subsection's starting starting (left-top corner of subsection) row number and col number
 * @param pos_end An array containing the subsection's ending (bottom-right corner of subsection) row number and col number
 ***********************************************/
RGBImage *get_rgb_subsection(RGBImage *image, int pos_start[2], int pos_end[2]);

/********************************************/ /**
 * Function used to clone an image
 *
 * @param image The Image we want to clone
 ***********************************************/
RGBImage *copy_rgb_image(RGBImage *image);


/********************************************/ /**
 * Function used to change an RGB Image's R, G and B intensities
 *
 * @param image The Image we want to change the intensity of
 * @param pixel_intensity An array with an R,G and B intensity modifier
 ***********************************************/
void change_rgb_intensity(RGBImage *image, int *pixel_intensity);
