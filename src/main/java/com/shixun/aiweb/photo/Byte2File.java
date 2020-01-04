package com.shixun.aiweb.photo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.List;
import java.util.UUID;

/**
 * com.caoqiang.code
 *
 * @desc
 * @author:Administrator
 * @year: 2018
 * @month: 06
 * @day: 07
 * @time: 2018/06/07 0007
 */
//继承一个处理器 这个处理器能接受传入的buf ,也能返回字节数组
public class Byte2File extends ByteToMessageCodec<String> {
    int remainSize = 0;//表示该文件还剩余多少字节需要接收
    int fileIndex = 0;//表示文件字节数组当前的索引
    boolean isHalf = false;//表示文件数据是否接收完
    byte[] wholeFile;//整个文件字节数组
    int i = 0;

    //设置APPID/AK/SK , 待优化
    public static final String APP_ID = "11469861";
    public static final String API_KEY = "2NmBChTTNG33BVixnbiZiMLR";
    public static final String SECRET_KEY = "l2gjFmGcxgnUUw6WIBQIGUVQkEqqnGNM";


    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
        //wiriteandflush的参数即为这个方法接收道德msg值
        if (msg.equals("success")) {
            out.writeBytes(new byte[]{(byte) 0x00});
        } else {
            out.writeBytes(new byte[]{(byte) 0xff});
        }
        //System.out.println("Byte2Str编码后： "+msg.getBytes("UTF-8"));

    }

    @Override
    //入站处理 in 即表示TCP包的实际内容
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("单包长度：" + in.readableBytes());
        if (isHalf) {//假如数据未全部接收完
            int readableSize = in.readableBytes();//表示此次接收到的数据长度
            //本次接收的字节数小于数组剩余空间
            if (readableSize < remainSize) {
                //把全部接收到的数据放入图片数组中
                in.readBytes(wholeFile, fileIndex, readableSize);
                //最新的剩余空间
                remainSize = remainSize - readableSize;
                fileIndex = fileIndex + readableSize;
            } else {//本次接收的字节数大于数组剩余空间
                //把图片数组填满，然后重置remainSize和fileIndex
                in.readBytes(wholeFile, fileIndex, remainSize);
                remainSize = 0;
                fileIndex = 0;
                //设置是否半包为false 表示图片数组已满
                isHalf = false;

//                System.out.println("=======================================");

                //存储文件
                String filePath = saveFile(wholeFile);


                //返回响应
                ctx.channel().writeAndFlush("success");
            }
            return;
        }

        //接收到的数据属于一个新的图片
        int imgSize = in.readInt();//读取图片长度
        wholeFile = new byte[imgSize];
        int dataSize = in.readableBytes();//剩余可读取长度；
        //int dataSize = 0;
        System.out.println("图片长度：" + imgSize + "  第一个包接收长度" + dataSize);
        if (dataSize < imgSize) {//说明此包数据未传完，会在下个包继续传输数据
            remainSize = imgSize - dataSize;
            isHalf = true;
            in.readBytes(wholeFile, 0, dataSize);
            fileIndex = fileIndex + dataSize;
        } else {
            in.readBytes(wholeFile, 0, imgSize);

            //存储文件
            saveFile(wholeFile);

            //返回响应
            ctx.channel().writeAndFlush("success");
        }
    }



    String saveFile(byte[] wholeFile) {

        //图片存储的目录
        String picDir = this.getClass().getClassLoader().getResource("./imgs").getPath();
        //图片存储的名称
        String picName = "plates" + (i++) + ".jpg";
        //进行存储操作
        File dir = new File(picDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, picName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            fos.write(wholeFile);
            fos.close();

            String path = file.getPath();
            System.out.println("图片存储文件名：" + path);
            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
