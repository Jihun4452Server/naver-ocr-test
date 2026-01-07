package cmc.naverocr.support;

import java.util.*;
import java.util.regex.*;

public final class OcrPostProcessor {

	private OcrPostProcessor() {}

	private static final Pattern EXP_LINE =
		Pattern.compile(".*\\d.*[÷×+\\-*/()]+.*[=?].*");

	private static final Pattern CHOICE_LINE =
		Pattern.compile("^\\s*([1-5])\\s*[).]?\\s*(.+?)\\s*$");

	private static final Pattern NOISE_SHORT_NUMBER =
		Pattern.compile("^\\s*\\d{1,2}\\s*$");

	public static String cleanToSingleLine(String plainText) {
		List<String> lines = Arrays.stream(plainText.split("\\R"))
			.map(String::trim)
			.filter(s -> !s.isBlank())
			.toList();

		if (!lines.isEmpty() && NOISE_SHORT_NUMBER.matcher(lines.get(0)).matches()) {
			lines = lines.subList(1, lines.size());
		}

		lines = lines.stream()
			.filter(s -> !(s.length() == 1 && !Character.isLetterOrDigit(s.charAt(0))))
			.toList();

		return String.join(" ", lines).replaceAll("\\s{2,}", " ").trim();
	}

	public static Optional<String> extractExpression(String plainText) {
		return Arrays.stream(plainText.split("\\R"))
			.map(String::trim)
			.filter(s -> !s.isBlank())
			.filter(s -> EXP_LINE.matcher(s).matches())
			.findFirst();
	}

	public static Map<Integer, String> extractChoices(String plainText) {
		Map<Integer, String> map = new LinkedHashMap<>();
		for (String line : plainText.split("\\R")) {
			Matcher m = CHOICE_LINE.matcher(line.trim());
			if (m.matches()) {
				map.put(Integer.parseInt(m.group(1)), m.group(2).trim());
			}
		}
		return map;
	}
}
