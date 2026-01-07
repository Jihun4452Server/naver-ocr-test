package cmc.naverocr.api;

import cmc.naverocr.application.OcrService;
import cmc.naverocr.model.OcrResult;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/poc")
public class OcrTestController {

	private final OcrService ocrService;

	@GetMapping("/ocr/static")
	public OcrResult ocrStatic(
		@RequestParam(defaultValue = "test.jpg") String fileName
	) throws IOException {
		Resource resource = new org.springframework.core.io.ClassPathResource("static/image/" + fileName);
		byte[] bytes = StreamUtils.copyToByteArray(resource.getInputStream());

		String format = ext(fileName, "jpg");
		String contentType = format.equals("png") ? "image/png" : "image/jpeg";

		return ocrService.ocr(bytes, fileName, format, contentType);
	}

	@PostMapping(value = "/ocr", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public OcrResult ocr(@RequestPart("image") MultipartFile image,
		@RequestParam(defaultValue = "jpg") String format) throws IOException {
		return ocrService.ocr(image.getBytes(), image.getOriginalFilename(), format, image.getContentType());
	}


	private static String ext(String filename, String defaultExt) {
		if (filename == null) return defaultExt;
		int idx = filename.lastIndexOf('.');
		if (idx < 0 || idx == filename.length() - 1) return defaultExt;
		return filename.substring(idx + 1).toLowerCase();
	}
}

