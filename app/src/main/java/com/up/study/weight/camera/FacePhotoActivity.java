package com.up.study.weight.camera;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

import com.up.study.R;

public class FacePhotoActivity extends Activity implements Callback,PreviewCallback{

	private SurfaceView sView;
	private SurfaceHolder surfaceHolder;
	
	private boolean isPreview = false;
	private static Camera camera;
	private static byte[] camBuf;
	private static int width = 640;
	private static int height = 480;
	private static int cameraIndex = 0;
	public final static int DEFAULT_ROTATE_VALUE = 0;
	
	// ͼ���Ƿ���Ҫ��ת
	private final static boolean isRotate = true;
//	private FaceVerify mFaceVerify;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.face_activity);
		sView = (SurfaceView) findViewById(R.id.camera_surfaceview);
		// ���SurfaceView��SurfaceHolder
		surfaceHolder = sView.getHolder();
		// ΪsurfaceHolder���һ���ص�������
		surfaceHolder.addCallback(this);
		// ���ø�SurfaceView�Լ���ά������
		sView.setKeepScreenOn(true);
		
//		mFaceVerify = new FaceVerify(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
		if (camera != null) {
			camera.stopPreview();
			isPreview = false;
			camera.setPreviewCallbackWithBuffer(this);
			camera.setAutoFocusMoveCallback(null);
			camera.startPreview();
			isPreview = true;
		}
		// ������ͷ
		initCamera();
	}
	
	public void initCamera() {

		try {
			if (!isPreview) {
				// ������ͷ
				camera = Camera.open(cameraIndex);
				if (isRotate) {
					// ������ת90��
					camera.setDisplayOrientation(DEFAULT_ROTATE_VALUE);
				}
				if (camera != null) {
					Parameters parameters = camera.getParameters();
					// ���÷ֱ���
					List<Size> list = parameters
							.getSupportedPreviewSizes();
					Iterator<Size> its = list.iterator();
					int minWidth = 0;
					Size size = null;
					while (its.hasNext()) {
						size = (Size) its.next();
						if (size.width / 4 != size.height / 3) {
							continue;
						}
						if (minWidth != 0 && minWidth < size.width) {
							continue;
						}
						minWidth = size.width;
						if (640 == size.width && 480 == size.height) {
							width = 640;
							height = 480;
						}
					}
					parameters.setPictureSize(width, height);
					parameters.setPreviewSize(width, height);
					if (camBuf == null) {
						camBuf = new byte[width * height * 3 / 2];
					}
					camera.setParameters(parameters);
					camera.enableShutterSound(true);
					// ͨ��SurfaceView��ʾȡ������
					camera.setPreviewDisplay(surfaceHolder);
					camera.addCallbackBuffer(camBuf);
					camera.setPreviewCallbackWithBuffer(this);
					// ��ʼԤ��
					camera.startPreview();
					isPreview = true;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			// �ͷ�����ͷ
			releaseCamera();
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			// ������ʾĿ�����
			if (camera != null) {
				camera.setPreviewDisplay(holder);
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		releaseCamera();
	}

	private void releaseCamera() {
		// �ͷ�����ͷ
		if (camera != null) {
			camera.setPreviewCallbackWithBuffer(null);
			if (isPreview) {
				camera.stopPreview();
				isPreview = false;
			}
			camera.release();
			camera = null;
		}

	}


	@Override
	public void onPreviewFrame(byte[] data, final Camera camera) {		
		if (camera != null && data != null && data.length > 0) {
						
			Parameters param = camera.getParameters();
			Size sz = param.getPictureSize();
			YuvImage img = new YuvImage(data, param.getPreviewFormat(),
					sz.width, sz.height, null);
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			img.compressToJpeg(new Rect(0, 0, sz.width, sz.height), 100, output);
			Matrix m = new Matrix();
			if (isRotate) {
				m.postScale(1, -1); // ����ֱ��ת
				m.postRotate(180);
			} else {
				m.postScale(-1, 1);// ����ˮƽ��ת
			}
			Bitmap bmp = BitmapFactory.decodeByteArray(output.toByteArray(), 0,
					output.size());
			final Bitmap detBmp = Bitmap.createBitmap(bmp, 0, 0, sz.width,
					sz.height, m, true);
			bmp.recycle();
			try {
				output.close();
				setPhotoPic(detBmp);
			} catch (Exception e) {
				e.printStackTrace();
			}
			camera.addCallbackBuffer(camBuf);
		}		
	}

	/**
	 * ʵʱ��ȡͼ������
	 * @param detBmp
	 */
	public void setPhotoPic(final Bitmap detBmp) {
//		byte[] resultdata = ImgUtil.bitmapToByte(detBmp,CompressFormat.JPEG,100);
//		mFaceVerify.getPhotoFace(resultdata);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		releaseCamera();
//		mFaceVerify.closedSDK();
		super.onDestroy();
	}
}
