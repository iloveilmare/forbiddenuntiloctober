package ac.as;

//TCP-��ȭ:Ȯ�ΰ���
//UDP-����
//Localhost ���α׷��� �������� ��ǻ���� �̸�
//InetAddress Ŭ����
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.io.*;

import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

class SendTread extends Thread
{
    OutputStream output;
    
    byte SendBuf[];
    
    client cli;
    
    SendTread(client cli)
    {
        this.cli = cli;
        this.SendBuf = new byte[cli.MSGLEN];
        
    }
    
    public void set_Send()
    {
        // ������ �����͸� ó����Ű�� �Լ��Դϴ�.
        if (cli.state == cli.Act_State)
        {
            // ó���� ����Ǿ� ������ ������ �κ����� �������� ��Ƽ��Ƽ�� ������ �˸��ϴ�.
            SendBuf[0] = 0x01;
            SendBuf[1] = 0x00;// null�� �ʼ�
        }
        else if (cli.state == cli.Message_State)
        {
            // ��Ƽ��Ƽ�� ����Ǿ��ٴ°��� ���մϴ�.
            // ���� ���������
            SendBuf[0] = 0x02;
            SendBuf[1] = 0x00;// null�� �ʼ�
            
        }
        
    }
    
    @Override
    public void run()
    {
        while (true)
        {
            // �ϴ��� ���۸� �����մϴ�.
            set_Send();
            
            // �� ��Ӻ����ϴ�.
            try
            {
                cli.outputS.write(SendBuf);
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            try
            {
                sleep(cli.sleepTime * 1000);
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }// end while
    }// end run
}// end class

class ReadTread extends Thread
{
    OutputStream output;
    
    final byte grap_header = 0x01;
    
    final byte Message_header = 0x02;
    
    client cli;
    
    Message ReadHandMsg;
    
    int nb = 0;
    
    CamView cv;
    
    byte[] ReadBuf;// ���Ź���
    
    byte[] size; // ũ�����
    
    byte[] mRGB; // ��Ʈ�ʹ�
    
    DatagramPacket dp;
    
    DatagramSocket soc;
    
    int RGB_Size;
    
    StartRecv st;
    
    Handler mHandler;
    
    final static int SIZEBUF = 64000;
    
    final static int SizeINT = 4;
    
    ReadTread(client cli)
    {
        this.cli = cli;
        this.ReadBuf = new byte[cli.MSGLEN];
        ReadHandMsg = cli.Cli_Hander.obtainMessage();
        this.cv = cv;
        
        ReadBuf = new byte[SIZEBUF]; // ���Ź���
        size = new byte[SizeINT]; // ũ�����
        mRGB = new byte[SIZEBUF]; // ��Ʈ�ʹ�
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
                cv.mBitmap = BitmapFactory.decodeByteArray(mRGB, 0, RGB_Size);
                ReadHandMsg = mHandler.obtainMessage();
                mHandler.sendEmptyMessage((int) (Math.random()));
                mHandler.sendMessage(ReadHandMsg);
                cli.Cli_Hander.sendEmptyMessageDelayed((int) (Math.random()), 10);
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }// end while
    }// end run
}// end class

// Ŭ���̾�Ʈ
class client
{
    Socket s;
    
    InputStream inputS;
    
    Handler Cli_Hander;
    
    final byte end = 0x00;
    
    OutputStream outputS;
    
    StringBuffer Msg = new StringBuffer("");
    
    final int MSGLEN = 80;
    
    final int Act_State = 0;
    
    final int Message_State = 1;
    
    byte buffCh[] = new byte[MSGLEN]; // msg�� byte��ȯ�� ���� �迭
    
    byte sendBuf[] = new byte[MSGLEN]; // �ڿ� null�� ���� �迭
    
    byte readBuf[] = new byte[MSGLEN]; // �ڿ� null�� ���� �迭
    
    int state;
    
    boolean sendFlag;
    
    CamView sv;
    
    int sleepTime;
    
    Thread send;
    
    Thread read;
    
    public client(CamView sv, Handler Cli_Hander) throws UnknownHostException, IOException
    {
        this.Cli_Hander = Cli_Hander;
        sendFlag = false;
        
        state = Act_State;
        sleepTime = 1;
        // 3.���ϻ���
        
        read = new ReadTread(this);
        read.start();
        
    }// ������
    
    public client(String ip, int port, Handler Cli_Hander) throws UnknownHostException, IOException
    {
        /*
         * �޼������ۿ��� ���Ǵ� �������Դϴ�. ���ѹ��� �޼����� �޴°��� �����Դϴ�.
         */
        this.Cli_Hander = Cli_Hander;
        sendFlag = false;
        state = Act_State;
        sleepTime = 1;
        
        read = new ReadTread(this);
        read.start();
        
    }// ������
    
    public String oneShot()
    {
        return null;
        
    }
    
    public void close()
    {
        try
        {
            send.stop();
            read.stop();
            inputS.close();
            outputS.close();
            s.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}