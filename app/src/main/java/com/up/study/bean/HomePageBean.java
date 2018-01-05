package com.up.study.bean;

import java.util.List;

import android.graphics.Bitmap;
/**
 * 
 * 主页内容实体类
 *
 * @author yyCai
 *
 * 2015年12月20日
 */
public class HomePageBean {

	/**
	 * 发布时间
	 */
	private String time;
	/**
	 * 发布文字内容
	 */
	private String content;
	/**
	 * 发布的图片
	 */
	private List<Bitmap> imgs;
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public List<Bitmap> getImgs() {
		return imgs;
	}
	public void setImgs(List<Bitmap> imgs) {
		this.imgs = imgs;
	}
	
}
