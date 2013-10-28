package gamers.associate.sr4pe;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.encryption.StandardDecryptionMaterial;
import org.apache.pdfbox.io.RandomAccessBuffer;

public class SR4PE {
	public static void main(String[] args) {
		System.out.println("SR4 pdf extractor");	
		if (args.length < 1) {
			System.out.println("usage: SR4PE [pdfName]");
			System.out.println("pdfName=PDF file name without extension in pdf folder");
			return;
		}

		String pdfInPath = "./pdf/" + args[0] + ".unencrypted.pdf";
		String outPath = "./out/" + args[0] + ".txt";
		try {
			Process p = Runtime.getRuntime().exec("cmd /C tools\\gs.cmd " + args[0]);
			p.waitFor();

			File pdfFile = new File(pdfInPath);			
			File outFile = new File(outPath);
			InputStream stream = new BufferedInputStream(new FileInputStream(pdfFile));
			PDFParser parser = new PDFParser(stream, new RandomAccessBuffer(), true);
			parser.parse();
			PDDocument doc = parser.getPDDocument();
			if (doc.isEncrypted()) {
				doc.openProtection(new StandardDecryptionMaterial("azerty123"));
			}

			FileWriter writer = new FileWriter(outFile);		

			PDFTextStripper textStripper = new PDFTextStripper();
			textStripper.setStartPage(14);
			textStripper.setEndPage(14);
			textStripper.writeText(doc, writer);
			writer.flush();
			writer.close();
			doc.close();
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			System.out.println(ex.getStackTrace());
		}


	}
}

