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
	/**传感器管理器*/
	SensorManager sensorManager=null;
	
	/**尺子计算值*/
	private int now,x,y;
	private boolean hasx;
	TextView tv;
	
	
	
	/**窗口管理器*/
	private WindowManager windowManager;
	/**屏幕宽高*/
	int screenwidth,screenheight;
	/**取景器*/
	SurfaceView surfaceView;
	/**系统摄像机*/
	Camera camera;
	/**预览状态*/
	boolean isPreView=false;
	SurfaceHolder surfaceHolder;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 不要标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		//全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		/**方向传感器*/
		sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				sensorManager.SENSOR_DELAY_UI);
		
		
		
		//获取窗口管理器
		windowManager=getWindowManager();
		//屏幕
		Display display= windowManager.getDefaultDisplay();
		//屏幕工具
		DisplayMetrics displayMetrics =new DisplayMetrics();
		//初始化屏幕工具
		display.getMetrics(displayMetrics);
		//从屏幕工具中获取宽高
		screenwidth=displayMetrics.widthPixels;
		screenheight=displayMetrics.heightPixels;
		 
		initView();
		
		/**第一步：获取surfaceView的管理器*/
		surfaceHolder= surfaceView.getHolder();
		/**第二步：设置SurfaceView的缓冲区*/
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		/**第三步：为surfaceHolder添加回调监听*/
		surfaceHolder.addCallback(new Callback() {
			/**在此处释放摄像头*/
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				if(null!=camera){
					if(isPreView)camera.stopPreview();//停止预览
					camera.release();//释放摄像头
					camera=null;     //手动gc回收
				}
			}
			/**在此处打开摄像头*/
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
	/**初始化*/
	private void initView() {
		surfaceView=(SurfaceView) findViewById(R.id.sv);
	}
	/**打开摄像头*/
	private void initCamera() {
		//取景器不为空时打开摄像头
		if(null!=surfaceView){
			//打开       前置摄像头1   后置为0  
			camera=Camera.open(0);
			//预览顺时针旋转角度
			camera.setDisplayOrientation(90);
		} 
		if(null!=camera&&!isPreView){//还没预览时
			//获取参数管理器
			Parameters parameters= camera.getParameters();
			//设置预览照片的大小
			parameters.setPreviewSize(screenwidth, screenheight);
			//设置预览照片时每秒多少帧的 最大值  最小值
			parameters.setPreviewFpsRange(4, 10);
			
			//设置图片格式JPEG   ??  setPictureFormat 与   setPreviewFormate的区别
			parameters.setPictureFormat(ImageFormat.JPEG);
			//设置图片的质量
			parameters.set("jpeg-quality", 85);
			//设置图片大小
			parameters.setPictureSize(screenwidth, screenheight);
			
			//设置通过surfaceView显示取景画面
			try {
				camera.setPreviewDisplay(surfaceHolder);
			} catch (IOException e) {
				System.out.println("===============>>>"+e.getMessage());
			}
			//开始预览
			camera.startPreview();
			isPreView=true;
			
		}
	}
	
	
	public void dw(View view) {
		//当x还没有值时
		if(!hasx){
			x=now;
			hasx=true;
		}else{
			//当没有值时显示y
			hasx=false;
			tv.setText(now-x+"");
		}
		
		Toast.makeText(getApplicationContext(), "流氓、、、", 1).show();
	}
	
	
	/**传感器监听*/
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}
	/**传感器监听*/
	@Override
	public void onSensorChanged(SensorEvent event) {
		float[]  orvalue=event.values;
		tv=(TextView)findViewById(R.id.orientation_tv);
		
		now=(int)orvalue[0];
		
		//当有x值时  显示角度
		if(hasx){
			tv.setText((int)orvalue[0]-x+"");
		}
	}
	//暂停时
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



//=======================相机参数=====翻译下就懂了=========================
//设置图片的质量
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
//=======================相机参数==============================







