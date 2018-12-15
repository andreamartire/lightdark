#!/usr/bin/python3.5

import urllib.request
import re
import requests
import json
import time
import traceback
from bs4 import BeautifulSoup

print ('Start')

listContentId = []
listContentId.append("ContentSet-e896c91d-513c-4d87-ae07-f4fb8fefca16")#limonov
listContentId.append("ContentSet-d65cda92-04fc-4fbc-93e2-75eed0a0593d")#moby dick
listContentId.append("ContentSet-770095cb-9699-41f6-a13c-0e7ecf5d718f")#lolita
listContentId.append("ContentSet-e698c9de-908a-461f-8bbd-400bd5240709")#avventure gordon pym
listContentId.append("ContentSet-fc566162-e2e5-401f-92f1-6f73d5d6179b")#iguana
listContentId.append("ContentSet-026c5734-a2c6-48a8-b6d8-db48639b93aa")#onda incrociatore
listContentId.append("ContentSet-394444eb-f2bf-4e2c-a351-17f7c490a582")#la ricorrenza
listContentId.append("ContentSet-bfc607f9-9d8f-475f-a686-f874567b7e1f")#jules e jim
listContentId.append("ContentSet-c4a6c9bc-cc23-4d0e-8c44-36d1efca4739")#pranzo babette
listContentId.append("ContentSet-9ded9377-fe66-4bbe-a78a-14bbe06b3b82")#il commesso
listContentId.append("ContentSet-a45ddd5f-0eb5-4d58-a300-a9b9d3a8c1ac")#grande fiume 
listContentId.append("ContentSet-9d3f634e-98e3-44b6-810a-cdfeafcb584e")#grisha
listContentId.append("ContentSet-c1e042d2-b3ad-4fec-aca2-55b373a385cc")#cuore tenebra
listContentId.append("ContentSet-209a70ae-d746-4ed8-9912-9025877f8f16")#donne usciamo
listContentId.append("ContentSet-af67620c-b4f6-4a6f-8477-5c93b6e3c80d")#diaro hillesum
listContentId.append("ContentSet-5cbc412f-dfce-43d3-9a24-400313cb4557")#dieci indiani
listContentId.append("ContentSet-297ea240-5805-4ab1-99ce-fe70ef849ec9")#distanza
listContentId.append("ContentSet-0a000996-16e0-41a9-8de2-96e61868d6e2")#chissà dov'ero
listContentId.append("ContentSet-af85e3eb-a64a-4ea8-979d-50eebf019f6e")#ciàula
listContentId.append("ContentSet-7bac0d85-636d-4538-a06f-aae0bed381c0")#cime tempestose
listContentId.append("ContentSet-8e494ca4-c62f-4290-8e91-101a962e811c")#collettori
listContentId.append("ContentSet-c2d94643-772c-4609-80e5-87cacf8dac13")#conservazione
listContentId.append("ContentSet-e63b9908-32a9-4cdc-beb1-401df52971c9")#bartleby
listContentId.append("ContentSet-df1245c8-1bbf-4347-b2c0-3b9308e7f0e6")#padri e figli
listContentId.append("ContentSet-4fd52647-8a7a-430e-93c3-789e78e3a06b")#sotto il vulcano
listContentId.append("ContentSet-5e178925-085d-4cc2-b95e-597f997e34cc")#studio rosso

#TODO Le linee d'ombra

#Example http://www.radio3.rai.it/dl/portaleRadio/Programmi/Page-9fe19bce-1c27-4b63-b41e-2d7581d21374.html?set=ContentSet-b63181f3-deec-4cb7-84db-96169b0725e7&type=A

audiobooks = []
bookId = 0

print ("=== Content List ===")
for i in range(len(listContentId)):
	
	print (listContentId[i])

	book = {}
	book['id'] = bookId
	bookContents = []
	chapterId = 0

	image = ''
	image_300 = ''
	image_433 = ''	
	booktitle = ''
	
	url = 'https://www.raiplayradio.it/playlist/' + listContentId[i] + '.html'
	params = {}
	headers = {'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36',
		   'Accept' : 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8' ,
		   'Accept-Language' : 'it-IT,it;q=0.9,en-US;q=0.8,en;q=0.7',
		   'Accept-Encoding' : 'gzip, deflate, br',
		   'X-Requested-With': 'XMLHttpRequest',
		   'Host': 'www.raiplayradio.it',
		   'Referer': 'https://www.raiplayradio.it/playlist/' + listContentId[i] + '.html',
		   'Cookie': '_iub_cs-844946=%7B%22consent%22%3Atrue%2C%22timestamp%22%3A%222018-12-11T09%3A54%3A42.004Z%22%2C%22version%22%3A%220.13.24%22%2C%22id%22%3A844946%7D; wt_rla=602039762736393%2C21%2C1544522080017; AKA_A2=A',
			   'Connection': 'keep-alive',
		   'Cache-Control': 'max-age=0',
			'Upgrade-Insecure-Requests': '1'
	} 

	response = requests.get(url, params=params, headers=headers)		

	try:
		print (response)

		body = BeautifulSoup(response.content, "html.parser")
		
		print(body.prettify())

		# td = soup.body.td  # or soup.find('td', id='aisd_calendar-2014-04-28-0')
		# print (td['date'].strip('*'))
		# link = soup.find('div', {'class': 'contents'}).a
		# print (link['href'])
		
		print('-------')
		listElements = body.find_all('li')
		
		print ('\n\n\n------------------ ELEMENTS ------------------\n\n\n')
		#print (listElements)

		for el in listElements:
			chapter = {}

			print ("------------------------------------")
			#print (el)
			
			try:
				print ("data-mediapolis: " + el['data-mediapolis'])	
				print ("data-title: " + el['data-title'])	
				
				if(booktitle == ''):
					booktitle = el['data-title']

				chapter['contentId'] = listContentId[i]
				chapter['id'] = chapterId
				chapter['title'] = el['data-title']
				chapter['desc'] = el.find('p').string
				chapter['url'] = el['data-mediapolis']
				chapter['format'] = 'mp3'
				chapter['weblink'] = url
				chapter['webLinkLookup'] = False
				
				chapterId = chapterId + 1
				
				print ('Chapter: ' + str(chapter))

				bookContents.append(chapter)
			except:
				print ('---Exception')
	except:
		receivedNotFound = True
		print ('----------------------- Exception ---------------------')
		traceback.print_exc()
		print(url)
	
	book['contents'] = bookContents
	
	book['metadata'] = {		
		'image': 'http://www.rai.tv' + image,
		'title': booktitle,
		'author': booktitle,
		'other_images' : {
			'image_300' : 'http://www.rai.tv' + image_300,
			'image_433' : 'http://www.rai.tv' + image_433,
		},
		'provider' : {
			'name' : 'Ad Alta Voce',
			'site' : 'http://www.adaltavoce.rai.it/',
			'image' : ''
		}
	}

	audiobooks.append(book)
	
	bookId = bookId + 1
	chapterId = 0

	data = {}
	data['version'] = 0
	data['config'] = {}
	data['audiobooks'] = audiobooks

	out_file = open("config_test.json","w")
	out_file.write(json.dumps(data, sort_keys=True, indent=4))
	out_file.close()

	time.sleep(3)

json_data = json.dumps(data)

print(json.dumps(data, sort_keys=True, indent=4))



