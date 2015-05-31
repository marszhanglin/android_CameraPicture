package com.example.marscamerapictrueview;

import java.io.IOException;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener{
	/**������������*/
	SensorManager sensorManager=null;
	
	/**���Ӽ���ֵ*/
	private int now,x,y;
	private boolean hasx;
	TextView tv;
	
	
	
	/**���ڹ�����*/
	private WindowManager windowManager;
	/**��Ļ���*/
	int screenwidth,screenheight;
	/**ȡ����*/
	SurfaceView surfaceView;
	/**ϵͳ�����*/
	Camera camera;
	/**Ԥ��״̬*/
	boolean isPreView=false;
	SurfaceHolder surfaceHolder;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ��Ҫ������
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		//ȫ��
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		/**���򴫸���*/
		sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				sensorManager.SENSOR_DELAY_UI);
		
		
		
		//��ȡ���ڹ�����
		windowManager=getWindowManager();
		//��Ļ
		Display display= windowManager.getDefaultDisplay();
		//��Ļ����
		DisplayMetrics displayMetrics =new DisplayMetrics();
		//��ʼ����Ļ����
		display.getMetrics(displayMetrics);
		//����Ļ�����л�ȡ���
		screenwidth=displayMetrics.widthPixels;
		screenheight=displayMetrics.heightPixels;
		 
		initView();
		
		/**��һ������ȡsurfaceView�Ĺ�����*/
		surfaceHolder= surfaceView.getHolder();
		/**�ڶ���������SurfaceView�Ļ�����*/
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		/**��������ΪsurfaceHolder��ӻص�����*/
		surfaceHolder.addCallback(new Callback() {
			/**�ڴ˴��ͷ�����ͷ*/
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				if(null!=camera){
					if(isPreView)camera.stopPreview();//ֹͣԤ��
					camera.release();//�ͷ�����ͷ
					camera=null;     //�ֶ�gc����
				}
			}
			/**�ڴ˴�������ͷ*/
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				initCamera();
			}
			/**toast*/
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				Toast.makeText(
						getApplicationContext(),
						"surfaceChanged  format:" + format + " width:" + width
								+ "   height:" + height, 0).show();
				System.out.println("surfaceChanged  format:" + format + " width:" + width
								+ "   height:" + height);
			}
		});
		
		
	}
	/**��ʼ��*/
	private void initView() {
		surfaceView=(SurfaceView) findViewById(R.id.sv);
	}
	/**������ͷ*/
	private void initCamera() {
		//ȡ������Ϊ��ʱ������ͷ
		if(null!=surfaceView){
			//��       ǰ������ͷ1   ����Ϊ0  
			camera=Camera.open(0);
			//Ԥ��˳ʱ����ת�Ƕ�
			camera.setDisplayOrientation(90);
		} 
		if(null!=camera&&!isPreView){//��ûԤ��ʱ
			//��ȡ����������
			Parameters parameters= camera.getParameters();
			//����Ԥ����Ƭ�Ĵ�С
			parameters.setPreviewSize(screenwidth, screenheight);
			//����Ԥ����Ƭʱÿ�����֡�� ���ֵ  ��Сֵ
			parameters.setPreviewFpsRange(4, 10);
			
			//����ͼƬ��ʽJPEG   ??  setPictureFormat ��   setPreviewFormate������
			parameters.setPictureFormat(ImageFormat.JPEG);
			//����ͼƬ������
			parameters.set("jpeg-quality", 85);
			//����ͼƬ��С
			parameters.setPictureSize(screenwidth, screenheight);
			
			//����ͨ��surfaceView��ʾȡ������
			try {
				camera.setPreviewDisplay(surfaceHolder);
			} catch (IOException e) {
				System.out.println("===============>>>"+e.getMessage());
			}
			//��ʼԤ��
			camera.startPreview();
			isPreView=true;
			
		}
	}
	
	
	public void dw(View view) {
		//��x��û��ֵʱ
		if(!hasx){
			x=now;
			hasx=true;
		}else{
			//��û��ֵʱ��ʾy
			hasx=false;
			tv.setText(now-x+"");
		}
		
		Toast.makeText(getApplicationContext(), "��å������", 1).show();
	}
	
	
	/**����������*/
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}
	/**����������*/
	@Override
	public void onSensorChanged(SensorEvent event) {
		float[]  orvalue=event.values;
		tv=(TextView)findViewById(R.id.orientation_tv);
		
		now=(int)orvalue[0];
		
		//����xֵʱ  ��ʾ�Ƕ�
		if(hasx){
			tv.setText((int)orvalue[0]-x+"");
		}
	}
	//��ͣʱ
		@Override
		protected void onPause() {
			sensorManager.unregisterListener(this);
			super.onPause();
		}
		@Override
		protected void onStop() {
			sensorManager.unregisterListener(this);
			super.onStop();
		}
	
}



