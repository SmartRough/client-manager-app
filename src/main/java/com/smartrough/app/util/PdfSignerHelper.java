package com.smartrough.app.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PdfSignerHelper {

	private static final Map<String, String> MARKERS = Map.of("contractorSignature", "[[SIGN_CONTRACTOR]]",
			"owner1Signature", "[[SIGN_OWNER1]]", "owner2Signature", "[[SIGN_OWNER2]]", "initialsClause1",
			"[[INITIALS_CLAUSE_1]]", "initialsClause2", "[[INITIALS_CLAUSE_2]]", "initialsClause3",
			"[[INITIALS_CLAUSE_3]]", "initialsClause4", "[[INITIALS_CLAUSE_4]]");

	public static void addSignatureFields(String inputPath, String outputPath) throws IOException {
		try (PDDocument document = PDDocument.load(new File(inputPath))) {
			PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
			if (acroForm == null) {
				acroForm = new PDAcroForm(document);
				document.getDocumentCatalog().setAcroForm(acroForm);
			}
			acroForm.setNeedAppearances(true);

			Map<String, float[]> coordinates = findMarkerCoordinates(document);

			for (Map.Entry<String, float[]> entry : coordinates.entrySet()) {
				String fieldName = entry.getKey();
				float[] coords = entry.getValue();
				if (coords != null) {
					addTextField(document, acroForm, fieldName, 0, coords[0], coords[1], 160, 50);
				}
			}

			document.save(outputPath);
		}
	}

	private static void addTextField(PDDocument doc, PDAcroForm form, String name, int pageIndex, float x, float y,
			float width, float height) throws IOException {
		PDPage page = doc.getPage(pageIndex);
		PDTextField textBox = new PDTextField(form);
		textBox.setPartialName(name);

		PDAnnotationWidget widget = new PDAnnotationWidget();
		widget.setRectangle(new PDRectangle(x, y, width, height));
		widget.setPage(page);

		textBox.getWidgets().add(widget);
		page.getAnnotations().add(widget);
		form.getFields().add(textBox);
	}

	private static Map<String, float[]> findMarkerCoordinates(PDDocument doc) throws IOException {
		Map<String, float[]> coordinates = new HashMap<>();

		PDFTextStripper stripper = new PDFTextStripper() {
			@Override
			protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
				for (int i = 0; i < textPositions.size(); i++) {
					TextPosition tp = textPositions.get(i);
					for (Map.Entry<String, String> entry : MARKERS.entrySet()) {
						String fieldName = entry.getKey();
						String marker = entry.getValue();
						if (text.contains(marker)) {
							coordinates.put(fieldName, new float[] { tp.getXDirAdj(), tp.getYDirAdj() });
						}
					}
				}
			}
		};

		stripper.setSortByPosition(true);
		stripper.setStartPage(1);
		stripper.setEndPage(1);
		stripper.getText(doc); // Trigger parsing

		return coordinates;
	}
}
