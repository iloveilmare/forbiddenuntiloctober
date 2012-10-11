package ac.as;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class cam_thread_UDP extends Thread
{
    int nb = 0;
    
    CamView cv;
    
    byte[] ReadBuf;// ���Ź���
    
    byte[] size; // ũ�����
    
    byte[] mRGB; // ��Ʈ�ʹ�
    
    DatagramPacket dp;
    
    DatagramSocket soc;
    
    int RGB_Size;
    
    StartRecv st;
    
    Message ReadHandMsg; // �ڵ鷯
    
    Handler mHandler;
    
    Options BitMap_option;
    
    Bitmap Bitmap_Buff;
    
    final static int SIZEBUF = 64000;
    
    final static int SizeINT = 4;
    
    private static int byteArrayToInt(byte[] bytes)
    {
        
        final int size = Integer.SIZE / 8;
        ByteBuffer buff = ByteBuffer.allocate(size);
        final byte[] newBytes = new byte[size];
        for (int i = 0; i < size; i++)
        {
            if (i + bytes.length < size)
            {
                newBytes[i] = (byte) 0x00;
            }
            else
            {
                newBytes[i] = bytes[i + bytes.length - size];
            }
        }
        buff = ByteBuffer.wrap(newBytes);
        buff.order(ByteOrder.BIG_ENDIAN);
        return buff.getInt();
    }
    
    public cam_thread_UDP(CamView cv, Handler mHander)
    {
        this.cv = cv;
        this.mHandler = mHander;
        ReadHandMsg = mHander.obtainMessage();
        
        ReadBuf = new byte[SIZEBUF]; // ���Ź���
        size = new byte[SizeINT]; // ũ�����
        mRGB = new byte[SIZEBUF]; // ��Ʈ�ʹ�
        
        /* ��Ʈ�� ����� �������ݴϴ� */
        BitMap_option = new Options();
        BitMap_option.inSampleSize = 2;
        
        /* ��ż��� */
        dp = new DatagramPacket(ReadBuf, ReadBuf.length);
        try
        {
            soc = new DatagramSocket(9000);
        }
        catch (SocketException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    @Override
    public void run()
    {
        Log.d("tel", "redy");
        while (true)
        {
            try
            {
                soc.receive(dp);
                size[3] = ReadBuf[3];
                size[2] = ReadBuf[2];
                size[1] = ReadBuf[1];
                size[0] = ReadBuf[0];
                
                RGB_Size = byteArrayToInt(size);
                Log.d("tel", "recv size:" + RGB_Size);
                System.arraycopy(ReadBuf, 4, mRGB, 0, SIZEBUF - SizeINT);
                cv.mBitmap = BitmapFactory.decodeByteArray(mRGB, 0, RGB_Size, BitMap_option);
                cv.mBitmap = Bitmap.createScaledBitmap(cv.mBitmap, cv.mBitmap.getWidth() * 2,
                                                       cv.mBitmap.getHeight() * 2, false);
                
                Bundle b = new Bundle();
                Message msg = mHandler.obtainMessage();
                msg.setData(b);
                // mHandler.sendEmptyMessage((int)(Math.random()));
                
                mHandler.sendMessage(msg);
                
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }// end while
    }
    
    public void Recive_UDP()
    {
        
    }
    
    @Override
    public void destroy()
    {
        // TODO Auto-generated method stub
        soc.disconnect();
        soc.close();
        
        super.destroy();
    }
    
}
