<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="/webchat/js/bootstrap/css/bootstrap.min.css" />
<link rel="stylesheet"
	href="/webchat/js/bootstrap/css/bootstrap-theme.min.css" />
<script type="text/javascript" src="/webchat/js/jquery-2.1.3.min.js"></script>
<script type="text/javascript"
	src="/webchat/js/bootstrap/js/bootstrap.min.js"></script>
<title>Index</title>
<script type="text/javascript">
	$(function() {
		var ws = null;
		var reconnectId=-1;
		function startWebSocket() {
			if ('WebSocket' in window)
				ws = new WebSocket(
						"ws://localhost:8080/webchat/websocket/${userId}");
			else if ('MozWebSocket' in window)
				ws = new MozWebSocket(
						"ws://localhost:8080/webchat/websocket/${userId}");
			else
				alert("not support");
			ws.onmessage = function(evt) {
				var data = JSON.parse(evt.data);
				switch (data['action']) {
				case 'message':
					$('#msg').append($(data['data']));
					$('#msg').scrollTop($('#msg')[0].scrollHeight);
					break;
				case 'refresh-room':
					var interRoomId=$('#rooms').find('span.active').attr('id');
					$('#rooms').empty();
					var rooms = JSON.parse(data['data']);
					for (var i = 0; i < rooms.length; i++) {
						$('#rooms')
								.append(
										$('<span style="display:block;" id="'+rooms[i]['roomId']+'">'
												+ rooms[i]['roomName']
												+ '<a href="javascript:void(0);" class="btn btn-default btn-sm enter">加入</a></span>'));
					}
					;
					$('#rooms').find('a.enter').click(function() {
						if(reconnectId!=-1){
							return;
						}
						var id = $(this).closest('span').attr('id');
						var data = {
							action : 'inter-room',
							data : id
						};
						ws.send(JSON.stringify(data));
					});
					$('#rooms').find('#' + interRoomId).addClass('active');
					break;
				case 'inter-room':
					$('#rooms').find('span.active').removeClass('active');
					$('#rooms').find('#' + data['data']).addClass('active');
					break;
				}

			};

			ws.onclose = function(evt) {
				//alert("close");
				$('#notice').text('已经断开连接,系统将在5秒之后重连...');
				reconnectId=
					setTimeout(function() {
						startWebSocket();
					}, 5000);
			};

			ws.onopen = function(evt) {
				$('#notice').text('已经连接到服务器,正在获取聊天室...');
				setTimeout(function() {
					$('#notice').empty();
					var data = {
						action : 'refresh-room',
						data : ''
					};
					ws.send(JSON.stringify(data));
				}, 2000);
				try{
					clearTimeout(reconnectId);
					reconnectId=-1;
				}catch(e){
					
				}
				
				
				//alert("open");

			};
		}

		$('#sendmsg').click(function() {
			if(reconnectId!=-1){
				return;
			}
			var data = {
				action : 'message',
				data : $('#writeMsg').val()
			};
			ws.send(JSON.stringify(data));
			$('#writeMsg').val('');
		});

		$('#createRoom').click(function() {
			if(reconnectId!=-1){
				return;
			}
			var data = {
				action : 'add-room',
				data : $('#writeRoom').val()
			};
			ws.send(JSON.stringify(data));
			 $('#writeRoom').val('');
		});
		startWebSocket();
	});
</script>
<style type="text/css">
#msg {
	max-height: 470px;
	min-height:470px;
	overflow-y: auto;
}
a.enter{
float: right;
}
span.message{
	word-break:break-all;
	color: #333c3e;
	word-wrap: break-word;
}
span.atTime{
	color: red;
}
span#notice{
	
}
#rooms>span{
line-height: 2.1;
padding: 3px 3px;
padding-left: 15px;
}
#rooms>span.active{
	background-color:#e7e6d1;
}

</style></head>

<body>
<div class="container">
		<div class="page-header">
			<h1>欢迎来到匿名聊天室...</h1>
		</div>
		<div class="row" style="height: 600px">
			<div class="col-md-8">
				<div class="row" style="height:500px">
					<div class="panel panel-info">
						<div class="panel-heading">聊天内容<span id="notice" class="help-block"></span></div>
						<div class="panel-body" id="msg"></div>
						<div class="panel-footer">
							<form class="form-inline" autocomplete="off">
								<div class="form-group" style="width: 75%;">
									<input type="text" class="form-control" id="writeMsg" style="width: 100%;"/>
								</div>
								<span class="btn btn-default" id="sendmsg">发送</span>
							</form>
						</div>
					</div>
				</div>
			</div>
			<div class="col-md-4">
				<div class="panel panel-info">
					<div class="panel-heading">匿名聊天室</div>
					<div class="panel-body" id="rooms"></div>
				</div>
			</div>
		</div>
		<div class="row">
			<input type="text" class="form-control" id="writeRoom" style="display: inline-block;width: 50%;"/>
			<span class="btn btn-default" id="createRoom">创建房间</span>
		</div>
	</div>
</body>
</html>