package com.tigeryoyo.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Kmeans {

	/**
	 * 文本向量化工具
	 */
	private VectorUtil vu;
	/**
	 * 划分k个簇
	 */
	private int k;
	/**
	 * 最大float值
	 */
	private static float MAX_VALUE = Float.MAX_VALUE;

	/**
	 * 判断是否结合canopy算法
	 */
	public static boolean linkWithCanopy = true;
	
	private boolean isLinkedWithCanopy;
	/**
	 * 质心集合
	 */
	private List<List<Float>> centroides;
	
	/**
	 * @param k 定义k个结果类簇
	 */
	public Kmeans(int k) {
		isLinkedWithCanopy = false;
		this.k = k;
	}

	/**
	 * 是否使用canopy算法
	 * @param linkWithCanopy
	 */
	public Kmeans(boolean linkWithCanopy){
		isLinkedWithCanopy = linkWithCanopy;
	}
	
	public static void main(String[] args) {
		Kmeans kmeans = new Kmeans(3);
		List<List<Integer>> canopies = kmeans.cluster("library/Test2.txt");
		List<String> docs = kmeans.getContents();
		for (int i = 0; i < canopies.size(); i++) {
			String line = "***************分类" + (i + 1) + "***************\n";
			System.out.println(line);
			List<Integer> canopy = canopies.get(i);
			for (int j = 0; j < canopy.size(); j++) {
				line = docs.get(canopy.get(j)) + '\n';
				System.out.print(line);
			}
			System.out.println();
		}
	}
	
	public List<List<Integer>> cluster(String fileName) {
		
		//如果结合canopy使用
		if(isLinkedWithCanopy){
			Canopy canopy = new Canopy();
			canopy.cluster(fileName);
			//获取向量空间模型
			this.vu = canopy.getVu();
			// 随机取初始化质心集合
			this.centroides = canopy.getCentroides();
		}else{
			//获取向量空间模型
			this.vu = new VectorUtil(fileName);
			// 随机取初始化质心集合
			this.centroides = randomInitKCenterPoint();
		}
		
		List<List<Integer>> canopies = null;
		// 规定迭代次数
		int iterations = 0;
		while (iterations < 10) {
			iterations++;
			// 获取文本向量空间模型
			List<List<Float>> vsmTable = vu.getVsmTable();
			//初始化结果集合
			canopies = new ArrayList<List<Integer>>();
			for(int i=0; i<centroides.size(); i++){
				canopies.add(new ArrayList<Integer>());
			}
			//聚类
			for (int i = 0; i < vsmTable.size(); i++) {
				float min_value = Kmeans.MAX_VALUE;
				int min_index = 0;
				
				for (int j = 0; j < centroides.size(); j++) {
					float currentDis = calcEuclidDis(vsmTable.get(i), centroides.get(j));

					if (currentDis < min_value) {
						min_value = currentDis;
						min_index = j;
					}
				}
				
				canopies.get(min_index).add(i);
			}
			//获取新的质心集合
			List<List<Float>> newCentroides = new ArrayList<List<Float>>();
			System.out.println("***********第"+(iterations)+"次代次********");
			for(int i=0; i<canopies.size(); i++){
				System.out.print("类簇"+(i+1)+"中有："+canopies.get(i).size());
				System.out.print(" :");
				for(int j=0; j<canopies.get(i).size(); j++){
					System.out.print(canopies.get(i).get(j)+",");
				}
				System.out.println();
			}
			System.out.println("若出现空分类则出现错误，肯定是第一次迭代出现的");
			
			for(int i=0; i<canopies.size(); i++){
				List<Float> newCentroid = calcCentroid(canopies.get(i));
				newCentroides.add(newCentroid);
			}
			
			//判断质心集合是否发生变化,若无变化则符合退出条件
			if(isQualified(centroides, newCentroides)){
				break;
			}
			
			//若质心集合发生变化，那么重新迭代
			centroides = new ArrayList<List<Float>>(newCentroides);
		}
		
		System.out.println("总得迭代次数"+iterations);
		return canopies;
	}
	
	/**
	 * 判断质心集合是否发生变化,若无变化则符合退出条件
	 * @param oldCentroides
	 * @param newCentroides
	 * @return
	 */
	private boolean isQualified(List<List<Float>> oldCentroides, List<List<Float>> newCentroides){
		for(int i=0; i<oldCentroides.size(); i++){
			List<Float> oldCentroid = oldCentroides.get(i);
			List<Float> newCentroid = newCentroides.get(i);
			
			if(!oldCentroid.equals(newCentroid)){
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * 计算两个文本的欧式距离
	 * 
	 * @param doc1
	 * @param doc2
	 * @return
	 */
	private float calcEuclidDis(List<Float> doc1, List<Float> doc2) {
		
		if(doc1.isEmpty() || doc2.isEmpty()){
			return Kmeans.MAX_VALUE;
		}
		
		
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
	 * 计算簇的质心
	 * @return
	 */
	private List<Float> calcCentroid(List<Integer> indexs) {
		
		if(indexs.isEmpty()){
			return new ArrayList<Float>();
		}
		
		List<List<Float>> canopy = new ArrayList<List<Float>>();
		List<List<Float>> vsmTable = vu.getVsmTable();
		for(Integer i:indexs){
			canopy.add(vsmTable.get(i));
		}
		
		
		List<Float> centroid = new ArrayList<Float>();
		int termsCount = canopy.get(0).size();

		for (int i = 0; i < canopy.size(); i++) {
			float original = 0.0f;
			for (int j = 0; j < termsCount; j++) {
				if(i == 0){
					centroid.add(j, canopy.get(i).get(j));
				} else{
					original = centroid.get(j);
					centroid.set(j, canopy.get(i).get(j)+original);
				}
				
			}
		}

		float mean = 0.0f;
		for (int i = 0; i < termsCount; i++) {
			mean = centroid.get(i) / canopy.size();
			centroid.set(i, mean);
		}

		return centroid;
	}

	/**
	 * 初始化K个质心
	 * 
	 * @param size
	 * @return
	 */
	public List<List<Float>> randomInitKCenterPoint() {
		List<List<Float>> centroides = new ArrayList<List<Float>>();

		List<List<Float>> vsmTable = vu.getVsmTable();
		int size = vsmTable.size();
		int[] randomBuff = new int[size];
		for (int i = 0; i < size; i++) {
			randomBuff[i] = i;
		}

		for (int i = 0; i < k; i++) {
			int index;
			if(size - 1 == 0){
				size--;
				break;
			}else{
				index = new Random().nextInt(size - 1);
			}

			int tmp = randomBuff[index];
			randomBuff[index] = randomBuff[size - 1];
			randomBuff[size - 1] = tmp;

			size--;
		}

		for (int i = size; i < randomBuff.length; i++) {
			centroides.add(vsmTable.get(randomBuff[i]));
		}

//		centroides.add(vsmTable.get(6));
//		centroides.add(vsmTable.get(0));
//		centroides.add(vsmTable.get(11));
		
		return centroides;
	}
	
	public List<List<Float>> planInitKCenterPoint(){
		return null;
	}

	public List<String> getContents() {
		return vu.getDocs();
	}
	
}
