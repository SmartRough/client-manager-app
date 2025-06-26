package com.smartrough.app.util;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class NumberFieldHelper {

	/**
	 * Aplica restricción para ingresar decimales con coma como separador de miles.
	 * Ej: 1,234.56
	 */
	public static void applyDecimalFormat(TextField field) {
		field.setTextFormatter(new TextFormatter<>(change -> {
			String newText = change.getControlNewText();
			if (newText.matches("[\\d,]*\\.?\\d{0,2}")) {
				return change;
			}
			return null;
		}));
	}

	/**
	 * Aplica restricción para solo números enteros positivos.
	 */
	public static void applyIntegerFormat(TextField field) {
		field.setTextFormatter(new TextFormatter<>(change -> {
			String newText = change.getControlNewText();
			return newText.matches("\\d*") ? change : null;
		}));
	}

	/**
	 * Aplica restricción para porcentajes con hasta 2 decimales (no permite comas).
	 * Ej: 12.50
	 */
	public static void applyPercentageFormat(TextField field) {
		field.setTextFormatter(new TextFormatter<>(change -> {
			String newText = change.getControlNewText();
			return newText.matches("^\\d*(\\.\\d{0,2})?$") ? change : null;
		}));
	}

	/**
	 * Convierte un texto formateado (con o sin coma) a BigDecimal.
	 */
	public static BigDecimal parse(String value) {
		if (value == null || value.isBlank())
			return BigDecimal.ZERO;
		value = value.replace(",", "");
		try {
			return new BigDecimal(value);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid number format: " + value);
		}
	}

	/**
	 * Formatea un BigDecimal con separador de miles y dos decimales. Ej: 1234.5 ->
	 * 1,234.50
	 */
	public static String format(BigDecimal value) {
		if (value == null)
			return "";
		NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
		formatter.setMinimumFractionDigits(2);
		formatter.setMaximumFractionDigits(2);
		return formatter.format(value);
	}

	public static void restrictToIntegerInput(TextField field, int maxLength) {
		field.setTextFormatter(new TextFormatter<>(change -> {
			String newText = change.getControlNewText();
			return newText.matches("\\d{0," + maxLength + "}") ? change : null;
		}));
	}

	public static String format(double value) {
		return format(BigDecimal.valueOf(value));
	}

}
