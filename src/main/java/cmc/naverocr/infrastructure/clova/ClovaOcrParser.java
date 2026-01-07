package cmc.naverocr.infrastructure.clova;

import org.springframework.stereotype.Component;

@Component
public class ClovaOcrParser {

	public String toPlainText(ClovaOcrResponse response) {
		if (response == null || response.images() == null || response.images().isEmpty()) return "";

		var img = response.images().get(0);
		if (img.fields() == null) return "";

		StringBuilder sb = new StringBuilder();
		for (var f : img.fields()) {
			if (f == null || f.inferText() == null) continue;
			sb.append(f.inferText());
			sb.append(Boolean.TRUE.equals(f.lineBreak()) ? "\n" : " ");
		}
		return sb.toString().trim();
	}
}
