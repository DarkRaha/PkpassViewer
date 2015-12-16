package com.dr.util.libs;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import android.graphics.Bitmap;

/**
 * In apple off. doc only BarcodeFormat.PDF_417, BarcodeFormat.QR_CODE and
 * BarcodeFormat.AZTEC supported.
 * 
 * @author Verma Rahul
 * 
 */
public class BarcodeEncoder {
	private static final int WHITE = 0xFFFFFFFF;
	private static final int BLACK = 0xFF000000;

	public BarcodeEncoder() {

	}

	public Bitmap getBitmap(String toEncode, String format, int width,
			int height) throws WriterException {

		BarcodeFormat zxingFormat = null;

		// in doc apple support only 3 types.
		switch (format) {
		case "PKBarcodeFormatPDF417":
			zxingFormat = BarcodeFormat.PDF_417;
			break;
		case "PKBarcodeFormatQR":
			zxingFormat = BarcodeFormat.QR_CODE;
			break;

		case "PKBarcodeFormatAztec":
			zxingFormat = BarcodeFormat.AZTEC;
			break;

		}

		com.google.zxing.Writer writer = new MultiFormatWriter();
		BitMatrix result = writer.encode(toEncode, zxingFormat, width, height);
		width = result.getWidth();
		height = result.getHeight();

		int[] pixels = new int[width * height];

		for (int y = 0; y < height; y++) {
			int offset = y * width;
			for (int x = 0; x < width; x++) {
				pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
			}
		}

		Bitmap barcode = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		barcode.setPixels(pixels, 0, width, 0, 0, width, height);
		return barcode;
	}
}
