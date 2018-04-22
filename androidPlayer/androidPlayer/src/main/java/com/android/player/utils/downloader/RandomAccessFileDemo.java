package com.android.player.utils.downloader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

@SuppressWarnings("unused")
public class RandomAccessFileDemo {

	public static void main(String[] args) throws Exception {
//		demo1();
//		demo2();
//		demo3();

		
		
	}

	private static void demo3() throws FileNotFoundException, IOException {
		RandomAccessFile raf = new RandomAccessFile("F:/test.txt", "rw");
		
		raf.writeLong(199797);		// 写出数字, 每个数字占8字节. DataOutputStream
		raf.writeLong(123998);
		raf.writeLong(321999);
		
		raf.seek(0);				// 指针移动到0号位置
		
		System.out.println(raf.readLong());		// 读出3个数字
		System.out.println(raf.readLong());
		System.out.println(raf.readLong());
		
		raf.close();
	}

	private static void demo2() throws FileNotFoundException, IOException {
		RandomAccessFile raf = new RandomAccessFile("F:/xxx.exe", "rw");
		raf.setLength(1024 * 100);	// 设置文件的大小, 如原文件不足该大小, 则写入0
		raf.close();
	}

	private static void demo1() throws FileNotFoundException, IOException {
		RandomAccessFile raf = new RandomAccessFile("F:/test.txt", "rw");
		
		raf.write(97);
		raf.write(98);
		raf.write(99);
		
		raf.seek(10);		// 将操作文件的指针移动到了10号索引位置(中间空的写出了0)
		
		raf.write(100);
		raf.write(101);
		raf.write(102);
		
		raf.seek(20);
		
		raf.write(103);
		raf.write(104);
		raf.write(105);
		
		raf.seek(3);
		
		raf.write('o');
		raf.write('o');
		raf.write('o');
		
		raf.close();
	}

}
