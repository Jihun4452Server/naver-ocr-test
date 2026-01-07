package cmc.naverocr.application;

import cmc.naverocr.infrastructure.clova.ClovaOcrClient;
import cmc.naverocr.infrastructure.clova.ClovaOcrParser;
import cmc.naverocr.infrastructure.clova.ClovaOcrResponse;
import cmc.naverocr.model.OcrResult;
import cmc.naverocr.support.OcrPostProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OcrService {

	private final ClovaOcrClient client;
	private final ObjectMapper objectMapper;
	private final ClovaOcrParser parser;

	public OcrResult ocr(byte[] bytes, String filename, String format, String contentType) {
		String rawJson = client.recognizeRawJson(bytes, filename, format, contentType);

		ClovaOcrResponse parsed;
		try {
			parsed = objectMapper.readValue(rawJson, ClovaOcrResponse.class);
		} catch (Exception e) {
			throw new IllegalStateException("response parse failed", e);
		}

		String plain = parser.toPlainText(parsed);

		String cleaned = OcrPostProcessor.cleanToSingleLine(plain);

		return new OcrResult(cleaned);
	}
}
