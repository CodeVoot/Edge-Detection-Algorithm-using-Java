import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageFilter {

    /*
     No. Pixelated comes from trying to enlarge a small pixel image.
     There are not enough pixels with low resolution. Blur is caused by movement.
    */

    public static void main(String[] args) {
        File file = new File("D:\\Java CSE406\\Edge Detection\\src\\photo.jpeg");

        //  BufferedImage is comprised of a ColorModel and a Raster of image data.
        BufferedImage img = null;

        try{
            img = ImageIO.read(file);
        }
        catch(IOException e){
            e.printStackTrace(System.out);
            //s a statement used to print the stack trace of an exception to the standard output stream.
        }

        if(img!=null){
            display(img);
//            img = toGrayScale(img);
//            display(img);

            // 2X2 pixelation
//             img = pixelate(img);
//            display(img);


            // NXN pixelation
//            img = pixelateN(img,100);
//            display(img);

            // resize image
//             img = resize(img,150); // to new height
//             display(img);

            // Gaussian blur
//            img = blur(img);
//            display(img);

            img = blur(blur(img));
            display(img);

//            img = heavyblur(img);
//            display(img);

//            img = detectEdges(img);
//            display(img);

        }
    }

    // display image in jpanel
    public static void display(BufferedImage img) {
        System.out.println("Displaying image");
        JFrame f = new JFrame("Edge Detection");
        JLabel l = new JLabel();
        f.setSize(700,800);
        l.setIcon(new ImageIcon(img));
        f.getContentPane().add(l, BorderLayout.CENTER);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setVisible(true);
        //f.pack(); // automatically resizes frame according to the content
    }

    // trying to convert the image into black and white image
    public static BufferedImage toGrayScale(BufferedImage img){
        System.out.println( "Converting the gray scale");

        // TYPE_BYTE_GRAY: each pixel in the image is represented by a single byte
        BufferedImage grayImage = new BufferedImage(
                img.getWidth(),img.getHeight(),BufferedImage.TYPE_BYTE_GRAY
        );

        // now convert the image by iterating through each pixel one by one
        int rgb=0,r=0,g=0,b=0;
        for(int y=0;y<img.getHeight();y++){
            for(int x=0;x<img.getWidth();x++){
                rgb = img.getRGB(x,y); // gives the rgb value at particular pixel
                r = ((rgb>>16) & 0xFF); // 3rd rightmost bit
                g = ((rgb>>8) & 0xFF); // 2nd rightmost bit // 0xFF = 11111111
                b = rgb & 0xFF; // 1st rightmost bit
                rgb = (r+g+b)/3;
                rgb =  255<<24 | rgb<<16 | rgb<<8 | rgb; // packing of all color data
                //       alpha   red     green   blue
                grayImage.setRGB(x,y,rgb);
            }
        }
        return grayImage;
     }



     // pixelation ---- means reduce the resolution of an image
     // pixelation of 2X2
     public static BufferedImage pixelate(BufferedImage img){
         // TYPE_BYTE_GRAY: each pixel in the image is represented by a single byte
         BufferedImage pixImg = new BufferedImage(
                 img.getWidth(),img.getHeight(),BufferedImage.TYPE_BYTE_GRAY
         );
         int pix=0,p=0;

         for(int y=0;y<img.getHeight()-2;y+=2){ // 3
             for(int x=0;x<img.getWidth()-2;x+=2){ // 4
                 pix = ((img.getRGB(x,y) & 0xFF) + (img.getRGB(x+1,y) & 0xFF) +
                         (img.getRGB(x,y+1) & 0xFF) + (img.getRGB(x+1,y+1) & 0xFF))/4;
                 p =  255<<24 | pix<<16 | pix<<8 | pix; // packing of all color data
                 pixImg.setRGB(x,y,p);
                 pixImg.setRGB(x+1,y,p);
                 pixImg.setRGB(x,y+1,p);
                 pixImg.setRGB(x+1,y+1,p);
             }
         }
         return pixImg;
     }



     // nXn pixelation
     public static BufferedImage pixelateN(BufferedImage img,int n){

         // TYPE_BYTE_GRAY: each pixel in the image is represented by a single byte
         BufferedImage pixImg = new BufferedImage(
                 img.getWidth(),img.getHeight(),BufferedImage.TYPE_BYTE_GRAY
         );
         int pix=0,p=0;
         for(int y=0;y<img.getHeight()-n;y+=n){
             for(int x=0;x<img.getWidth()-n;x+=n){
                for(int a=0;a<n;a++){
                    for(int b=0;b<n;b++){
                        pix += (img.getRGB(x+a,y+b) & 0xFF);
                    }
                }
                pix = pix/n/n; // applying nXn pixels
                 for(int a=0;a<n;a++){
                     for(int b=0;b<n;b++){
                         p =  255<<24 | pix<<16 | pix<<8 | pix; // packing of all color data
                         pixImg.setRGB(x+a,y+b,p);
                     }
                 }
                 pix = 0;
             }
         }
         return pixImg;
     }



     // resize image
     public static BufferedImage resize(BufferedImage img,int newHeight){
         System.out.println("Scaling Image");
         double scaleFactor = (double) newHeight / img.getHeight();
         BufferedImage scaledImg = new BufferedImage(
                 (int)(scaleFactor*img.getWidth()),newHeight,BufferedImage.TYPE_BYTE_GRAY
         );
         // performs a linear mapping from 2D coordinates to other 2D coordinates
         AffineTransform at = new AffineTransform();
         at.scale(scaleFactor,scaleFactor);
         AffineTransformOp scaleOp = new AffineTransformOp(at,AffineTransformOp.TYPE_BILINEAR);
         return scaleOp.filter(img,scaledImg);
     }


     // blur image
     // 3x3 Gaussian blur to a grayscale image
     public static BufferedImage blur (BufferedImage img) {
         BufferedImage blurImg = new BufferedImage(
                 img.getWidth()-2, img.getHeight()-2, BufferedImage.TYPE_BYTE_GRAY);

         int pix = 0;

         for (int y=0; y<blurImg.getHeight(); y++) {
             for (int x=0; x<blurImg.getWidth(); x++) {
                 pix = (int)(4*(img.getRGB(x+1, y+1)& 0xFF)
                         + 2*(img.getRGB(x+1, y)& 0xFF)
                         + 2*(img.getRGB(x+1, y+2)& 0xFF)
                         + 2*(img.getRGB(x, y+1)& 0xFF)
                         + 2*(img.getRGB(x+2, y+1)& 0xFF)
                         + (img.getRGB(x, y)& 0xFF)
                         + (img.getRGB(x, y+2)& 0xFF)
                         + (img.getRGB(x+2, y)& 0xFF)
                         + (img.getRGB(x+2, y+2)& 0xFF))/16;
                 int p = (255<<24) | (pix<<16) | (pix<<8) | pix;
                 blurImg.setRGB(x,y,p);
             }
         }
         return blurImg;
     }



     // blur image
     // 5x5 Gaussian blur to a grayscale image
    public static BufferedImage heavyblur (BufferedImage img) {
        BufferedImage blurImg = new BufferedImage(
                img.getWidth()-4, img.getHeight()-4, BufferedImage.TYPE_BYTE_GRAY);
        int pix = 0;
        for (int y=0; y<blurImg.getHeight(); y++) {
            for (int x=0; x<blurImg.getWidth(); x++) {
                pix = (int)(
                                10*(img.getRGB(x+3, y+3)& 0xFF)
                                + 6*(img.getRGB(x+2, y+1)& 0xFF)
                                + 6*(img.getRGB(x+1, y+2)& 0xFF)
                                + 6*(img.getRGB(x+2, y+3)& 0xFF)
                                + 6*(img.getRGB(x+3, y+2)& 0xFF)
                                + 4*(img.getRGB(x+1, y+1)& 0xFF)
                                + 4*(img.getRGB(x+1, y+3)& 0xFF)
                                + 4*(img.getRGB(x+3, y+1)& 0xFF)
                                + 4*(img.getRGB(x+3, y+3)& 0xFF)
                                + 2*(img.getRGB(x, y+1)& 0xFF)
                                + 2*(img.getRGB(x, y+2)& 0xFF)
                                + 2*(img.getRGB(x, y+3)& 0xFF)
                                + 2*(img.getRGB(x+4, y+1)& 0xFF)
                                + 2*(img.getRGB(x+4, y+2)& 0xFF)
                                + 2*(img.getRGB(x+4, y+3)& 0xFF)
                                + 2*(img.getRGB(x+1, y)& 0xFF)
                                + 2*(img.getRGB(x+2, y)& 0xFF)
                                + 2*(img.getRGB(x+3, y)& 0xFF)
                                + 2*(img.getRGB(x+1, y+4)& 0xFF)
                                + 2*(img.getRGB(x+2, y+4)& 0xFF)
                                + 2*(img.getRGB(x+3, y+4)& 0xFF)
                                + (img.getRGB(x, y)& 0xFF)
                                + (img.getRGB(x, y+2)& 0xFF)
                                + (img.getRGB(x+2, y)& 0xFF)
                                + (img.getRGB(x+2, y+2)& 0xFF))/74;
                int p = (255<<24) | (pix<<16) | (pix<<8) | pix;
                blurImg.setRGB(x,y,p);
            }
        }
        return blurImg;
    }




    // detect edges of a grayscale image using Sobel algorithm
    // (for best results, apply blur before finding edges)
    public static BufferedImage detectEdges (BufferedImage img) {
        int h = img.getHeight(), w = img.getWidth(), threshold_range=30, p = 0;
        BufferedImage edgeImg = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        int[][] vert = new int[w][h];
        int[][] horiz = new int[w][h];
        int[][] edgeWeight = new int[w][h];

        for (int y=1; y<h-1; y++) {
            for (int x=1; x<w-1; x++) {

                vert[x][y] = (int)(img.getRGB(x+1, y-1)& 0xFF + 2*(img.getRGB(x+1, y)& 0xFF) + img.getRGB(x+1, y+1)& 0xFF
                        - img.getRGB(x-1, y-1)& 0xFF - 2*(img.getRGB(x-1, y)& 0xFF) - img.getRGB(x-1, y+1)& 0xFF);

                horiz[x][y] = (int)(img.getRGB(x-1, y+1)& 0xFF + 2*(img.getRGB(x, y+1)& 0xFF) + img.getRGB(x+1, y+1)& 0xFF
                        - img.getRGB(x-1, y-1)& 0xFF - 2*(img.getRGB(x, y-1)& 0xFF) - img.getRGB(x+1, y-1)& 0xFF);

                edgeWeight[x][y] = (int)(Math.sqrt(vert[x][y] * vert[x][y] + horiz[x][y] * horiz[x][y]));

                if (edgeWeight[x][y] > threshold_range) // make it white
                    p = (255<<24) | (255<<16) | (255<<8) | 255;
                else         // make it black
                    p = (255<<24) | (0<<16) | (0<<8) | 0;
                edgeImg.setRGB(x,y,p);
            }
        }
        return edgeImg;
    }






}
