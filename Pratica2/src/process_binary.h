/**
 * @file
 * @brief File containing Binary related methods and structures
*/

//STRUCTURE DECLARATIONS

/// Structure used to represent Binary images
/**
 *  The way we chose to represent our bits was through the use of an array of chars (1 byte structures). 
 *  Instead of thinking of the image as a bidimensional array, we simply used a linear array noting that the image position can be given by the formula:
 *  position = line*noOfColumns+column
*/
typedef struct
{
    int height, width;
    unsigned char *bin_array;
} BinaryImage;


// FUNCTION DECLARATIONS

/********************************************/ /**
 * Function used to save a Binary image
 *
 * @param file_name The File's path to which we want to save our image
 * @param image The Binary image we want to save
 ***********************************************/
int save_to_bin_file(char *file_name, BinaryImage *image);

/********************************************/ /**
 * Function used to load a Binary image
 *
 * @param file_name The File's path containing a Binary Image (PBM - P4)
 ***********************************************/
BinaryImage* load_bin_file(char *file_name);

/********************************************/ /**
 * Function used to acess a specific pixel within a Binary Image
 * 
 * @param image A pointer to a binary image
 * @param line The y coordinate for our bit
 * @param col The x coordinate
************************************************/
unsigned char access_bin_pixel(BinaryImage *image, int line, int col);

///Function used to acess a subsection of image
/********************************************/ /**
 * Function used to access a subsection of image
 * 
 * @param image A pointer to a binary image to be copied
 * @param pos_start The position where the subsection must start
 * @param pos_end The position where the subsection must end
************************************************/
BinaryImage *get_bin_subsection(BinaryImage *image, int *pos_start, int *pos_end);

/********************************************/ /**
 * Function used to fully copy to memory an existing Binary Image
 * 
 * @param image A pointer to a binary image to be copied
************************************************/
BinaryImage *copy_binary_image(BinaryImage *image);