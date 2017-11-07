#!/usr/bin/python3.5

import re
import requests
import json
import time
import traceback
import datetime
import krakenex
import random

wallet = {
	"XBT": { "price": 7102 },
	"ETH": { "price": 302.05 },
	'EOS': { 'price': 0.9787939200000001 },
	'ETC': { 'price': 14.3995644 },
	'GNO': { 'price': 62.18816400000001 },
	'MLN': { 'price': 56.193775200000005 }
}

marketHighPrev = {
	"XBT": { "price": 7250 },
	"ETH": { "price": 305 },
	"EOS": { "price": 1.02 },
	"ETC": { "price": 14.1 },
	"GNO": { "price": 62 },
	"MLN": { "price": 56 }
}

marketLowPrev = {
	"XBT": { "price": 7100 },
	"ETH": { "price": 295 },
	"EOS": { "price": 0.96 },
	"ETC": { "price": 14.3 },
	"GNO": { "price": 60 },
	"MLN": { "price": 52 }
}

previousCoins = {}
coins = {}

fee = 0.26
earnBound = 1

secInterval = 300
secIntervalStr = str(secInterval) + "s"

def refreshWallet():
	coinMap = {"XXBT": "XBT", "XETH": "ETH", "XETC": "ETC", "XMLN": "MLN"}

	k = krakenex.API()
	k.load_key('kraken.key')
	balance = k.query_private('Balance')["result"]

	for key in balance:
		if(key in coinMap.keys()):
			remappedKey = coinMap[key]
			if(remappedKey not in wallet):
				wallet[remappedKey] = { 'num': 0, 'price': 0 }
			p = wallet[remappedKey]["price"]
			wallet[remappedKey] = { 'num': float(balance[key]), 'price': p }
		else:
			if(key not in wallet):
				wallet[key] = { 'num': 0, 'price': 0 }
			p = wallet[key]["price"]
			wallet[key] = { 'num': float(balance[key]), 'price': p }
			
def updateKrakenData():
	#https://api.kraken.com/0/public/Ticker?pair=XXBTZUSD,XETHXXBT,XETCXXBT,XMLNXXBT,GNOXBT,EOSXBT,BCHXBT,LTCXBT,XRPXBT,DASHXBT,XMRXBT,XLMXBT,REPXBT,ICNXBT,ZECXBT,XDGXBT
	krakenUrl = "https://api.kraken.com/0/public/Ticker?pair=XXBTZUSD,XETHXXBT,XETCXXBT,XMLNXXBT,GNOXBT,EOSXBT,BCHXBT,LTCXBT,XRPXBT,DASHXBT,XMRXBT,XLMXBT,REPXBT,ICNXBT,ZECXBT,XDGXBT"
	global coins
	previousCoins = coins
	coins = {}

	headers = {
		'User-Agent' : 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36'
	}

	result = json.loads(str(requests.get(krakenUrl, headers = headers).text))["result"]

	#print (result)

	ticker = []
	for key in result.keys():
		#print (key)
		#print (result[key]["c"][0])
		
		if(key == "DASHXBT"):
			key1 = "DASH"
			key2 = "XBT"
		elif(len(key) == 6):
			key1 = key[0:3]
			key2 = key[3:6]
		elif(len(key) == 8):
			key1 = key[1:4]
			key2 = key[5:8]
		#print ("key1: " + key1 + " - key2: " + key2)

		ticker.append({ "coin1" : key1, "coin2": key2, "price": float(result[key]["c"][0]) })

		if(key == "XXBTZUSD"):
			usdPrice = float(result[key]["c"][0])

	#print ("USD PRICE: "+str(usdPrice))
	for tick in ticker:
		if(tick["coin2"] == "XBT"):
			#print (tick)
			tick["price"] = tick["price"]*usdPrice
			#print (tick)

		coin = {}
		coin["name"] = tick["coin1"]
		coin["price"] = tick["price"]
		coins[coin["name"]] = coin

	if(len(previousCoins.keys()) > 0):
		for key in previousCoins.keys():
			#print ("-- key "+key)
			oldCoin = previousCoins[key]
			newCoin = coins[key]	

			#print ("Old Price "+str(oldCoin["price"]))
			#print ("New Price "+str(newCoin["price"]))
			delta = (newCoin["price"] - oldCoin["price"])/oldCoin["price"]

			if(secIntervalStr in oldCoin):
				newCoin[secIntervalStr] = oldCoin[secIntervalStr]

			if(delta != 0):
				newCoin[secIntervalStr] = "{0:.5f}".format(delta)
				
			#print ("% var "+str(delta))

	print ("======================================= MARKET ======================================")
	for key in sorted(wallet.keys()):
		#print (key)
		print (str(coins[key]))
	print ("=====================================================================================")

