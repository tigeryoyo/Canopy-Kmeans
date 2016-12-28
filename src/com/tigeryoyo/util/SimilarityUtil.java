package com.tigeryoyo.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * 文本相似度计算，利用余弦公式计算相似度
 * 
 * @author Chan
 *
 */
public class SimilarityUtil {
	/**
	 * @description 存储单词与权重，int[0]代表第一个文本的单词权重，int[1]表示第二个。
	 * @warning 不能设置为静态属性，要不然map里数值未清空造成错误。
	 */
	private HashMap<String, int[]> map;

	/**
	 * 构造函数
	 */
	public SimilarityUtil() {

	}

	/**
	 * @description 根据余弦公式，比较两个文本的相似度。
	 * @param str1
	 *            文本1,String类型。
	 * @param str2
	 *            文本2,String类型。
	 * @return 文本1与文本2的相似度。
	 */
	public double getSimilarity(String str1, String str2) {
		map = new HashMap<String, int[]>();
		// 将文本str1与文本str2分词
		List<String> doc1 = SplitWordUtil.getDocSplit(str1);
		List<String> doc2 = SplitWordUtil.getDocSplit(str2);

		// 统计doc1中出现的词与词频。
		for (String s : doc1) {
			if (map.get(s) == null) {
				map.put(s, new int[] { 1, 0 });
			} else {
				map.get(s)[0]++;
			}
		}

		// 统计doc2中出现的词与词频。
		for (String s : doc2) {
			if (map.get(s) == null) {
				map.put(s, new int[] { 0, 1 });
			} else {
				map.get(s)[1]++;
			}
		}

		// 余弦公式的分子与分母
		double numerator = 0.0f;
		double denominator1 = 0.0f;
		double denominator2 = 0.0f;

		// 根据map里的元素计算余弦公式的分子与分母
		Iterator<String> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			int[] mapElement = map.get(iterator.next());
			numerator += mapElement[0] * mapElement[1];
			denominator1 += mapElement[0] * mapElement[0];
			denominator2 += mapElement[1] * mapElement[1];
		}

		// 分母为零时
		if (denominator1 == 0 || 0 == denominator2) {
			return 0.0f;
		}

		return numerator / Math.sqrt(denominator1 * denominator2);
	}

	/**
	 * 一条数据与一簇数据的相似度
	 * 
	 * @param str
	 * @param strList
	 * @return
	 */
	public double getSimilarity(String str, List<String> strList) {
		if (strList.isEmpty()) {
			return 0.0f;
		}

		// 文本str与strList的每条str的相似度之和
		double sumSim = 0.0f;
		for (String string : strList) {
			sumSim += getSimilarity(str, string);
		}

		return sumSim / strList.size();
	}

	// *******************欧式距离作为相似度**********************//
	/**
	 * 计算两条文本之间的欧式距离
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public double getDistance(String str1, String str2) {
		map = new HashMap<String, int[]>();
		// 将文本str1与文本str2分词
		List<String> doc1 = SplitWordUtil.getDocSplit(str1);
		List<String> doc2 = SplitWordUtil.getDocSplit(str2);

		// 统计doc1中出现的词与词频。
		for (String s : doc1) {
			if (map.get(s) == null) {
				map.put(s, new int[] { 1, 0 });
			} else {
				map.get(s)[0]++;
			}
		}

		// 统计doc2中出现的词与词频。
		for (String s : doc2) {
			if (map.get(s) == null) {
				map.put(s, new int[] { 0, 1 });
			} else {
				map.get(s)[1]++;
			}
		}

		double sumDis = 0.0f;
		Iterator<String> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			int[] mapElement = map.get(iterator.next());
			sumDis += Math.pow(mapElement[0] - mapElement[1], 2);
		}

		return Math.sqrt(sumDis);
	}

	// /**
	// * 计算一条文本与一簇文本质心的欧式距离
	// * @param str
	// * @param strList
	// * @return
	// */
	// public double getDistance(String str, List<String> strList) {
	// double sumDis = 0.0f;
	// for(int i=0; i<strList.size(); i++){
	// for(int j=i+1; j<strList.size(); j++){
	// sumDis += getDistance(strList.get(i),strList.get(j));
	// }
	// }
	// }

	public static void main(String[] args) {
		// String str1 = "天气不错啊华中科技大学";
		// String str2 = "小明是个不错的同学";

		String str1 = "华中科技大学是个久负盛名的学校。";
		String str2 = "华中科技大学是个不错的学校。";

		SimilarityUtil sim = new SimilarityUtil();
		System.out.println(sim.getSimilarity(str1, str2));
	}

}
