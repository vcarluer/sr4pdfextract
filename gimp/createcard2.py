#!/usr/bin/env python

import math
import os
import sys
from gimpfu import *

def python_createcard2(templateXcf="C:\\java\\projects\\Sr4PdfExtract\\gimp\\backcard.xcf", dirIn="C:\\java\\projects\\Sr4PdfExtract\\extract", dirOut="C:\\java\\projects\\Sr4PdfExtract\\images", countMax = 999):
	
	# Open text file and get texts
	count = 0
	for files in os.listdir(dirIn):
		f = open(os.path.join(dirIn, files))
		data = f.read()
		f.close()
		fileMain = files[:files.find(".")]
		fileMain = fileMain.decode("windows-1252")
		createImages(templateXcf, data, dirOut, fileMain)
		count += 1
		if count > countMax:
			break

def createImages(templateXcf, data, dirOut, fileMain):
	lines = data.split("\n")
	titleFull = lines[0]
	line = 1
	if titleFull.find("(") > -1:
		title = titleFull[:titleFull.find("(")-1]
		subText = titleFull[titleFull.find("(")+1:titleFull.find(")")]
	else:
		title = titleFull
		cost = ""

		while cost.find("(") < 0 and line <= len(lines):			
			cost = lines[line - 1]
			line += 1			

		subText = cost[cost.find("(")+1:cost.find(")")]

	line += 1
	if line <= len(lines):
		text = lines[line - 1].strip()
		while text == "" and line <= len(lines):
			text = lines[line - 1].strip()
			line += 1

	body = "\n".join(lines[line-2:])	

	# Open template
	backgroundname = templateXcf
	imgBkg = pdb.gimp_xcf_load(0, backgroundname, backgroundname)

	# Replace texts
	layerTitle = pdb.gimp_image_get_layer_by_name(imgBkg, "textTitle");
	pdb.gimp_text_layer_set_text(layerTitle, title)
	layerBody = pdb.gimp_image_get_layer_by_name(imgBkg, "textBody");
	pdb.gimp_text_layer_set_text(layerBody, body)
	layerSub = pdb.gimp_image_get_layer_by_name(imgBkg, "textSub");
	pdb.gimp_text_layer_set_text(layerSub, subText)
	
	# Save as XCF
	saveTo = os.path.join(dirOut, fileMain + ".xcf")
	pdb.gimp_xcf_save(0, imgBkg, layerBody, saveTo, saveTo)
	
	# Save as PNG
	layer_export = pdb.gimp_image_merge_visible_layers(imgBkg, CLIP_TO_IMAGE)
	pngname = os.path.join(dirOut, fileMain + ".png")
	pdb.file_png_save(imgBkg, layer_export, pngname, pngname, 0, 0, 0, 0, 0, 0, 0)

	

register(
        "python_fu_createcard2",
        "Create a card from text",
        "Create a card from text",
	"Vincent Carluer",
	"Vincent Carluer",
        "2013",
        "<Toolbox>/MyScripts/_CreateCard2...",
        "",
        [
		(PF_STRING, "templateXcf", "templateXcf", "C:\\java\\projects\\Sr4PdfExtract\\gimp\\backcard.xcf"),
		(PF_STRING, "dirIn", "dirIn", "C:\\java\\projects\\Sr4PdfExtract\\extract"),
		(PF_STRING, "dirOut", "dirOut", "C:\\java\\projects\\Sr4PdfExtract\\images"),
		(PF_INT, "countMax", "countMax", 999)
        ],
        [],
        python_createcard2)

main()
