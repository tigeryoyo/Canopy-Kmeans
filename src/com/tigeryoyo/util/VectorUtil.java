package com.tigeryoyo.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 文本向量化
 * 
 * @author Chan
 *
 */
public class VectorUtil {

	/**
	 * 存储原文本(去除了空行)
	 */
	private List<String> docs;
	/**
	 * 存储每一文本内单词的总数
	 */
	private List<Integer> docTermsCount;
	/**
	 * 包含某一词的文档数
	 */
	private HashMap<String, Integer> termDocs;
	/**
	 * 计算所有文本的VSM table
	 */
	private List<List<Float>> vsmTable;

	public List<HashMap<String, Float>> testTable;
	
	public VectorUtil(String filePath) {
		testTable = new ArrayList<HashMap<String, Float>>();
		vsmTable = calcVsmTable(filePath);
	}
	
	public static void main(String[] args) {
		
		VectorUtil vu = new VectorUtil("library/Test2.txt");
		List<List<Float>> list = vu.getVsmTable();
//		List<HashMap<String, Float>> lh = vu.testTable;
//		for(List<Float> lf : list){
//			for(Float f:lf){
//				System.out.print(f+" ");
//			}
//			System.out.println();
//		}
		
		List<HashMap<String, Float>> lh = vu.testTable;
		boolean flag = true;
		int a = 1;
		for(HashMap<String, Float> lf : lh){
			Set<String> set = lf.keySet();
			Iterator<String> iterator = set.iterator();
			
			if(flag){
				int i = 0;
				Iterator<String> tempiterator = set.iterator();
				while(tempiterator.hasNext()){
					String term = tempiterator.next();
					i++;
					System.out.printf("\t%s",term);
				}
				System.out.println();
				System.out.println("总共有"+i+"个词");
				flag = false;
			}
			
			System.out.print("文档"+a+": ");
			a++;
			while(iterator.hasNext()){
				String term = iterator.next();
				System.out.printf("%f  ",lf.get(term));
			}
			System.out.println();
		}
	}

	/**
	 * 计算文本的向量空间模型
	 * 
	 * @param file
	 * @return
	 */
	public List<List<Float>> calcVsmTable(String file) {
		List<List<Float>> vsmTable = new ArrayList<List<Float>>();
		// 所有文档的<词,词数>对
		List<HashMap<String, Integer>> docsTerms = setDocsTerms(file);
		List<Float> vsmSubitem;
		
		HashMap<String, Float> testMap = null;

		for (int i = 0; i < docs.size(); i++) {
			HashMap<String, Integer> docTerms = docsTerms.get(i);
			vsmSubitem = new ArrayList<Float>();
			testMap = new HashMap<String, Float>();
			Set<String> set = termDocs.keySet();
			Iterator<String> iterator = set.iterator();
			while (iterator.hasNext()) {
				String term = iterator.next();
				float tf, idf;
				if (docTerms.containsKey(term)) {
					// tf=当前文本 特定词的个数/词的总数
					tf = (float) docsTerms.get(i).get(term) / docTermsCount.get(i);
					// idf=log(文档总数/(包含某词的文本数+1))
					idf = (float) (Math.log((float) docsTerms.size() / (termDocs.get(term)))+1);
				} else {
					testMap.put(term, 0.0f);
					tf = idf = 0.0f;
				}
				
				testMap.put(term, tf*idf);
				vsmSubitem.add(tf * idf);
			}
			testTable.add(testMap);
			vsmTable.add(vsmSubitem);
		}

		return vsmTable;
	}

	/**
	 * 获取所有文档的<词,词数>对,包含某一词的文档数，存储各自文本词的总数。
	 * 
	 * @param docsTerm
	 * @return
	 */
	private List<HashMap<String, Integer>> setDocsTerms(String file) {
		List<HashMap<String, Integer>> docsTerm = new ArrayList<HashMap<String, Integer>>();
		docs = new ArrayList<String>();
		docTermsCount = new ArrayList<Integer>();
		termDocs = new HashMap<String, Integer>();
		HashMap<String, Integer> docMap;
		try {
			FileReader fr = new FileReader(new File(file));
			BufferedReader br = new BufferedReader(fr);

			String line;
			while ((line = br.readLine()) != null) {

				if (line.isEmpty()) {
					continue;
				}

				docMap = new HashMap<String, Integer>();
				Integer count = 0;
				List<String> terms = SplitWordUtil.getDocSplit(line);
				for (String term : terms) {
					if (docMap.containsKey(term)) {
						docMap.replace(term, docMap.get(term) + 1);
					} else {
						docMap.put(term, 1);

						if (termDocs.containsKey(term)) {
							termDocs.replace(term, termDocs.get(term) + 1);
						} else {
							termDocs.put(term, 1);
						}
					}

					count++;
				}

				docs.add(line);
				docsTerm.add(docMap);
				docTermsCount.add(count);
			}

			br.close();
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return docsTerm;
	}

	/**
	 * @return the docs
	 */
	public List<String> getDocs() {
		return docs;
	}

	/**
	 * @return the vsmTable
	 */
	public List<List<Float>> getVsmTable() {
		return vsmTable;
	}

}
