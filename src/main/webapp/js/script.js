/**
 * Copyright (C) 2011 Daniel Kurashige-Gollub, daniel@kurashige-gollub.de
 * Please see the README file for details.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

//$(document).ready(function() {
//	log("ready function");
//	
//} );

//those are the css class names for the <span> tags we are interested in
var entryClasses = "title,content,author,updated,time".split(","); 

var log = function(s) {
	//console is an object present in chrome which can be useful for debugging	
	if (typeof(console) == 'undefined' || console == null) 
		alert(s);
	else
		console.log(s);
};

function isValidObject(o) {
	return typeof(o) != 'undefined' && o != null;	
}

function handleMissingInput(id, msg) {
	var o = $("#"+id);
	if (!isValidObject(o))
		throw Error("Object with id ["+id+"] not found.");
	if (o.focus)
		o.focus();
	if (o.css) {
		o.css("background-color", "red");	
	}
	alert(msg);
	throw Error(msg);	
}

function get_field_input(id, allowEmpty) {
	if (typeof(allowEmpty) !== typeof(true))
		allowEmpty = true;
	var oid = "#"+id;
	var so = $(oid);
	if (!isValidObject(so))
		throw Error("Could not find object with id ["+id+"].");
	var st = so.val();	
	if (!allowEmpty && !isValidObject(st))
		handleMissingInput(id, "No data entered for " + so.attr("title"));
	var s = st.trim();
	if (s.length == 0 && !allowEmpty) {
		log("data: " + s.length);		
		handleMissingInput(id, "No data entered for this field: " + so.attr("title"));
	}
	return s;
}
function openGmail() {
	log("openGmail");
	try {
		var data = _prepareDataForGmail();
		if (data != null && data.entries.length > 0) {
			log("got data - send it w/ gmail: " + JSON.stringify(data));			
			_openGmailWindow(data);
		}
		else {
			log("No entries selected.");
		}
	}
	catch (error) {
		log("Something went wrong: " + error);
	}	
}

function _createBodyUrlPart(entries) {
	log("_createBodyUrlPart");	
//format of the entries paramter as follows
//++++
//"entries":[{"title":"This is my entry title.","content":"Dinner with Jen.","author":"Author: 
//...@gmail.com","updated":"Updated: 2011-11-17T04:37:40.000Z","time":["Start: 2011-12-01T15:30:00.000+09:00","End: 
//2011-12-01T16:30:00.000+09:00"]}, {…<next entry>…}, …]
//++++

	//This will create the body of our email, which will be later URL encoded, so we can add it to our gmail window url.	
	var body = "";
	entries.forEach(function(element) {
//		log("Element: " + element);
		//var o = $(element);
		//log("o: " + o);
		body += "\n\nEntry\n";
		body +=     "-----\n\n";
		body += "Title: " + element.title + "\n";
		body += "Content: " + element.content + "\n";
		body += element.author + "\n";
		if (element.time && element.time.length>1) {
			element.time.forEach(function(el) {
				body += el + "\n";	
			});
		}
	});
	
	log("BODY: " + body);

	return body;
}

function _openGmailWindow(data) {
	log("_openGmailWindow");
	
//	https://mail.google.com/mail/?view=cm&fs=1&tf=1&to=TO&cc=CC&su=SUBJECT&body=BODY
	
	var url = "https://mail.google.com/mail/?view=cm&fs=1&tf=1&";

	//We have to encode the url in order for this to work correctly.
	//IMPORTANT: also, if we have a larger message (eg. body or subject) and exceed Google's URL character limit,
	//			 then this method won't work.
	url += "&to=" + encodeURIComponent(data.to);
	if (data.cc.length > 0)
		url += "&cc=" + encodeURIComponent(data.cc);
	url += "&su=" + encodeURIComponent(data.subject);
	url += "&body=" + encodeURIComponent(_createBodyUrlPart(data.entries));
	
	log("URL: " + url);
	
	window.open(url, "gmail_window");
}

function _prepareDataForGmail() {
	
	//reset all input elements
	$("input:text").css("background-color", "");
	
	//TODO: Maybe we should validate all fields at once and not one by one, cause right now when one validation fails
	//		the whole application flow is interrupted and the user is greeted by an error message
	var tos      = get_field_input("mail_to", false);
	var ccs      = get_field_input("mail_cc");
	var subject  = get_field_input("mail_subject", false);
	
	//Get all the calendar entries the user marked
	var checkboxes = $("input:checked");
	var l = checkboxes.length;

	if (l == 0) {
		alert("You have to select at least one entry to include in your mail.");
		return null;
	}
	else {
		//TODO: think about getting the data not via JavaScript on client side, but via a server roundtrip
		//		via AJAX, just to make sure we have the correct and "un-tampered" data.
				
		var data = {};
		var entriesData = [];
		checkboxes.each(function(index) {
			//'this' is one of the checked entries' checkboxes
			var entryDiv = $(this).parent().parent(); //first parent is the <span> around the checkbox		
			var entry = {};
			entryClasses.forEach(function(clsName) {
				$(entry).attr(clsName, entry_value(entryDiv, clsName));
			});
//			log("Entry: " + JSON.stringify(entry));
			entriesData.push(entry);
		});	
//		log("Entries data: " + JSON.stringify(entriesData));
		data.entries = entriesData;
		data.to      = tos;
		data.cc      = ccs;
		data.subject = subject;
		return data;
	}
}

function entry_value(entryDiv, clsName) {
	var elements = entryDiv.find("[class="+clsName+"]");
	var count    = elements.length;
	//In case of the clsName == 'time' we have 2 <span> tags, one with start and one with the end time, 
	//therefore we need to be able to return both in separate fields, not as one!
	var ret = (count > 1 ? []: ""); 
	elements.each(function(index, elem){
		if (count > 1)
			ret.push( $(elem).text() );
		else
			ret = $(elem).text();
	});
	return ret;
}
