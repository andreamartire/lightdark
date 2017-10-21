#!/usr/bin/python3.5

import json
import urllib.request
import re
import sys

#'https://librivox.org/search/get_results?primary_key=4&search_category=language&search_page=1&search_project=alpha&project_type=either'

print ('Start')

with open('list_lv.html', 'r', encoding="utf8") as myfile:
    sourceMainPage=myfile.read().replace('\n', '')

#print (sourceMainPage)

rows = str(sourceMainPage).split("book-cover")#, sourceMainPage.count(str))

#prepare regex to extract audiobook contentId
#<h3><a href="http://librivox.org/la-vita-nuova-by-dante-alighieri/">La Vita Nuova</a></h3>
regContent = re.compile(".*?<h3><a href=\"(.*?)\">(.*?)</a>")
#<p class="book-author"> <a href="https://librivox.org/author/1189">Dante ALIGHIERI (1265 - 1321)</a> </p>
regexAuthor = re.compile(".*?<p class=\"book-author\"> <a href=\".*?\">(.*?) (\(.*?\))?</a>")

audiobooks = []
setUrl = {}

for i in range(len(rows)):
	#print ("ROW: " + rows[i] + "\n");
	
	#<a href=\"https://www.liberliber.it/online/autori/autori-j/jerome-k-jerome/tre-uomini-in-una-barca-audiolibro/\">Tre uomini in una barca [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-j/jerome-k-jerome/\">Jerome K. Jerome</a><br><span class=\"ll_libro_sottotitolo\">(per tacer del cane)</span></li>
	if (regContent.match(rows[i])):
		link = str(regContent.match(rows[i]).group(1));
		#print("--- Link ---"+link+"---")
		title = str(regContent.match(rows[i]).group(2))
		#print("--- Title ---"+title+"---")
		author = ""
		if(regexAuthor.match(rows[i])):
			author = str(regexAuthor.match(rows[i]).group(1))
		#print("--- Author ---"+author+"---")
		
		if(link not in setUrl):
			print (link)
			setUrl[link] = 1
		
			book = {}
			bookContents = []
			
			try:
				wp = urllib.request.urlopen(link)
				book_str = str(wp.read())
				
				#<td><a href="http://www.archive.org/download/novelle_anno_vol2_1109_librivox/novelle02_00_pirandello.mp3" class="chapter-name">Avvertenza</a></td>
				chaptLinkReg = re.compile(".*<td><a href=\"(.*?\.mp3)\" class=\"chapter-name\">(.*?)</a></td>")

				chapterId = 0
				bookRows = book_str.split("<tr>")
				for i in range(len(bookRows)):
					#print ("BOOK ROW: " + bookRows[i] + "\n");
					
					if (chaptLinkReg.match(bookRows[i])):
						url = chaptLinkReg.match(bookRows[i]).group(1)
						chapTitle = chaptLinkReg.match(bookRows[i]).group(2)
						#print("--- Url ---" + url + "---")
						#print("--- Title ---" + chapTitle + "---")
						
						chapter = {}
						chapter['id'] = chapterId
						chapter['title'] = chapTitle
						chapter['desc'] = ''
						chapter['url'] = url
						chapter['format'] = 'mp3'
						
						chapterId = chapterId + 1
						
						#print ('Chapter: ' + str(chapter))

						bookContents.append(chapter)
						
				book['contents'] = bookContents
				
				book['metadata'] = {		
					'image': '',
					'other_images' : {
						'image_300' : '',
						'image_433' : '',
					},
					'provider' : {
						'name' : 'LibriVox',
						'site' : 'https://librivox.org',
						'image' : ''
					},
					'title': title,
					'author': author
				}
				
				#print ('Book: ' + str(book))
				
				audiobooks.append(book)
			except:
				print ('----------------------- Exception ---------------------')
				print (sys.exc_info()[0])
		else:
			print ("ALREADY ADDED " + link)
			
	data = {}
	data['version'] = 0
	data['config'] = {}
	data['audiobooks'] = audiobooks

	out_file = open("config_lv.json","w")
	out_file.write(json.dumps(data, sort_keys=True, indent=4))
	out_file.close()

	#time.sleep(10)

print ("Num links: " + len(setLink))

json_data = json.dumps(data)

#print(json.dumps(data, sort_keys=True, indent=4))






