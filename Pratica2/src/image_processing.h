
/**
 * @file
 * @brief File containing methods that involve different types of image structures
*/

//FUNCTION DECLARATIONS
 
/********************************************/ /**
 * Function used to create a grayscale image out of an rgb one by computing all of its r, g and b parameters
 *
 * @param rgb_image The RGB Image we want to convert
 ***********************************************/
GrayscaleImage* convert_rgb_to_grayscale(RGBImage *rgb_image);

/********************************************/ /**
 * Function used to create a grayscale image out of a single parameter of an RGB one (R, G or B). Returns an array of GrayScaleImages where it's positions
 * correspond to the Red Grayscale, Green Grayscale and Blue Grayscale (in this order)
 *
 * @param rgb_image The RGB image we want to convert into 3 Grayscales
 ***********************************************/
GrayscaleImage** convert_rgb_to_three_grayscale(RGBImage *rgb_image);

/********************************************/ /**
 * Function used to create a binary image out of a grayscale image with a provided threshold. 
 *
 * @param image The Grayscale image to be converted
 * @param threshold An unsigned char that will provide as a threshold to know what to input as black or white in the binary image
 ***********************************************/
BinaryImage* convert_gray_to_bin(GrayscaleImage *image, unsigned char threshold);

/********************************************/ /**
 * Function used to create a binary image out of a grayscale image automatically creating an appropriate threshold for the image using the Otsu Algorithm
 *
 * @param image The Grayscale image to be converted
 ***********************************************/
BinaryImage* convert_gray_to_bin_otsu(GrayscaleImage *image);

/********************************************/ /**

 * Function used to add an rgb logo to an rgb image
 *
 * @param rgb_image The RGB image we want to add our logo to
 * @param logo The RGB logo we want to add to our image
 * @param pixel The pixel we want our logo to start in
 * @param intensity The intensity of the logo to add to image
 ***********************************************/
void add_logo_to_image(RGBImage *rgb_image, RGBImage *logo, int* pixel, float intensity);
/********************************************/ /**
 * Function used to apply a filter kernel on a given RGB image, returning 0 if it succeeded or 1 if it failed
 *
 * @param image The RGB image we want to apply the filter to
 * @param kernel The Kernel array (as a linear array representing a 2d value matrix)
 * @param image The Kernel's dimensions (filter_dimensions[0] - # of rows ; filter_dimension[1] - # of cols)

 ***********************************************/
int apply_rgb_filter(RGBImage *image, double *kernel, int filter_dimension[2]);

/********************************************/ /**
 * Function used to apply a filter kernel on a given Grayscale image, returning 0 if it succeeded or 1 if it failed
 *
 * @param image The Grayscale image we want to apply the filter to
 * @param kernel The Kernel array (as a linear array representing a 2d value matrix)
 * @param image The Kernel's dimensions (filter_dimensions[0] - # of rows ; filter_dimension[1] - # of cols)

 ***********************************************/
int apply_grayscale_filter(GrayscaleImage *image, double *kernel, int filter_dimension[2]);

/********************************************/ /**
 * Function used to reduce an RGB image to a new height and width. Due to lack of time constraints this only works to make it smaller, not time to make it big
 * 
 * @param image The RGB image we want to resize
 * @param new_height The RGB image's new height
 * @param new_width The RGB image's new width
 ***********************************************/
RGBImage* reduce_image(RGBImage* image, int new_height, int new_width);
