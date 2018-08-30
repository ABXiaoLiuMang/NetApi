package com.cn.div.net;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileItem {
	private String fileName;
	private String mimeType;
	private byte[] content;
	private File file;

	/**
	 * 基于本地文件的构造器�?
	 * 
	 * @param file 本地文件
	 */
	public FileItem(File file) {
		this.file = file;
	}

	/**
	 * 基于文件绝对路径的构造器�?
	 * 
	 * @param filePath 文件绝对路径
	 */
	public FileItem(String filePath) {
		this(new File(filePath));
	}

	/**
	 * 基于文件名和字节流的构�?器�?
	 * 
	 * @param fileName 文件�?
	 * @param content 文件字节�?
	 */
	public FileItem(String fileName, byte[] content) {
		this.fileName = fileName;
		this.content = content;
	}

	/**
	 * 基于文件名�?字节流和媒体类型的构造器�?
	 * 
	 * @param fileName 文件�?
	 * @param content 文件字节�?
	 * @param mimeType 媒体类型
	 */
	public FileItem(String fileName, byte[] content, String mimeType) {
		this(fileName, content);
		this.mimeType = mimeType;
	}

	public String getFileName() {
		if (this.fileName == null && this.file != null && this.file.exists()) {
			this.fileName = file.getName();
		}
		return this.fileName;
	}

	public String getMimeType() throws IOException {
		if (this.mimeType == null) {
			this.mimeType = getMimeType(getContent());
		}
		return this.mimeType;
	}

	public byte[] getContent() throws IOException {
		if (this.content == null && this.file != null && this.file.exists()) {
			InputStream in = null;
			BufferedInputStream bis = null;
			ByteArrayOutputStream out = null;

			try {
				in = new FileInputStream(this.file);
				bis = new BufferedInputStream(in);
				out = new ByteArrayOutputStream();
				int len=bis.available();
				byte[] isBuffer = new byte[len];
				bis.read(isBuffer);
				this.content=isBuffer;
//				int len = 0;
//				while ((len = bis.read(isBuffer)) != -1) {
//					out.write(isBuffer, 0, len);
//				}
				
//				int ch;
//				while ((ch = in.read()) != -1) {
//					out.write(ch);
//				}
//				this.content = out.toByteArray();
			} finally {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
				if (bis != null) {
					bis.close();
				}
			}
		}
		return this.content;
	}
	
    
    
    /**
	 * 获取文件的真实后�?��。目前只支持JPG, GIF, PNG, BMP四种图片文件�?
	 * 
	 * @param bytes
	 *            文件字节�?
	 * @return JPG, GIF, PNG or null
	 */
	private String getFileSuffix(byte[] bytes) {
		if (bytes == null || bytes.length < 10) {
			return null;
		}

		if (bytes[0] == 'G' && bytes[1] == 'I' && bytes[2] == 'F') {
			return "GIF";
		} else if (bytes[1] == 'P' && bytes[2] == 'N' && bytes[3] == 'G') {
			return "PNG";
		} else if (bytes[6] == 'J' && bytes[7] == 'F' && bytes[8] == 'I'
				&& bytes[9] == 'F') {
			return "JPG";
		} else if (bytes[0] == 'B' && bytes[1] == 'M') {
			return "BMP";
		} else {
			return null;
		}
	}

	/**
	 * 获取文件的真实媒体类型�?目前只支持JPG, GIF, PNG, BMP四种图片文件�?
	 * 
	 * @param bytes
	 *            文件字节�?
	 * @return 媒体类型(MEME-TYPE)
	 */
	private String getMimeType(byte[] bytes) {
		String suffix = getFileSuffix(bytes);
		String mimeType;

		if ("JPG".equals(suffix)) {
			mimeType = "image/jpeg";
		} else if ("GIF".equals(suffix)) {
			mimeType = "image/gif";
		} else if ("PNG".equals(suffix)) {
			mimeType = "image/png";
		} else if ("BMP".equals(suffix)) {
			mimeType = "image/bmp";
		} else {
			mimeType = "application/octet-stream";
		}

		return mimeType;
	}

}
