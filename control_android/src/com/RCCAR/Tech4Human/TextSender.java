package com.RCCAR.Tech4Human;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.util.Log;

/**
 * @author Administrator UDP���� �����ϴ� �κ�
 * 
 */
public class TextSender
{
    private final int port = 8500; // �������� ������ UDP ��Ʈ��ȣ�� �߰��մϴ�.
    
    private final String serverIP;
    
    private DatagramSocket socket;
    
    // edittext���� ���ڿ��� �޾Ƽ� �������� ������ �κ�
    public TextSender(String serverIP)
    {
        this.serverIP = serverIP;
    }
    
    // Udp ������ �����ϱ� ���� �޼ҵ�
    public void run(String msg)
    {
        try
        {
            // ������ ����ϴ�.
            socket = new DatagramSocket();
            if (serverIP != null)
            {
                InetAddress serverAddr = InetAddress.getByName(serverIP);
                Log.d("UDP", serverIP);
                // TCP�� �ٸ��� UDP�� byte������ �����͸� �����մϴ�. �׷��� byte�� �������ݴϴ�.
                byte[] buf = new byte[128];
                
                // �޾ƿ� msg�� ����Ʈ ������ �����մϴ�.
                // DatagramPacket�� �̿��Ͽ� ������ �����մϴ�.
                DatagramPacket Packet = new DatagramPacket(buf, buf.length, serverAddr, port);
                // ����κ��� �ɽ��ϴ�.
                buf[0] = 0x22;
                buf[1] = (byte) msg.getBytes("MS949").length;
                System.arraycopy(msg.getBytes("MS949"), 0, buf, 2, buf[1]);
                Log.d("UDP", "sendpacket.... " + new String(buf));
                // ������ �����մϴ�.
                socket.send(Packet);
                Log.d("UDP", "send....");
                Log.d("UDP", "Done.");
            }
        }
        catch (Exception ex)
        {
            Log.d("UDP", "C: Error", ex);
        }
        finally
        {
            if (socket != null)
            {
                socket.close();
            }
        }
    }
}