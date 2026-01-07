package cmc.naverocr.infrastructure.clova;

import java.util.List;

public record ClovaOcrRequest(
	String version,
	String requestId,
	long timestamp,
	List<Image> images
) {
	public record Image(String format, String name) {}
}
