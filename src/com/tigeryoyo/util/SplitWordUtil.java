package com.tigeryoyo.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.ansj.util.FilterModifWord;

/**
 * @author Chan
 * @description 利用Ansj分词工具将文本分词 {@link} http://nlpchina.github.io/ansj_seg/
 */
public class SplitWordUtil {
	/**
	 * @description 将文本分词
	 * @return
	 */
	public static List<String> getDocSplit(String string) {
		// 设置停用词
		setStopword();

		List<Term> splitRes = ToAnalysis.parse(string);
		// 去除停用词后的分词结果
		splitRes = FilterModifWord.modifResult(splitRes);

		List<String> finalRes = new ArrayList<String>();
		for (Term t : splitRes) {
			finalRes.add(t.getName());
		}

		return finalRes;
	}
	
	

	/**
	 * 设置停用词
	 */
	private static void setStopword() {
		File stopwords = new File("library/stopwords.txt");

		try {
			FileReader fr = new FileReader(stopwords);
			BufferedReader br = new BufferedReader(fr);

			String line;
			while ((line = br.readLine())!= null) {
				FilterModifWord.insertStopWord(line);
			}
			
			br.close();
			fr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		List<String> ls = SplitWordUtil.getDocSplit("武汉大学是一所十分厉害的学校");
		for(String str : ls){
			System.out.println(str);
		}
		System.out.println();
	}

}
