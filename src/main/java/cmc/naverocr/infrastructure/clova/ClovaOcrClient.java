package cmc.naverocr.infrastructure.clova;

import cmc.naverocr.config.ClovaOcrProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class ClovaOcrClient {

	private final ClovaOcrProperties props;
	private final ObjectMapper objectMapper;

	public String recognizeRawJson(byte[] imageBytes, String filename, String format, String contentType) {
		if (imageBytes == null || imageBytes.length == 0) {
			throw new IllegalArgumentException("imageBytes is empty");
		}

		final String resolvedFormat = StringUtils.hasText(format) ? format : "jpg";
		final String resolvedFilename = StringUtils.hasText(filename)
			? filename
			: "ocr-" + UUID.randomUUID() + "." + resolvedFormat;

		ClovaOcrRequest req = new ClovaOcrRequest(
			"V2",
			UUID.randomUUID().toString(),
			System.currentTimeMillis(),
			List.of(new ClovaOcrRequest.Image(resolvedFormat, resolvedFilename))
		);

		final String messageJson;
		try {
			messageJson = objectMapper.writeValueAsString(req);
		} catch (Exception e) {
			throw new IllegalStateException("message json serialize failed", e);
		}

		MediaType ct;
		try {
			ct = StringUtils.hasText(contentType)
				? MediaType.parseMediaType(contentType)
				: MediaType.APPLICATION_OCTET_STREAM;
		} catch (Exception e) {
			ct = MediaType.APPLICATION_OCTET_STREAM;
		}

		MultipartBodyBuilder mb = new MultipartBodyBuilder();
		mb.part("message", messageJson).contentType(MediaType.APPLICATION_JSON);

		mb.part("file", new ByteArrayResource(imageBytes) {
			@Override public String getFilename() { return resolvedFilename; }
		}).contentType(ct);

		RestClient client = RestClient.builder()
			.baseUrl(props.url())
			.defaultHeader("X-OCR-SECRET", props.secret())
			.build();

		return client.post()
			.contentType(MediaType.MULTIPART_FORM_DATA)
			.body(mb.build())
			.retrieve()
			.body(String.class);
	}
}
