/*******************************************************************************************************
Copyright (c) 2011 Regents of the University of California.
All rights reserved.

This software was developed at the University of California, Irvine.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in
   the documentation and/or other materials provided with the
   distribution.

3. All advertising materials mentioning features or use of this
   software must display the following acknowledgment:
   "This product includes software developed at the University of
   California, Irvine by Nicolas Oros, Ph.D.
   (http://www.cogsci.uci.edu/~noros/)."

4. The name of the University may not be used to endorse or promote
   products derived from this software without specific prior written
   permission.

5. Redistributions of any form whatsoever must retain the following
   acknowledgment:
   "This product includes software developed at the University of
   California, Irvine by Nicolas Oros, Ph.D.
   (http://www.cogsci.uci.edu/~noros/)."

THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
IN NO EVENT SHALL THE UNIVERSITY OR THE PROGRAM CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*******************************************************************************************************/
package ioio.examples.hello_power;

import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.SurfaceView;


public class Cam_thread
{
	Camera mCamera;

	Bitmap mBitmap;
	int[] mRGBData;
	int width_ima, height_ima;
	private static final String TAG = "IP_cam";
	
	SurfaceView parent_context;
	Send_Pakge sp;
	private boolean STOP_THREAD;
	String ip_address;


	public Cam_thread(SurfaceView context, String ip)
	{
		parent_context = context;	
		sp =new Send_Pakge(ip);
	}
	
	private void init()
	{
		try 
		{			 
    	

			mCamera = Camera.open();        
			Camera.Parameters parameters = mCamera.getParameters(); 
			parameters.setPreviewSize(320, 240);
			parameters.setPreviewFrameRate(30);
			parameters.setSceneMode(Camera.Parameters.SCENE_MODE_SPORTS);
			parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
			parameters.setColorEffect(Camera.Parameters.EFFECT_NONE);
			mCamera.setParameters(parameters);
			mCamera.setPreviewDisplay(parent_context.getHolder());			
			mCamera.setPreviewCallback(new cam_PreviewCallback());           
			mCamera.startPreview();
		} 
		catch (Exception exception) 
		{
			Log.e(TAG, "Error: ", exception);
		}
	}

    public void start_thread()
    {
    	init();
    }

    public void stop_thread()
    {
    	STOP_THREAD = true;
		
    }
	
	public void send_data_UDP()
	{
		if(mBitmap != null)
		{
		
			Log.d("cam", "send");
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream(); 
			
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteStream);	// !!!!!!!  change compression rate to change packets size

			byte data[] = byteStream.toByteArray();
			sp.send(data, data.length);
			
		}
	}    
	
	
		static public void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) 
	{
		final int frameSize = width * height;

		for (int j = 0, yp = 0; j < height; j++) {
			int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
			for (int i = 0; i < width; i++, yp++) {
				int y = (0xff & ((int) yuv420sp[yp])) - 16;
				if (y < 0) y = 0;
				if ((i & 1) == 0) {
					v = (0xff & yuv420sp[uvp++]) - 128;
					u = (0xff & yuv420sp[uvp++]) - 128;
				}

				int y1192 = 1192 * y;
				int r = (y1192 + 1634 * v);
				int g = (y1192 - 833 * v - 400 * u);
				int b = (y1192 + 2066 * u);

				if (r < 0) r = 0; else if (r > 262143) r = 262143;
				if (g < 0) g = 0; else if (g > 262143) g = 262143;
				if (b < 0) b = 0; else if (b > 262143) b = 262143;

				rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
			}
		}
	}

	// Preview callback used whenever new frame is available...send image via UDP !!!
	private class cam_PreviewCallback implements PreviewCallback 
	{
		@Override
		public void onPreviewFrame(byte[] data, Camera camera)
		{
			if(STOP_THREAD == true)
			{
				mCamera.setPreviewCallback(null);
				mCamera.stopPreview();
				mCamera.release();
				mCamera = null;
				return;
			}
			
			if (mBitmap == null)		//create Bitmap image first time
			{
				Camera.Parameters params = camera.getParameters();
				width_ima = params.getPreviewSize().width;
				height_ima = params.getPreviewSize().height;        			  
				mBitmap = Bitmap.createBitmap(width_ima, height_ima, Bitmap.Config.RGB_565);
				mRGBData = new int[width_ima * height_ima];
			}

			decodeYUV420SP(mRGBData, data, width_ima, height_ima);
			mBitmap.setPixels(mRGBData, 0, width_ima, 0, 0, width_ima, height_ima);
			
			send_data_UDP();
		}
	}

}