def updateCoinMarketDataDISABLED():
	global coins
	previousCoins = coins
	coins = {}

	headers = {
		'User-Agent' : 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36'
	}
	sourceMainPage = str(requests.get(link, headers = headers).text)

	link = "https://coinmarketcap.com/all/views/all/"

	#<span class="currency-symbol"><a href="/currencies/infchain/">INF</a></span>
	#<span class="currency-symbol"><a href="/currencies/bitcoin/">BTC</a></span>
	regCoinName = re.compile(".*?<span class=\"currency-symbol\">.*?>(.*?)</a></span>")

	#<a class="currency-name-container" href="/currencies/bitcoin/">Bitcoin</a>
	regCoinDesc = re.compile(".*?<a class=\"currency-name-container\".*?>(.*?)</a>")

	#<a href="/currencies/infchain/#markets" class="price" data-usd="0.00726931" data-btc="1.05737e-06" >$0.007269</a>
	regCoinPrice = re.compile(".*? class=\"price\" .*?>\$(.*?)</a>")

	#<td class="no-wrap percent-1h  positive_change  text-right" data-usd="0.32" data-btc="0.82" >0.32%</td>
	regLastHourChange = re.compile(".*?<td class=\"no-wrap percent-1h .*?>(&gt; )?(.*?)%</td>")

	#<td class="no-wrap percent-24h  positive_change  text-right" data-usd="2.33" data-btc="5.11" >=2.33%</td>
	regLastDayChange = re.compile(".*?<td class=\"no-wrap percent-24h .*?>(&gt; )?(.*?)%</td>")

	#<td class="no-wrap percent-7d  negative_change text-right" data-usd="-26.69" data-btc="-41.32" >-26.69%</td>
	regLastWeekChange = re.compile(".*?<td class=\"no-wrap percent-7d .*?>(&gt; )?(.*?)%</td>")

	#print ("size: " + str(len(rows)))

	rows = str(sourceMainPage).split("<tbody>")
	rows = str(rows[1]).split("</tbody>")
	rows = str(rows[0]).split("</tr>")

	for i in range(len(rows)):
		#print ("C: " + rows[i] + "\n");

		elements = str(rows[i]).split("\n")

		coin = {}
		coin["desc"] = None
		coin["price"] = 0
		coin["5m"] = 0
		coin["1h"] = 0
		coin["24h"] = 0
		coin["7d"] = 0

		for j in range(len(elements)):
			if(regCoinName.match(elements[j])):
				cName = str(regCoinName.match(elements[j]).group(1))
				#print("Name:\t"+cName)
				coin["name"] = cName
			
			if(regCoinDesc.match(elements[j])):
				cDesc = str(regCoinDesc.match(elements[j]).group(1))
				#print("Desc:\t"+cDesc)
				coin["desc"] = cDesc
			
			if(regCoinPrice.match(elements[j])):
				cPrice = float(regCoinPrice.match(elements[j]).group(1).replace(",",''))
				#print("Price:\t"+str(cPrice))
				coin["price"] = cPrice
			
			if(regLastHourChange.match(elements[j])):
				cLastHourChange = float(regLastHourChange.match(elements[j]).group(2))
				#print("1h:\t"+str(cLastHourChange))
				coin["1h"] = cLastHourChange
			
			if(regLastDayChange.match(elements[j])):
				cLastDayChange = float(regLastDayChange.match(elements[j]).group(2))
				#print("24h:\t"+str(cLastDayChange))
				coin["24h"] = cLastDayChange
						
			if(regLastWeekChange.match(elements[j])):
				cLastWeekChange = float(regLastWeekChange.match(elements[j]).group(2))
				#print("7d:\t"+str(cLastWeekChange))
				coin["7d"] = cLastWeekChange

		if('name' in coin):
			coins[coin["name"]] = coin

			#print("--------------------------------------")

	if(len(previousCoins.keys()) > 0):
		for key in previousCoins.keys():
			#print ("-- key "+key)
			oldCoin = previousCoins[key]
			newCoin = coins[key]	

			#print ("Old Price "+str(oldCoin["price"]))
			#print ("New Price "+str(newCoin["price"]))
			delta = (newCoin["price"] - oldCoin["price"])/oldCoin["price"]
			coins[key]["5m"] = delta		
			#print ("% var "+str(delta))


	#print(json.dumps(coins, sort_keys=True, indent=4))
	print ("======================================= MARKET ======================================")
	for key in sorted(wallet.keys()):
		#print (key)
		print (str(coins[key]))
	print ("=====================================================================================")

