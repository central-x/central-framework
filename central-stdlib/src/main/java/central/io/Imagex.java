/*
 * MIT License
 *
 * Copyright (c) 2022-present Alan Yeh <alan@yeh.cn>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package central.io;

import central.lang.PublicApi;
import lombok.experimental.UtilityClass;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 图片工具
 *
 * @author Alan Yeh
 * @since 2022/07/05
 */
@PublicApi
@UtilityClass
public class Imagex {
    /**
     * 调整图片大小
     *
     * @param imageFile 图片文件，只支持 PNG
     * @param width     图片宽度
     * @param height    图片高度
     */
    public static void resize(File imageFile, int width, int height) throws IOException {
        var sourceImage = new ImageIcon(imageFile.getCanonicalPath()).getImage();
        var originWidth = sourceImage.getWidth(null);
        var originHeight = sourceImage.getHeight(null);

        // 裁剪
        {
            Rectangle rect;
            if (originWidth * height / width > originHeight) {
                // 截取宽，裁掉多余的高
                var standardWidth = originWidth * height / width;
                rect = new Rectangle((originWidth - standardWidth) / 2, 0, standardWidth, originHeight);
            } else {
                // 截取高，裁掉多余的高
                var standardHeight = originWidth * height / width;
                rect = new Rectangle(0, (originHeight - standardHeight) / 2, originWidth, standardHeight);
            }

            var imageInputStream = ImageIO.createImageInputStream(new FileInputStream(imageFile));
            var imageReader = ImageIO.getImageReadersBySuffix("png").next();
            imageReader.setInput(imageInputStream, true);
            var param = imageReader.getDefaultReadParam();
            param.setSourceRegion(rect);

            var bufferedImage = imageReader.read(0, param);
            ImageIO.write(bufferedImage, "png", imageFile);

            sourceImage = new ImageIcon(imageFile.getCanonicalPath()).getImage();
            originWidth = sourceImage.getWidth(null);
            originHeight = sourceImage.getHeight(null);
        }

        var targetImage = sourceImage.getScaledInstance(originWidth, originHeight, Image.SCALE_SMOOTH);

        // This code ensures that all the pixels in the image are loaded.
        var tmp = new ImageIcon(targetImage).getImage();

        // Create the buffered image.
        var bufferedImage = new BufferedImage(tmp.getWidth(null), tmp.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Copy image to buffered image.
        var graphics = bufferedImage.createGraphics();
        graphics.getDeviceConfiguration().createCompatibleImage(tmp.getWidth(null), tmp.getHeight(null), Transparency.TRANSLUCENT);
        graphics.dispose();

        graphics = bufferedImage.createGraphics();
        graphics.drawImage(tmp, 0, 0, null);
        graphics.dispose();

        // Soften.
        var softenFactor = 0.05f;
        var softenArray = new float[]{0f, softenFactor, 0f, softenFactor, 1 - (softenFactor * 4), softenFactor, 0f, softenFactor, 0f};
        var kernel = new Kernel(3, 3, softenArray);
        var cOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        bufferedImage = cOp.filter(bufferedImage, null);

        ImageIO.write(bufferedImage, "png", imageFile);
    }
}
