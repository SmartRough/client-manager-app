package com.smartrough.app.util;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

public class PdfMerger {

	public static void mergeWithAttachments(File basePdf, List<File> pdfAttachments, File outputFile)
			throws IOException {
		PDFMergerUtility merger = new PDFMergerUtility();
		merger.setDestinationFileName(outputFile.getAbsolutePath());

		// 1. Contrato principal
		merger.addSource(basePdf);

		// 2. Anexos como imágenes
		char annexLetter = 'A';
		for (File attachment : pdfAttachments) {
			String annexTitle = "Annex " + annexLetter + ": " + attachment.getName();
			File titlePage = ContractExporter.createAnnexTitlePage(annexTitle);
			merger.addSource(titlePage);

			// Convertir cada página del PDF en imagen y generar un PDF con esas imágenes
			File imagePdf = convertPdfToImagePdf(attachment);
			merger.addSource(imagePdf);

			annexLetter++;
		}

		merger.mergeDocuments(null);
	}

	private static File convertPdfToImagePdf(File pdfFile) throws IOException {
		File tempPdf = File.createTempFile("annex_image_", ".pdf");
		tempPdf.deleteOnExit();

		try (PDDocument inputDoc = PDDocument.load(pdfFile); PDDocument outputDoc = new PDDocument()) {

			PDFRenderer renderer = new PDFRenderer(inputDoc);
			for (int i = 0; i < inputDoc.getNumberOfPages(); i++) {
				BufferedImage image = renderer.renderImageWithDPI(i, 200); // buena calidad
				File imageFile = File.createTempFile("page_" + i, ".png");
				imageFile.deleteOnExit();
				ImageIO.write(image, "png", imageFile);

				// Convertir imagen a página PDF
				ImageToPdfConverter.createPageFromImage(imageFile, outputDoc);
			}

			outputDoc.save(tempPdf);
		}

		return tempPdf;
	}
}
