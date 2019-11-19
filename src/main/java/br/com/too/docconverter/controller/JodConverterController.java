package br.com.too.docconverter.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.jodconverter.JodConverter;
import org.jodconverter.document.DefaultDocumentFormatRegistry;
import org.jodconverter.document.DocumentFamily;
import org.jodconverter.document.DocumentFormat;
import org.jodconverter.office.LocalOfficeManager;
import org.jodconverter.office.OfficeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping("/jod")
public class JodConverterController {
	@Value("${LO_PORTS:1}")
	private String numPorts;
	
	@PostConstruct
	private void init () throws OfficeException {
		int[] ports = new int[Integer.parseInt(numPorts)];
		for (int i=0 ; i<Integer.parseInt(numPorts) ; i++) {
			ports[i] = 2002+i;
		}
		LocalOfficeManager.builder().portNumbers(ports).install().build().start();
	}

	@PostMapping("/{from}/{to}")
	public ResponseEntity<StreamingResponseBody> convert(HttpServletRequest request,
			@PathVariable("from") String from, @PathVariable("to") String to) {

		try {
		
			InputStream inputStream = request.getInputStream();
	
			if (inputStream == null) {
				return ResponseEntity.badRequest().build();
			}
			
			StreamingResponseBody responseBody = new StreamingResponseBody() {
				public void writeTo(OutputStream outputStream) throws IOException {
					try {
						DocumentFormat toFormat = null;
						if ( "html".equals(to) ) {
							toFormat = DocumentFormat.builder()
					        .from(DefaultDocumentFormatRegistry.HTML)
					        .storeProperty(DocumentFamily.TEXT, "FilterOptions", "EmbedImages")
					        .build();
						} else {
							toFormat = DefaultDocumentFormatRegistry.getInstance().getFormatByExtension(to);
						}
						
						JodConverter.
							convert(inputStream).as(DefaultDocumentFormatRegistry.getInstance().getFormatByExtension(from))
							.to(outputStream).as(toFormat)
						.execute();
						
						
					} catch (OfficeException e) {
						throw new IOException(e);
					}
				}
			};
		
			StringBuilder attachment = new StringBuilder("attachment; filename=file."+to);
			
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, attachment.toString())
					.contentType(MediaType.APPLICATION_OCTET_STREAM).body(responseBody);
			
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
			

	}
	
}

