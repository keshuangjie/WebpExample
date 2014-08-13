package com.example.webp_example;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

import com.google.webp.libwebp;

public class WebpUtil {
	
	public static Bitmap loadBitmapFromResource(Resources res, int id){
		Bitmap bm = null;
		if(VERSION.SDK_INT >=  VERSION_CODES.ICE_CREAM_SANDWICH){
			bm = BitmapFactory.decodeResource(res, id);
		}else{
			
		}
		return bm;
	}
	
	public static Bitmap loadBitmapFromFile(String filePath){
		Bitmap bm = null;
		byte[] data = loadFileAsByteArray(filePath);
		if(filePath.endsWith(".webp")){
			bm = webpToBitmap(data);
		}else{
			bm = BitmapFactory.decodeByteArray(data, 0, data.length);
		}
		return bm;
	}

	public static byte[] loadFileAsByteArray(String filePath) {
		File file = new File(filePath);
		byte[] data = new byte[(int) file.length()];
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			inputStream.read(data);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return data;
	}

	/**
	 * convert webp to Bitmap
	 * 
	 * @param encoded
	 * @return
	 */
	public static Bitmap webpToBitmap(byte[] encoded) {
		int[] width = new int[] { 0 };
		int[] height = new int[] { 0 };
		byte[] decoded = libwebp.WebPDecodeARGB(encoded, encoded.length, width,
				height);
		int[] pixels = new int[decoded.length / 4];
		ByteBuffer.wrap(decoded).asIntBuffer().get(pixels);
		return Bitmap.createBitmap(pixels, width[0], height[0],
				Bitmap.Config.RGB_565);
	}

	/**
	 * convert bitmap to webp
	 * 
	 * @param filePath
	 * @return
	 */
	public static byte[] bitmapToWebp(String filePath) {
		Bitmap bitmap = BitmapFactory.decodeFile(filePath);
//		int bytes = bitmap.getByteCount();
//		ByteBuffer buffer = ByteBuffer.allocate(bytes);
//		bitmap.copyPixelsToBuffer(buffer);
//		byte[] pixels = buffer.array();
		 byte[] pixels = Bitmap2Bytes(bitmap);

		int height = bitmap.getHeight();
		int width = bitmap.getWidth();
		int stride = width * 4;
		int quality = 100;
		byte[] rgb = new byte[3];

		for (int y = 0; y < height * 4; y++) {
			for (int x = 0; x < width; x += 4) {
				for (int i = 0; i < 3; i++) {
					int index1 = x + y * width + i;
					if (index1 < pixels.length) {
						rgb[i] = pixels[index1];
					}
				}
				for (int i = 0; i < 3; i++) {
					int index2 = x + y * width + 2 - i;
					if (index2 < pixels.length) {
						pixels[index2] = rgb[i];
					}
				}
			}
		}

		byte[] encoded = libwebp.WebPEncodeBGRA(pixels, width, height, stride,
				quality);
		return encoded;
	}

	/**
	 * 將byte写入文件filePath
	 * 
	 * @param filePath
	 * @param data
	 */
	public static void writeFileFromByteArray(String filePath, byte[] data) {
		File webpFile = new File(filePath);
		BufferedOutputStream bos;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(webpFile));
			bos.write(data);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * bitmap to byte数组
	 * 
	 * @param bm
	 * @return
	 */
	private static byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		recycleBitmap(bm);
		return baos.toByteArray();
	}

	/**
	 * 回收照片
	 * 
	 * @param photo
	 *            要回收的照片
	 */
	private static void recycleBitmap(Bitmap photo) {
		// && photo.isRecycled()
		if (photo != null) {
			photo.recycle();
		}
	}

}
