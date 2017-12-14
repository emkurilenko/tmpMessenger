package userAction;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Compression {
    public static BufferedImage compress(BufferedImage image,float quality){
        try {
            ByteArrayOutputStream compressed = new ByteArrayOutputStream();
            ImageOutputStream outputStream = ImageIO.createImageOutputStream(compressed);


            ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();

            ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
            jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            jpgWriteParam.setCompressionQuality(quality);

            jpgWriter.setOutput(outputStream);
            jpgWriter.write(null, new IIOImage(image, null, null), jpgWriteParam);
            jpgWriter.dispose();
            byte[] jpegData = compressed.toByteArray();

            return createImageFromBytes(jpegData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static BufferedImage createImageFromBytes(byte[] imageData) {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
        try {
            return ImageIO.read(bais);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


