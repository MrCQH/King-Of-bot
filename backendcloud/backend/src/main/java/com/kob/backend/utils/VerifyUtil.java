package com.kob.backend.utils;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Random;

@Component
//  生成随机验证码
public class VerifyUtil {
    private static final Random random = new Random();
    private final int width = 300;// 宽
    private final int height = 50;// 高
    private final int lineSize = 50;// 干扰线数量
    private final int stringNum = 4;//随机产生字符的个数
    private StringBuffer backupRandomString = new StringBuffer();

    private final String randomString = "0123456789abcdefghijklmnopqrstuvwxyz";

    private final String sessionKey = "RANDOMKEY";

    public String getRandomString(){
        return this.backupRandomString.toString();
    }

    /*
     *  获取字体
     */
    private Font getFont() {
        return new Font("Times New Roman", Font.ROMAN_BASELINE, 40);
    }

    /*
     *  获取颜色
     */
    private static Color getRandomColor(int fc, int bc) {

        fc = Math.min(fc, 255);
        bc = Math.min(bc, 255);

        int r = fc + random.nextInt(bc - fc - 16);
        int g = fc + random.nextInt(bc - fc - 14);
        int b = fc + random.nextInt(bc - fc - 12);

        return new Color(r, g, b);
    }

    /*
     *  绘制干扰线
     */
    private void drawLine(Graphics g) {
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        int xl = random.nextInt(50);
        int yl = random.nextInt(40);
        g.drawLine(x, y, x + xl, y + yl);
    }

    /*
     *  获取随机字符
     */
    private String getRandomString(int num) {
        num = num > 0 ? num : randomString.length();
        return String.valueOf(randomString.charAt(random.nextInt(num)));
    }

    /*
     *  绘制字符串
     */
    private String drawString(Graphics g, String randomStr, int i) {
        g.setFont(getFont());
        g.setColor(getRandomColor(108, 190));
        String rand = getRandomString(random.nextInt(randomString.length()));
        randomStr += rand;
        g.translate(random.nextInt(50), random.nextInt(6));
        g.drawString(rand, 40 * i + 10, 25);
        return randomStr;
    }

    /*
     *  生成随机图片，返回 base64 字符串
     */
    public String getRandomCodeBase64(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        // BufferedImage类是具有缓冲区的Image类,Image类是用于描述图像信息的类
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        Graphics g = image.getGraphics();
        g.fillRect(0, 0, width, height);
        g.setColor(getRandomColor(105, 189));
        g.setFont(getFont());

        // 绘制干扰线
        for (int i = 0; i < lineSize; i++) {
            drawLine(g);
        }

        // 绘制随机字符
        backupRandomString.setLength(0);
        String random_string = "";
        for (int i = 0; i < stringNum; i++) {
            random_string = drawString(g, random_string, i);
        }
        backupRandomString.append(random_string);


        g.dispose();

        session.removeAttribute(sessionKey);
        session.setAttribute(sessionKey, random_string);

        String base64String = "";
        try {
            //  直接返回图片
            //  ImageIO.write(image, "PNG", response.getOutputStream());
            //返回 base64
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", bos);

            byte[] bytes = bos.toByteArray();
            Base64.Encoder encoder = Base64.getEncoder();
            base64String = encoder.encodeToString(bytes);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return base64String;
    }
}
