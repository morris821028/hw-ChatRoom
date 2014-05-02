package gameMaterial.UI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class StringUtil {
	private static String str = null;

	public static String generateRandomString() {
		if (str == null)
			str = Test();
		int len = (int) (Math.random() * 50) + 2;
		String ret = "";
		try {
			ret = str.substring(0, len);
			str = str.substring(len);
		} catch (Exception e) {

		}
		return ret;
	}

	private static String Test() {
		String fileText = "";
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					StringUtil.class.getResource("text/testdialog.txt")
							.openStream(), "unicode"));
			String htmlText;
			while ((htmlText = reader.readLine()) != null) {
				// Keep in mind that readLine() strips the newline
				// characters
				fileText += htmlText + "\n";
			}
			reader.close();
		} catch (Exception ee) {
			System.out.println(ee.getMessage());
			ee.getStackTrace();
		}
		return fileText;
	}

	public static int countByteString(String text) {
		int textLength = text.length();
		int byteLength = 0;
		StringBuffer returnStr = new StringBuffer();
		for (int i = 0; i < textLength; i++) {
			String str_i = text.substring(i, i + 1);
			if (str_i.getBytes().length == 1) {// 英文
				byteLength++;
			} else {// 中文
				byteLength += 2;
			}
			returnStr.append(str_i);
		}
		try {
			if (byteLength < text.getBytes("GBK").length) {// getBytes("GBK")每个汉字长2，getBytes("UTF-8")每个汉字长度为3
				returnStr.append(" ");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return byteLength;
	}

	public static String subString(String text, int beginIdx, int length) {
		int textLength = text.length();
		int byteLength = 0;
		StringBuffer returnStr = new StringBuffer();
		for (int i = beginIdx; byteLength < length && i < textLength; i++) {
			String str_i = text.substring(i, i + 1);
			if (str_i.getBytes().length == 1) {// 英文
				byteLength++;
			} else {// 中文
				byteLength += 2;
			}
			if (byteLength > length)
				break;
			returnStr.append(str_i);
		}
		try {
			if (byteLength < text.getBytes("GBK").length) {// getBytes("GBK")每个汉字长2，getBytes("UTF-8")每个汉字长度为3
				returnStr.append(" ");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return returnStr.toString();
	}
}