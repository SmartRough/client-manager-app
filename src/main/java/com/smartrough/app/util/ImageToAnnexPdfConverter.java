package com.smartrough.app.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class ImageToAnnexPdfConverter {

	public static File createAnnexPdf(File originalImageFile, String annexTitle) throws IOException {
		BufferedImage inputImage = ImageIO.read(originalImageFile);

		// Redimensionar si es muy grande
		int maxWidth = 1200;
		int newWidth = inputImage.getWidth();
		int newHeight = inputImage.getHeight();

		if (newWidth > maxWidth) {
			float scale = (float) maxWidth / newWidth;
			newWidth = maxWidth;
			newHeight = Math.round(inputImage.getHeight() * scale);
		}

		BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
		resizedImage.getGraphics().drawImage(inputImage, 0, 0, newWidth, newHeight, null);

		// Comprimir a JPEG con 60% calidad
		File compressedFile = File.createTempFile("compressed_", ".jpg");
		compressedFile.deleteOnExit();

		ImageOutputStream ios = ImageIO.createImageOutputStream(compressedFile);
		ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
		writer.setOutput(ios);

		ImageWriteParam param = writer.getDefaultWriteParam();
		param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		param.setCompressionQuality(0.6f);
		writer.write(null, new IIOImage(resizedImage, null, null), param);
		writer.dispose();
		ios.close();

		// Generar el PDF desde la imagen comprimida
		return generatePdfFromImageWithTitle(compressedFile, annexTitle);
	}

	private static File generatePdfFromImageWithTitle(File imageFile, String annexTitle) throws IOException {
		File tempPdf = File.createTempFile("annex_", ".pdf");
		tempPdf.deleteOnExit();

		try (PDDocument doc = new PDDocument()) {
			PDPage page = new PDPage(PDRectangle.LETTER);
			doc.addPage(page);

			PDImageXObject pdImage = PDImageXObject.createFromFileByContent(imageFile, doc);

			float pageWidth = PDRectangle.LETTER.getWidth();
			float pageHeight = PDRectangle.LETTER.getHeight();

			float maxWidth = pageWidth - 80;
			float maxHeight = pageHeight - 120;

			float scale = Math.min(maxWidth / pdImage.getWidth(), maxHeight / pdImage.getHeight());

			float width = pdImage.getWidth() * scale;
			float height = pdImage.getHeight() * scale;

			float x = (pageWidth - width) / 2;
			float titleY = pageHeight - 50;
			float imageY = titleY - height - 20;

			if (imageY < 20)
				imageY = 20;

			try (PDPageContentStream stream = new PDPageContentStream(doc, page)) {
				stream.beginText();
				stream.setFont(PDType1Font.HELVETICA_BOLD, 14);
				stream.newLineAtOffset(60, titleY);
				stream.showText(annexTitle);
				stream.endText();

				stream.drawImage(pdImage, x, imageY, width, height);
			}

			doc.save(tempPdf);
		}

		return tempPdf;
	}

}
