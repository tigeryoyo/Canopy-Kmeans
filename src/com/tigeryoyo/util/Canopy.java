package com.tigeryoyo.util;

import java.util.ArrayList;
import java.util.List;

public class Canopy {

	/**
	 * 阀值T
	 */
	private float T = 0.15f;

	/**
	 * 文本向量化工具
	 */
	private VectorUtil vu;

	/**
	 * 存储转换后的k个canopy,存储文本数据序列号
	 */
	private List<List<Integer>> canopies;

	/**
	 * 构造函数，初始化各个对象
	 */
	public Canopy() {
		canopies = new ArrayList<List<Integer>>();
	}

	/**
	 * 
	 * @param fileName
	 *            文件名为完全限定名
	 */
	public void cluster(String fileName) {
		vu = new VectorUtil(fileName);
		// 获取文本向量空间模型
		List<List<Float>> vsmTable = vu.getVsmTable();

		boolean[] isMapped = new boolean[vsmTable.size()];
		for (int i = 0; i < vsmTable.size(); i++) {
			if (isMapped[i]) {
				continue;
			}
			isMapped[i] = true;
			List<Integer> canopy = new ArrayList<Integer>();
			canopy.add(i);

			for (int j = i + 1; j < vsmTable.size(); j++) {
				if (isMapped[j]) {
					continue;
				}

				float test = getEuclidDis(vsmTable.get(i), vsmTable.get(j));
				float cosDis = getCosDis(vsmTable.get(i), vsmTable.get(j));
				// 如果相似度结果在T阀值圈内，将compareStr加入pointStr所在的canopy
				if (cosDis >= T) {
					canopy.add(j);
					isMapped[j] = true;
				}
			}

			canopies.add(canopy);
		}
	}

	private float getCosDis(List<Float> doc1, List<Float> doc2) {
		if (doc1.size() != doc2.size()) {
			System.out.println("Error!");
			return -1.0f;
		}

		float fzSum = 0.0f;
		float fmaSum = 0.0f;
		float fmbSum = 0.0f;
		for (int i = 0; i < doc1.size(); i++) {
			float a = doc1.get(i);
			float b = doc2.get(i);

			fzSum += a * b;
			fmaSum += a * a;
			fmbSum += b * b;
		}

		return (float) (fzSum / Math.sqrt(fmaSum * fmbSum));
	}

	public List<List<Float>> getCentroides(){
		List<List<Float>> centroides = new ArrayList<List<Float>>();
		List<List<Float>> vsmTable = vu.getVsmTable();
		for(List<Integer> canopy : getCanopies()){
			centroides.add(vsmTable.get(canopy.get(0)));
		}
		
		return centroides;
	}
	
	/**
	 * 计算两个文本的欧式距离
	 * 
	 * @param doc1
	 * @param doc2
	 * @return
	 */
	private float getEuclidDis(List<Float> doc1, List<Float> doc2) {
		if (doc1.size() != doc2.size()) {
			System.out.println("Error!");
			return -1.0f;
		}

		float sum = 0.0f;
		for (int i = 0; i < doc1.size(); i++) {
			sum += Math.pow(doc1.get(i) - doc2.get(i), 2);
		}

		return (float) Math.sqrt(sum);
	}

	/**
	 * 获取canopies.
	 * 
	 * @return
	 */
	public List<List<Integer>> getCanopies() {
		return canopies;
	}

	public List<String> getContents() {
		return vu.getDocs();
	}

	
	/*
我在华中科技大学的网络与计算中心学习呀
男子假冒刑警骗财骗色 多名女子上当1人已怀孕
假冒刑警骗财骗色
利用微信骗财骗色 一假冒刑警领刑两年半
国考50万人弃考 答题已用尽“洪荒之力”
我在华中科技大学的网络与计算中心学习
国考50万人弃考 国考“弃考”折射出就业的理性
国考50万人弃考 参加国考笔试考生竞争激烈程度有所缓和
国考50万人弃考 2016年国考行测真题及答案
我正在华中科技大学的网络与计算中心学习
	 */
	
	/**
	 * @return the vu
	 */
	public VectorUtil getVu() {
		return vu;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub


			Canopy canopyCluster = new Canopy();
			canopyCluster.cluster("library/Test2.txt");
			List<String> docs = canopyCluster.getContents();
			for (int i = 0; i < canopyCluster.getCanopies().size(); i++) {
				String line = "***************分类" + (i + 1) + "***************\n";
				System.out.println(line);
				List<Integer> canopy = canopyCluster.getCanopies().get(i);
				for (int j = 0; j < canopy.size(); j++) {
					line = docs.get(canopy.get(j)) + '\n';
					System.out.print(line);
				}
				System.out.println();
			}
			// for (List<Integer> canopy : canopyCluster.getCanopies()) {
			// for (Integer str : canopy) {
			// System.out.println(str);
			// }
			// System.out.println();
			// }
	}

}
