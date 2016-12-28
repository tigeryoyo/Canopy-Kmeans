package com.tigeryoyo.cluster;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.tigeryoyo.util.SimilarityUtil;

/**
 * 利用Canopy对文档进行一次粗聚类，得到簇数K与各个簇的质心。 目前支持输入的测试数据为txt格式数据，一条数据为一个文本。
 * 
 * @author Chan
 *
 */
public class Canopy {

	/**
	 * 阀值T
	 */
	private double T = 0.2f;

	/**
	 * 存储原文档的内容，每一条String数据为一个文本
	 */
	private List<String> fileList;
	/**
	 * 存储转换后的k个canopy
	 */
	private List<List<String>> canopies;

	/**
	 * 构造函数，初始化各个对象
	 */
	public Canopy() {
		fileList = new ArrayList<String>();
		canopies = new ArrayList<List<String>>();
	}

	/**
	 * 
	 * @param fileName
	 *            文件名为完全限定名
	 */
	public void cluster(String fileName) {
		fileList = getFileString(fileName);
		List<String> tmpFileList = new ArrayList<String>(fileList);

		for (int i = 0; i < tmpFileList.size(); i++) {

			String pointStr = tmpFileList.get(i);
			if (pointStr == null) {
				continue;
			}
			tmpFileList.set(i, null);

			List<String> canopy = new ArrayList<String>();
			canopy.add(pointStr);

			/**
			 * eclipse的调试器不能调试iterator。why？ version:eclipse-jee-neon-1-win32
			 * eclipse-jee-neon-R-win32 可以正确调试
			 */
			for (int j = i + 1; j < tmpFileList.size(); j++) {

				String compareStr = tmpFileList.get(j);
				if (compareStr == null) {
					continue;
				}

				// 初始化Similar分词工具
				SimilarityUtil simUtil = new SimilarityUtil();
				// 计算两个文本的相似度
				double cmpSimilarity = simUtil.getSimilarity(pointStr, compareStr);

				// 如果相似度结果在T阀值圈内，将compareStr加入pointStr所在的canopy
				if (cmpSimilarity >= T) {
					canopy.add(compareStr);
					tmpFileList.set(j, null);
				}
			}
			canopies.add(canopy);
		}
	}

	/**
	 * 将文件转换为指定格式list
	 * 
	 * @param file
	 * @return
	 */
	private List<String> getFileString(String fileName) {
		// 结果保存在res中
		List<String> res = new ArrayList<String>();

		File file = new File(fileName);
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);

			String line = br.readLine();
			while (line != null) {
				// 去掉文档中的空字符串
				if (!line.isEmpty()) {
					res.add(line);
				}
				line = br.readLine();
			}
			br.close();
			fr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return res;
	}

	/**
	 * 获取canopies.
	 * 
	 * @return
	 */
	public List<List<String>> getCanopies() {
		return canopies;
	}

	/**
	 * 获取原始数据集
	 * 
	 * @return
	 */
	public List<String> getFileList() {
		return fileList;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			FileOutputStream fos = new FileOutputStream("C:/Users/Chan/Desktop/测试结果/0.-300.txt");

			long start = System.currentTimeMillis();

			Canopy canopyCluster = new Canopy();
			canopyCluster.cluster("library/Test.txt");
			int i = 0;
			for (List<String> canopy : canopyCluster.getCanopies()) {
				String line = "***************分类" + (++i) + "***************\n";
				fos.write(line.getBytes());
				System.out.println(line);
				for (String str : canopy) {
					fos.write((str + '\n').getBytes());
					System.out.println(str);
				}
				fos.write("\n".getBytes());
				System.out.println();
			}
			fos.close();
			long end = System.currentTimeMillis();
			System.out.println(end - start);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// List<String> test = new ArrayList<String>();
		// test.add("a");
		// test.add("b");
		// test.add("c");
		// test.add("d");
		// test.add("e");
		//
		// Iterator<String> iterator = test.iterator();
		// while (iterator.hasNext()) {
		// String x = iterator.next();
		//
		// test.remove(0);
		//
		// Iterator<String> iteratorAnother = test.iterator();
		// while (iteratorAnother.hasNext()) {
		// String y = iteratorAnother.next();
		//
		// }
		//
		// iterator = test.iterator();
		// }
		// int j = 4;
		// j = 1;
	}

}
