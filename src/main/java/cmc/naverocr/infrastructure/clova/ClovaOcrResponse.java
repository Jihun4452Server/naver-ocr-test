package cmc.naverocr.infrastructure.clova;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClovaOcrResponse(
	List<ImageResult> images
) {
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record ImageResult(
		List<Field> fields
	) {}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Field(
		String inferText,
		Boolean lineBreak
	) {}
}
