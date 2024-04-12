package com.yannqing.dockerdesktop.socket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class MyWebSocketHandler extends TextWebSocketHandler {
    private final Timer timer = new Timer();
    private Robot robot;

    /**
     * webSocket建立连接之后的操作
     * @param session
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // 开始发送图像数据
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    BufferedImage image = captureScreen();
                    byte[] imageData = imageToBytes(image);
                    session.sendMessage(new TextMessage(imageData));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1000); // 每隔一秒发送一次图像数据
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // 处理收到的消息
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // 处理连接关闭
    }


    private BufferedImage captureScreen() {
        // 捕获屏幕图像
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        return robot.createScreenCapture(screenRect);
    }

    private byte[] imageToBytes(BufferedImage image) throws IOException {
        // 将图像转换为字节数组
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        return outputStream.toByteArray();
    }
}
