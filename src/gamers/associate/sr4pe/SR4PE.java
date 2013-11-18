package gamers.associate.sr4pe;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.BufferedInputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.PDFText2HTML;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.encryption.StandardDecryptionMaterial;
import org.apache.pdfbox.io.RandomAccessBuffer;

public class SR4PE {
	private static OutputStreamWriter psout;
	private static String compConn = "Suggestions de Compétences de connaissances";
	private static String compAct = "Compétences actives";

	public static void main(String[] args) {
		System.out.println("SR4 pdf extractor");	
		if (args.length < 4) {
			System.out.println("usage: SR4PE [pdfName] [page start] [page end] [decrypt pdf]");
			System.out.println("pdfName=PDF file name without extension in pdf folder");
			return;
		}
		
		try {
			psout = new OutputStreamWriter(System.out, "windows-1252");
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
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
			InputStream stream = new FileInputStream(pdfInPath);
			PDFParser parser = new PDFParser(stream, new RandomAccessBuffer(), true);
			parser.parse();
			PDDocument doc = parser.getPDDocument();
			if (doc.isEncrypted()) {
				doc.openProtection(new StandardDecryptionMaterial("coolcool"));
			}

			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outPath), "UTF-8");

			PDFTextStripper textStripper = new PDFTextStripper();
			for (int i = start; i <= end; i++) {

				textStripper.setStartPage(i);
				textStripper.setEndPage(i);
				textStripper.writeText(doc, writer);
				writer.flush();
			}

			writer.close();

			/*FileWriter writerHtml = new FileWriter(outFileHtml);		
			PDFText2HTML html = new PDFText2HTML("utf8");
			html.setStartPage(1);
			html.setEndPage(4);
			html.writeText(doc, writerHtml);
			writerHtml.flush();
			writerHtml.close();*/

			doc.close();


			splitText(outPath);
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public static void splitText(String inPath) {
		File inFile = new File(inPath);
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inPath), "UTF-8"));
			int upperCount = 0;
			String line = reader.readLine();

			FileOutputStream fos = null;
			OutputStreamWriter out = null;
			boolean handleParagraph = false;
			boolean breakParagraph = false;
			boolean nextLineIsTitle = false;
			boolean isTitle = false;
			boolean tableLine = false;
			boolean compConnPass = false;
			boolean compActPass = false;
			while (line != null) { 
				isTitle = false;
				tableLine = false;
				boolean isUpper = true;
				boolean hasLetters = false;
				char[] lineChars = line.toCharArray();

				for (char c : lineChars) {
					if (Character.isLetter(c)) {
						hasLetters = true;
					}

					if ((Character.isLetter(c) && !Character.isUpperCase(c)) || c == '/') {
						isUpper = false;
						break;
					}
				}	
				
				if (!nextLineIsTitle && (!hasLetters || line.contains("\t"))) {
					isUpper = false;
					tableLine = true;
				}
		
				if (isUpper) {
					handleParagraph = true;
					breakParagraph = true;
					
					if (
						line.contains("KITS") ||
						line.contains("TOUCHE FINALE") ||
						line.contains("PACKS") ||
						line.contains("SR4A")						
							) {
						handleParagraph = false;
					}

					if (
						line == "" ||
						line.contains("[") ||
						line.contains("]") ||
						lineChars[0] == '(' ||
						lineChars[0] == '.'
							) {
						breakParagraph = false;
					}

					if (nextLineIsTitle) {
						breakParagraph = false;
						nextLineIsTitle = false;
						isTitle = true;
					}	

					if (handleParagraph && breakParagraph) {						
						// this is a new paragraph					
						if (out != null) {
							out.close();

						}
						
						compConnPass = false;
						compActPass = false;
						
						if (!line.contains(")")) {
							nextLineIsTitle = true;
							isTitle = false;
						} else {
							nextLineIsTitle = false;
						}


						upperCount++;
						
						String fileName = getFileName(line);
						if (fileName != "") {
							sout(fileName);
							fos = new FileOutputStream("extract\\" + fileName + ".txt");
							out = new OutputStreamWriter(fos, "UTF-8");
							isTitle = true;
						} else {
							handleParagraph = false;
							sout("Cannot find a correct file name for line " + line);
						}
					}
				} else {
					if (line.contains(compConn)) {
						compConnPass = true;
					}

					if (line.contains(compAct)) {
						compActPass = true;
					}
				}
				
				if (handleParagraph && out != null) {
					String toWrite = getToWrite(line, isTitle || nextLineIsTitle);
					if (toWrite != "") {
						if (line.contains(":") || tableLine) {
							out.write("\n" + toWrite + "\n");
						} else {
							if (isTitle) {
								out.write(toWrite + "\n\n");
							} else {
								char lastChar = lineChars[lineChars.length-1];
								if (
									lastChar == ']' || 
									lastChar == '.' || 
									lastChar == ')' ||
									Character.isDigit(lastChar)
									) {
									out.write(toWrite + "\n");
								} else {
									out.write(toWrite);
								}
							}
						}
					} else {
						out.write("\n");
					}
				}

				line = reader.readLine();
			}

			if (out != null) {
				out.close();
			}

			System.out.println(String.valueOf(upperCount));
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	private static void sout(String line) {
		char[] lineChars = line.toCharArray();
		try {
			for (char c : lineChars) {
				psout.write(c);
				psout.flush();
			}
	
			psout.write('\n');
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}	

	private static String getFileName(String line) {
		String fileNameTmp = line.replaceAll("[\\s+, *]", "");
		String fileName = "";
		char[] chars = fileNameTmp.toCharArray();
		for(char c : chars) {
			if (c == '(' || c == '[') break;
			fileName += c;
		}
		
		return fileName;
	}

	private static String getToWrite(String line, boolean isTitle) {
		String toWrite = "";

		if (!isTitle && (line.contains("["))) {
			char[] lineChars = line.toCharArray();
			for (char c : lineChars) {
				if (c == '[') break;
				toWrite += c;
			}
		} else {
			if (line.contains(compConn)) {
				toWrite = "Compétences de connaissances :";
			} else {
				toWrite = line;
			}
		}

		return toWrite;
	}
}

