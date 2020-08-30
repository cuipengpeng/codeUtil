package com.test.xcamera.test.video;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by smz on 2020/5/28.
 */

public class CameraTest {
    public LinkedBlockingQueue<Frame> mFrmList = new LinkedBlockingQueue<>();

    public Camera m_camera;
    int width = 1920;
    int height = 1080;
    int framerate = 1;
    int bitrate = 2500000;
    AvcEncoder avcCodec;


    public void open(SurfaceHolder holder) {
        try {
            avcCodec = new AvcEncoder(width, height, framerate, bitrate);
        } catch (IOException e1) {
            Log.d("Fuck", "Fail to AvcEncoder");
        }
        m_camera = Camera.open();
        try {
            m_camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Camera.Parameters parameters = m_camera.getParameters();
        parameters.setPreviewSize(width, height);
        parameters.setPictureSize(width, height);
        parameters.setPreviewFormat(ImageFormat.YV12);
        parameters.set("rotation", 90);
        //parameters.set("orientation", "portrait");
        m_camera.setParameters(parameters);
        m_camera.setDisplayOrientation(90);
        m_camera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                byte[] h264 = new byte[width * height * 3 / 2];
                int ret = avcCodec.offerEncoder(data, h264);

                Frame frame = new Frame();
                frame.len = ret;
                frame.data = h264;
                mFrmList.offer(frame);

                Log.e("=====", mFrmList.size() + "---" + ret);
//                ll.camera(frame);

//                    if (ret > 0) {
//                        try {
//                            byte[] length_bytes = intToBytes(ret);
//                            file.write(length_bytes);
//                            file.write(h264, 0, ret);
//                            //file.flush();
//                            //byteOffset += h264.length;
//                            //DatagramPacket packet=new DatagramPacket(h264,ret, address,5000);
//                            //socket.send(packet);
//                        } catch (IOException e) {
//                            Log.d("Fuck", "@@@@@@@@ exception: " + e.toString());
//                        }
            }
        });
        m_camera.startPreview();
    }

    public void close() {
        m_camera.setPreviewCallback(null); //m_camera.stopPreview();
        m_camera.release();
        m_camera = null;
        avcCodec.close();
    }

    public static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        src[3] = (byte) ((value >> 24) & 0xFF);
        src[2] = (byte) ((value >> 16) & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return src;
    }

    public interface OnCamera {
        void camera(Frame frame);
    }

    public class Frame {
        public int len;
        public byte data[];
    }
}
