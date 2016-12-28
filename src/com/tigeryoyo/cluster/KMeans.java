package com.tigeryoyo.cluster;

import java.util.ArrayList;
import java.util.List;

import com.tigeryoyo.util.SimilarityUtil;

public class KMeans {

	/**
	 * 分成k个簇
	 */
	private int k;
	/**
	 * 最大迭代次数
	 */
	private int maxIterator;
	/**
	 * 存储k个中心点，每个中心点表示最接近该簇的平均相似度的一条文本数据，初始为各个canopy的第一条数据
	 */
	private List<String> centerPoints;
	/**
	 * 存储k个簇
	 */
	private List<List<String>> clusters;
	/**
	 * 原始数据集
	 */
	private List<String> fileList;

	/**
	 * 初始化
	 * 
	 * @param fileName
	 *            完全限定性文件名
	 */
	public KMeans(String fileName) {
		centerPoints = new ArrayList<String>();
		Canopy canopy = new Canopy();
		canopy.cluster(fileName);
		fileList = canopy.getFileList();
		k = canopy.getCanopies().size();
		for (List<String> ls : canopy.getCanopies()) {
			centerPoints.add(ls.get(0));
		}

		clusters = initClusters();
	}

	/**
	 * 聚类核心过程
	 */
	public void cluster() {
		SimilarityUtil su = new SimilarityUtil();
		maxIterator = 5;
		while (true && maxIterator > 0) {
			maxIterator--;
			for (int i = 0; i < fileList.size(); i++) {
				double maxSim = 0.0f;
				int nearest = -1;
				for (int j = 0; j < centerPoints.size(); j++) {
					String tmpi = fileList.get(i);
					String tmpj = centerPoints.get(j);
					double sim = su.getSimilarity(tmpi, tmpj);
					if (sim >= maxSim) {
						maxSim = sim;
						nearest = j;
					}
				}

				if (nearest != -1) {
					clusters.get(nearest).add(fileList.get(i));
				}
			}

			List<String> newCenterPoints = getCenterPoints(clusters);
			// 判断函数，符合(中心点移动不大)则结束迭代，否则重新计算中心点，重新迭代
			if (judge(centerPoints, newCenterPoints)) {
				break;
			} else {
				// 若不满足退出迭代条件则清空clusters,清空centerPoints
				clusters.clear();
				clusters = initClusters();
				centerPoints.clear();
				centerPoints = newCenterPoints;
			}
		}
	}

	/**
	 * 得到聚类结果
	 * 
	 * @return
	 */
	public List<List<String>> getClusters() {
		return clusters;
	}
	
	/**
	 * 判断迭代能否结束函数
	 * 
	 * @param centerPoints2
	 * @param newCenterPoints
	 * @return
	 */
	private boolean judge(List<String> centerPoints, List<String> newCenterPoints) {
		// TODO Auto-generated method stub
		SimilarityUtil su = new SimilarityUtil();
		double sumSim = 0.0f;
		for(int i=0; i<clusters.size(); i++){
			sumSim += su.getSimilarity(centerPoints.get(i), newCenterPoints.get(i));
		}
		//比较新旧质心簇，若波动不大则表示趋于稳定结束迭代
		if(sumSim/clusters.size()>0.95f){
			return true;
		}
		return false;
	}

	/**
	 * 初始化clusters
	 * 
	 * @return
	 */
	private List<List<String>> initClusters() {
		List<List<String>> clusters = new ArrayList<List<String>>();
		for (int i = 0; i < k; i++) {
			clusters.add(new ArrayList<String>());
		}

		return clusters;
	}

	/**
	 * 获取中心点组成的簇
	 * 
	 * @return
	 */
	private List<String> getCenterPoints(List<List<String>> clusters) {
		List<String> centerPoints = new ArrayList<String>();
		for (List<String> cluster : clusters) {
			double meanSim = getMeanSimilarity(cluster);
			int nearest = getCenterPoint(cluster, meanSim);
			centerPoints.add(cluster.get(nearest));
		}
		return centerPoints;
	}

	/**
	 * 获取距离质心最近的点
	 * 
	 * @param cluster
	 * @param meanSim
	 * @return
	 */
	private int getCenterPoint(List<String> cluster, double meanSim) {
		if (cluster.size() == 1 || cluster.size() == 2) {
			return 0;
		}

		// 存储簇中的一点簇中其他点的平均相似度
		double sim[] = new double[cluster.size()];
		// 初始化相似度计算工具
		SimilarityUtil su = new SimilarityUtil();
		// 计算簇中的一点到其他点的平均距离存储在sim[]中
		for (int i = 0; i < cluster.size(); i++) {
			List<String> tmpCluster = new ArrayList<String>(cluster);
			tmpCluster.remove(i);
			sim[i] = su.getSimilarity(cluster.get(i), tmpCluster);
		}

		// 将sim[]与meanSim比较，与meanSim最接近的点即近似为为中心点
		int min = 0;
		double minDiff = Math.abs(sim[min] - meanSim);
		for (int i = 1; i < cluster.size(); i++) {
			double currDiff = Math.abs(sim[i] - meanSim);
			if (currDiff < minDiff) {
				minDiff = currDiff;
				min = i;
			}
		}

		return min;
	}

	/**
	 * 获取一个簇的平均相似度值
	 * 
	 * @param cluster
	 * @return
	 */
	private double getMeanSimilarity(List<String> cluster) {
		SimilarityUtil su = new SimilarityUtil();
		double sumSim = 0.0f;
		for (int i = 0; i < cluster.size(); i++) {
			for (int j = i + 1; j < cluster.size(); j++) {
				sumSim += su.getSimilarity(cluster.get(i), cluster.get(j));
			}
		}
		return 2 * sumSim / (cluster.size() * (cluster.size() - 1));
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		KMeans l = new KMeans("library/Test2.txt");
		l.cluster();
		int count=1;
		for (List<String> ls : l.getClusters()) {
			System.out.println("***************分类"+count+++":");

			for (String str : ls) {
				System.out.println(str);
			}
			System.out.println();
		}
	}

}
