package de.schlund.rtstat.graphs.refactor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Changes a xml graph line to BAS or BAP (or ignores it if it's not a graphs
 * <b>elem<b>)
 * 
 * @author rcirstoiu
 * 
 */
public class SingleLineChanger {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		LineTypeDiscriminator discriminator = new LineTypeDiscriminator();

		System.out.println(discriminator.isBAPLine("   <elem>xrtp2:xrtp2_telefonica_b:asr</elem>"));
		System.out.println(discriminator.isBSLine("   <elem>xrtp2:xrtp2_telefonica_b:asr</elem>"));

		System.out.println(discriminator.isBAPLine("   <elem>xrtp2:xrtp2_telefonica:asr</elem>"));
		System.out.println(discriminator.isBSLine("   <elem>xrtp2:xrtp2_telefonica:asr</elem>"));

		SingleLineChanger refactor = new SingleLineChanger();
		System.out.println(refactor.refactorLine("<elem>xrtp2:xrtp2_telefonica_b:asr</elem>"));
		System.out.println(refactor.refactorLine("<elem>xrtp2:xrtp2_telefonica:asr</elem>"));

		System.out.println();

	}

	LineTypeDiscriminator discriminator = new LineTypeDiscriminator();

	public String refactorLine(String line) {

		if (discriminator.isBAPLine(line)) {

			return line.replace("<elem>xrtp2_test:", "<elem>xrtp2_bap:").replace("_b_test:", ":");
		}
		if (discriminator.isBSLine(line)) {

			return line.replace("<elem>xrtp2_test:", "<elem>xrtp2_bs:").replace("_test:", ":");
		}

		if (discriminator.isGraphNameLine(line)) {

			return line.replace("_b_test\"", "_bap_bs\"");

		} else {
			return line;
		}
	}

	/**
	 * determines what type of line a given string is
	 * 
	 * @author rcirstoiu
	 * 
	 */
	private static class LineTypeDiscriminator {
		final static Pattern BAP_PATTERN = Pattern.compile("(\\w*)<elem>xrtp2_test:xrtp2(\\w+)_b_test:(\\w+)</elem>");
		final static Pattern BS_PATTERN = Pattern.compile("(\\w*)<elem>xrtp2_test:xrtp2(\\w+)_test:(\\w+)</elem>");
		final static Pattern GRAPH_NAME_LINE = Pattern.compile("<graph");

		boolean isBAPLine(String line) {
			Matcher matcher = BAP_PATTERN.matcher(line);
			if (matcher.find())
				return true;
			else
				return false;

		}

		boolean isGraphNameLine(String line) {
			Matcher matcher = GRAPH_NAME_LINE.matcher(line);
			if (matcher.find())
				return true;
			else
				return false;

		}

		boolean isBSLine(String line) {
			Matcher matcher = BS_PATTERN.matcher(line);
			if (matcher.find())
				return true;
			else
				return false;

		}
	}

}
