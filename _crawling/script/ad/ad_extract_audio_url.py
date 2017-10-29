#!/usr/bin/python3.5

import re
import requests
import json
import time
import traceback

headers = {
    'User-Agent' : 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36'
}

cookies = dict(PHPSESSID="nk0frnjcjcl4e9g7t7ksnfjkl3")

print ('Start')

#http://www.audioteca-adov.it/elenco_libri_autore/

sourceMainPage = ""
with open('book_test.txt', 'r') as myfile:
		sourceMainPage = myfile.read().replace('\n', '')

rows = str(sourceMainPage).split("<li>")

#<div align="left"><a href="/elenco_libri_autore/affinati_eraldo">
regContent = re.compile(".*?<a href=\"(/elenco_libri_autore/(.*?))\">")

audiobooks = []
listUrl = []

for i in range(len(rows)):
	print ("ROW: " + rows[i] + "\n");
	
	#<a href=\"https://www.liberliber.it/online/autori/autori-j/jerome-k-jerome/tre-uomini-in-una-barca-audiolibro/\">Tre uomini in una barca [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-j/jerome-k-jerome/\">Jerome K. Jerome</a><br><span class=\"ll_libro_sottotitolo\">(per tacer del cane)</span></li>
	if (regContent.match(rows[i])):
		link = "http://www.audioteca-adov.it/download_files/"+str(regContent.match(rows[i]).group(2))+"/books";
		print("--- Link --- "+link+" ---")
		author = str(regContent.match(rows[i]).group(2))
		print("--- Author ---"+author+"---")
		listUrl.append(link)
		
		book = {}
		bookContents = []
		
		print(link)

		#wp = urllib.request.urlopen(link)
		author_str = str(requests.get(link, headers = headers, cookies = cookies).text)
		#author_str = str(wp.read())

		#print (author_str)
		
		booksHtml = str(author_str).split("<ul>")

		for i in range(len(booksHtml)):
			print ("bookHtml: " + booksHtml[i] + "\n");

			singleBook = str(booksHtml[i]).split("<li style='background-color:#1165ad;")

			for i in range(len(singleBook)):
				#print ("singleBook: " + singleBook[i] + "\n");

				#<span class="toggle" >BARNUM_DUE_ALTRE_CRONACHE_DAL_GRANDE_SHOW</span>
				titleReg = re.compile(".*<span class=\"toggle\" >(.*?)</span>")

				title = ""
				if(titleReg.match(singleBook[i])):
					title = titleReg.match(singleBook[i]).group(1)
				
				#<a href="/download_file.php?file=LIBRI/LETTERA B/BARICCO ALESSANDRO/BARNUM_CORNACHE_DAL_GRANDE_SHOW/01_PARTE.mp3" style='color:#ffffff;'
				chaptLinkReg = re.compile(".*<a href=\"(/download_file.php\?file=LIBRI/.*/(.*?)\.mp3)\" style")

				#/download_file.php?file=

				chapterId = 0
				bookRows = singleBook[i].split("\n")
				for i in range(len(bookRows)):
					print ("BOOK ROW: " + bookRows[i] + "\n");
					#<a href="/download_file.php?file=LIBRI/LETTERA B/BARICCO ALESSANDRO/BARNUM_CORNACHE_DAL_GRANDE_SHOW/01_PARTE.mp3" style='color:#ffffff;'
					if (chaptLinkReg.match(bookRows[i])):
						url = chaptLinkReg.match(bookRows[i]).group(1)
						chapTitle = chaptLinkReg.match(bookRows[i]).group(2)
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

		time.sleep(10)

#json_data = json.dumps(data)

#print(json.dumps(data, sort_keys=True, indent=4))






