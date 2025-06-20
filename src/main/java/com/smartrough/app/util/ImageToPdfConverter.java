package com.smartrough.app.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.io.IOException;

public class ImageToPdfConverter {

	public static PDPage createPageFromImage(File imageFile, PDDocument targetDoc) throws IOException {
		PDPage page = new PDPage(PDRectangle.LETTER);
		targetDoc.addPage(page);

		PDImageXObject pdImage = PDImageXObject.createFromFileByContent(imageFile, targetDoc);

		float scale = Math.min(PDRectangle.LETTER.getWidth() / pdImage.getWidth(),
				PDRectangle.LETTER.getHeight() / pdImage.getHeight());

		float width = pdImage.getWidth() * scale;
		float height = pdImage.getHeight() * scale;
		float x = (PDRectangle.LETTER.getWidth() - width) / 2;
		float y = (PDRectangle.LETTER.getHeight() - height) / 2;

		try (PDPageContentStream contentStream = new PDPageContentStream(targetDoc, page)) {
			contentStream.drawImage(pdImage, x, y, width, height);
		}

		return page;
	}
}
