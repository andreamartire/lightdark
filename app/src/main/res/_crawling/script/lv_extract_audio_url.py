#!/usr/bin/python3.5

import requests

url = 'https://librivox.org/search/get_results?primary_key=0&search_category=language&sub_category=&search_page=1&search_order=alpha&project_type=either'
HEADERS = {
	 'user-agent': ('Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) '
						 'AppleWebKit/537.36 (KHTML, like Gecko) '
					 	'Chrome/45.0.2454.101 Safari/537.36'),
}

params = {"primary_key":4,"project_type":"either","search_category":"language","search_order":"alpha","search_page":"1","sub_category":""}
print('Getting all...')
resp = requests.get(url, headers=HEADERS, params=params)
print (resp)
data = resp.responseText
print (data)

print ('Start')

def do_something(response, **kwargs):
	print (response)

def onComplete(url, **kwargs):
	print (url)
	
requests.get(link, params={"primary_key":4,"project_type":"either","search_category":"language","search_order":"alpha","search_page":"1","sub_category":""}, hooks = {'response' : do_something})

#r = requests.post(link, data={'primary_key':4,'search_category':'language','search_page':1})

exit

wp = urllib.request.urlopen('')
sourceMainPage = str(wp.read())

rows = str(sourceMainPage).split("<li><em>")#, sourceMainPage.count(str))

#prepare regex to extract audiobook contentId
regContent = re.compile(".*?<a href=\"(.*?)\">(.*) \[audiolibro\]</a>.*?di <a href=\".*?\">(.*?)</a>")

audiobooks = []
listUrl = []

for i in range(len(rows)):
	#print ("ROW: " + rows[i] + "\n");
	
	#<a href=\"https://www.liberliber.it/online/autori/autori-j/jerome-k-jerome/tre-uomini-in-una-barca-audiolibro/\">Tre uomini in una barca [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-j/jerome-k-jerome/\">Jerome K. Jerome</a><br><span class=\"ll_libro_sottotitolo\">(per tacer del cane)</span></li>
	if (regContent.match(rows[i])):
		link = str(regContent.match(rows[i]).group(1));
		print("--- Link ---"+link+"---")
		title = str(regContent.match(rows[i]).group(2))
		print("--- Title ---"+title+"---")
		author = str(regContent.match(rows[i]).group(3))
		print("--- Author ---"+author+"---")
		listUrl.append(link)
		
		book = {}
		bookContents = []
		
		wp = urllib.request.urlopen(link)
		book_str = str(wp.read())
		
		#with open('book_test.txt', 'r') as myfile:
		#	book_str=myfile.read().replace('\n', '')

		chaptSectionReg = re.compile(".*\"lm_mp3\">(.*?)</ul><br")
		chaptLinkReg = re.compile(".*<a href=\"(.*?)\">(.*?)</a>")

		if (chaptSectionReg.match(book_str)):
			book_str = chaptSectionReg.match(book_str).group(1)
			print(book_str)
			
			chapterId = 0
			bookRows = book_str.split("<li>")
			for i in range(len(bookRows)):
				print ("BOOK ROW: " + bookRows[i] + "\n");
				#<a href="https://www.liberliber.it/mediateca/audiolibri/d/de_roberto/i_vicere/mp3/de_rober_i_vicere_sil_51_p3_9a.mp3">Parte terza: 9 - a</a></li>
				if (chaptLinkReg.match(bookRows[i])):
					url = chaptLinkReg.match(bookRows[i]).group(1)
					chapTitle = chaptLinkReg.match(bookRows[i]).group(2)
					print("--- Url ---" + url + "---")
					print("--- Title ---" + chapTitle + "---")
					
					chapter = {}
					chapter['id'] = chapterId
					chapter['title'] = chapTitle
					chapter['desc'] = ''
					chapter['url'] = url
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
				'name' : 'Liber Liber',
				'site' : 'https://www.liberliber.it',
				'image' : ''
			},
			'title': title,
			'author': author
		}
		
		print ('Book: ' + str(book))
		
		audiobooks.append(book)

	data = {}
	data['version'] = 0
	data['config'] = {}
	data['audiobooks'] = audiobooks

	out_file = open("config_liblib.json","w")
	out_file.write(json.dumps(data, sort_keys=True, indent=4))
	out_file.close()

	#time.sleep(10)

json_data = json.dumps(data)

print(json.dumps(data, sort_keys=True, indent=4))






