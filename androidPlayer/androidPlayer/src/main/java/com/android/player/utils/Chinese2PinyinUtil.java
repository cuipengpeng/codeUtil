package com.android.player.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 例如： 中国转换成： zhongguo 或 zg
 */
public class Chinese2PinyinUtil {
	/**
	 * 汉字转换位汉语拼音首字母，英文字符不变
	 * 
	 * @return 拼音
	 */
	public static String converterToFirstSpell(String chines) {
		StringBuilder sb = new StringBuilder();
		String tempPinyin = null;
		for (int i = 0; i < chines.length(); ++i) {
			tempPinyin = getCharacterPinYin(chines.charAt(i));
			if (tempPinyin == null) {
				// 如果str.charAt(i)非汉字，则保持原样
				sb.append(chines.charAt(i));
			} else {
				sb.append(tempPinyin.charAt(0));
			}
		}
		return sb.toString();
	}

	/**
	 * 汉字转换位汉语拼音，英文字符不变
	 * 
	 * @param chines
	 *            汉字
	 * @return 拼音
	 */
	public static String converterToSpell(String chines) {
		StringBuilder sb = new StringBuilder();
		String tempPinyin = null;
		for (int i = 0; i < chines.length(); ++i) {
			tempPinyin = getCharacterPinYin(chines.charAt(i));
			if (tempPinyin == null) {
				// 如果str.charAt(i)非汉字，则保持原样
				sb.append(chines.charAt(i));
			} else {
				sb.append(tempPinyin);
			}
		}
		return sb.toString();
	}

	private static String getCharacterPinYin(char c) {
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
		
		String[] pinyin = null;
		try {
			pinyin = PinyinHelper.toHanyuPinyinStringArray(c, defaultFormat);
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
		// 如果是日文等特殊字符，可能抛出该异常
		}
		// 如果c不是汉字，toHanyuPinyinStringArray会返回null
		if (pinyin == null)
			return null;

		// 如果是多音字，只取第一个发音
		return pinyin[0];
	}
}
