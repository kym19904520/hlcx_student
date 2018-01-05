package com.up.study.weight.showimages;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.up.common.conf.Constants;
import com.up.common.utils.Logger;
import com.up.common.utils.SPUtil;
import com.up.study.R;
import com.up.study.TApplication;

import uk.co.senab.photoview.PhotoViewAttacher;


public class ImageDetailFragment extends Fragment {
	private String mImageUrl;
	private ImageView mImageView;
	private ProgressBar progressBar;
	private PhotoViewAttacher mAttacher;

	public static ImageDetailFragment newInstance(String imageUrl) {
		final ImageDetailFragment f = new ImageDetailFragment();

		final Bundle args = new Bundle();
		args.putString("url", imageUrl);
		f.setArguments(args);

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageUrl = getArguments() != null ? getArguments().getString("url") : null;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.image_detail_fragment, container, false);
		mImageView = (ImageView) v.findViewById(R.id.image);
		mAttacher = new PhotoViewAttacher(mImageView);
		
		mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
			
			@Override
			public void onPhotoTap(View arg0, float arg1, float arg2) {
				//getActivity().finish();
			}

			@Override
			public void onOutsidePhotoTap() {

			}
		});
		
		progressBar = (ProgressBar) v.findViewById(R.id.loading);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Logger.i(Logger.TAG,"wwwwwwwww图片地址："+mImageUrl.toString());
		if(!mImageUrl.toString().contains("http")&&mImageUrl.toString().contains("user-dir")){
			String host = SPUtil.getString(getActivity(), Constants.SP_IMG_URL,"");
			Logger.i(Logger.TAG,"wwwwwwwww添加域名头:"+host);
			mImageUrl = "http://" + host+ "/" + mImageUrl;
		}

		Glide.with(this).load(mImageUrl).error(R.mipmap.def_img).into(new GlideDrawableImageViewTarget(mImageView){
			@Override
			public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
				super.onResourceReady(resource, animation);
			}

			@Override
			public void onStart() {
				super.onStart();
				progressBar.setVisibility(View.GONE);
				Logger.i(Logger.TAG,"img---------------------onStart");
			}

			@Override
			public void onStop() {
				super.onStop();
				progressBar.setVisibility(View.VISIBLE);
				mAttacher.update();
				Logger.i(Logger.TAG,"img---------------------onStop");
			}
		});
		/*ImageLoader.getInstance().displayImage(mImageUrl, mImageView, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				progressBar.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				String message = null;
				switch (failReason.getType()) {
				case IO_ERROR:
					message = "下载错误";
					break;
				case DECODING_ERROR:
					message = "图片无法显示";
					break;
				case NETWORK_DENIED:
					message = "网络有问题，无法下载";
					break;
				case OUT_OF_MEMORY:
					message = "图片太大无法显示";
					break;
				case UNKNOWN:
					message = "未知的错误";
					break;
				}
				Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
				progressBar.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				progressBar.setVisibility(View.GONE);
				mAttacher.update();
			}
		});*/

		
		
		
	}

}
