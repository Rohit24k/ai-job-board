package com.rohit.ai_job_board.util;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PdfUtil {

    public static String extractText(byte[] pdf)
            throws IOException {

        try(PDDocument document =
                    Loader.loadPDF(pdf)){

            PDFTextStripper stripper =
                    new PDFTextStripper();

            return stripper.getText(document);

        }

    }

}