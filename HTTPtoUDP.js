var http = require('http');
var dispatcher = require('httpdispatcher');
var dgram = require('dgram');

var HTTP_PORT = 8080;
var UDP_PORT = 4000;
var HOST = '127.0.0.1';

var handleRequest = function(request, response){	
	try{
		console.log(request.url);
		dispatcher.dispatch(request,response);
	}catch(err){
		console.log(err);
	}
};

dispatcher.onGet('/', function(req, res){
	console.log('-> /');
	res.writeHead(200, {'Content-Type':'text/plain'});
	res.end('Test Message: Accessed / Successfully');
});


var udp = dgram.createSocket('udp4');
udp.bind(UDP_PORT);

udp.on('message',function(message){
	console.log('Got : ' + message);
});

dispatcher.onPost('/udp', function(req, res){
	var data = JSON.parse(req.body);
	console.log(data.target + ':' + data.port + '/');
	var message = new Buffer(data.message);
	udp.send(message,0,message.length, data.port, data.target);

	res.writeHead(200,{'Content-Type':'text/plain'});
	res.end('No post on Sundays!');
});

var httpServer = http.createServer(handleRequest);
httpServer.listen(HTTP_PORT, function () {
	console.log('Server listening on http://localhost:%s', HTTP_PORT);
});