//=======================�������=====�����¾Ͷ���=========================
//����ͼƬ������
//parameters.set("jpeg-quality", 85);

//zoom=0;//  
//scene-detect-values=off,on; 
//zoom-supported=true;  
//strtextures=OFF; 
//face-detection-values=; 
//sharpness=10; 
//contrast=5; 
//whitebalance=auto; 
//max-sharpness=30; 
//scene-mode=auto; 
//jpeg-quality=85; 
//preview-format-values=yuv420sp; 
//histogram-values=enable,disable; 
//jpeg-thumbnail-quality=90; 
//preview-format=yuv420sp; 
//ace-detection=off; 
//skinToneEnhancement=disable; 
//touch-index-af=-1x-1; 
//preview-size=640x480; 
//focal-length=4.31; 
//auto-exposure-values=frame-average,center-weighted,spot-metering; 
//video-zoom-support=true; 
//iso=auto; 
//record-size=; 
//flash-mode-values=off,auto,on,torch; 
//preview-frame-rate-values=5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31; 
//preview-frame-rate=31; 
//focus-mode-values=auto,infinity,normal,macro,continuous-video; 
//preview-frame-rate-mode=frame-rate-auto; 
//jpeg-thumbnail-width=512; 
//scene-mode-values=auto,action,portrait,landscape,night,night-portrait,theatre,beach,snow,sunset,steadyphoto,fireworks,sports,party,candlelight,backlight,flowers;preview-fps-range-values=(5000,31000); 
//auto-exposure=frame-average; 
//jpeg-thumbnail-size-values=512x288,480x288,432x288,512x384,352x288,0x0; 
//histogram=disable; 
//zoom-ratios=100,102,104,107,109,112,114,117,120,123,125,128,131,135,138,141,144,148,151,155,158,162,166,170,174,178,182,186,190,195,200,204,209,214,219,224,229,235,240,246,251,257,263,270,276,282,289,296,303,310,317,324,332,340,348,356,364,373,381,390; 
//preview-size-values=1280x720,800x480,768x432,720x480,640x480,576x432,480x320,384x288,352x288,320x240,240x160,176x144;picture-size-values=3200x2400,3200x1800,2592x1944,2592x1456,2048x1536,1920x1080,1600x1200,1280x768,1280x720,1024x768,800x600,800x480,640x480,352x288,320x240,176x144; 
//touch-af-aec=touch-off; 
//preview-fps-range=5000,31000; 
//min-exposure-compensation=-12; 
//antibanding=off; 
//touch-af-aec-values=touch-off,touch-on;vertical-view-angle=42.5; 
//luma-adaptation=3; 
//horizontal-view-angle=54.8;touch-index-aec=-1x-1;skinToneEnhancement-values=enable,disable;jpeg-thumbnail-height=384; 
//focus-mode=auto; 
//max-saturation=10; 
//max-contrast=10; 
//preview-frame-rate-modes=frame-rate-auto,frame-rate-fixed; 
//picture-format-values=jpeg,raw;max-exposure-compensation=12; 
//exposure-compensation=0; 
//exposure-compensation-step=0.166667; 
//scene-detect=off; 
//flash-mode=off; 
//effect-values=none,mono,negative,solarize,sepia,posterize,whiteboard,blackboard,aqua;picture-size=640x480; 
//max-zoom=59;effect=none; 
//saturation=5; 
//whitebalance-values=auto,incandescent,fluorescent,daylight,cloudy-daylight; 
//picture-format=jpeg; 
//focus-distances=1.000000,34.000000,34.000000;lensshade-values=enable,disable; 
//selectable-zone-af=auto; 
//iso-values=auto,ISO_HJR,ISO100,ISO200,ISO400,ISO800,ISO1600; 
//selectable-zone-af-values=auto,spot-metering,center-weighted,frame-average; 
//lensshade=enable; 
//antibanding-values=off,50hz,60hz,auto 
//=======================�������==============================







