package com.scar.jobflow_backend.ai;

import com.scar.jobflow_backend.common.exception.BadRequestException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class PdfTextExtractor {

    public String extractText(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }
        if (!"application/pdf".equals(file.getContentType())) {
            throw new BadRequestException("File must be a PDF");
        }

        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            if (text == null || text.isBlank()) {
                throw new BadRequestException("Could not extract any text from this PDF — it may be a scanned image without a text layer");
            }

            return text;
        } catch (IOException e) {
            throw new BadRequestException("Failed to read PDF file: " + e.getMessage());
        }
    }
}