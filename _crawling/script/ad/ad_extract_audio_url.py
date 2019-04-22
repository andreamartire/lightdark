#!/usr/bin/python3.5

import re
import requests
import json
import time
import traceback

headers = {
    'User-Agent' : 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36'
}

cookies = dict(PHPSESSID="e3v0vq2e97u0oiukfvp21jqg83")

print ('Start')

#http://www.audioteca-adov.it/elenco_libri_autore/

sourceMainPage = ""
with open('book_test.txt', 'r') as myfile:
	sourceMainPage = myfile.read().replace('\n', '')

rows = str(sourceMainPage).split("<li>")

#<div align="left"><a href="/elenco_libri_autore/affinati_eraldo">
regContent = re.compile(".*?<a href=\"(/elenco_libri_autore/(.*?))\">")

audiobooks = []

#for each author
for i in range(len(rows)):
	print ("INDEX: " + str(i) + " - ROW: " + rows[i] + "\n");
	
	#<a href=\"https://www.liberliber.it/online/autori/autori-j/jerome-k-jerome/tre-uomini-in-una-barca-audiolibro/\">Tre uomini in una barca [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-j/jerome-k-jerome/\">Jerome K. Jerome</a><br><span class=\"ll_libro_sottotitolo\">(per tacer del cane)</span></li>
	if (regContent.match(rows[i])):
		link = "http://www.audioteca-adov.it/download_files/"+str(regContent.match(rows[i]).group(2))+"/books";
		print("--- Link --- "+link+" ---")
		author = str(regContent.match(rows[i]).group(2))
		print("--- Author ---"+author+"---")
				
		print(link)

		#wp = urllib.request.urlopen(link)
		author_str = u''.join(requests.get(link, headers = headers, cookies = cookies).text).encode('utf-8').strip()
		#author_str = str(wp.read())

		singleBook = str(author_str).split("<li style='background-color:#1165ad;")
		
		#for each book
		for j in range(len(singleBook)):
			print ("singleBook: " + singleBook[j] + "\n");
			
			book = {}
			bookContents = []

			#<span class="toggle" >BARNUM_DUE_ALTRE_CRONACHE_DAL_GRANDE_SHOW</span>
			titleReg = re.compile(".*<span class=\"toggle\" >(.*?)</span>")

			title = ""
			if(titleReg.match(singleBook[j])):
				title = titleReg.match(singleBook[j]).group(1)
			
			#<a href="/download_file.php?file=LIBRI/LETTERA B/BARICCO ALESSANDRO/BARNUM_CORNACHE_DAL_GRANDE_SHOW/01_PARTE.mp3" style='color:#ffffff;'
			chaptLinkReg = re.compile(".*<a href=\"(/download_file.php\?file=LIBRI/.*/(.*?)\.mp3)\" style")

			#/download_file.php?file=

			chapterId = 0
			bookRows = singleBook[j].split("\n")
			for k in range(len(bookRows)):
				print ("BOOK ROW: " + bookRows[k] + "\n");
				#<a href="/download_file.php?file=LIBRI/LETTERA B/BARICCO ALESSANDRO/BARNUM_CORNACHE_DAL_GRANDE_SHOW/01_PARTE.mp3" style='color:#ffffff;'
				if (chaptLinkReg.match(bookRows[k])):
					url = chaptLinkReg.match(bookRows[k]).group(1)
					chapTitle = chaptLinkReg.match(bookRows[k]).group(2)
					print("--- Url ---" + url + "---")
					print("--- Title ---" + chapTitle + "---")

					chapter = {}
					chapter['id'] = chapterId
					chapter['title'] = chapTitle.replace('_', ' ').title()
					chapter['desc'] = ''
					chapter['url'] = 'http://www.audioteca-adov.it' + url
					chapter['format'] = 'mp3'

					chapterId = chapterId + 1

					print ('Chapter: ' + str(chapter))

					bookContents.append(chapter)
	
			if(len(bookContents) > 0):
				book['contents'] = bookContents
	
				book['metadata'] = {		
					'image': '',
					'other_images' : {
						'image_300' : '',
						'image_433' : '',
					},
					'provider' : {
						'name' : 'Adov',
						'site' : 'http://www.audioteca-adov.it',
						'image' : ''
					},
					'hidden': True,
					'title': title.replace('_', ' ').title(),
					'author': author.replace('_', ' ').title()
				}

				print ('Book: ' + str(book))

				audiobooks.append(book)

				data = {}
				data['version'] = 0
				data['config'] = {}
				data['audiobooks'] = audiobooks

				out_file = open("config_ad.json","w")
				out_file.write(json.dumps(data, sort_keys=True, indent=4))
				out_file.close()

		print ("INDEX: " + str(i) + "/" + str(len(rows)));
		time.sleep(3)

#json_data = json.dumps(data)

#print(json.dumps(data, sort_keys=True, indent=4))






