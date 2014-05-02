package frame;

import java.net.ServerSocket;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.swing.text.*;
import javax.swing.text.html.*;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.parser.*;
import org.jsoup.nodes.*;
import org.jsoup.nodes.Document;
import java.util.*;

import jsyntaxpane.DefaultSyntaxKit;

/**
 * 一個提供 HTML 語法的聊天面板，以及聊天室專用語法的轉換。
 * 
 * @author Shiang-Yun Yang
 * 
 */
public class MTextArea extends JEditorPane {
	HTMLEditorKit kit;
	HTMLDocument doc;

	MTextArea() {
		kit = new HTMLEditorKit();
		doc = new HTMLDocument();
		this.setCSS();
		this.setEditorKit(kit);
		doc = (HTMLDocument) kit.createDefaultDocument();
		kit.setAutoFormSubmission(true);
		this.setDocument(doc);
		this.setEditable(false);
		this.setOpaque(false);
		this.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					if (Desktop.isDesktopSupported()) {
						try {
							Desktop.getDesktop().browse(e.getURL().toURI());
						} catch (Exception ee) {
							System.err.println(ee.getMessage());
						}
					}
				}
			}
		});
		this.appendHTML("<u><a href=\"http://mypaper.pchome.com.tw/zerojudge\">VERSION 2.0 - Contact Me.</a></u>");
		/*
		 * this.append("一般輸入", 0); this.append("密語", 1); this.append("系統", 2);
		 */
	}

	/**
	 * testing
	 * @param tagClass
	 * @return
	 */
	public MTextArea extractClass(String tagClass) {
		org.jsoup.nodes.Document doc = Jsoup.parse(this.getText());
		org.jsoup.select.Elements links = doc.getElementsByClass(tagClass);
		String other = "";
		for (org.jsoup.nodes.Element link : links) {
			other += link.toString();
			link.remove();
		}
		// System.out.println(other);
		MTextArea textarea = new MTextArea();
		textarea.setText(other);
		return textarea;
	}

	public void setCSS() {
		StyleSheet styleSheet = kit.getStyleSheet();
		styleSheet.addRule(".hide {text-indent:-9999px; overflow:hidden;}");
		styleSheet.addRule(".system {color: #A52A2A;}");
		styleSheet.addRule(".secret {color: #00ff00;}");
		styleSheet.addRule(".normal {color: #e8a317;}");
		styleSheet
				.addRule("font {font-family: Comic Sans MS; font-size: 20px; font-weight: bold;}");
		styleSheet
				.addRule("a {font-family: Comic Sans MS; font-size: 20px; color: #00ffff;}");
	}

	public void setTextFontFamily(String fontName) {
		StyleSheet styleSheet = kit.getStyleSheet();
		styleSheet.getStyle("a").removeAttribute(
				CSS.getAttribute("font-family"));
		styleSheet.getStyle("font").removeAttribute(
				CSS.getAttribute("font-family"));
		styleSheet.getStyle("a").addAttribute(StyleConstants.FontFamily,
				fontName);
		styleSheet.getStyle("font").addAttribute(StyleConstants.FontFamily,
				fontName);
		this.updateUI();
	}

	public void setTextFontSize(int fsize) {
		StyleSheet styleSheet = kit.getStyleSheet();
		styleSheet.getStyle("a").removeAttribute(CSS.getAttribute("font-size"));
		styleSheet.getStyle("font").removeAttribute(
				CSS.getAttribute("font-size"));
		styleSheet.getStyle("a").addAttribute(StyleConstants.FontSize, fsize);
		styleSheet.getStyle("font")
				.addAttribute(StyleConstants.FontSize, fsize);
		this.updateUI();
	}

	public static String transferHyperlink(String val) {
		return "<u><a href=\"" + val + "\">" + val
				+ "</a></u>";
	}

	public static String transferImageHyperlink(String val) {
		int height, width;
		try {
			URL url = new URL(val);
			BufferedImage image = ImageIO.read(url);
			height = image.getHeight();
			width = image.getWidth();
		} catch (Exception e) {
			return "";
		}
		double rate = 500.0 / width;
		height = (int) (height * rate);
		width = (int) (width * rate);
		return "<img src='" + val + "' width='" + width + "' height='" + height
				+ "' alt='" + val + "' class=\"normal\"></img>";
	}

	public void appendHTML(String content) {
		// insertHTML(HTMLDocument doc, int offset, String html, int popDepth,
		// int pushDepth, HTML.Tag insertTag)

		try {
			kit.insertHTML(doc, doc.getLength(), content, 0, 0, null);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public void append(String content, int sflag) {
		content = content.replaceAll("&", "&amp;");
		content = content.replaceAll("<", "&lt;");
		content = content.replaceAll(">", "&gt;");
		content = content.replaceAll(" ", "&nbsp;");
		content = content.replaceAll("\"", "&quot;");
		if (sflag == 0) {// NORMAL TALK TEXT
			content = "<div class=\"normal\"><font>" + content
					+ "</font></div>";
		} else if (sflag == 1) {// SECRET TALK TEXT
			content = "<div class=\"secret\"><font>" + content
					+ "</font></div>";
		} else {
			content = "<div class=\"system\"><font>" + content
					+ "</font></div>";
		}
		try {
			kit.insertHTML(doc, doc.getLength(), content, 0, 0, null);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}
