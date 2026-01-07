package cmc.naverocr.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "clova.ocr")
public record ClovaOcrProperties(
	String url,
	String secret
) {}
