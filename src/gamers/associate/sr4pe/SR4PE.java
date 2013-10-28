package gamers.associate.sr4pe;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.PDFText2HTML;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.encryption.StandardDecryptionMaterial;
import org.apache.pdfbox.io.RandomAccessBuffer;

public class SR4PE {
	public static void main(String[] args) {
		System.out.println("SR4 pdf extractor");	
		if (args.length < 4) {
			System.out.println("usage: SR4PE [pdfName] [page start] [page end] [decrypt pdf]");
			System.out.println("pdfName=PDF file name without extension in pdf folder");
			return;
		}
		
		String file = args[0];
		int start = Integer.parseInt(args[1]);
		int end = Integer.parseInt(args[2]);
		boolean decrypt = Boolean.parseBoolean(args[3]);
		String pdfInPath = "./temp/" + file + ".unencrypted.pdf";
		String outPath = "./out/" + file + ".txt";
		String outPathHtml = "./out/" + file + ".html";
		try {
			if (decrypt) {
				Process p = Runtime.getRuntime().exec("cmd /C tools\\gs.cmd " + file);
				p.waitFor();
			}

			File pdfFile = new File(pdfInPath);			
			File outFile = new File(outPath);
			File outFileHtml = new File(outPathHtml);
			InputStream stream = new BufferedInputStream(new FileInputStream(pdfFile));
			PDFParser parser = new PDFParser(stream, new RandomAccessBuffer(), true);
			parser.parse();
			PDDocument doc = parser.getPDDocument();
			if (doc.isEncrypted()) {
				doc.openProtection(new StandardDecryptionMaterial("coolcool"));
			}

			FileWriter writer = new FileWriter(outFile);		

			PDFTextStripper textStripper = new PDFTextStripper();
			textStripper.setStartPage(start);
			textStripper.setEndPage(end);
			textStripper.writeText(doc, writer);
			writer.flush();
			writer.close();
			
			/*FileWriter writerHtml = new FileWriter(outFileHtml);		
			PDFText2HTML html = new PDFText2HTML("utf8");
			html.setStartPage(1);
			html.setEndPage(4);
			html.writeText(doc, writerHtml);
			writerHtml.flush();
			writerHtml.close();*/

			doc.close();
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			System.out.println(ex.getStackTrace());
		}


	}
}

