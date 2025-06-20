package com.smartrough.app.util;

import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PdfMerger {

	public static void mergePdfFiles(File basePdf, List<File> attachments, File outputFile) throws IOException {
		PDFMergerUtility merger = new PDFMergerUtility();
		merger.setDestinationFileName(outputFile.getAbsolutePath());

		// 1. Agregar el contrato principal
		merger.addSource(basePdf);

		// 2. Agregar los archivos PDF anexos directamente (sin procesarlos como imagen)
		for (File attachment : attachments) {
			merger.addSource(attachment);
		}

		merger.mergeDocuments(null);
	}
}
