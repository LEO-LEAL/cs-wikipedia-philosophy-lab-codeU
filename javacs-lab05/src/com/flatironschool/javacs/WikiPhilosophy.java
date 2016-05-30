package com.flatironschool.javacs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import org.jsoup.select.Elements;

public class WikiPhilosophy {

	final static WikiFetcher wf = new WikiFetcher();
	final static String phil_url = "https://en.wikipedia.org/wiki/Philosophy";
	/**
	* Tests a conjecture about Wikipedia and Philosophy.
	*
	* https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
	*
	* 1. Clicking on the first non-parenthesized, non-italicized link
		* 2. Ignoring external links, links to the current page, or red links
	  * 3. Stopping when reaching "Philosophy", a page with no links or a page
	  *    that does not exist, or when a loop occurs
	*
	* @param args
	* @throws IOException
	*/
	public static void main(String[] args) throws IOException {
		String url = "https://en.wikipedia.org/wiki/Java_(programming_language)";
		List<String> visit = new ArrayList<String>();
	 	visit.add(url);
	 	int parenthesisCount = 0;
	 	depthFirstParse(url, visit, parenthesisCount);
	  }

	public static void depthFirstParse (String url, List<String> visit, int parenthesisCount) throws IOException{
		if (visit.contains(url)){
			return;
	  }
		Elements paragraph = wf.fetchWikipedia(url);
	  for (int i = 0; i < paragraph.size(); i++) {
			Element currPara = paragraph.get(i);
			Iterable<Node> iter = new WikiNodeIterable(currPara);
			for (Node node: iter) {
				if (node instanceof TextNode) {
					if (node.toString().contains("(")) {
						parenthesisCount++;
					}
					if (node.toString().contains(")")) {
						parenthesisCount--;
					}
				}
				if (parenthesisCount == 0) {
					if (node instanceof Element) {
						Element curr = (Element) node;
						if (curr.tagName().equals("a")){
							String check = node.attr("abs:href");
							boolean isValid = true;
							if (url.equals(check)){
								isValid = false;
							}
							Element par = (Element) curr.parentNode();
							if (par.tagName().equals("i") || par.tagName().equals("em")){
								isValid = false;
							}
							if (isValid) {
								visit.add(check);
								depthFirstParse(check, visit, parenthesisCount);
								return;
							}
						}
					}
				}
			}
		}
		System.err.println("Nothing Valid Found");
		return;
	}
}