def change(wallet, coins, oldVal, newVal):
	print ("CHANGE "+oldVal+" to "+newVal)
	old = wallet[oldVal]
	if(newVal not in wallet):
		wallet[newVal] = 0
	new = wallet[newVal]

	oldCoinMkt = coins[oldVal]
	newCoinMkt = coins[newVal]

	startVal = old["num"]*oldCoinMkt["price"] 
	#print ("startVal " + str(startVal))
	valAfterFee = startVal - (startVal*fee/100)#fee
	#print ("valAfterFee " + str(valAfterFee))
	valConverted = valAfterFee/newCoinMkt["price"]
	#print ("valConverted " + str(valConverted))
	
	wallet[oldVal]["num"] = 0
	wallet[newVal]["num"] = valConverted
	
	#refresh wallet buy price
	for walletCoin in wallet:
		wallet[walletCoin]["price"] = coins[walletCoin]["price"]

def getWalletVal(wallet):
	tot = 0
	for key in wallet.keys():
		tot += coins[key]["price"]*wallet[key]["num"]	
	return tot

def getLastChangeWalletVal(wallet):
	tot = 0
	for key in wallet.keys():
		tot += wallet[key]["price"]*wallet[key]["num"]	
	return tot

def showWalletLine(wallet, key):
	if(wallet[key]["num"] != 0):
		print (key + ": " + str("{0:.10f}".format(wallet[key]["num"])) + "\tV:" +str("{0:.5f}".format(coins[key]["price"]*wallet[key]["num"])) + "\tM:" +str(coins[key]["price"]) + "\t(Old " + str(wallet[key]["price"]) + ")")

def showWallet(wallet):
	print ("="*30+" WALLET "+"="*30)
	for key in wallet.keys():
		showWalletLine(wallet, key)

	lastW = getLastChangeWalletVal(wallet)
	currW = getWalletVal(wallet)
	print ("LAST CHANGE VAL: " + str(lastW))
	print ("CURRENT VALUE:   " + str(currW))
	
	diff = currW-lastW
	if(lastW != 0):
		print ("="*27 + " " + str("{0:.2f}".format(diff)) + "$ (" + str("{0:.3f}".format((diff/lastW)*100)) + "%) "+"="*27)

def engine(wallet):

	actions = [];

	lastChangeVal = getLastChangeWalletVal(wallet)
	currVal = getWalletVal(wallet)
	
	currFee = currVal*fee/100
	maxGain = 0
	bestAction = None
	
	print ("IF CHANGE -> CurrValue: " + str(currVal) + " FutureValue: " +str(currVal - currFee) + " (Fee: " + str(currFee) + ")")
	if(currVal - currFee <= lastChangeVal + lastChangeVal*earnBound/100):
		print ("NB. IF CHANGE LOSE VALUE. ")
	else:
		print ("NB. IF CHANGE GAIN. ")
	
		#update prev margin gain
		for key in marketHighPrev:
			marketHighPrev[key]["gain"] = ((marketHighPrev[key]["price"] - wallet[key]["price"])/wallet[key]["price"])*100
			for walletCoin in wallet:
				if(wallet[walletCoin]["num"] > 0 and walletCoin != key):
					#generate action
					actions.append({"type": "CHANGE", "old_coin": walletCoin, "new_coin": key, "gain": marketHighPrev[key]["gain"]})
		#print (marketHighPrev)
		
		for action in actions:
			#print ("Candidate: " + str(action))
			if(action["gain"] > maxGain):
				maxGain = action["gain"]
				bestAction = action

	if(bestAction is None):
		return []
	return [bestAction]

def randomMarketData():
	global coins
	if("XBT" not in coins):
		coins["XBT"] = { "price": 7100 }
	if("ETH" not in coins):
		coins["ETH"] = { "price": 295 }
	if("EOS" not in coins):
		coins["EOS"] = { "price": 0.96 }
	if("ETC" not in coins):
		coins["ETC"] = { "price": 14.3 }
	if("GNO" not in coins):
		coins["GNO"] = { "price": 60 }
	if("MLN" not in coins):
		coins["MLN"] = { "price": 52 }

	for coin in coins:
		coins[coin]["price"] = coins[coin]["price"] + random.uniform(-1, 1)
	
	print ("======================================= MARKET ======================================")
	for key in sorted(wallet.keys()):
		#print (key)
		print (str(coins[key]))
	print ("=====================================================================================")
		
lastWalletVal = 0

refreshWallet()

while(True):	
	#updateCoinMarketData()
	updateKrakenData()
	#randomMarketData()
	
	showWallet(wallet)

	actions = engine(wallet)

	print ("ACTIONS: " + str(actions))

	if(len(actions)>0):
		for action in actions:
			if(action["type"] == "CHANGE"):#TODO
				change(wallet, coins, action["old_coin"], action["new_coin"])
	
	lastWalletVal = getWalletVal(wallet)

	showWallet(wallet)

	print (datetime.datetime.now().strftime("%Y-%m-%d %H:%M"))
	print ("--------------------------------------------------------------------------------------------------")

	time.sleep(secInterval)# 5 sec
	



