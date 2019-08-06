package com.laizhong.hotel.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtil {
	
	private static void printException(Exception e) {
		log.error("FileUtil.Exception", e);
	}

	/**
	 * 列举传入文件夹下所有文件的全路径
	 * @param filePath 文件夹，如C:\program\
	 * @return 传入目录下所有文件的全路径，不包括文件夹及子目录下的文件
	 */
	public static List<String> listFullFileName(String filePath) {
		List<String> result = new ArrayList<String>();
		File f = new File(filePath);
		String path;
		try {
			path = f.getCanonicalPath() + File.separator;
		} catch (IOException e) {
			printException(e);
			throw new RuntimeException(e);
		}
		File[] fs = (new File(path)).listFiles();
		for (int i = 0; i < fs.length; i++) {
			f = fs[i];
			if (f.isFile()) {
				String fName = f.getName();
				result.add(path + fName);
			}

		}
		return result;

	}

	/**
	 * 获得当前程序的启动路径
	 * @return 当前程序的启动路径，返回的是全路径，不是相对路径
	 */
	public static String getCurrentPath() {
		File f = new File(".");
		String path = null;
		try {
			path = f.getCanonicalPath() + File.separator;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return path;
	}

	/**
	 * 将文件读成byte[]数组，不适用于大文件
	 * @param f 要读取的文件
	 * @return 文件的byte[]
	 */
	public static byte[] readFileToByteArray(File f) {
		ByteArrayOutputStream out = null;
		BufferedInputStream in = null;
		try {
			out = new ByteArrayOutputStream();
			in = new BufferedInputStream(new FileInputStream(f));
			byte[] bt = new byte[1024 * 1024];
			while (true) {
				int len = in.read(bt);
				out.write(bt, 0, len);
				if (len != 1024 * 1024) {
					break;
				}
			}
			bt = out.toByteArray();
			return bt;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					printException(e);
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					printException(e);
				}
			}
		}
		
	}

	/**
	 * 将byte[]数组写入文件
	 * @param f 要写入的文件
	 * @param bt 要写入的数组
	 * @throws IOException
	 */
	public static void saveByteArrayToFile(File f, byte[] bt) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(f);
			out.write(bt);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					printException(e);
				}
			}
		}
	}

	/**
	 * 从csv文件中读取内容
	 * @param f 要读取的文件，该文件为标准的csv文件，标准csv的格式请在Excel中将文件存为csv查看
	 * @return 读取的内容为一个List，List每一个元素代表csv文件的一行，每一行为一个List<String>，每个String代表这行的列的内容
	 * @param charset 字符编码，如果为空，则使用操作系统默认编码
	 * @throws IOException 
	 */
	public static List<List<String>> readCSV(File f, String charset) throws IOException {
		List<String> list = readFileToStringList(f, charset);
		List<List<String>> result = new ArrayList<List<String>>(list.size());
		for (String line : list) {
			String[] ss = line.split(",");
			List<String> lineList = new ArrayList<String>(ss.length);
			for (String s : ss) {
				lineList.add(s);
			}
			result.add(lineList);
		}
		return result;
	}

	/**
	 * 将List<String>存成文件，List<String>的每一个元素为文件里的每一行
	 * @param f 要存的文件
	 * @param stringList 要存的字符串列表
	 * @param charset 字符编码，如果为空，则使用操作系统默认编码
	 * @throws IOException
	 */
	public static void saveFileFromStringList(File f, List<String> stringList, String charset) {
		PrintWriter out = null;
		try {
			if (charset == null) {
				out = new PrintWriter(f);
			} else {
				out = new PrintWriter(f, charset);
			}
			for (int i = 0; i < stringList.size(); i++) {
				out.println(stringList.get(i));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (out != null) {
				out.close();
			}
		}

	}

	/**
	 * 将文本文件读成List<String>，List<String>里的每个元素代表文件里的每一行
	 * @param f 要读取的文件
	 * @param charset 字符编码，如果为空，则使用操作系统默认编码
	 * @return 读取的文件内容
	 * @throws IOException 
	 */
	public static List<String> readFileToStringList(File f, String charset) {
		List<String> result = new ArrayList<String>();
		BufferedReader in = null;
		try {
			if (charset == null) {
				in = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			} else {
				in = new BufferedReader(new InputStreamReader(new FileInputStream(f), charset));
			}
			String line = null;
			while ((line = in.readLine()) != null) {
				result.add(line);
			}
			return result;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}  finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					printException(e);
				}
			}
		}
		
	}

	/**
	 * 获得传入文件的不包括路径的文件名
	 * @param fileFullPathName 传全的文件名，可以是全路径，也可以不包括任何路径
	 * @return 不包括路径的文件名
	 */
	public static String getFileNameWithoutPath(String fileFullPathName) {
		int pos = fileFullPathName.lastIndexOf("/");
		if (pos == -1) {
			pos = fileFullPathName.lastIndexOf("\\");
		}
		if (pos == -1) {
			return fileFullPathName;
		} else {
			return fileFullPathName.substring(pos + 1);
		}
	}

	/**
	 * 获取文件的路径
	 * @param fileFullPathName 传全的文件名，必需是全路径
	 * @return 以"/"或"\"结尾的文件路径
	 */
	public static String getFilePath(String fileFullPathName) {
		int pos1 = fileFullPathName.lastIndexOf("/");
		int pos2 = fileFullPathName.lastIndexOf("\\");
		if (pos1 < pos2) {
			pos1 = pos2;
		}
		return fileFullPathName.substring(0, pos1 + 1);
	}

	/**
	 * 获得去掉扩展名（后缀）的文件名
	 * @param fileName 文件名，不包括路径的文件名
	 * @return 去掉扩展名（后缀）的文件名
	 */
	public static String getFileNameWithoutExt(String fileName) {
		int pos = fileName.lastIndexOf(".");
		if (pos > -1) {
			return fileName.substring(0, pos);
		} else {
			return fileName;
		}
	}

	/**
	 * 将多个文件压缩成一个zip文件
	 * @param fileFullPathNameList 要压缩的文件，List里的每个元素为要压缩文件的全路径
	 * @param zipFileName 压缩后的文件，要传入全路径
	 */
	public static void zipFile(List<String> fileFullPathNameList, String zipFileName) {
		List<byte[]> fileDataList = new ArrayList<byte[]>();
		List<String> fileNameList = new ArrayList<String>();
		for (int i = 0; i < fileFullPathNameList.size(); i++) {
			String s = (String) fileFullPathNameList.get(i);
			fileDataList.add(readFileToByteArray(s));
			fileNameList.add(getFileNameWithoutPath(s));
		}
		byte[] bytes = zipFile(fileDataList, fileNameList);
		saveByteArrayToFile(zipFileName, bytes);
	}

	/**
	 * 将多个文件压缩成一个文件，文件的内容均以byte[]的形式展现
	 * @param fileDataList 要压缩的文件内容，List里的每个元素为要压缩文件的内容
	 * @param fileNameList 要压缩的文件名，List里的每个元素为要压缩文件的文件名，顺序与fileDataList的顺序对应，压缩后的zip文件会每个压缩文件的文件名
	 * @return 压缩成zip文件的内容，以byte[]形式存放
	 */
	public static byte[] zipFile(List<byte[]> fileDataList, List<String> fileNameList) {
		byte[] result = null;
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		ZipOutputStream zipOut = new ZipOutputStream(byteOut);
		try {
			for (int i = 0; i < fileDataList.size(); i++) {
				zipOut.putNextEntry(new ZipEntry((String) fileNameList.get(i)));
				zipOut.write((byte[]) fileDataList.get(i));
				zipOut.closeEntry();
			}
			zipOut.finish();
			zipOut.flush();
			try {
				zipOut.close(); //这似乎是一个BUG，不将ZipOutputStream close，会有数据不写进ByteArrayOutputStream中
				zipOut = null;
			} catch (IOException e) {
				printException(e);
			}
			result = byteOut.toByteArray();
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (zipOut != null) {
				try {
					zipOut.close();
				} catch (IOException e) {
					printException(e);
				}
			}
			try {
				byteOut.close();
			} catch (IOException e) {
				printException(e);
			}
		}
	}

	/**
	 * 将文件读取byte[]
	 * @param fileName 要读取文件的全路径
	 * @return 文件的内容，以byte[]形式存放
	 */
	public static byte[] readFileToByteArray(String fileName) {
		return FileUtil.readFileToByteArray(new File(fileName));
	}

	/**
	 * 将List<String>存成文件，List<String>的每一个元素为文件里的每一行
	 * @param fileName 要保全的文件，传入文件全路径
	 * @param stringList 要保存的文件内容
	 * @param charset 字符编码，如果为空，则使用操作系统默认编码
	 */
	public static void saveFileFromStringList(String fileName, List<String> stringList, String charset) {
		FileUtil.saveFileFromStringList(new File(fileName), stringList, charset);		
	}

	/**
	 * 将byte[]数组写入文件
	 * @param fileName 要写入的文件，传入全路径
	 * @param data 要写入的数组
	 */
	public static void saveByteArrayToFile(String fileName, byte[] data) {
		FileUtil.saveByteArrayToFile(new File(fileName), data);
	}
	
	/**
	 * 创建文件夹
	 */
	public static void createDirIfNotExists(String path){
		File f1 = new File(path);
		if (!f1.exists() && !f1.isDirectory()) {
			f1.mkdirs();
		}
	}
	
	

	public static boolean deleteFile(String filePath) {// 删除单个文件
		boolean flag = false;
		File file = new File(filePath);
		if (file.isFile() && file.exists()) {// 路径为文件且不为空则进行删除
			file.delete();// 文件删除
			flag = true;
		}
		return flag;
	}

	public static boolean deleteDirectory(String dirPath) {// 删除目录（文件夹）以及目录下的文件
		// 如果sPath不以文件分隔符结尾，自动添加文件分隔符
		if (!dirPath.endsWith(File.separator)) {
			dirPath = dirPath + File.separator;
		}
		File dirFile = new File(dirPath);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;
		File[] files = dirFile.listFiles();// 获得传入路径下的所有文件
		for (int i = 0; i < files.length; i++) {// 循环遍历删除文件夹下的所有文件(包括子目录)
			if (files[i].isFile()) {// 删除子文件
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;// 如果删除失败，则跳出
			} else {// 运用递归，删除子目录
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;// 如果删除失败，则跳出
			}
		}
		if (!flag)
			return false;
		
		return dirFile.delete();
			 
	}

}
